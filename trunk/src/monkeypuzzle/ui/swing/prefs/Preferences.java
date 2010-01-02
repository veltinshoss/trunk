package monkeypuzzle.ui.swing.prefs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import monkeypuzzle.central.IPhone;
import monkeypuzzle.ui.swing.Mediator;
import monkeypuzzle.ui.swing.Mediator.SelectedBackupDirectoryChangeListener;

public class Preferences implements SelectedBackupDirectoryChangeListener {
	private java.util.prefs.Preferences prefs;
	private static final String RECENT_BACUP_DIRECTORIES = "RecentBackupDirectory";
	private static final int MAX_BACKUP_DIR = 5;
	private static final String CUSTOMER_NUMBER = "CustomerNumber";

	public Preferences(final Mediator mediator) {
		mediator.addSelectedBackupDirectoryChangeListener(this);
		this.prefs = java.util.prefs.Preferences.userNodeForPackage(this
				.getClass());
	}

	@Override
	public void backupDirectoryChanged(final File directory,
			final IPhone backupDirectory) {
		// FIXME to deal with SSH server
		if (directory != null) {
			List<String> previous = getRecentBackupDirectories();
			previous.remove(directory.getAbsolutePath());
			previous.add(0, directory.getAbsolutePath());
			for (int loop = 0; loop < MAX_BACKUP_DIR; loop++) {
				if (loop < previous.size()) {
					this.prefs.node(RECENT_BACUP_DIRECTORIES).put("" + loop,
							previous.get(loop));
					// else
					// prefs.node(RECENT_BACUP_DIRECTORIES).put("" + loop,
					// null);
				}
			}
		}
	}

	public String getCustomerId() {
		return this.prefs.get(CUSTOMER_NUMBER, null);
	}

	public List<String> getRecentBackupDirectories() {
		List<String> result = new ArrayList<String>();
		for (int loop = 0; loop < MAX_BACKUP_DIR; loop++) {
			String entry = this.prefs.node(RECENT_BACUP_DIRECTORIES).get(
					"" + loop, null);
			if (entry != null) {
				result.add(entry);
			}
		}
		return result;
	}

	public void removeCustomerId() {
		this.prefs.remove(CUSTOMER_NUMBER);
	}

	public void setCustomerId(final String id) {
		if (id != null) {
			this.prefs.put(CUSTOMER_NUMBER, id);
		}
	}
}
