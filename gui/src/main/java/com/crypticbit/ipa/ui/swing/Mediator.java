package com.crypticbit.ipa.ui.swing;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import com.crypticbit.ipa.central.IPhone;
import com.crypticbit.ipa.central.IPhoneFactory;
import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.licence.UnlimitedLicenceValidator;
import com.crypticbit.ipa.licence.ValidatorUi;
import com.crypticbit.ipa.results.Location;
import com.crypticbit.ipa.ui.swing.prefs.Preferences;

public class Mediator
{
	public class CachedLicenceHandler implements ValidatorUi
	{
		ValidatorUi licenceHandler;

		CachedLicenceHandler(final ValidatorUi licenceHandler)
		{
			this.licenceHandler = licenceHandler;
		}

		@Override
		public String getCustomerNumber()
		{
			Preferences prefs = getPreferences();
			String id = prefs.getCustomerId();
			if (id == null)
			{
				id = this.licenceHandler.getCustomerNumber();
			}
			prefs.setCustomerId(id);
			return id;
		}

	}

	public interface HighlightChangeListener
	{
		public void clearHighlighting();

		public void highlight(Collection<Location> locations);

		public void moveTo(Location location);
	};

	public interface RequestTabChangeListener
	{
		public void requestTabChanged(Tabs tab);
	}

	public interface SelectedBackupDirectoryChangeListener
	{
		public void backupDirectoryChanged(File directory,
				IPhone backupDirectory);
	}

	public interface SelectedFileChangeListener
	{
		public void fileChanged(BackupFile bfd, boolean resetDefaultTab,
				Callback callback);

		void showPanel(GeneralPanelTypes panel);
	}

	enum Tabs
	{
		REPORT,
		INFO
	}

	private Preferences preferences;
	private IPhone backupDirectory;
	private ErrorHandler errorHandler;
	private ValidatorUi licenceHandler;
	private Set<HighlightChangeListener> highlightChangeListeners = new HashSet<HighlightChangeListener>();

	private Set<SelectedBackupDirectoryChangeListener> selectedBackupDirectoryChangeListeners = new HashSet<SelectedBackupDirectoryChangeListener>();

	private Set<SelectedFileChangeListener> selectedFileChangeListeners = new HashSet<SelectedFileChangeListener>();

	private Set<RequestTabChangeListener> requestTabChangeListeners = new HashSet<RequestTabChangeListener>();

	private IPhoneFactory iPhoneFactory;
	private Component mainFrame;

	public Mediator(final Component mainFrame, final ErrorHandler errorHandler,
			final ValidatorUi licenceHandler)
	{
		this.mainFrame = mainFrame;
		this.errorHandler = errorHandler;
		this.licenceHandler = new CachedLicenceHandler(licenceHandler);
	}

	public void addHighlightChangeListener(
			final HighlightChangeListener listener)
	{
		this.highlightChangeListeners.add(listener);

	}

	public void addRequestTabChangeListener(
			final RequestTabChangeListener listener)
	{
		this.requestTabChangeListeners.add(listener);
	}

	public void addSelectedBackupDirectoryChangeListener(
			final SelectedBackupDirectoryChangeListener listener)
	{
		this.selectedBackupDirectoryChangeListeners.add(listener);
	}

	public void addSelectedFileChangeListener(
			final SelectedFileChangeListener listener)
	{
		this.selectedFileChangeListeners.add(listener);
	}

	public void changeBackupDirectory(final File directory,
			final IPhone backupDirectory)
	{
		this.backupDirectory = backupDirectory;
		fireSelectedBackupDirectoryChangeListeners(directory, backupDirectory);
	}

	public void displayErrorDialog(final String message, final Throwable cause)
	{
		this.errorHandler.displayErrorDialog(message, cause);
		LogFactory.getLogger().log(Level.SEVERE, "Exception", cause);
	}

	public void fireHighlightChangeListeners(final BackupFile bfd,
			final Collection<Location> locations)
	{
		fireHighlightChangeListeners(bfd, locations, null);
	}

