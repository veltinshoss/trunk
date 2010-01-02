package monkeypuzzle.ui.swing;

import java.io.IOException;
import java.io.InputStream;

import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.ui.swing.ImagePanel.ImageSource;

public class BackupFileImageAdapter implements ImageSource
{
private BackupFile bfd;
	public BackupFileImageAdapter(BackupFile bfd)
	{
		this.bfd = bfd;
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		return bfd.getContentsInputStream();
	}
	
	@Override
	public String toString() {
		return "Image source: "+bfd;
	}

}
