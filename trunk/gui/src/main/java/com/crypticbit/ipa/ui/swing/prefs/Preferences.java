package com.crypticbit.ipa.ui.swing.prefs;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.crypticbit.ipa.central.IPhone;
import com.crypticbit.ipa.ui.swing.Mediator;
import com.crypticbit.ipa.ui.swing.Mediator.SelectedBackupDirectoryChangeListener;

public class Preferences implements SelectedBackupDirectoryChangeListener
{

	public class PreferencesWeakReference extends
			WeakReference<PrefsChangeListener> implements PrefsChangeListener
	{

		public PreferencesWeakReference(PrefsChangeListener referent)
		{
			super(referent);
			// fixme - ideally we would remove the weak reference too. At the
			// moment we could end up with a leak of WeakReferences building up
			// - but they are only small
		}

		@Override
		public void preferenceUpdated(PrefType prefType)
		{
			if (get() != null)
				get().preferenceUpdated(prefType);
		}

	}

	public enum PrefType
	{
		Mapping
	}

	public interface PrefsChangeListener
	{
		public void preferenceUpdated(PrefType prefType);

	}

	private java.util.prefs.Preferences prefs;
	private static final String RECENT_BACKUP_DIRECTORIES = "RecentBackupDirectory";
	private static final int MAX_BACKUP_DIR = 5;
	private static final String CUSTOMER_NUMBER = "CustomerNumber";
	private static final String OFFLINE_MAP_DIR = "OfflineMapDir";
	private static final String USE_OFFLINE_MAPS = "UseOfflineMaps";
	private static final String OBSCURED_DISPLAY = "ObscuredDisplay";
	private static final String REGISTERED_USER = "RegisteredUser";
	private static final String REGISTERED_KEY = "RegisteredKey";

	public Preferences(final Mediator mediator)
	{
		mediator.addSelectedBackupDirectoryChangeListener(this);
		this.prefs = java.util.prefs.Preferences.userNodeForPackage(this
				.getClass());
	}

	Set<PrefsChangeListener> changeListeners = new HashSet<PrefsChangeListener>();

	public void addWeakPreferenceChangeListener(PrefsChangeListener listener)
	{
		changeListeners.add(new PreferencesWeakReference(listener));
	}

	public void addPreferenceChangeListener(PrefsChangeListener listener)
	{
		changeListeners.add(listener);
	}

	private void informListeners(PrefType prefType)
	{
		for (PrefsChangeListener l : changeListeners)
		{
			l.preferenceUpdated(prefType);
		}
	}

	@Override
	public void backupDirectoryChanged(final File directory, final IPhone iphone)
	{
		// FIXME to deal with SSH server
		if (directory != null)
		{
			List<String> previous = getRecentBackupDirectories();
			previous.remove(directory.getAbsolutePath());
			previous.add(0, directory.getAbsolutePath());
			for (int loop = 0; loop < MAX_BACKUP_DIR; loop++)
			{
				if (loop < previous.size())
				{
					this.prefs.node(RECENT_BACKUP_DIRECTORIES).put("" + loop,
							previous.get(loop));
					// else
					// prefs.node(RECENT_BACKUP_DIRECTORIES).put("" + loop,
					// null);
				}
			}
		}
	}

	public String getCustomerId()
	{
		return this.prefs.get(CUSTOMER_NUMBER, null);
	}

	public List<String> getRecentBackupDirectories()
	{
		List<String> result = new ArrayList<String>();
		for (int loop = 0; loop < MAX_BACKUP_DIR; loop++)
		{
			String entry = this.prefs.node(RECENT_BACKUP_DIRECTORIES).get(
					"" + loop, null);
			if (entry != null)
			{
				result.add(entry);
			}
		}
		return result;
	}

	public void removeCustomerId()
	{
		this.prefs.remove(CUSTOMER_NUMBER);
	}

	public void setCustomerId(final String id)
	{
		if (id != null)
		{
			this.prefs.put(CUSTOMER_NUMBER, id);
		}
	}

	public void setUseOfflineMaps(final boolean offline)
	{
		this.prefs.putBoolean(USE_OFFLINE_MAPS, offline);
		informListeners(PrefType.Mapping);

	}

	public boolean getUseOfflineMaps()
	{
		return this.prefs.getBoolean(USE_OFFLINE_MAPS, false);

	}

	public void setObscuredDisplay(final boolean obscured)
	{
		this.prefs.putBoolean(OBSCURED_DISPLAY, obscured);
		// informListeners(PrefType.Mapping);
	
	}

	public boolean getObscuredDisplay()
	{
		return this.prefs.getBoolean(OBSCURED_DISPLAY, false);

	}

	public void setOfflineMapDir(final String path)
	{
		this.prefs.put(OFFLINE_MAP_DIR, path);
		informListeners(PrefType.Mapping);

	}

	public File getOfflineMapDir()
	{
		String defaultPathStr = this.getClass().getProtectionDomain()
				.getCodeSource().getLocation().getFile();
		File defaultDir = new File(new File(defaultPathStr).getParentFile(),
				"maptiles");
		File returnPath = new File(this.prefs.get(OFFLINE_MAP_DIR,
				defaultDir.getAbsolutePath()));
		return returnPath;
	}

	public String getRegisteredUser()
	{
		return this.prefs.get(REGISTERED_USER, null);
	}

	public void setRegisteredUser(String user)
	{
		this.prefs.put(REGISTERED_USER, user);
	}

	public String getRegisteredKey()
	{
		return this.prefs.get(REGISTERED_KEY, null);
	}

	public void setRegisteredKey(String key)
	{
		this.prefs.put(REGISTERED_KEY, key);
	}

	public void clearRegistrationDetails()
	{
		this.prefs.remove(REGISTERED_USER);
		this.prefs.remove(REGISTERED_KEY);
	}

}
