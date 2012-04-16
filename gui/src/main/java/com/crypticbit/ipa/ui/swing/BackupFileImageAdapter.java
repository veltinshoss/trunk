package com.crypticbit.ipa.ui.swing;

import java.io.IOException;
import java.io.InputStream;

import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.ui.swing.ImagePanel.ImageSource;


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
