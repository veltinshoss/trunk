package com.crypticbit.ipa.central.backupfile;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import javax.swing.filechooser.FileSystemView;

import org.jdesktop.swingx.util.OS;

import com.crypticbit.ipa.central.BackupDirectoryParser;
import com.crypticbit.ipa.central.LogFactory;

public class BackupScanner
{

	private ExecutorService xServ = Executors.newSingleThreadExecutor();
	private boolean stopScanning = false;

	public void scanForBackups(BackupFound found, File root)
	{
		if (root != null && root.exists() && root.canRead() && root.isDirectory())
		{
			if (BackupDirectoryParser.isBackup(root))
				found.report(root);
			for (File entry : root.listFiles())
			{
				if (stopScanning)
				{
					return;
				}
				scanForBackups(found, entry);
			}
		}
	}

	public void scanForBackupsAsync(final BackupFound bf, final File root)
	{
		// better run this in the background
		xServ.execute(new Runnable() {
			@Override
			public void run()
			{
				synchronized (BackupScanner.this)
				{
					scanForBackups(bf, root);
				}
			}
		});

	}

	public void stopScanning()
	{
		stopScanning = true;
		synchronized (this)
		{
			stopScanning = false;
		}
	}

	/*
	 * 1. Windows XP Backup Location:\Documents and
	 * Settings\(username)\Application Data\Apple Computer\MobileSync\Backup\
	 * <br> 2. Windows Vista Backup
	 * Location:\Users\(username)\AppData\Roaming\Apple
	 * Computer\MobileSync\Backup\ <br>3. OS X Backup
	 * Location:~/Library/Application Support/MobileSync/Backup/
	 */
	public static File getDefaultRoot()
	{
		LogFactory.getLogger().log(Level.INFO,
				"OS: " + System.getProperty("os.name"));

		if (OS.isWindows())
			return new File(
					System.getProperty("user.home")
							+ "\\Application Data\\Apple Computer\\MobileSync\\Backup\\");
		if (OS.isWindowsVista()
				|| System.getProperty("os.name").equals("Windows 7"))
			return new File(
					System.getProperty("user.home")
							+ "\\AppData\\Roaming\\Apple Computer\\MobileSync\\Backup\\");
		if (OS.isMacOSX())
			return new File(System.getProperty("user.home")
					+ "/Library/Application Support/MobileSync/Backup/");
		else
			return FileSystemView.getFileSystemView().getHomeDirectory();
	}

	public interface BackupFound
	{
		void report(File entry);
	}
}
