package monkeypuzzle.ui.swing;

import java.awt.Font;
import java.text.DateFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import monkeypuzzle.central.IPhone;
import monkeypuzzle.entity.status.Info;
import monkeypuzzle.ui.swing.plist.PListPanel;

import org.jdesktop.swingx.JXTitledPanel;

@SuppressWarnings("serial")
public class InfoView extends JSplitPane
{
	private Mediator mediator;

	private static final String[] labels = { "Last Backup Date", "GUID",
			"Unique Identifier", "Phone Number", "Product Version",
			"Product Type", "Target Type", "Serial Number", "iTunes Version",
			"Target Identifier", "Display Name", "Build Version", "Device Name" };
	private JLabel[] field = new JLabel[labels.length];

	InfoView(final Mediator mediator)
	{
		this.mediator = mediator;
		setUpGui();
		init(mediator.getBackupDirectory());
	}

	public void init(final IPhone backupDirectory)
	{
		Info info = backupDirectory.getConfigElements().getInfo();
		String[] values = {
				DateFormat.getDateInstance().format(info.getLastBackupDate()),
				info.getGUID(), info.getUniqueIdentifier(),
				info.getPhoneNumber(), info.getProductVersion(),
				info.getProductType(), info.getTargetType(),
				info.getSerialNumber(), info.getITunesVersion(),
				info.getTargetIdentifier(), info.getDisplayName(),
				info.getBuildVersion(), info.getDeviceName() };
		for (int loop = 0; loop < labels.length; loop++)
		{
			this.field[loop].setText(values[loop]);
		}

	}

	private void setUpGui()
	{
		JPanel left = new JPanel();
		// Create and populate the panel.
		left.setLayout(new SpringLayout());
		for (int i = 0; i < labels.length; i++)
		{
			JLabel l = new JLabel(labels[i], SwingConstants.TRAILING);
			left.add(l);
			this.field[i] = new JLabel();
			this.field[i].setFont(this.field[i].getFont().deriveFont(
					Font.ITALIC));
			l.setLabelFor(this.field[i]);
			left.add(this.field[i]);
		}

		// Lay out the panel.
		SpringUtilities.makeCompactGrid(left, labels.length, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad
		setLeftComponent(new JXTitledPanel("Phone Information", left));

		try
		{
			JScrollPane right = new JScrollPane(
					new JXTitledPanel("Manifest", new PListPanel(this.mediator
							.getBackupDirectory().getConfigElements()
							.getManifest().getDataAsManifest().getContainer(),
							this.mediator)));
			setRightComponent(right);
		} catch (Exception e)
		{
			this.mediator.displayErrorDialog("Unable to display manifest", e);
			setRightComponent(new JLabel("Unable to display manifest"));
		}

	}
}
