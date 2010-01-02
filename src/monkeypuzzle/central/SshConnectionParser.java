package monkeypuzzle.central;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import monkeypuzzle.central.backupfile.BackupFile;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;

public class SshConnectionParser extends IPhoneParser
{

	private IPhoneFactory factory;
	private IPhone bd;

	SshConnectionParser(final IPhoneFactory factory,
			final String serverAddress, final String userId,
			final String password, final ProgressIndicator progressIndicator)
			throws IPhoneParseException, FileParseException, IOException
	{
		super(progressIndicator);
		this.factory = factory;
		this.bd = new IPhone(null, null);

		FileSystemOptions opts = new FileSystemOptions();
		FileSystemManager mgr = VFS.getManager();
		SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(
				opts, "no");
		final FileObject root = mgr.resolveFile("sftp://" + userId + ":"
				+ password + "@" + serverAddress, opts);
		// final FileObject root = mgr
		// .resolveFile("file://C:\\Users\\Leo Crawford\\AppData\\Local\\Temp\\monkeypuzzle34888.deviceRoot");

		processFiles(root);

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

	private void processFiles(final FileObject root) throws IOException,
			IPhoneParseException

	{

		List<FileObject> filesToProcess = new ArrayList<FileObject>();
		buildList(root, filesToProcess);
		System.out.println(filesToProcess);
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
