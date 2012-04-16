package com.crypticbit.ipa.central;

import java.io.File;
import java.io.IOException;

import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.central.backupfile.BackupFileException;
import com.crypticbit.ipa.central.backupfile.Version4BackupFile;
import com.crypticbit.ipa.central.filters.MdbackupFilenameFilter;
import com.crypticbit.ipa.central.filters.MddataFilenameFilter;
import com.crypticbit.ipa.central.filters.NoSuffixFilenameFilter;
import com.crypticbit.ipa.entity.status.Info;
import com.crypticbit.ipa.io.parser.manifest.ManifestParser;

/**
 * Parses a backup directory with methods to get the backup metadata or parsed
 * content
 * 
 */
public class BackupDirectoryParser extends IPhoneParser
{

	private File sourceDir;
	private IPhoneFactory factory;
	private IPhone bd;

	public static boolean isBackup(File sourceDir)
	{
		Info info;
		try
		{
			info = getConfiguration(sourceDir).getInfo();
		} catch (Exception e)
		{
			return false;
		}
		return info != null;
	}

	public static String getBackupSummary(File sourceDir)
			throws FileParseException, IOException
	{
		Info info = getConfiguration(sourceDir).getInfo();
		if (info == null)
			return "unknown backup (" + sourceDir.getAbsolutePath() + ")";
		else
			return info.getProductType() + ": " + info.getDeviceName() + " v"
					+ info.getProductVersion() + " - "
					+ info.getLastBackupDate();
	}

	BackupDirectoryParser(final IPhoneFactory factory, final File sourceDir,
			final ProgressIndicator progressIndicator)
			throws IPhoneParseException, FileParseException, IOException
	{
		super(progressIndicator);
		this.factory = factory;
		this.sourceDir = sourceDir;
		BackupConfigurationElements elements = getConfiguration(sourceDir);
		this.bd = new IPhone(getGlobalVars(elements), elements);
		ManifestParser manifestParser = new ManifestParser(sourceDir);
		try
		{
			if (manifestParser.succeeded())
			{
				// assume iOS 4.0
				File[] nosuffix = sourceDir
						.listFiles(new NoSuffixFilenameFilter());
				parseEncodedFilesUsingManifest(nosuffix, manifestParser);
			} else
			{

				// must be an older version
				// get list of mdbackup files
				File[] mdbackups = sourceDir
						.listFiles(new MdbackupFilenameFilter());
				File[] mddata = sourceDir.listFiles(new MddataFilenameFilter());

				if ((mdbackups == null) || (mddata == null))
				{
					throw new IPhoneParseException(
							"Problem accessing directory");
				}
				if ((mdbackups.length == 0) && (mddata.length == 0))
				{
					throw new NotaBackupDirectoryException(
							"No backup files found in directory");
				}
				parseEncodedFiles(mdbackups);
				parseEncodedFiles(mddata);
			}
		} catch (BackupFileException bfe)
		{
			throw new IPhoneParseException("Unable to parse directory \""
					+ sourceDir + "\"", bfe);
		}

	}

	public File getDirectory()
	{
		return this.sourceDir;
	}

	@Override
	public IPhone getIphoneConfiguration()
	{
		return this.bd;
	}

	private void parseEncodedFilesUsingManifest(final File[] files,
			ManifestParser manifest) throws BackupFileException, IOException

	{
		int count = 0;
		for (File f : files)
		{
			String fileName = manifest.getRealFilePath(f);
			if (fileName == null)
			{
				System.err
						.println("Could not find filename for " + f.getName());
			} else
			{
				BackupFile bfd = new Version4BackupFile(fileName, f);
				this.bd.addBackupFile(bfd);
				getProgressIndicator().progressUpdate(count++, files.length,
						bfd.getCompleteOriginalFileName());
			}
		}
	}

	private void parseEncodedFiles(final File[] files)
			throws BackupFileException, IOException

	{
		int count = 0;
		for (File f : files)
		{
			BackupFile bfd = this.factory.createIPhoneFileFromEncoded(f);
			this.bd.addBackupFile(bfd);
			getProgressIndicator().progressUpdate(count++, files.length,
					bfd.getCompleteOriginalFileName());
		}
	}

}
