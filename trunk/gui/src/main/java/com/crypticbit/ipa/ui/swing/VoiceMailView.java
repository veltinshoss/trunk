package com.crypticbit.ipa.ui.swing;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.entity.sqlite.Voicemails;
import com.crypticbit.ipa.entity.sqlite.Voicemails.Voicemail;


public class VoiceMailView extends JPanel implements SpecialView {

	private Mediator mediator;
	private Voicemails v;
	private DisplayConverter displayConverter;

	private JTable table;

	public VoiceMailView(final Mediator mediator) {
		this.mediator = mediator;
		this.displayConverter = mediator.getDisplayConverter();
		this.setLayout(new BorderLayout());

		v = mediator.getBackupDirectory().getByInterface(Voicemails.class);

		TableModel tm = new AbstractTableModel() {

			@Override
			public String getColumnName(int column) {
				switch (column) {
				case 0:
					return "Date";
				case 1:
					return "Return number";
				case 2:
					return "Caller number";
				case 3:
					return "Action";
				default:
					return null;
				}
			}

			@Override
			public int getRowCount() {
				return v.size();
			}

			@Override
			public int getColumnCount() {
				return 3;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Voicemail record = v.get(rowIndex);
				switch (columnIndex) {
				case 0:
					return record.getDateDate();
				case 1:
					return displayConverter.convertNumber(record.getCallbackNumber());
				case 2:
					return displayConverter.convertNumber(record.getSender());
				}
				return "";
			}
		};
		table = new JTable(tm);
		table.getActionMap().put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
		// table.setS
		this.add(new JScrollPane(table), BorderLayout.CENTER);
		JPanel playPanel = new JPanel();
		playPanel.setLayout(new BorderLayout());
		JButton button = new JButton("Play");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {

					for (int row : table.getSelectedRows()) {

						BackupFile bf = getBackupFile(v.get(row));
						if (bf != null)
							Desktop.getDesktop().open(
									bf.restoreFile(com.crypticbit.ipa.io.util.IoUtils
											.createTempDir(".deviceRoot")));
					}
				} catch (Exception ee) {
					LogFactory.getLogger().log(Level.SEVERE,"Exception",ee);
				}

			}
		});
		playPanel.add(button, BorderLayout.CENTER);
		JTextArea message = new JTextArea(
				"Select whichever voicemails you'd like to play and hit \"Play\". "
						+ "You'll need to have a player installed that can play these files.");
		message.setEditable(false);
		playPanel.add(message, BorderLayout.SOUTH);
		this.add(playPanel, BorderLayout.SOUTH);
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	private BackupFile getBackupFile(Voicemail vm) {
		return mediator.getBackupDirectory().getBackupFileFromName(
				"Library/Voicemail/" + vm.getFilenameNumber() + ".amr");
	}

}
