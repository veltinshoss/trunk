package monkeypuzzle.central;

import monkeypuzzle.central.backupfile.BackupFileException;

public class UnsupportedFileFormat extends BackupFileException
{

	public UnsupportedFileFormat(final String message)
	{
		super(message);
	}

	public UnsupportedFileFormat(final String message, final Throwable cause)
	{
		super(message, cause);
	}

}
