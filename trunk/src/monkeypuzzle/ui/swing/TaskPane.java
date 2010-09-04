package monkeypuzzle.ui.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JScrollPane;

import monkeypuzzle.central.BackupFileView;
import monkeypuzzle.central.IPhone;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.entity.sqlite.Messages.MessageType;
import monkeypuzzle.ui.swing.Mediator.SelectedBackupDirectoryChangeListener;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

@SuppressWarnings("serial")
public class TaskPane extends JXPanel implements
		SelectedBackupDirectoryChangeListener {
	private boolean alreadyInit = false;
	private final Mediator mediator;

	TaskPane(final Mediator mediator) {
		this.mediator = mediator;
		setLayout(new BorderLayout());
		mediator.addSelectedBackupDirectoryChangeListener(this);
	}

	@Override
	public void backupDirectoryChanged(final File directory,
			final IPhone backupDirectory) {
		if (!this.alreadyInit) {
			init();
			this.alreadyInit = true;
		}
	}

	Action makeAction(final String title, final String iconPath,
			final SpecialViewType svt) {
		return makeAction(title, iconPath, svt, Callback.doNothing());
	}

	Action makeAction(final String title, final String iconPath,
			final SpecialViewType svt, final Callback callback) {
		Action action = new AbstractAction(title) {
			public void actionPerformed(final ActionEvent e) {
				for (BackupFileView type : svt.getBackupFileType()) {
					for (BackupFile bfd : TaskPane.this.mediator
							.getBackupDirectory().getBackupFileByType(type)) {
						TaskPane.this.mediator.fireSelectedFileChangeListeners(
								bfd, true, callback);
					}
				}
			}
		};
		// action.putValue(Action.SMALL_ICON, new ImageIcon(
		// JXTaskPaneDemoPanel.class.getResource(iconPath)));
		return action;
	}

	private void init() {
		JXTaskPaneContainer taskPane = new JXTaskPaneContainer();

		// Contacts
		JXTaskPane contactGroup = new JXTaskPane();
		contactGroup.setTitle(("Contacts"));
		// systemGroup.setIcon(new ImageIcon(JXTaskPaneDemoPanel.class
		// .getResource("resources/tasks-email.png")));
		contactGroup.add(makeAction("Address Book",
				"resources/tasks-email.png", SpecialViewType.ADDRESS_BOOK));

		taskPane.add(contactGroup);

		// Messages
		JXTaskPane messageGroup = new JXTaskPane();
		messageGroup.setTitle(("Messages"));
		messageGroup.add(makeAction("All", "resources/tasks-writedoc.png",
				SpecialViewType.SMS, new Callback() {
					@Override
					void callback(final ViewingPane viewingPane) {
						((SmsView) viewingPane.getComponentAt(0)).clearFilter();
					}
				}));
		messageGroup.add(makeAction("Sent", "resources/tasks-writedoc.png",
				SpecialViewType.SMS, new Callback() {
					@Override
					void callback(final ViewingPane viewingPane) {
						((SmsView) viewingPane.getComponentAt(0))
								.setFilter(MessageType.SENT);
					}
				}));
		messageGroup.add(makeAction("Received", "resources/tasks-writedoc.png",
				SpecialViewType.SMS, new Callback() {
					@Override
					void callback(final ViewingPane viewingPane) {
						((SmsView) viewingPane.getComponentAt(0))
								.setFilter(MessageType.RECEIVED);
					}
				}));

		taskPane.add(messageGroup);

		// Shortcut
		JXTaskPane shortcutGroup = new JXTaskPane();
		shortcutGroup.setTitle(("Shortcuts"));
		// systemGroup.setIcon(new ImageIcon(JXTaskPaneDemoPanel.class
		// .getResource("resources/tasks-email.png")));
		shortcutGroup.add(new AbstractAction("Photos") {

			@Override
			public void actionPerformed(ActionEvent e) {
				mediator.showDirectory(null);

			}
		});

		taskPane.add(shortcutGroup);

		JScrollPane scroll = new JScrollPane(taskPane);
		add(scroll, BorderLayout.CENTER);
		validateTree();

	}
}
