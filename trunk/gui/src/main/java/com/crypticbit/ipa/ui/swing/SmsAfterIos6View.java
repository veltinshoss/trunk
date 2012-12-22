package com.crypticbit.ipa.ui.swing;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.crypticbit.ipa.central.IPhone;
import com.crypticbit.ipa.entity.sqlite.MessageDirection;
import com.crypticbit.ipa.entity.sqlite.MessageAfterIos6;

@SuppressWarnings("serial")
public class SmsAfterIos6View extends JPanel implements SpecialView {
    private TableRowSorter<TableModel> sorter;
    private JTable table;
    private DisplayConverter displayConverter;

    SmsAfterIos6View(final Mediator mediator) {
	displayConverter = mediator.getDisplayConverter();
	setLayout(new GridLayout(1, 1));
	this.table = new JTable();
	this.add(new JScrollPane(this.table));

	table.setTransferHandler(new CSVTransferHandler());
	table.getActionMap().put(
		TransferHandler.getCopyAction().getValue(Action.NAME),
		TransferHandler.getCopyAction());

	init(mediator.getBackupDirectory());
    }

    public void clearFilter() {
	this.sorter.setRowFilter(new RowFilter<TableModel, Integer>() {
	    @Override
	    public boolean include(
		    final javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
		return true;
	    }
	});
    }

    @Override
    public JComponent getComponent() {
	return this;
    }

	public void setFilter(final MessageDirection filter)
	{
		this.sorter.setRowFilter(new RowFilter<TableModel, Integer>() {
			@Override
			public boolean include(
					final javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry)
			{
				return entry.getValue(3) == filter;
			}
		});
	}

    private void init(final IPhone backupDirectory) {

	List<com.crypticbit.ipa.entity.sqlite.MessageAfterIos6.Message> messages = backupDirectory
		.getByInterface(MessageAfterIos6.class);

	List<Object[]> results = new ArrayList<Object[]>();
	for (com.crypticbit.ipa.entity.sqlite.MessageAfterIos6.Message message : messages) {
	    results.add(new Object[] {
		    backupDirectory.lookupNumber(message.getHandle() == null ? "<none>"
			    : displayConverter.convertNumber(message
				    .getHandle().getId())),
		    displayConverter.convertString(message.getText()),
		    message.getDate(), message.getDirection(), message.getService()
		     });
	}
	this.table.setModel(new DefaultTableModel(results
		.toArray(new Object[results.size()][]), new String[] {
		"Number", "Message", "Date", "Direction","Service" }) {
	    @Override
	    public boolean isCellEditable(final int row, final int column) {
		return false;
	    }
	});
	this.sorter = new TableRowSorter<TableModel>(this.table.getModel());
	this.table.setRowSorter(this.sorter);
    }

}
