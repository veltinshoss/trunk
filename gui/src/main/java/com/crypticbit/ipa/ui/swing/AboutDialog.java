package com.crypticbit.ipa.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import com.crypticbit.ipa.About;

@SuppressWarnings("serial")
public final class AboutDialog extends JDialog implements ActionListener
{
	class ButtonsPanel extends JPanel
	{
		ButtonsPanel()
		{
			// Create and initialize the buttons.
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(AboutDialog.this);

			this.add(cancelButton);
		}
	}

	class InfoPanel extends JTabbedPane
	{
		InfoPanel()
		{
			this.add("Credits", createTextPanel(About.getCredits()));
			this.add("Dependencies", createTextPanel(About.getDependencies()));
			if (About.showMessage())
				this.add("Licence", createTextPanel(About.getLicence()));
		}
	}

	private static JComponent createTextPanel(URL url)
	{

		try
		{
			JEditorPane panel = new JEditorPane(url);
			panel.setEditable(false);
			panel.setCaretPosition(0);
			JScrollPane scrollPane = new JScrollPane(panel);
			return scrollPane;
		} catch (IOException e)
		{
			return new JLabel("Unable to find: " + url);
		}

	}

	class TopPanel extends JPanel
	{
		TopPanel()
		{
			setLayout(new GridLayout(2, 1));
			this.add(new JLabel(About.getCopyright(), SwingConstants.LEFT));
			this.add(new JLabel("Version: " + About.getVersion(),
					SwingConstants.LEFT));
		}
	}

	private static AboutDialog dialog;

	public static void showDialog(final Frame frame)
	{
		dialog = new AboutDialog(frame);
		dialog.setSize(new Dimension(500, 500));
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}

	private AboutDialog(final Frame frame)
	{
		super(frame, "About IPhone Analyzer", true);
		setLayout(new BorderLayout());

		this.add(new TopPanel(), BorderLayout.NORTH);
		this.add(new InfoPanel(), BorderLayout.CENTER);
		this.add(new ButtonsPanel(), BorderLayout.SOUTH);
		pack();

	}

	// Handle clicks on the Set and Cancel buttons.
	public void actionPerformed(final ActionEvent e)
	{
		AboutDialog.dialog.setVisible(false);
	}
}
