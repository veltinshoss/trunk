package com.crypticbit.ipa.ui.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import com.crypticbit.ipa.central.BackupFileView;
import com.crypticbit.ipa.central.IPhone;
import com.crypticbit.ipa.central.ProgressIndicator;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.ui.swing.Mediator.SelectedBackupDirectoryChangeListener;
import com.crypticbit.ipa.ui.swing.SpecialViewType.Event;

@SuppressWarnings("serial")
public class TaskPane extends JXPanel implements
		SelectedBackupDirectoryChangeListener {

	private final Mediator mediator;

	TaskPane(final Mediator mediator) {
		this.mediator = mediator;
		setLayout(new BorderLayout());
		mediator.addSelectedBackupDirectoryChangeListener(this);
	}

	@Override
	public void backupDirectoryChanged(final File directory, final IPhone iphone) {
		// re-init each time to ensure correct items are greyed out
		init();
	}

	Action makeAction(final SpecialViewType svt) {
		return makeAction(svt, svt.getName(), Callback.doNothing());
	}

	Action makeAction(final SpecialViewType svt, String itemName,
			final Callback callback) {
		Action action = new AbstractAction(itemName) {
			public void actionPerformed(final ActionEvent e) {
				BackupFileView type = svt.getBackupFileType();
				if (type != null) {
					for (BackupFile bfd : TaskPane.this.mediator
							.getBackupDirectory().getBackupFileByType(type)) {
						TaskPane.this.mediator.fireSelectedFileChangeListeners(
								bfd, true, callback);
					}
				}
			}
		};
		action.setEnabled(svt.isEnabled(mediator));
		return action;
	}

	private void init() {
		JXTaskPaneContainer taskPane = new JXTaskPaneContainer();

		JXTaskPane mainGroup = new JXTaskPane();
		mainGroup.setTitle("Main");

		for (SpecialViewType svt : SpecialViewType.values()) {
			if (svt.getOptions() == null) {
				Action action = makeAction(svt);
				mainGroup.add(action);
			}
		}
		taskPane.add(mainGroup);

		for (SpecialViewType svt : SpecialViewType.values()) {
			if (svt.getOptions() != null) {
				JXTaskPane subGroup = new JXTaskPane();
				subGroup.setTitle(svt.getName());
				for (Event event : svt.getOptions()) {
					Action action = makeAction(svt, event.getName(),
							event.getCallback());
					subGroup.add(action);
					taskPane.add(subGroup);
				}
			}
		}

		final JXTaskPane shortcutGroup = new JXTaskPane();
		shortcutGroup.setTitle(("Shortcuts"));
		createShortcuts(shortcutGroup);
		taskPane.add(shortcutGroup);

		JScrollPane scroll = new JScrollPane(taskPane);
		add(scroll, BorderLayout.CENTER);
//		validateTree();

	}

	private void createShortcuts(final JXTaskPane shortcutGroup) {
		for (final GeneralPanelTypes panel : GeneralPanelTypes.values()) {
			if (panel.isShowOntask()) {
				if (panel.isReady(mediator))
					shortcutGroup.add(new ShowPanelAction(panel
							.getDescription(), panel));
				else {
					final JProgressBar progress = new JProgressBar();
					progress.setStringPainted(true);
					shortcutGroup.add(progress);
					new Thread(new Runnable() {

						@Override
						public void run() {
							panel.load(mediator, new ProgressIndicator() {

								@Override
								public void progressUpdate(int entry,
										int outOf, String description) {
									progress.setValue(entry);
									progress.setMaximum(outOf);
									progress.setString(description);
								}
							});
							shortcutGroup.removeAll();
							createShortcuts(shortcutGroup);
						}

					}).start();
				}
			}
		}
	}

	private final class ShowPanelAction extends AbstractAction {
		private final GeneralPanelTypes panel;

		private ShowPanelAction(String name, GeneralPanelTypes panel) {
			super(name);
			this.panel = panel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			mediator.showPanel(panel);
		}
	}
}
