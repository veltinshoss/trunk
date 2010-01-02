package monkeypuzzle.ui.swing;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import monkeypuzzle.central.IPhone;
import monkeypuzzle.central.IPhoneFactory;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.licence.UnlimitedLicenceValidator;
import monkeypuzzle.licence.ValidatorUi;
import monkeypuzzle.results.Location;
import monkeypuzzle.ui.swing.prefs.Preferences;

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

		void directoryChanged(String directory, boolean recursive,
				boolean resetDefaultTab);
	}

	enum Tabs
	{
		REPORT, INFO
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

	public Mediator(final ErrorHandler errorHandler,
			final ValidatorUi licenceHandler)
	{
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
	}

	public void fireHighlightChangeListeners(final BackupFile bfd,
			final Collection<Location> locations)
	{
		// first fire select file change listener to open (if required) and
		// focus on tab for this file
		fireSelectedFileChangeListeners(bfd);
		// highlight the correct bits
		for (HighlightChangeListener highlightChangeListener : this.highlightChangeListeners)
		{
			highlightChangeListener.highlight(locations);
		}
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
			this.iPhoneFactory = new IPhoneFactory(new UnlimitedLicenceValidator());
			//this.iPhoneFactory = new IPhoneFactory(new HardCodedKeyValidator(this.licenceHandler));
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

	public void showDirectory(String directory)
	{
		for (SelectedFileChangeListener selectedFileChangeListener : this.selectedFileChangeListeners)
		{
			selectedFileChangeListener.directoryChanged(directory, true, true);
		}
			
	}
}
