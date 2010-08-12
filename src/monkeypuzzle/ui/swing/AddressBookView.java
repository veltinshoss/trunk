package monkeypuzzle.ui.swing;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import monkeypuzzle.central.IPhone;
import monkeypuzzle.entity.sqlite.AddressBook;
import monkeypuzzle.entity.sqlite.AddressBookImages;
import monkeypuzzle.entity.sqlite.AddressBook.Person;
import monkeypuzzle.entity.sqlite.AddressBook.Person.ContactItem;
import monkeypuzzle.entity.sqlite.AddressBookImages.AddressBookImage;

@SuppressWarnings("serial")
public class AddressBookView extends JPanel implements SpecialView {
	private class ContactPane extends JPanel {
		// private JTextArea text = new JTextArea();
		private JPanel panel = new JPanel();
		private JScrollPane scroll = new JScrollPane(panel);

		ContactPane() {
			setLayout(new GridLayout(1, 1));
			this.add(this.scroll);
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		}

		public void show(final AddressBook.Person person) {
			panel.removeAll();
			JTextArea ta = new JTextArea(((person.getFirstName() == null) ? ""
					: (person.getFirstName() + " "))
					+ person.getLastName() == null ? "" : person.getLastName());
			ta.setFont(new Font("Arial", Font.BOLD, 18));
			panel.add(ta);
			panel.add(Box.createVerticalStrut(20));
			AddressBookImage addressBookImage = (AddressBookImage) imageLookup
					.get(person.getRowId());
			if (addressBookImage != null)
				panel.add(new JLabel(new ImageIcon(addressBookImage
						.getImageData())));
			for (ContactItem ci : person.getContactDetails())
				panel.add(new JTextArea(ci.getLabel() + " "
						+ ci.getContactType() + ": " + ci.getValue()));
			panel.add(Box.createVerticalGlue());
			panel.add(Box.createVerticalStrut(20));
			panel.add(Box.createVerticalGlue());
			panel.add(new JTextArea(person.toString()));
			panel.add(Box.createVerticalGlue());
			this.invalidate();
			this.revalidate();
			this.repaint();
		}
	}

	private static final int ROW_HEIGHT = 60;

	private ContactPane contactPane = new ContactPane();
	private List<AddressBook.Person> people;

	private TableRowSorter<TableModel> sorter;

	private JTable table;

	private Map<Integer, AddressBookImage> imageLookup;

	AddressBookView(final Mediator mediator) {
		setLayout(new GridLayout(1, 1));
		this.table = new JTable() {
			@Override
			public Class<?> getColumnClass(final int column) {
				if (column == 3)
					return AddressBookImages.class;
				else
					return String.class;
			}
		};
		this.table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(
							final ListSelectionEvent selectionEvent) {
						int viewRow = AddressBookView.this.table
								.getSelectedRow();
						if (viewRow >= 0) {
							JTable table = AddressBookView.this.table;
							int selectedPersonIndex = table
									.convertRowIndexToModel(viewRow);
							Person person = AddressBookView.this.people
									.get(selectedPersonIndex);
							AddressBookView.this.contactPane.show(person);
						}

					}
				});
		this.table.setRowSelectionAllowed(true);
		this.table.setColumnSelectionAllowed(false);
		this.table.setCellSelectionEnabled(false);
		this.table.setRowHeight(ROW_HEIGHT);
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.add(new JScrollPane(this.table), JSplitPane.LEFT);

		split.add(this.contactPane, JSplitPane.RIGHT);
		this.add(split);
		this.table.setDefaultRenderer(AddressBookImages.class,
				new DefaultTableCellRenderer() {
					@Override
					public Component getTableCellRendererComponent(
							final JTable table, final Object value,
							final boolean isSelected, final boolean hasFocus,
							final int row, final int column) {
						if (value != null) {
							ImageIcon imageIcon = new ImageIcon(
									((AddressBookImage) value).getImageData());
							setIcon(new ImageIcon(
									imageIcon
											.getImage()
											.getScaledInstance(
													(int) (((float) ROW_HEIGHT / (float) imageIcon
															.getIconHeight()) * imageIcon
															.getIconWidth()),
													ROW_HEIGHT,
													Image.SCALE_FAST)));
						} else {
							setIcon(null);
						}
						return this;
					}
				});
		init(mediator.getBackupDirectory());
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	private void init(final IPhone backupDirectory) {

		this.people = backupDirectory.getAddressBook();
		List<AddressBookImage> images = backupDirectory.getAddressBookImages();
		if (images == null)
			images = Collections.emptyList();

		imageLookup = new HashMap<Integer, AddressBookImage>();
		for (AddressBookImage image : images) {
			imageLookup.put(image.getRecordId(), image);
		}

		List<Object[]> results = new ArrayList<Object[]>();
		for (AddressBook.Person person : this.people) {
			results.add(new Object[] { person.getFirstName(),
					person.getLastName(), person.getOrganization(),
					imageLookup.get(person.getRowId()) });

		}
		this.table.setModel(new DefaultTableModel(results
				.toArray(new Object[results.size()][]), new String[] { "First",
				"Last", "Organisation", "Photo" }) {
			@Override
			public boolean isCellEditable(final int row, final int column) {
				return false;
			}
		});
		this.sorter = new TableRowSorter<TableModel>(this.table.getModel());
		this.table.setRowSorter(this.sorter);

		// set default sort order
		List<RowSorter.SortKey> sortKeys = new LinkedList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING)); // sort
		// by
		// last
		// name
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING)); // then
		// by
		// first
		// name
		this.sorter.setSortKeys(sortKeys);
		this.sorter.sort();
	}
}
