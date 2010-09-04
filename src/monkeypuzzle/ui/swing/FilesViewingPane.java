/**
 * 
 */
package monkeypuzzle.ui.swing;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.central.IPhone;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.ui.swing.Mediator.RequestTabChangeListener;
import monkeypuzzle.ui.swing.Mediator.SelectedBackupDirectoryChangeListener;
import monkeypuzzle.ui.swing.Mediator.SelectedFileChangeListener;
import monkeypuzzle.ui.swing.Mediator.Tabs;

@SuppressWarnings("serial")
public final class FilesViewingPane extends JTabbedPane implements
		SelectedFileChangeListener, Remover,
		SelectedBackupDirectoryChangeListener, RequestTabChangeListener {
	private static final int TITLE_LENGTH = 18;
	private Map<String, ViewingPane> fileTabs = new HashMap<String, ViewingPane>();
	private JFrame mainFrame;

	private Mediator mediator;

	public FilesViewingPane(final JFrame mainFrame, final Mediator mediator) {
		super(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		this.mainFrame = mainFrame;
		mediator.addSelectedBackupDirectoryChangeListener(this);
		this.mediator = mediator;
		mediator.addSelectedFileChangeListener(this);
	}

	@Override
	public void backupDirectoryChanged(final File directory,
			final IPhone backupDirectory) {
		removeAll();
		cleanUp();
		this.add("Device Info", new InfoView(this.mediator));
		this.mediator.addRequestTabChangeListener(this);
	}


	@Override
	public void fileChanged(final BackupFile bfd,
			final boolean resetDefaultTab, final Callback callback) {
		try {
			if (this.fileTabs.containsKey(bfd.getCompleteOriginalFileName())) {
				ViewingPane fileViewingPane = this.fileTabs.get(bfd
						.getCompleteOriginalFileName());
				setSelectedComponent(fileViewingPane);
				fileViewingPane.resetDefaultTab();
				callback.callback(fileViewingPane);
			} else {
				// open file
				FileViewingPane view = new FileViewingPane(bfd, this.mediator);
				callback.callback(view);
				this.mediator.addHighlightChangeListener(view);
				this.fileTabs.put(bfd.getCompleteOriginalFileName(), view);
				this.add(shortVersion(bfd.getCompleteOriginalFileName()), view);
				setSelectedComponent(view);

				String toolTipText = bfd.getCompleteOriginalFileName() + " ("
						+ bfd.getContentsFile().getName() + ')';

				setToolTipTextAt(getSelectedIndex(), toolTipText);
				setTabComponentAt(getSelectedIndex(), new ButtonTabComponent(
						this, this));
			}
		} catch (IOException e) {
			this.mediator.displayErrorDialog("Problem accessing file", e);
		} catch (FileParseException e) {
			this.mediator.displayErrorDialog("Problem parsing file", e);
		}
	}

	@Override
	public void directoryChanged(String directory, boolean recursive,
			final boolean resetDefaultTab) // , final Callback callback)
	{

		if (this.fileTabs.containsKey(directory)) {
			ViewingPane fileViewingPane = this.fileTabs.get(directory);
			setSelectedComponent(fileViewingPane);
			fileViewingPane.resetDefaultTab();
			// callback.callback(fileViewingPane);
		} else {
			// open file
			ViewingPane view = new DirectoryViewingPane(mainFrame, directory,
					this.mediator);
			// callback.callback(view);
			this.fileTabs.put(directory, view);
			this.add("Directory view"
					+ (directory == null ? "" : (": " + directory)), view);
			setSelectedComponent(view);

			String toolTipText = directory;

			setToolTipTextAt(getSelectedIndex(), toolTipText);
			setTabComponentAt(getSelectedIndex(), new ButtonTabComponent(this,
					this));
		}

	}

	@Override
	public void removeItem(final int index) {
		this.fileTabs.remove(getToolTipTextAt(index));
		this.remove(index);
		cleanUp();
	}

	@Override
	public void requestTabChanged(final Tabs tab) {
		if (tab == Tabs.INFO) {
			setSelectedIndex(0);
		
		} else
			throw new UnsupportedOperationException("Type not yet supported: "
					+ tab);
	}

	private void cleanUp() {
		for (ViewingPane pane : this.fileTabs.values()) {
			pane.cleanUp();
		}
		this.fileTabs = new HashMap<String, ViewingPane>();
	}

	private String shortVersion(final String originalFileName) {
		if (originalFileName.length() > TITLE_LENGTH)
			return ".."
					+ originalFileName.substring(originalFileName.length()
							- TITLE_LENGTH);
		else
			return originalFileName;
	}
}