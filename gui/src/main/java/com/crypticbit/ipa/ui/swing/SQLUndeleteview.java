package com.crypticbit.ipa.ui.swing;

import java.awt.BorderLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.io.parser.sqlite.SqlResults;
import com.crypticbit.ipa.results.ContentType;
import com.crypticbit.ipa.results.Location;

public class SQLUndeleteview extends View
{
	
	private DisplayConverter displayConverter;

	public SQLUndeleteview(BackupFile bfd, Mediator mediator)
			throws IOException, FileParseException
	{
		super(bfd, mediator);
		displayConverter = mediator.getDisplayConverter();

	}

	public static class Util
	{

		public static String[] IGNORE_LIST_REGEXS = {

		"CREATE TABLE .*?\\)?\\)", "CREATE TRIGGER .*?; END",
				"CREATE INDEX .*?ON.*?\\)", "tablemsg_piecesmsg_pieces"

		};
		public static Pattern[] KNOWN_SQLite_SYSTEM_PATTERNS = new Pattern[IGNORE_LIST_REGEXS.length];

		static
		{
			for (int i = 0; i < IGNORE_LIST_REGEXS.length; i++)
			{
				KNOWN_SQLite_SYSTEM_PATTERNS[i] = Pattern
						.compile(IGNORE_LIST_REGEXS[i]);
			}
		}

		/**
		 * Copies one file to/over another
		 * 
		 * @param src
		 *            - file to read from
		 * @param dest
		 *            - file to create and write to
		 * @return
		 * @throws IOException
		 */
		public static void copyFile(File src, File dest) throws IOException
		{
			// tmpFile.deleteOnExit();
			FileInputStream fis = new FileInputStream(src);
			FileOutputStream fos = new FileOutputStream(dest);
			byte[] buff = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = fis.read(buff)) > 0)
			{
				fos.write(buff, 0, bytesRead);
			}

