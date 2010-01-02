package monkeypuzzle.ui.swing;

import java.util.Map;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import monkeypuzzle.central.IPhone;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.results.ContentType;
import monkeypuzzle.results.Location;
import monkeypuzzle.results.LocationMatcher;
import monkeypuzzle.results.MatcherException;

@SuppressWarnings("serial")
public class LocationSearchPanel extends JPanel implements SearchPane
{
	private JTextField fileNameField = new JTextField();
	private JLabel fileNameLabel = new JLabel("Filename");

	private JTextField locationField = new JTextField();
	private JLabel locationLabel = new JLabel("Location");

	private JComboBox typeField = new JComboBox(ContentType.values());
	private JLabel typeLabel = new JLabel("Type");

	public LocationSearchPanel()
	{
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.fileNameLabel).addComponent(
								this.typeLabel)
						.addComponent(this.locationLabel)).addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.fileNameField).addComponent(
								this.typeField)
						.addComponent(this.locationField)));

		layout.setVerticalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.fileNameLabel).addComponent(
								this.fileNameField)).addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.typeLabel).addComponent(
								this.typeField)).addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.locationLabel).addComponent(
								this.locationField)));

	}

	@Override
	public Map<BackupFile, Set<Location>> search(final IPhone backupDir)
			throws MatcherException
	{
		return backupDir.locateGrouped(LocationMatcher.parse(this.fileNameField
				.getText(), (ContentType) this.typeField.getSelectedItem(),
				this.locationField.getText()));
	}
}
