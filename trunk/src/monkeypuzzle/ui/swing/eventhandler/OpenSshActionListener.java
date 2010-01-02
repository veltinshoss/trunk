/**
 * 
 */
package monkeypuzzle.ui.swing.eventhandler;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.central.IPhone;
import monkeypuzzle.central.IPhoneParseException;
import monkeypuzzle.licence.NotLicencedException;
import monkeypuzzle.ui.swing.MainFrame;
import monkeypuzzle.ui.swing.Mediator;

public final class OpenSshActionListener extends AbstractOpenAction implements
		java.awt.event.ActionListener {

	public static void openSsh(final JFrame frame, final Mediator mediator,
			final String serverAddress, final String userId,
			final String password) {
		openDataSource(frame, mediator, new Creator() {

			@Override
			public IPhone getIPhone() throws IPhoneParseException, IOException,
					FileParseException, NotLicencedException {
				return mediator.getIPhoneFactory().createIPhoneState(
						serverAddress, userId, password);
			}

			@Override
			public void postCreate(Mediator mediator, IPhone iPhone) {
				mediator.changeBackupDirectory(null, iPhone);
			}
		});
	}

	private File lastSelectedDir;
	private final MainFrame mainFrame;

	// chose last time
	/**
	 * @param mainFrame
	 */
	public OpenSshActionListener(final MainFrame mainFrame,
			final Mediator mediator) {
		super(mediator);
		this.mainFrame = mainFrame;
		this.lastSelectedDir = mediator.getPreferences()
				.getRecentBackupDirectories().size() >= 1 ? new File(mediator
				.getPreferences().getRecentBackupDirectories().get(0)) : null;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
//		JDialog dialog = new JDialog(this.mainFrame, "Select ssh server");
//		dialog.getContentPane().add(new JLabel("Hi"));
//		dialog.setVisible(true);
		  String[] unamePwd = JOptionPaneExt.showUserLoginDialog(this.mainFrame, "Login Dialog", "Server", "Username: ", "Password: ");
//		  System.out.println(unamePwd[0]);
//		  System.out.println(unamePwd[1]);

		 openSsh(OpenSshActionListener.this.mainFrame,
		 this.mediator,unamePwd[0],unamePwd[1],unamePwd[2] );

	}

	static class JOptionPaneExt extends JOptionPane {
		  static String[] showUserLoginDialog(Component parentComponent, String title, String label0, String label1, String label2) {
		    JPanel jp = new JPanel();
		    JTextField server = new JTextField(25);
		    JTextField username = new JTextField(10);
		    JTextField password = new JPasswordField(10);
		    jp.add(new JLabel(label0));
		    jp.add(server);
		    jp.add(new JLabel(label1));
		    jp.add(username);
		    jp.add(new JLabel(label2));
		    jp.add(password);
		    int n = showConfirmDialog(parentComponent, jp, title, JOptionPane.OK_CANCEL_OPTION);
		    if (n==OK_OPTION) {
		      return new String[] {server.getText(),username.getText(), password.getText()};
		    } else {
		      return new String[] {"anonymous", ""};
		    }
		  }
		}
	
}