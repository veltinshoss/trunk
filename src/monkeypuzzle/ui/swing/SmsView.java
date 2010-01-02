package monkeypuzzle.ui.swing;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import monkeypuzzle.central.IPhone;
import monkeypuzzle.entity.sqlite.Messages;
import monkeypuzzle.entity.sqlite.Messages.Message;

@SuppressWarnings("serial")
public class SmsView extends JPanel implements SpecialView
{
	private TableRowSorter<TableModel> sorter;
	private JTable table;

	SmsView(final Mediator mediator)
	{
		setLayout(new GridLayout(1, 1));
		this.table = new JTable();
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

	public void setFilter(final Messages.MessageType filter)
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

		List<Message> messages = backupDirectory.getMessages();

		List<Object[]> results = new ArrayList<Object[]>();
		for (Message message : messages)
		{
			results.add(new Object[] { message.getAddress(), message.getText(),
					message.getDate(), message.getFlags() });
		}
		this.table.setModel(new DefaultTableModel(results
				.toArray(new Object[results.size()][]), new String[] {
				"Number", "Message", "Date", "Direction" }) {
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
