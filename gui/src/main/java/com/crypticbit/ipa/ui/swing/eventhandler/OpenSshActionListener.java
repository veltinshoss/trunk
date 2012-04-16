/**
 * 
 */
package com.crypticbit.ipa.ui.swing.eventhandler;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.IPhone;
import com.crypticbit.ipa.central.IPhoneParseException;
import com.crypticbit.ipa.central.ProgressIndicator;
import com.crypticbit.ipa.licence.NotLicencedException;
import com.crypticbit.ipa.ui.swing.Mediator;


public final class OpenSshActionListener extends AbstractActionListener {

	public OpenSshActionListener(Mediator mediator) {
		super(mediator);
	}

	public void openSsh(final String serverAddress, final String userId,
			final String password) {
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
				} catch (IPhoneParseException e) {
					mediator.displayErrorDialog(
							"The format of the iPhone filesystem was not legal", e);
				} catch (IOException e) {
					mediator
							.displayErrorDialog(
									"There was a problem accessing the local filesystem",
									e);
				} catch (FileParseException e) {
					mediator.displayErrorDialog("Unable to parse a file", e);
				} catch (NotLicencedException e) {
					mediator.displayErrorDialog(
							"There has been a licence problem", e);
				}

			};

			private IPhone getIPhone() throws IPhoneParseException,
					IOException, FileParseException, NotLicencedException {
				return mediator.getIPhoneFactory().createIPhoneState(
						serverAddress, userId, password);
			}

			private void update(Mediator mediator, IPhone iPhone) {
				mediator.changeBackupDirectory(null, iPhone);
			}
		}, "Opening SSH server "+serverAddress);
	}


	@Override
	public void actionPerformed(final ActionEvent e) {
		String[] unamePwd = JOptionPaneExt.showUserLoginDialog(this.mainFrame,
				"Login Dialog", "Server", "Username: ", "Password: ");
		openSsh(unamePwd[0], unamePwd[1], unamePwd[2]);

	}

	static class JOptionPaneExt extends JOptionPane {
		static String[] showUserLoginDialog(Component parentComponent,
				String title, String label0, String label1, String label2) {
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
			int n = showConfirmDialog(parentComponent, jp, title,
					JOptionPane.OK_CANCEL_OPTION);
			if (n == OK_OPTION) {
				return new String[] { server.getText(), username.getText(),
						password.getText() };
			} else {
				return new String[] { "anonymous", "" };
			}
		}
	}

}