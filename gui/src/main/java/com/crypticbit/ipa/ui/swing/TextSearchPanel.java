/**
 * 
 */
package com.crypticbit.ipa.ui.swing;

import java.awt.BorderLayout;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.crypticbit.ipa.central.IPhone;
import com.crypticbit.ipa.central.NavigateException;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.results.Location;
import com.crypticbit.ipa.results.TextSearchAlgorithm;


@SuppressWarnings("serial")
class TextSearchPanel extends JPanel implements SearchPane
{
	private static final String[] SEARCH_OPTIONS = { "Case insensitive",
			"Case sensitive", "Regular expression", "Fuzzy match" };
	private JComboBox searchOptionsList = new JComboBox(SEARCH_OPTIONS);
	private JTextField t = new JTextField(20);

	TextSearchPanel()
	{
		setLayout(new BorderLayout());

		// Create layout panel for top section of dialog box's GUI.

		JPanel p = new JPanel();

		// Create and add "Search text:" label to top section layout panel.

		p.add(new JLabel("Search text:"));

		// Create and add search text text field to top section layout
		// panel.

		p.add(this.t);

		// Add top section layout panel to North area of main window's
		// content
		// pane.

		add(p, BorderLayout.NORTH);

		// Create layout panel for middle section of dialog box's GUI.

		JPanel p2 = new JPanel();

		// Create and add case-sensitive search checkbox to middle section
		// panel.

		this.searchOptionsList.setSelectedIndex(0);
		p2.add(this.searchOptionsList);

		// Add middle section layout panel to Center area of main window's
		// content pane.

		add(p2, BorderLayout.CENTER);
	}

	@Override
	public Map<BackupFile, Set<Location>> search(final IPhone backupDir)
			throws NavigateException
	{
		return backupDir.searchGrouped(getSearchType(), getSearchString());
	}

	void cancel()
	{

	}

	private String getSearchString()
	{
		return this.t.getText().trim();
	}

	private TextSearchAlgorithm getSearchType()
	{
		return TextSearchAlgorithm.values()[this.searchOptionsList
				.getSelectedIndex()];
	}

}