package com.crypticbit.ipa.ui.swing;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.HorizontalAlignment;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.VerticalAlignment;
import org.jdesktop.swingx.painter.ImagePainter;

import com.crypticbit.ipa.central.BackupDirectoryParser;
import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.central.backupfile.BackupScanner;
import com.crypticbit.ipa.licence.WebValidator;
import com.crypticbit.ipa.ui.swing.eventhandler.OpenBackupActionListener;
import com.crypticbit.ipa.ui.swing.prefs.Preferences;

public class OpenPanel extends JXPanel {

	public class TransparentJEditorPane extends JEditorPane {

		public TransparentJEditorPane(String text) {
			super("text/html", text);
		}

		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					0.5f));
			super.paint(g2);
			g2.dispose();
		}

	}

	private Mediator mediator;
	private final MainFrame mainFrame;

	public OpenPanel(final Mediator mediator, final MainFrame mainFrame) {
		this.mediator = mediator;
		this.mainFrame = mainFrame;
		this.setLayout(new BorderLayout());
		// this.setSize(600, 600);
		StringBuffer text = new StringBuffer();

		ImagePainter image;
		try {
			URL backgroundImage = ClassLoader.getSystemClassLoader()
					.getResource("crypticbit-backdrop.jpg");
			image = new ImagePainter(backgroundImage,
					HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
			image.setScaleToFit(false);

			this.setBackgroundPainter(image);
			this.setBackground(Color.WHITE);

		} catch (IOException e) {
			LogFactory.getLogger().log(Level.INFO,
					"Unable to open background image", e);
		}

		text.append("<h1>Welcome to iPhone Analyzer</h1>");
		text.append("<p><font size=\"5\">To begin, select an iOS device backup to open.</font></p>");
		text.append("<p><font size=\"5\">You can select from either:</font></p>");
		text.append("<ol><li>The default iTunes backup location:<ol>");
		File defaultRoot = BackupScanner.getDefaultRoot();
		if (defaultRoot != null && defaultRoot.exists()) {
			for (File entry : defaultRoot.listFiles()) {
				if (BackupDirectoryParser.isBackup(entry))
					text.append("<li>" + createOpenEntry(entry) + "</li>");
			}
		}
		if (defaultRoot == null || !defaultRoot.exists()
				|| defaultRoot.listFiles().length == 0) {
			text.append("<i>No files found</i>");
		}
		text.append("</ol></li>");

		text.append("<li>A backup which you have opened before:<ol>");

		final Preferences prefs = this.mediator.getPreferences();
		for (String backupDirectory : prefs.getRecentBackupDirectories()) {
			text.append("<li>" + createOpenEntry(new File(backupDirectory))
					+ "</li>");
		}
		if (prefs.getRecentBackupDirectories().size() == 0) {
			text.append("<i>No files found</i>");
		}
		text.append("</ol></li>");

		text.append("<li>Or browse for a new backup directory:<ol>");
		text.append("<li><a href=\"\">Browse for a new backup</a></li></ol>");
		text.append("</li></ul>");

		JEditorPane linksPane = new TransparentJEditorPane(text.toString());
		linksPane.setEditable(false);
		linksPane.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {

				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (e.getURL() == null)
						OpenBackupActionListener.browseForBackup(mainFrame,
								mediator, false);
					else
						try {
							mainFrame.openFile(new File(e.getURL().toURI()));
						} catch (URISyntaxException e1) {
							LogFactory.getLogger().log(Level.WARNING,
									"Unable to parse url to open location", e);
						}
				}
			}
		});
		this.add(linksPane, BorderLayout.EAST);

		// License Pane (along bottom)
		final TransparentJEditorPane bannerPane = new TransparentJEditorPane(
				"<p align=\"right\"><font size=\"12\">This copy is not licensed for commercial or governement use.</font><br><font size=\"8\">If you have paid for a license, please <a href=''>register</a> this copy.</font></p>");

		String prefsUser = prefs.getRegisteredUser();
		String prefsKey = prefs.getRegisteredKey();

		if (this.isLicensed(prefsUser, prefsKey)) {
			bannerPane.setText(getRegisteredText(prefsUser));
		} else {
			bannerPane.addHyperlinkListener(new HyperlinkListener() {

				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						Frame frame = (JFrame) mediator.getMainFrame();
						RegDialog regDialog = new RegDialog(frame);
						regDialog.setVisible(true);

						if (regDialog.isOkayed()) {
							String user = regDialog.getUsername();
							String key = regDialog.getKey();

							if (isLicensed(user, key)) {
								// pass
								prefs.setRegisteredUser(user);
								prefs.setRegisteredKey(key);
								bannerPane.setText(getRegisteredText(user));
								JOptionPane.showMessageDialog(frame,
										"Registration successful");
							} else {
								JOptionPane
										.showMessageDialog(
												frame,
												"Sorry, registration was not successful: Unrecognised email address or key.",
												"Registration Unsuccessful",
												JOptionPane.WARNING_MESSAGE);
							}
						}

					}

				}
			});
		}

		bannerPane.setEditable(false);
		bannerPane.setBackground(Color.BLUE);
		this.add(bannerPane, BorderLayout.SOUTH);

	}

	private String getRegisteredText(String user) {
		return "Registered for commercial and/or government use by " + user;
	}

	private boolean isLicensed(final String user, final String key) {
		if (user == null || key == null) {
			return false;
		}

		String magic = "kjuy" + "htrg" + "ewq32" + "4567" + "uyth" + "gfds"
				+ "reaw" + "4356" + "ytuf" + user.toLowerCase();
		String emailSha = "";
		try {
			emailSha = WebValidator.convertToSha1(magic);
		} catch (Exception e) {
			return false;
		}
		return emailSha.equals(key);
	}

	private String createOpenEntry(final File file) {
		return "<a href=\"" + file.toURI().toASCIIString() + "\">"
				+ BackupFile.getBackupName(file) + "</a>";

	}

}
