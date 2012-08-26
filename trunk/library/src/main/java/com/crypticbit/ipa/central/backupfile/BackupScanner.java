package com.crypticbit.ipa.central.backupfile;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import com.crypticbit.ipa.central.BackupDirectoryParser;
import com.crypticbit.ipa.central.LogFactory;

public class BackupScanner {

	private ExecutorService xServ = Executors.newSingleThreadExecutor();
	private boolean stopScanning = false;

	public void scanForBackups(BackupFound found, File root) {
		File canonical = getCanonical(root);
		scanForBackups(found, canonical, new HashSet<File>());
	}

	private File getCanonical(File root) {
		File canonical = root;
		try {
			canonical = root.getCanonicalFile();
		} catch (Exception e) {
			// do nothing
		}
		return canonical;
	}

	public void scanForBackups(BackupFound found, File root, Set<File> history) {
		if (root != null && root.exists() && root.canRead()
				&& root.isDirectory()) {
			File canonical = getCanonical(root);
			if (history.contains(canonical)) {
				LogFactory.getLogger().log(
						Level.INFO,
						"Skipping " + canonical
								+ " as already seen - presumed symlink");
				return;
			} else
				history.add(canonical);
			if (BackupDirectoryParser.isBackup(canonical))
				found.report(canonical);
			for (File entry : canonical.listFiles()) {
				if (stopScanning) {
					return;
				}
				scanForBackups(found, entry, history);
			}
		}
	}

	public void scanForBackupsAsync(final BackupFound bf, final File root) {
		// better run this in the background
		xServ.execute(new Runnable() {
			@Override
			public void run() {
				synchronized (BackupScanner.this) {
					scanForBackups(bf, root);
				}
			}
		});

	}

	public void stopScanning() {
		stopScanning = true;
		synchronized (this) {
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
	public static File getDefaultRoot() {
		LogFactory.getLogger().log(Level.INFO,
				"OS: " + System.getProperty("os.name"));

		if (SystemUtils.IS_OS_WINDOWS_7
				|| System.getProperty("os.name").equals("Windows 7"))
			return new File(
					System.getProperty("user.home")
							+ "\\AppData\\Roaming\\Apple Computer\\MobileSync\\Backup\\");
		if (SystemUtils.IS_OS_WINDOWS)
			return new File(
					System.getProperty("user.home")
							+ "\\Application Data\\Apple Computer\\MobileSync\\Backup\\");
		if (SystemUtils.IS_OS_MAC_OSX)
			return new File(System.getProperty("user.home")
					+ "/Library/Application Support/MobileSync/Backup/");
		else
			return FileSystemView.getFileSystemView().getHomeDirectory();
	}

	public interface BackupFound {
		void report(File entry);
	}
}
