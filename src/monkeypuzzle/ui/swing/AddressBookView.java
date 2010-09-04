package monkeypuzzle.ui.swing;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
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

import org.jdesktop.swingx.JXTitledPanel;

@SuppressWarnings("serial")
public class AddressBookView extends JPanel implements SpecialView {

	private final class NonBlankComparator implements Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
			System.out.println("C:" + o1 + "," + o2);
			if (o1 == o2)
				return 0;
			if (o1 == null || o1.length() == 0)
				return 1;
			if (o2 == null || o2.length() == 0)
				return -1;

			return o1.compareTo(o2);

		}
	}

	private class ContactPane extends JPanel {
		// private JTextArea text = new JTextArea();
		private JPanel summaryPanel = new JPanel();
		private JTextArea detailsText = new JTextArea();
		private JScrollPane summaryScroll = new JScrollPane(summaryPanel);
		private JScrollPane detailScroll = new JScrollPane(detailsText);
		private JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				true, new JXTitledPanel("Summary", summaryScroll),
				new JXTitledPanel("Detail", detailScroll));

		ContactPane() {
			this.setLayout(new GridLayout(1, 1));
			split.setDividerLocation(0.8f);
			split.setOneTouchExpandable(true);
			split.setResizeWeight(1);
			summaryPanel
					.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
			this.add(split);
		}

		public void show(final AddressBook.Person person) {
			summaryPanel.removeAll();
			JLabel ta = new JLabel(((person.getFirstName() == null) ? ""
					: (person.getFirstName() + " "))
					+ ((person.getLastName() == null) ? "" : person
							.getLastName()));
			ta.setFont(new Font("Arial", Font.BOLD, 18));
			summaryPanel.add(ta);
			summaryPanel.add(Box.createVerticalStrut(20));
			new JSeparator(SwingConstants.HORIZONTAL);
			AddressBookImage addressBookImage = (AddressBookImage) imageLookup
					.get(person.getRowId());
			if (addressBookImage != null) {
				JLabel imageLabel = new JLabel(AddressBookView
						.getImageWithMaxHeight(new ImageIcon(addressBookImage
								.getImageData()), 100), JLabel.LEFT);
				summaryPanel.add(imageLabel);
				summaryPanel.add(Box.createVerticalStrut(10));
			}
			for (ContactItem ci : person.getContactDetails())
				summaryPanel.add(new JLabel(ci.getLabel() + " "
						+ ci.getContactType() + ": " + ci.getValue()));
			summaryPanel.add(Box.createVerticalGlue());
			detailsText.setText(person.toString());
			summaryScroll.validate();
			detailScroll.validate();
			split.resetToPreferredSizes();
			split.revalidate();
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
		split.add(new JXTitledPanel("Contacts", new JScrollPane(this.table)),
				JSplitPane.LEFT);

		split.add(this.contactPane, JSplitPane.RIGHT);
		this.add(split);
		System.out.println("Starting images");
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
							setIcon(getImageWithMaxHeight(imageIcon, ROW_HEIGHT));

						} else {
							setIcon(null);
						}
						return this;
					}

				});
		init(mediator.getBackupDirectory());
		System.out.println("Finsihed");
	}

	private static ImageIcon getImageWithMaxHeight(ImageIcon imageIcon,
			int maxHeight) {
		return new ImageIcon(imageIcon.getImage()
				.getScaledInstance(
						(int) (((float) maxHeight / (float) imageIcon
								.getIconHeight()) * imageIcon.getIconWidth()),
						maxHeight, Image.SCALE_FAST));
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
			results.add(new Object[] { makeNonNull(person.getFirstName()),
					makeNonNull(person.getLastName()),
					person.getOrganization(),
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
		this.sorter.setComparator(0, new NonBlankComparator());
		this.sorter.setComparator(1, new NonBlankComparator());
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

	private String makeNonNull(String value) {
		return value == null ? "" : value;
	}
}
