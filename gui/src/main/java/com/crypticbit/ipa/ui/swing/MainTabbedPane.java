/**
 * 
 */
package com.crypticbit.ipa.ui.swing;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.IPhone;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.ui.swing.Mediator.RequestTabChangeListener;
import com.crypticbit.ipa.ui.swing.Mediator.SelectedBackupDirectoryChangeListener;
import com.crypticbit.ipa.ui.swing.Mediator.SelectedFileChangeListener;
import com.crypticbit.ipa.ui.swing.Mediator.Tabs;


@SuppressWarnings("serial")
public final class MainTabbedPane extends JTabbedPane implements
		SelectedFileChangeListener, Remover,
		SelectedBackupDirectoryChangeListener, RequestTabChangeListener {
	private static final int TITLE_LENGTH = 18;
	private Map<String, ViewingPane> fileTabs = new HashMap<String, ViewingPane>();
	private JFrame mainFrame;

	private Mediator mediator;

	public MainTabbedPane(final JFrame mainFrame, final Mediator mediator) {
		super(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		this.mainFrame = mainFrame;
		this.mediator = mediator;

		mediator.addSelectedBackupDirectoryChangeListener(this);
		mediator.addRequestTabChangeListener(this);		
		mediator.addSelectedFileChangeListener(this);
	}

	@Override
	public void backupDirectoryChanged(final File directory,
			final IPhone backupDirectory) {
		removeAll();
		cleanUp();
		this.add("Device Info", new InfoView(this.mediator));
		reportIndex = -1;
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
				FileViewingPane view = new FileViewingPane(bfd, this.mediator, callback);
//				callback.callback(view);
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
	public void showPanel(GeneralPanelTypes panel) // , final Callback callback)
	{

		if (this.fileTabs.containsKey(panel.name())) {
			ViewingPane fileViewingPane = this.fileTabs.get(panel.name());
			setSelectedComponent(fileViewingPane);
			fileViewingPane.resetDefaultTab();
			// callback.callback(fileViewingPane);
		} else {
			// open file
			ViewingPane view = panel.create(this.mediator);
			// callback.callback(view);
			this.fileTabs.put(panel.name(), view);
			this.add(panel.getDescription(), view);
			setSelectedComponent(view);

			String toolTipText = panel.getDescription();

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
		} 
		else
		{
			throw new UnsupportedOperationException("Type not yet supported: " + tab);
		}
	}

	private int reportIndex = -1;

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