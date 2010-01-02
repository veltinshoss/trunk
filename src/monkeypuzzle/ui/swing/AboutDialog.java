package monkeypuzzle.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import monkeypuzzle.About;

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

	class InfoPanel extends JPanel
	{
		InfoPanel()
		{
			setLayout(new BorderLayout());
			JTextArea infoArea = new JTextArea();

			infoArea.setText(About.getCredits());
			infoArea.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(infoArea);
			scrollPane.setMaximumSize(new Dimension(300, 300));
			this.add(scrollPane);
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
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}

	private AboutDialog(final Frame frame)
	{
		super(frame, "monkeypuzzle about", true);
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
