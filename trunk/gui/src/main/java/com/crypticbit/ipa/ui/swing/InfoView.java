package com.crypticbit.ipa.ui.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.DateFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXTitledPanel;

import com.crypticbit.ipa.entity.status.Info;
import com.crypticbit.ipa.io.parser.plist.PListContainer;
import com.crypticbit.ipa.ui.swing.plist.PListPanel;

@SuppressWarnings("serial")
public class InfoView extends JSplitPane
{
	private Mediator mediator;

	private static final String[] labels = { "Last Backup Date", "GUID",
			"Unique Identifier", "Phone Number", "Product Version",
			"Product Type", "Target Type", "Serial Number", "iTunes Version",
			"Target Identifier", "Display Name", "Build Version", "Device Name" };
	private JLabel[] field = new JLabel[labels.length];

	private Info info;

	private DisplayConverter displayConverter;

	InfoView(final Mediator mediator)
	{
		this.mediator = mediator;
		displayConverter = mediator.getDisplayConverter();
		if (mediator.getBackupDirectory().getConfigElements() != null
				&& mediator.getBackupDirectory().getConfigElements().getInfo() != null)
		{
			info = mediator.getBackupDirectory().getConfigElements().getInfo();
			setUpGui();
			init();
		} else
		{
			this.setLayout(new BorderLayout());
			this.add(new JLabel("No info.plist attached to show"),
					BorderLayout.CENTER);
		}
	}

	public void init()
	{
		String[] values = null;
		if (info != null)
		{
			String[] values1 = {
					DateFormat.getDateInstance().format(
							info.getLastBackupDate()),
					displayConverter.convertNumber(info.getGUID()),
					displayConverter.convertNumber(info.getUniqueIdentifier()),
					displayConverter.convertNumber(info.getPhoneNumber()),
					info.getProductVersion(), info.getProductType(),
					info.getTargetType(),
					displayConverter.convertNumber(info.getSerialNumber()),
					info.getITunesVersion(), info.getTargetIdentifier(),
					info.getDisplayName(), info.getBuildVersion(),
					displayConverter.convertString(info.getDeviceName()) };
			values = values1;
		}
		;
		for (int loop = 0; loop < labels.length; loop++)
		{
			this.field[loop].setText(values[loop]);
		}

	}

	private void setUpGui()
	{

		JPanel topLeft = new JPanel();
		// Create and populate the panel.
		topLeft.setLayout(new SpringLayout());
		for (int i = 0; i < labels.length; i++)
		{
			JLabel l = new JLabel(labels[i], SwingConstants.TRAILING);
			topLeft.add(l);
			this.field[i] = new JLabel();
			this.field[i].setFont(this.field[i].getFont().deriveFont(
					Font.ITALIC));
			l.setLabelFor(this.field[i]);
			topLeft.add(this.field[i]);
		}

		// Lay out the panel.
		SpringUtilities.makeCompactGrid(topLeft, labels.length, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		JPanel left = new JPanel(new BorderLayout());
		left.add(topLeft, BorderLayout.CENTER);
		try
		{
			left.add(
					new JXTitledPanel("Detail", new PListPanel(info
							.getContainer(), mediator)), BorderLayout.SOUTH);
		} catch (Exception e1)
		{
			left.add(new JLabel("Unable to display detail"), BorderLayout.SOUTH);
		}

		setLeftComponent(new JXTitledPanel("Phone Information", left));

		try
		{
			PListContainer manifest;
			if (this.mediator.getBackupDirectory().getConfigElements()
					.getManifest().getDataAsManifest() == null)
			{
				manifest = this.mediator.getBackupDirectory()
						.getConfigElements().getManifest().getContainer();
			} else
			{
				manifest = this.mediator.getBackupDirectory()
						.getConfigElements().getManifest().getDataAsManifest()
						.getContainer();
			}
			JScrollPane right = new JScrollPane(new JXTitledPanel("Manifest",
					new PListPanel(manifest, this.mediator)));
			setRightComponent(right);

		} catch (Exception e)
		{
			setRightComponent(new JLabel("Unable to display manifest"));
		}

	}
}
