package monkeypuzzle.central.backupfile;

import java.io.File;
import java.io.IOException;

import monkeypuzzle.io.util.IoUtils;

/**
 * This class represents a backup file that has already been restored to disk.
 * It assumes that the filename has been restored.
 * 
 * @author Leo
 * 
 */
public class PlainBackupFile extends BackupFile
{

	protected File contentsFile;

	public PlainBackupFile(final File contentsFile)
	{
		super.originalFileName = contentsFile.getAbsolutePath();
		this.contentsFile = contentsFile;

	}

	@Override
	public File getContentsFile()
	{
		return this.contentsFile;
	}

	@Override
	public void restoreFile(final File directory)
	{
		// do nothing
	}

	@Override
	protected byte[] createByteArrayFromBackupFile() throws IOException
	{
		return IoUtils.getBytesFromFile(this.contentsFile);
	}

}
