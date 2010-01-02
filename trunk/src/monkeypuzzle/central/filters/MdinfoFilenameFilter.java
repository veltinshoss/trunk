package monkeypuzzle.central.filters;

import java.io.File;
import java.io.FilenameFilter;

import monkeypuzzle.central.backupfile.Version2BackupFile;

public class MdinfoFilenameFilter implements FilenameFilter
{

	@Override
	public boolean accept(final File dir, final String name)
	{
		return name.endsWith(Version2BackupFile.MDINFO_EXTENSION);
	}
}