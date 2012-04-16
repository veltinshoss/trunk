package com.crypticbit.ipa.central;

import java.io.File;
import java.io.IOException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;

public class UnpackedFileConnectionParser extends VfsConnectionParser
{


	UnpackedFileConnectionParser(final IPhoneFactory factory,
			final File file, final ProgressIndicator progressIndicator)
			throws IPhoneParseException, FileParseException, IOException
	{
		super(factory, progressIndicator);
		
		BackupConfigurationElements elements = getConfiguration(file);
		this.bd = new IPhone(getGlobalVars(elements), elements);
		
		FileSystemOptions opts = new FileSystemOptions();
		FileSystemManager mgr = VFS.getManager();
		
		final FileObject root = mgr.toFileObject(file);
		
		processFiles(root);
		getConfiguration(file);

	}

}