			fis.close();
			fos.close();
		}

		public static File copyToTmpFile(File src) throws IOException
		{
			File f = File.createTempFile("sql", ".db");
			copyFile(src, f);
			return f;
		}

		// define the continuous run of valid chars
		private final static int MAX_CHAR_VALUE = 0x7e; // '~'
		private final static int MIN_CHAR_VALUE = 0x20; // space
		private final static String OTHER_CHARSs = "\r\n\t";

		final static Set<Character> VALID_CHARS = new HashSet<Character>();
		static
		{

			for (char c : OTHER_CHARSs.toCharArray())
			{
				VALID_CHARS.add(c);
			}
			for (int i = MIN_CHAR_VALUE; i <= MAX_CHAR_VALUE; i++)
			{
				VALID_CHARS.add((char) i);
			}
		}

		public static Set<String> strings(File f, int minLength)
				throws IOException
		{
			Set<String> strings = new HashSet<String>();

			FileInputStream in = new FileInputStream(f);

			int c;
			StringBuilder stringBuilder = new StringBuilder();
			while ((c = in.read()) != -1)
			{
				Character x = new Character((char) c);
				if (VALID_CHARS.contains(x))
				{
					stringBuilder.append((char) c);
				} else
				{
					if (stringBuilder.length() >= minLength)
					{
						strings.add(stringBuilder.toString());
					}
					stringBuilder = new StringBuilder();
				}
			}

			return strings;
		}

	}

	@Override
	public void clearHighlighting()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void highlight(Collection<Location> locations)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected ContentType getSupportedContentView()
	{
		return ContentType.SQL;
	}

	@Override
	protected boolean shouldBeVisible()
	{
		return getBackupFile().getParsedData() instanceof SqlResults;
	}

	@Override
	protected void init() throws IOException, FileParseException
	{
		final DefaultTableModel tm = new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		tm.addColumn("Index");
		tm.addColumn("Text");
		File srcDbFile;

		try
		{
			srcDbFile = getBackupFile().getContentsFile();

			File tmpFile = Util.copyToTmpFile(srcDbFile);

			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:"
					+ tmpFile.getAbsolutePath());
			Statement stat = conn.createStatement();
			stat.executeUpdate("VACUUM;");

			conn.close();

//			System.out.println("Original File Size: " + srcDbFile.length());
//			System.out.println("Vacuumed File Size: " + tmpFile.length());
//
//			System.out.println(srcDbFile.getAbsolutePath());
//			System.out.println(tmpFile.getAbsolutePath());

			int minStrLen = 8;
			Set<String> allStrings = Util.strings(srcDbFile, minStrLen);
			Set<String> lessStrings = Util.strings(tmpFile, minStrLen);

//			System.out.println("All Strings: " + allStrings.size());
//			System.out.println("Less Strings: " + lessStrings.size());

			allStrings.removeAll(lessStrings);
//			System.out
//					.println("AllStrings - lessStrings: " + allStrings.size());
			int index = 0;
//			for (String s : allStrings)
//			{
//				System.out.println(++index + ": " + s);
//			}

			// get all db setup strings from the db and remove those...
			for (Pattern ignore : Util.KNOWN_SQLite_SYSTEM_PATTERNS)
			{
				Iterator<String> iterator = allStrings.iterator();
				while (iterator.hasNext())
				{
					String s = iterator.next();
					Matcher m = ignore.matcher(s);
					if (m.find())
					{
						iterator.remove();
					}
				}
			}

//			System.out.println("Removed SQLite things: " + allStrings.size());
//			index = 0;
//			for (String s : allStrings)
//			{
//				System.out.println(++index + ": " + s);
//			}

			Iterator<String> iterator = allStrings.iterator();
			while (iterator.hasNext())
			{
				String s = iterator.next();
				Matcher m = Pattern.compile(".?((\n|\r|\t)\\S)+(\r|\n|\t)?",
						Pattern.MULTILINE).matcher(s);
				if (m.matches())
				{
					iterator.remove();
				}
			}
//			System.out.println("Removed anything with alternate whitespace: "
//					+ allStrings.size());
//			index = 0;
			for (String s : allStrings)
			{
				tm.addRow(new String[] { String.valueOf(++index), displayConverter.convertString( s) });

				// System.out.println(index + ": " +
				// TypeFormatter.toHex(s.getBytes()));
			}
		} catch (Exception ee)
		{
			// LogFactory.getLogger().log(Level.ERROR,
			// "Failed to get deleted SQLite fragements", ee);
			ee.printStackTrace();
		}

		this.setLayout(new BorderLayout());
		final JTable table = new JTable(tm);
		table.getActionMap().put(
				TransferHandler.getCopyAction().getValue(Action.NAME),
				TransferHandler.getCopyAction());
		table.getColumnModel().getColumn(0).setWidth(100);

		JScrollPane scrollPane = new JScrollPane(table);
		PopupListener popupListener = new PopupListener(tm, table, scrollPane);
		table.addMouseListener(popupListener);
		table.addMouseMotionListener(popupListener);
		this.add(scrollPane);

	}

	@Override
	protected void moveTo0(Location location)
	{
		// TODO Auto-generated method stub

	}

	private static final class PopupListener extends MouseAdapter
	{
		private final DefaultTableModel tm;
		private final JTable table;
		private Popup lastPopup = null;
		private int lastRow = -1;
		private JTextArea contents;
		private JScrollPane scrollPane;

		private PopupListener(DefaultTableModel tm, JTable table,
				JScrollPane scrollPane)
		{
			this.tm = tm;
			this.table = table;
			this.scrollPane = scrollPane;
		}

		public void mouseExited(MouseEvent e)
		{
			if (lastPopup != null && !table.contains(e.getPoint()))
			{
				lastPopup.hide();
				lastPopup = null;
				lastRow = -1;
			}
		}

		public void mouseMoved(MouseEvent e)
		{
			Point p = e.getPoint();
			int row = table.rowAtPoint(p);
			if (row != lastRow)
			{
				if (lastPopup != null)
					lastPopup.hide();
				lastRow = row;

				contents = new JTextArea((String) tm.getValueAt(row, 1));
				contents.setEditable(false);
				contents.setBorder(BorderFactory.createEtchedBorder());
				lastPopup = PopupFactory.getSharedInstance().getPopup(table,
						contents, MouseInfo.getPointerInfo().getLocation().x,
						MouseInfo.getPointerInfo().getLocation().y);
				lastPopup.show();
			}
		}
	}

}
