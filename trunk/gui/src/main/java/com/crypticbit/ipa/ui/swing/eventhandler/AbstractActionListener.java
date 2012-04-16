package com.crypticbit.ipa.ui.swing.eventhandler;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import com.crypticbit.ipa.central.ProgressIndicator;
import com.crypticbit.ipa.ui.swing.Mediator;


public abstract class AbstractActionListener implements ActionListener
{
	protected final Component mainFrame;
	protected final Mediator mediator;

	public AbstractActionListener(
			final Mediator mediator)
	{
		this.mainFrame = mediator.getMainFrame();
		this.mediator = mediator;
	}
	
	protected File showSaveDialog(boolean directory)
	{
		final JFileChooser fc = new JFileChooser();
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		fc.setFileSelectionMode(directory ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
		if (fc.showSaveDialog(this.mainFrame) == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile();
		else
			return null;
	}

	interface Command extends Runnable {
		void setProgressIndicator(ProgressIndicator progressIndicator);
	}
	
	protected void runInBackground(final Command c, final String action) {
		{
			new SwingWorker<Void, Void>() {
				private ProgressMonitor progressMonitor = null;

				@Override
				public Void doInBackground() {
					try {
						mainFrame.setCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));
						c.setProgressIndicator(
								new ProgressIndicator() {
									@Override
									public void progressUpdate(final int entry,
											final int outOf,
											final String description) {
										if (progressMonitor == null) {
											progressMonitor = new ProgressMonitor(
													mainFrame,
													action,
													description, 0, outOf);
											progressMonitor
													.setMillisToPopup(0);
											progressMonitor
													.setMillisToDecideToPopup(0);
										} else {
											progressMonitor.setProgress(entry);
											progressMonitor
													.setNote(description);
										}
									}
								});
						c.run();
					} catch (Throwable e1) {
						mediator.displayErrorDialog(
								"Problem with action", e1);
					} finally {
						mainFrame.setCursor(Cursor
								.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						if (this.progressMonitor != null) {
							this.progressMonitor.close();
						}
						this.progressMonitor = null;
					}
					return null;
				}
			}.execute();
		}
	}
}