	public void fireHighlightChangeListeners(final BackupFile bfd,
			final Collection<Location> locations, final Location location)
	{
		// first fire select file change listener to open (if required) and
		// focus on tab for this file
		fireSelectedFileChangeListeners(bfd, true, new Callback() {

			@Override
			void callback(ViewingPane viewingPane)
			{
				for (HighlightChangeListener highlightChangeListener : highlightChangeListeners)
				{
					highlightChangeListener.highlight(locations);
				}
				if (location != null)
					fireMoveToChangeListeners(location);
			}

		});
		// highlight the correct bits

	}

	public void fireMoveToChangeListeners(final Location location)
	{
		for (HighlightChangeListener highlightChangeListener : this.highlightChangeListeners)
		{
			highlightChangeListener.moveTo(location);
		}
	}

	public void fireRemoveHighlightChangeListeners()
	{
		for (HighlightChangeListener highlightChangeListener : this.highlightChangeListeners)
		{
			highlightChangeListener.clearHighlighting();
		}
	}

	public void fireRequestToChangeTab(final Tabs request)
	{
		for (RequestTabChangeListener requestTabChangeListener : this.requestTabChangeListeners)
		{
			requestTabChangeListener.requestTabChanged(request);
		}
	}

	public void fireSelectedBackupDirectoryChangeListeners(
			final File directory, final IPhone backupDirectory)
	{
		for (SelectedBackupDirectoryChangeListener selectedBackupDirectoryChangeListener : this.selectedBackupDirectoryChangeListeners)
		{
			selectedBackupDirectoryChangeListener.backupDirectoryChanged(
					directory, backupDirectory);
		}
	}

	/**
	 * Causes the given file to be opened without resetting the default tab if
	 * already opened.
	 * 
	 * @param bfd
	 */
	public void fireSelectedFileChangeListeners(final BackupFile bfd)
	{
		fireSelectedFileChangeListeners(bfd, false, Callback.doNothing());
	}

	public void fireSelectedFileChangeListeners(final BackupFile bfd,
			final boolean resetDefaultTab, final Callback callback)
	{
		for (SelectedFileChangeListener selectedFileChangeListener : this.selectedFileChangeListeners)
		{
			selectedFileChangeListener.fileChanged(bfd, resetDefaultTab,
					callback);
		}
	}

	public IPhone getBackupDirectory()
	{
		return this.backupDirectory;
	}

	public IPhoneFactory getIPhoneFactory() throws IOException
	{
		if (this.iPhoneFactory == null)
		{
			this.iPhoneFactory = new IPhoneFactory(
					new UnlimitedLicenceValidator());
			// this.iPhoneFactory = new IPhoneFactory(new
			// HardCodedKeyValidator(this.licenceHandler));
		}
		return this.iPhoneFactory;
	}

	public Preferences getPreferences()
	{
		if (this.preferences == null)
		{
			this.preferences = new Preferences(this);
		}
		return this.preferences;
	}

	public void removeHighlightChangeListener(
			final HighlightChangeListener listener)
	{
		this.highlightChangeListeners.remove(listener);
	}

	public void showPanel(GeneralPanelTypes panel)
	{
		for (SelectedFileChangeListener selectedFileChangeListener : this.selectedFileChangeListeners)
		{
			selectedFileChangeListener.showPanel(panel);
		}

	}

	public void displayWarningDialog(String message, Exception cause)
	{
		this.errorHandler.displayWarningDialog(message, cause);

	}

	public Component getMainFrame()
	{
		return mainFrame;
	}

	public void fireHighlightChangeListeners(Location loc)
	{
		List<Location> r = new LinkedList<Location>();
		r.add(loc);
		fireHighlightChangeListeners(loc.getBackupFile(), r, null);

	}

	public DisplayConverter getDisplayConverter()
	{
			return preferences.getObscuredDisplay() ? DisplayConverter.OBSCURED
				: DisplayConverter.NULL;
	}

}
