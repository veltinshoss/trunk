package com.crypticbit.ipa.ui.swing;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Random;
import java.util.logging.Level;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.HorizontalAlignment;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.VerticalAlignment;
import org.jdesktop.swingx.painter.ImagePainter;

import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.central.backupfile.BackupScanner;

public class OpenPanel extends JXPanel implements ActionListener {

	private BackupScanner backupScanner = new BackupScanner();
	// the place to start scanning directories
	private File defaultRoot = BackupScanner.getDefaultRoot();
	// the backup to open - null before initial selection
	private File backup;
	private JPanel filePanel = new JPanel();
	private JScrollPane sp;
	private JTextField location = new JTextField();
	private JFrame frame;

	private ButtonGroup group = new ButtonGroup();
	private JButton go;

	OpenPanel(Mediator mediator, final JFrame frame) {
		super(new BorderLayout());
		this.frame = frame;

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

		JPanel locationPanel = new JPanel(new BorderLayout());
		locationPanel.setOpaque(false);
		location.setText(defaultRoot.toString());
		locationPanel.add(location, BorderLayout.CENTER);
		JButton browserButton = new JButton("Browse");
		browserButton.addActionListener(this);
		locationPanel.add(browserButton, BorderLayout.EAST);
		JEditorPane tp = new JEditorPane("text/html", createHtml());
		tp.setOpaque(false);
		locationPanel.add(tp, BorderLayout.NORTH);
		this.add(locationPanel, BorderLayout.NORTH);

		filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
		filePanel.setBackground(new Color(0.9f, 0.9f, 1.0f, 0.6f));

		sp = new JScrollPane(new AlphaContainer(filePanel));

		sp.setBorder(new EmptyBorder(10, 10, 10, 10));
		sp.getViewport().setOpaque(false);

		JPanel x = new JPanel(new GridLayout(0, 2));
		x.setOpaque(false);

		// x.setOpaque(false);
		this.add(x, BorderLayout.CENTER);
		x.add(sp);
		go = new JButton("Analyze IPhone Backup >>>");
		go.setEnabled(false);
		go.setFont(go.getFont().deriveFont(25.0f));
		go.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((MainFrame) frame).openFile(backup);
			}
		});

		JPanel y = new JPanel(new BorderLayout());
		y.setOpaque(false);
		JPanel z = new JPanel();
		z.setOpaque(false);
		z.add(go);
		y.add(z, BorderLayout.SOUTH);
		x.add(y);
		scanForBackups();

		tp.setEditable(false);

		// License Pane (along bottom)

		String messages[] = {
				"This product is available for free, but please consider <a href=\"https://sourceforge.net/project/project_donations.php?group_id=296588\">donating</a>.",
				"This product is available for free, but please consider <a href=\"https://sourceforge.net/projects/iphoneanalyzer/reviews/?sort=usefulness&filter=all\">up voting us on sourceforge</a>",
				"This product is available for free, but please consider write a blog or reccomending us to a friend</a>" };
//		String message = "Thank you for purchasing this product.";
		String message = messages[new Random().nextInt(messages.length)];
		
		final TransparentJEditorPane bannerPane = new TransparentJEditorPane(
				"<p align=\"right\"><font size=\"12\">"+message+"</font></p>");
		bannerPane.setBackground(Color.BLUE);
		bannerPane.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (Exception e1) {
						// do nothing
					}
				}

			}
		});

		bannerPane.setEditable(false);
		this.add(bannerPane, BorderLayout.SOUTH);

	}

	private String createHtml() {
		StringBuffer text = new StringBuffer();
		text.append("<h1>IPhone Analyzer</h1>");
		text.append("<p><font size=\"5\">To begin, select an iOS device backup to open.</font></p>");
		return text.toString();
	}

	private void scanForBackups() {
		backupScanner.scanForBackupsAsync(new BackupScanner.BackupFound() {

			@Override
			public void report(final File entry) {
				// we ought to run the updated in the swing
				// thread
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						JRadioButton r = new JRadioButton(BackupFile
								.getBackupName(entry));
						r.setOpaque(false);
						r.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								backup = entry;
								go.setEnabled(true);
							}

						});
						group.add(r);
						filePanel.add(r);
						filePanel.revalidate();

					}
				});

			}
		}, defaultRoot);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		final JFileChooser fc;
		fc = new JFileChooser(location.getText());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			defaultRoot = fc.getSelectedFile();
			location.setText(defaultRoot.toString());
			backupScanner.stopScanning();
			filePanel.removeAll();
			Enumeration<AbstractButton> en = group.getElements();
			while (en.hasMoreElements())
				group.remove(en.nextElement());
			filePanel.revalidate();
			this.revalidate();
			frame.repaint();
			backup = null;
			go.setEnabled(false);
			scanForBackups();
		}
	}

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

}
