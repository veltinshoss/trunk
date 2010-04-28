package monkeypuzzle.ui.swing;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import monkeypuzzle.central.IPhone;
import monkeypuzzle.central.NavigateException;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.results.Location;
import monkeypuzzle.results.MatcherException;

/**
 * This class creates the JSearch dialog box.
 */

@SuppressWarnings("serial")
public class SearchDialog extends JDialog
{

	private Map<BackupFile, Set<Location>> results;
	private JTabbedPane searchTypeTabs;

	// ============
	// PRIVATE AREA
	// ============

	/**
	 * Construct a JSearch dialog box
	 * 
	 * @param mainFrame
	 *            the parent JFrame-based window's reference
	 * @param title
	 *            the title to appear in the JSearch dialog box's title bar
	 */

	public SearchDialog(final JFrame mainFrame, final Mediator mediator)
	{
		super(mainFrame, "Search", true);

		this.searchTypeTabs = new JTabbedPane();
		this.searchTypeTabs.add("Text", new TextSearchPanel());
		this.searchTypeTabs.add("Location", new LocationSearchPanel());

		getContentPane().add(this.searchTypeTabs, BorderLayout.CENTER);

		// Create layout panel for bottom section of dialog box's GUI. Change
		// default layout manager to a flow that right-justifies its components.

		JPanel p3 = new JPanel();
		p3.setLayout(new FlowLayout(FlowLayout.RIGHT));

		// Create and add Search button to bottom section layout panel.

		JButton b = new JButton("Search");
		p3.add(b);

		// Make button the default, so that pressing Enter causes the Search
		// button to fire an action event.

		getRootPane().setDefaultButton(b);

		// Add action listener to respond to Search button action events. In
		// response to those events, listener clears the canceled status (to
		// indicate that the Cancel button is not clicked), caches the search
		// text (with leading/trailing whitespace characters removed), caches
		// the case-sensitive search checkbox value, and disposes of the dialog
		// box -- returning GUI control to its caller.

		b.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					mainFrame.setCursor(Cursor
							.getPredefinedCursor(Cursor.WAIT_CURSOR));
					mediator.fireRemoveHighlightChangeListeners();

					IPhone backupDir = mediator.getBackupDirectory();

					SearchDialog.this.results = ((SearchPane) SearchDialog.this.searchTypeTabs
							.getSelectedComponent()).search(backupDir);
				} catch (MatcherException MatcherException)
				{
					mediator.displayErrorDialog("Illegal search term",
							MatcherException);
				} catch (NavigateException ne)
				{
					mediator.displayErrorDialog("Can't find item", ne);
				} finally
				{
					mainFrame.setCursor(Cursor
							.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					dispose();
				}
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
	 * @return the resultsMap
	 */
	public Map<BackupFile, Set<Location>> getResultsMap()
	{
		return this.results;
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