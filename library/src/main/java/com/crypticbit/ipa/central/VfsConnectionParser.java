package com.crypticbit.ipa.central;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;

import com.crypticbit.ipa.central.backupfile.BackupFile;

public abstract class VfsConnectionParser extends IPhoneParser
{

	protected IPhoneFactory factory;
	protected IPhone bd;

	VfsConnectionParser(final IPhoneFactory factory,
			final ProgressIndicator progressIndicator)
			throws IPhoneParseException, FileParseException, IOException
	{
		super(progressIndicator);
		this.factory = factory;
	}

	@Override
	public IPhone getIphoneConfiguration()
	{
		return this.bd;
	}

	private void buildList(final FileObject root,
			final List<FileObject> filesToProcess) throws FileSystemException
	{

		for (FileObject file : root.getChildren())
		{
			if (file.getType() == FileType.FOLDER)
			{
				buildList(file, filesToProcess);
			}
			if (file.getType() == FileType.FILE)
			{
				filesToProcess.add(file);
				getProgressIndicator().progressUpdate(0, filesToProcess.size(),
						"finding files: " + file.getName());
			}
		}
	}

	protected void processFiles(final FileObject root) throws IOException,
			IPhoneParseException

	{

		List<FileObject> filesToProcess = new ArrayList<FileObject>();
		buildList(root, filesToProcess);
		int count = 0;
		for (FileObject file : filesToProcess)
		{
			BackupFile bfd = this.factory.createIPhoneFileFromFileObject(file,
					root);
			this.bd.addBackupFile(bfd);
			getProgressIndicator().progressUpdate(count++,
					filesToProcess.size(), bfd.getCompleteOriginalFileName());

		}

	}
}
