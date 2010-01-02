/**
 * 
 */
package monkeypuzzle.ui.swing.eventhandler;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.central.IPhone;
import monkeypuzzle.central.IPhoneParseException;
import monkeypuzzle.licence.NotLicencedException;
import monkeypuzzle.ui.swing.MainFrame;
import monkeypuzzle.ui.swing.Mediator;

public final class OpenBackupActionListener extends AbstractOpenAction implements
		java.awt.event.ActionListener {



	

	public static void openBackupFile(final JFrame frame,
			final Mediator mediator, final File directory) {
		openDataSource(frame, mediator, new Creator() {

			@Override
			public IPhone getIPhone() throws IPhoneParseException, IOException,
					FileParseException, NotLicencedException {
				return mediator.getIPhoneFactory().createIPhoneState(directory);
			}

			@Override
			public void postCreate(Mediator mediator, IPhone iPhone) {
						mediator.changeBackupDirectory(directory,
								iPhone);
				
			}
		});
	}

	
	private File lastSelectedDir;
	private final MainFrame mainFrame;


	// chose last time
	/**
	 * @param mainFrame
	 */
	public OpenBackupActionListener(final MainFrame mainFrame,
			final Mediator mediator) {
		super(mediator);
		this.mainFrame = mainFrame;
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
			openBackupFile(OpenBackupActionListener.this.mainFrame,
					this.mediator, this.lastSelectedDir);
		}
	}
}