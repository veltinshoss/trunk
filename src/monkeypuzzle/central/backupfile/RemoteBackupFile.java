/**
 * 
 */
package monkeypuzzle.central.backupfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import monkeypuzzle.io.util.IoUtils;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;

public class RemoteBackupFile extends BackupFile
{
	private File tempDir;
	private final FileObject file;

	public RemoteBackupFile(final File tempDir, final FileObject file,
			final FileObject root) throws FileSystemException
	{
		this.tempDir = tempDir;
		this.file = file;
		super.originalFileName = file.getName().getPath().substring(
				root.getName().getPath().length() + 1);
	}

	@Override
	public File getContentsFile() throws IOException
	{
		InputStream is = getContentsInputStream();
		File f = new File(this.tempDir, this.file.getName().getBaseName());
		OutputStream out = new FileOutputStream(f);
		byte buf[] = new byte[1024];
		int len;
		while ((len = is.read(buf)) > 0)
		{
			out.write(buf, 0, len);
		}
		out.close();
		is.close();
		return f;
	}

	@Override
	public InputStream getContentsInputStream() throws IOException
	{
		return this.file.getContent().getInputStream();
	}

	@Override
	public void restoreFile(final File directory) throws IOException
	{

		// FIXME do nothing

	}

	@Override
	protected byte[] createByteArrayFromBackupFile() throws IOException
	{
		return IoUtils.getBytesFromFile(getContentsInputStream());
	}
}