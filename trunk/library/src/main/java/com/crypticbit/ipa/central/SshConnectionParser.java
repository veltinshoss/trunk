package com.crypticbit.ipa.central;

import java.io.IOException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

public class SshConnectionParser extends VfsConnectionParser
{

	SshConnectionParser(final IPhoneFactory factory,
			final String serverAddress, final String userId,
			final String password, final ProgressIndicator progressIndicator)
			throws IPhoneParseException, FileParseException, IOException
	{
		super(factory, progressIndicator);
		
		
		
		
		this.bd = new IPhone(null, null);
		FileSystemOptions opts = new FileSystemOptions();
		FileSystemManager mgr = VFS.getManager();
		SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(
				opts, "no");
		final FileObject root = mgr.resolveFile("sftp://" + userId + ":"
				+ password + "@" + serverAddress, opts);
		// final FileObject root = mgr
		// .resolveFile("file://C:\\Users\\Leo Crawford\\AppData\\Local\\Temp\\com.crypticbit.ipa34888.deviceRoot");

		processFiles(root);

	}

}
