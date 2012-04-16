package com.crypticbit.ipa.ui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.crypticbit.ipa.ui.swing.prefs.Preferences;

/**
 * This class creates the JSearch dialog box.
 */

@SuppressWarnings("serial")
public class PrefsDialog extends JDialog
{
	private Preferences prefs;
	private Mediator mediator;

	public class MappingPrefsPanel extends JPanel
	{
		private JTextField offlineMappingPath = new JTextField();
		private JButton browseButton = new JButton(
				new AbstractAction("Browse") {

					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						File currentOfflineDir = new File(offlineMappingPath
								.getText());
						JFileChooser fc = new JFileChooser(currentOfflineDir);
						fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						fc.setDialogTitle("Please select your Offline map tile directory");
						int returnVal = fc.showOpenDialog(PrefsDialog.this);
						if (returnVal == JFileChooser.APPROVE_OPTION)
						{
							offlineMappingPath.setText(fc.getSelectedFile()
									.getAbsolutePath());
						}

					}
				});
		private JCheckBox offlineMappingCheckbox;

		MappingPrefsPanel()
		{
			offlineMappingCheckbox = new JCheckBox(new AbstractAction(
					"Offline mapping") {

				@Override
				public void actionPerformed(ActionEvent event)
				{
					JCheckBox cb = (JCheckBox) event.getSource();
					initState(cb.isSelected());
				}

			});
			this.add(offlineMappingCheckbox);
			this.add(offlineMappingPath);
			this.add(browseButton);
			boolean useOfflineMaps = prefs.getUseOfflineMaps();
			offlineMappingCheckbox.setSelected(useOfflineMaps);
			offlineMappingPath.setText(prefs.getOfflineMapDir().toString());
			initState(useOfflineMaps);
		}

		private void initState(boolean isSel)
		{
			offlineMappingPath.setEnabled(isSel);
			browseButton.setEnabled(isSel);
		}

		public void save()
		{
			prefs.setOfflineMapDir(offlineMappingPath.getText());
			prefs.setUseOfflineMaps(offlineMappingCheckbox.isSelected());

		}
	}

	public class GeneralPrefsPanel extends JPanel
	{
		private JCheckBox obscuredCheckbox;

		GeneralPrefsPanel()
		{
			obscuredCheckbox = new JCheckBox(new AbstractAction("Obscured") {

				@Override
				public void actionPerformed(ActionEvent event)
				{
					// do nothing
				}

			});
			obscuredCheckbox.setSelected(prefs.getObscuredDisplay());
			this.add(obscuredCheckbox);
		}

		public void save()
		{
			prefs.setObscuredDisplay(obscuredCheckbox.isSelected());
		}
	}

	private MappingPrefsPanel mappingPanel;
	private GeneralPrefsPanel generalPanel;

	public PrefsDialog(final JFrame mainFrame, final Mediator mediator)
	{
		super(mainFrame, "Preferences", true);
		this.mediator = mediator;
		prefs = mediator.getPreferences();
		mappingPanel = new MappingPrefsPanel();
		generalPanel = new GeneralPrefsPanel();
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
		tabs.add("Mapping", mappingPanel);
		tabs.add("General", generalPanel);
		getContentPane().add(tabs, BorderLayout.CENTER);
		JPanel p3 = new JPanel();
		p3.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton b = new JButton("OK");
		p3.add(b);
		getRootPane().setDefaultButton(b);

		b.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e)
			{
				mappingPanel.save();
				generalPanel.save();
				dispose();
			}
		});

		// Create and add Cancel button to bottom section layout panel.

		b = new JButton("Cancel");
		p3.add(b);

		// Modify dialog box so that pressing Esc appears to invoke the Cancel
		// button's actionPerformed() method.

		addCancelByEscapeKey();

		// Add action listener to respond to Cancel button action events. In
		// response to those events, listener sets the canceled status (to
		// indicate that the Cancel button is clicked), and disposes of the
		// dialog box -- returning GUI control to its caller.

		b.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e)
			{
				dispose();
			}
		});

		// Add bottom section layout panel to South area of main window's
		// content pane.

		getContentPane().add(p3, BorderLayout.SOUTH);

		// Resize dialog box to union of collective preferred sizes of all
		// contained components.

		pack();

		// Center dialog box (when it appears) relative to main window.

		// setLocationRelativeTo(f);
	}

	/**
	 * Modify the dialog box, so that pressing the Esc key does the same thing
	 * as clicking the Cancel button. This method's actionPerformed() method
	 * must contain the same code as the Cancel button's actionPerformed()
	 * method.
	 */

	private void addCancelByEscapeKey()
	{
		// Map the Esc key to the cancel action description in the dialog box's
		// input map.

		String CANCEL_ACTION_KEY = "CANCEL_ACTION_KEY";

		int noModifiers = 0;

		KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
				noModifiers, false);

		InputMap inputMap = getRootPane().getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		inputMap.put(escapeKey, CANCEL_ACTION_KEY);

		// Map the cancel action description to the cancel action in the dialog
		// box's action map.

		AbstractAction cancelAction = new AbstractAction() {
			public void actionPerformed(final ActionEvent e)
			{
				dispose();
			}
		};

		getRootPane().getActionMap().put(CANCEL_ACTION_KEY, cancelAction);
	}

}