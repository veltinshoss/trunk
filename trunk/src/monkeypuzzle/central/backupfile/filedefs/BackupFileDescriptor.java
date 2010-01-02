package monkeypuzzle.central.backupfile.filedefs;

/**
 * Interface used to parse the contents of a hashed backup file
 * 
 */
public interface BackupFileDescriptor
{
	public byte[] getData();

	public String getPath();
}