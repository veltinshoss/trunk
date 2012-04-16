/**
 * 
 */
package com.crypticbit.ipa.ui.swing.eventhandler;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.IPhone;
import com.crypticbit.ipa.central.IPhoneParseException;
import com.crypticbit.ipa.central.NotaBackupDirectoryException;
import com.crypticbit.ipa.central.ProgressIndicator;
import com.crypticbit.ipa.licence.NotLicencedException;
import com.crypticbit.ipa.ui.swing.MainFrame;
import com.crypticbit.ipa.ui.swing.Mediator;


public final class OpenBackupActionListener extends AbstractActionListener {

	public void openBackupFile(final File directory) {
		runInBackground(new Command() {

			@Override
			public void setProgressIndicator(ProgressIndicator progressIndicator) {
				try {
					mediator.getIPhoneFactory().setProgressIndicator(
							progressIndicator);
				} catch (IOException e) {
					// do nothing - we'll fail later
				}
			}

			@Override
			public void run() {
				IPhone iphone;
				try {
					iphone = getIPhone();
					update(mediator, iphone);				
				} catch (NotaBackupDirectoryException e) {
										mediator.displayWarningDialog("The directory you selected does not appear to contain any backup files.",e);
				} catch (IPhoneParseException e) {
					mediator.displayErrorDialog(
							"The format of the iPhone backup was not legal", e);
				} catch (IOException e) {
					mediator
							.displayErrorDialog(
									"There was a problem accessing the local filesystem",
									e);
				} catch (FileParseException e) {
					mediator.displayErrorDialog("Unable to parse a file", e);
				} catch (NotLicencedException e) {
					mediator.displayWarningDialog(
							"There has been a licence problem", e);
				}

			};

			private IPhone getIPhone() throws IPhoneParseException,
					IOException, FileParseException, NotLicencedException {
				return mediator.getIPhoneFactory().createIPhoneState(directory,
						unpacked);
			}

			private void update(Mediator mediator, IPhone iPhone) {
				mediator.changeBackupDirectory(directory, iPhone);
			}
		}, "Opening backup directory...");
	}

	private File lastSelectedDir;
	private final boolean unpacked;

	// chose last time
	/**
	 * @param mainFrame
	 */
	public OpenBackupActionListener(final MainFrame mainFrame,
			final Mediator mediator, final boolean unpacked) {
		super(mediator);
		this.unpacked = unpacked;
		this.lastSelectedDir = mediator.getPreferences()
				.getRecentBackupDirectories().size() >= 1 ? new File(mediator
				.getPreferences().getRecentBackupDirectories().get(0)) : null;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final JFileChooser fc;
		if (this.lastSelectedDir == null) {
			fc = new JFileChooser();
		} else {
			fc = new JFileChooser(this.lastSelectedDir);
		}
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(this.mainFrame);
		this.lastSelectedDir = fc.getSelectedFile();
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			openBackupFile(this.lastSelectedDir);
		}
	}
	
	public static void browseForBackup(final MainFrame mainFrame,
			final Mediator mediator, final boolean unpacked) {
		new OpenBackupActionListener(mainFrame, mediator, unpacked).actionPerformed(null);
	}
}