package com.crypticbit.ipa.central;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.central.backupfile.BackupFileException;
import com.crypticbit.ipa.central.backupfile.EncodedBackupFile;
import com.crypticbit.ipa.central.backupfile.RemoteBackupFile;
import com.crypticbit.ipa.central.backupfile.Version2BackupFile;
import com.crypticbit.ipa.central.filters.MdbackupFilenameFilter;
import com.crypticbit.ipa.central.filters.MddataFilenameFilter;
import com.crypticbit.ipa.central.filters.MdinfoFilenameFilter;
import com.crypticbit.ipa.io.util.IoUtils;
import com.crypticbit.ipa.licence.LicenceValidator;
import com.crypticbit.ipa.licence.NotLicencedException;

/**
 * The API gateway to the system, allowing individual files, directories, etc.
 * to be processed by the system. Because a temporary directory is used for some
 * process, and in some caces data is cached it is reccomended that a new
 * factory is used for each logical seperate set of files, i.e. they come from
 * different devices or from different time periods.
 * 
 * @author Leo
 * 
 */
public class IPhoneFactory {

	private ProgressIndicator progressIndicator = ProgressIndicator.nullProgressIndicator;
	File tempDir;
	private Map<String, File> data = new HashMap<String, File>();
	private Map<String, File> info = new HashMap<String, File>();
	private LicenceValidator licenceValidator;

	/**
	 * Create a new factory. As this needs to create a temporary directory
	 * (which will live for the life of the system, it may throw an IOException.
	 * No progress updates will be created.
	 * 
	 * @throws IOException
	 *             if the temp directory can not be created
	 */
	public IPhoneFactory(final LicenceValidator licenceValidator)
			throws IOException {
		this.licenceValidator = licenceValidator;
		this.tempDir = com.crypticbit.ipa.io.util.IoUtils.createTempDir(".working");
	}

	/**
	 * Create a new factory. As this needs to create a temporary directory
	 * (which will live for the life of the system, it may throw an IOException.
	 * 
	 * @param progressIndicator
	 *            a progress updater to update when new files are processed.
	 *            Currently only used for multiple file sets
	 * @throws IOException
	 *             if the temp directory can not be created
	 */
	public IPhoneFactory(final LicenceValidator licenceValidator,
			final ProgressIndicator progressIndicator) throws IOException {
		this(licenceValidator);
		this.progressIndicator = progressIndicator;
	}

	/**
	 * Unpacks an encoded iPhone file and makes it available for parsing, etc.
	 * An encoded iPhone file is named with a long hex string followed by either
	 * mddata, mdinfo or mdbackup. If it is a mdinfo or mddata file then the
	 * system will search for it's matching pair in the same directory. If not
	 * found it will either fail or attempt to continue.
	 * 
	 * @param file
	 *            the file to process
	 * @return an instance of a class allowing access to the original file name,
	 *         file contents, and paring details of file
	 * @throws BackupFileException
	 *             if any problems parsing file
	 * @throws IOException
	 *             if any problems accessing file
	 */
	public BackupFile createIPhoneFileFromEncoded(final File file)
			throws BackupFileException, IOException {
		if (new MdbackupFilenameFilter().accept(file.getParentFile(), file
				.getName()))
			return new EncodedBackupFile(file, this.tempDir);
		else if (new MddataFilenameFilter().accept(file.getParentFile(), file
				.getName()))
			return new Version2BackupFile(file);
		throw new UnsupportedFileFormat("Unable to identify format of file: "
				+ file.getName());
	}

	/**
	 * Unpacks an encoded iPhone file and makes it available for parsing, etc.
	 * An encoded iPhone file is named with a long hex string followed by either
	 * mddata, mdinfo or mdbackup. If it is a mdinfo or mddata file then the
	 * system will use it's pair if already seen (it caches history) and if not
	 * will cache and return null.
	 * 
	 * @param file
	 *            the file to process
	 * @return an instance of a class allowing access to the original file name,
	 *         file contents, and paring details of file or null if the pair has
	 *         not yet been seen.
	 * @throws BackupFileException
	 *             if any problems parsing file
	 * @throws IOException
	 *             if any problems accessing file
	 */
	public BackupFile createIPhoneFileFromEncodedWithCaching(final File file)
			throws BackupFileException, IOException {
		if (new MdbackupFilenameFilter().accept(file.getParentFile(), file
				.getName()))
			return createIPhoneFileFromEncoded(file);
		else {
			if (new MddataFilenameFilter().accept(file.getParentFile(), file
					.getName())) {
				this.data.put(getFileName(file), file);
			} else if (new MdinfoFilenameFilter().accept(file.getParentFile(),
					file.getName())) {
				this.info.put(getFileName(file), file);
			} else
				throw new UnsupportedFileFormat(
						"Unable to identify format of file: " + file.getName());
			if ((this.data.get(getFileName(file)) != null)
					&& (this.info.get(getFileName(file)) != null))
				return new Version2BackupFile(this.data.get(getFileName(file)),
						this.info.get(getFileName(file)));
			return null;
		}

	}

	public BackupFile createIPhoneFileFromFileObject(final FileObject file,
			final FileObject root) throws IPhoneParseException {
		try {
			return new RemoteBackupFile(this.tempDir, file, root);
		} catch (FileSystemException e) {
			throw new IPhoneParseException(
					"Unable to access iphone filesystem", e);
		}
	}

	/**
	 * Used to scan an entire directory and produce a cohesive set of data
	 * including access to files (by name and type) as well as manifest, status,
	 * info etc. and global vars
	 * 
	 * @param directory
	 *            the directory to scan
	 * @return an instance of IPhone that has access to all the available data
	 * @throws IPhoneParseException
	 *             If any errors in parsing directory, or non file based
	 *             structures
	 * @throws IOException
	 *             if files can not be accessed, etc.
	 * @throws FileParseException
	 *             if any errors parsing files
	 * @throws NotLicencedException
	 */
	public IPhone createIPhoneState(final File directory, final boolean unpacked)
			throws IPhoneParseException, IOException, FileParseException,
			NotLicencedException {
		IPhone iphone;
		if (unpacked)
			iphone = new UnpackedFileConnectionParser(this, directory,
					this.progressIndicator).getIphoneConfiguration();
		else
			iphone = new BackupDirectoryParser(this, directory,
					this.progressIndicator).getIphoneConfiguration();
		this.licenceValidator
				.checkLicence(iphone.getConfigElements().getInfo());
		return iphone;
	}

	public IPhone createIPhoneState(final String serverAddress,
			final String userId, final String password)
			throws IPhoneParseException, FileParseException, IOException,
			NotLicencedException {
		IPhone iphone = new SshConnectionParser(this, serverAddress, userId,
				password, this.progressIndicator).getIphoneConfiguration();
		// licenceValidator.checkLicence(iphone.getConfigElements().getInfo());
		return iphone;
	}

	public void setProgressIndicator(final ProgressIndicator progressIndicator) {
		this.progressIndicator = progressIndicator;

	}

	private String getFileName(final File file) {
		return IoUtils.getFilenameNoExtension(file);
	}
}
