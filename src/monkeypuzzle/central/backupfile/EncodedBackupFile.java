package monkeypuzzle.central.backupfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import monkeypuzzle.central.backupfile.filedefs.BackupFileDescriptor;
import monkeypuzzle.io.parser.plist.bin.BplParser;

/**
 * This class represents a backup file that contains the encoded contents and
 * file details within a PList.
 * 
 * @author Leo
 * 
 */
public class EncodedBackupFile extends BackupFile
{

	private File contentsFile;

	/**
	 * The filename extension used for all temporary files containing the
	 * original backup files before they were packaged
	 */
	static final String MDBACKUP_CONTENT_EXT = ".content";
	private File originalFile;
	private File workingDir;

	/**
	 * Instantiates a new description.
	 * 
	 * @param originalFileName
	 *            the original filename - including path - of the file
	 * @param contentsFile
	 *            a reference to a temporary file containing the original file
	 *            contents
	 * @throws IOException
	 */
	public EncodedBackupFile(final File originalFile, final File workingDir)
			throws IOException
	{
		this.originalFile = originalFile;
		this.workingDir = workingDir;
		createByteArrayFromBackupFile();
	}

	/**
	 * Get the unwrapped contents of the file
	 * 
	 * @return the contents of the unwrapped file
	 * @throws IOException
	 */
	@Override
	public File getContentsFile() throws IOException
	{
		if (this.contentsFile == null)
		{
			createFile();
		}
		return this.contentsFile;
	}

	@Override
	public void restoreFile(final File deviceRootDir) throws IOException
	{
		// create file in deviceRoot
		File destFile = null;
		if (this.originalFileName != null)
		{
			destFile = new File(deviceRootDir, this.originalFileName);
			destFile.getParentFile().mkdirs(); // create sub dirs in
			// deviceRoot
		}

		// write file to deviceRoot directory
		writeData(destFile, getContentsAsByteArray());
	}

	@Override
	protected byte[] createByteArrayFromBackupFile() throws IOException
	{
		BplParser bplParser = new BplParser(new FileInputStream(
				this.originalFile));
		BackupFileDescriptor c = bplParser.getRootContainer().getAsInterface(
				BackupFileDescriptor.class);
		this.originalFileName = c.getPath();
		byte[] tempData = c.getData();
		if (tempData == null)
		{
			System.err.println("no byte data in " + c.getPath());
		}
		return tempData;
	}

	private void createFile() throws IOException
	{
		// create file in working dir
		this.contentsFile = new File(this.workingDir, this.originalFile
				.getName()
				+ MDBACKUP_CONTENT_EXT);
		this.contentsFile.createNewFile();

		// write data
		writeData(this.contentsFile, getContentsAsByteArray());
	}

	private void writeData(final File file, final byte[] data)
			throws FileNotFoundException, IOException
	{
		// Write file to working dir
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(getContentsAsByteArray());
		fos.close();
	}

}
