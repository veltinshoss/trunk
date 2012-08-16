/**
 * 
 */
package com.crypticbit.ipa.central.backupfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import com.crypticbit.ipa.io.util.IoUtils;

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
				root.getName().getPath().length() );
	}

	@Override
	public synchronized File getContentsFile() throws IOException
	{
		System.out.println(file+","+file.getContent().isOpen());
		
		InputStream is = 		this.file.getContent().getInputStream(); // getContentsInputStream();
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
		this.file.getContent().close();
		return f;
	}

	@Override
	public synchronized InputStream getContentsInputStream() throws IOException
	{
//		throw new UnsupportedOperationException();
		return this.file.getContent().getInputStream();
	}



	@Override
	protected synchronized byte[] createByteArrayFromBackupFile() throws IOException
	{
		return IoUtils.getBytesFromFile(getContentsInputStream());
	}
	
	@Override
	public String toString()
	{
		try {
			return "backup File (remote): " + this.originalFileName+" ("+file.getURL()+")";
		} catch (FileSystemException e) {
			return "backup File (remote): " + this.originalFileName+" (error getting location)";
		}
	}
}