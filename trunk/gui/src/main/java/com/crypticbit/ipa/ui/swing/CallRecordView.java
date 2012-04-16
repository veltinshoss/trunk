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
import com.crypticbit.ipa.entity.sqlite.CallHistory;
import com.crypticbit.ipa.entity.sqlite.CallHistory.Call;

@SuppressWarnings("serial")
public class CallRecordView extends JPanel implements SpecialView
{
	private TableRowSorter<TableModel> sorter;
	private JTable table;
	private DisplayConverter displayConverter;

	CallRecordView(final Mediator mediator)
	{
		displayConverter = mediator.getDisplayConverter();
		setLayout(new GridLayout(1, 1));
		this.table = new JTable();
		table.getActionMap().put(
				TransferHandler.getCopyAction().getValue(Action.NAME),
				TransferHandler.getCopyAction());
		this.add(new JScrollPane(this.table));
		init(mediator.getBackupDirectory());
	}

	public void clearFilter()
	{
		this.sorter.setRowFilter(new RowFilter<TableModel, Integer>() {
			@Override
			public boolean include(
					final javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry)
			{
				return true;
			}
		});
	}

	@Override
	public JComponent getComponent()
	{
		return this;
	}

	public void setFilter(final CallHistory.Flags filter)
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

	private void init(final IPhone backupDirectory)
	{

		CallHistory calls = backupDirectory.getByInterface(CallHistory.class);

		List<Object[]> results = new ArrayList<Object[]>();
		for (Call call : calls)
		{
			results.add(new Object[] {
					displayConverter.convertString(backupDirectory.lookupNumber(call.getPhoneNumber())),
					call.getDate(), call.getDuration(), call.getFlags() });
		}
		this.table.setModel(new DefaultTableModel(results
				.toArray(new Object[results.size()][]), new String[] {
				"Number", "Date", "Duration", "Call Direction" }) {
			@Override
			public boolean isCellEditable(final int row, final int column)
			{
				return false;
			}
		});
		this.sorter = new TableRowSorter<TableModel>(this.table.getModel());
		this.table.setRowSorter(this.sorter);
	}
}
