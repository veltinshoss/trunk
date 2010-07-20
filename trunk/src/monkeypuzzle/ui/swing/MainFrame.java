package monkeypuzzle.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import monkeypuzzle.central.BackupDirectoryParser;
import monkeypuzzle.central.FileParseException;
import monkeypuzzle.central.IPhone;
import monkeypuzzle.central.backupfile.PlainBackupFile;
import monkeypuzzle.results.Location;
import monkeypuzzle.ui.swing.Mediator.HighlightChangeListener;
import monkeypuzzle.ui.swing.Mediator.SelectedBackupDirectoryChangeListener;
import monkeypuzzle.ui.swing.eventhandler.ExportBackupActionListener;
import monkeypuzzle.ui.swing.eventhandler.OpenBackupActionListener;
import monkeypuzzle.ui.swing.eventhandler.OpenSshActionListener;

import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.util.OS;

@SuppressWarnings("serial")
public class MainFrame extends javax.swing.JFrame implements ErrorHandler,
		HighlightChangeListener
{
	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(final String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				MainFrame inst = new MainFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	private JSplitPane mainHorizontalSplit;
	private Mediator mediator = new Mediator(this, new LicenceRequestor(this));
	private JTree navTree;
	private SearchResultsPane searchResultsPane;

	private JTabbedPane viewPane;
	private JSplitPane mainVerticalSplit;

	public MainFrame()
	{
		super("iPhone Analyzer by CrypticBit.com");
		initGUI();
	}

	@Override
	public void clearHighlighting()
	{
		// Do nothing
	}

	@Override
	public void displayErrorDialog(final String message, final Throwable cause)
	{
		String logFileMessage = "An unexpected problem has occurred and been logged.\nPlease email the following file to support@crypticbit.com to allow the issue to be identified and fixed:\n " ;
		try {
			logFileMessage  += writeErrorLog(message, cause);
		} catch (IOException e) {
			logFileMessage = message;
		}
		
		org.jdesktop.swingx.JXErrorPane.showDialog(getContentPane(),
				new ErrorInfo("Error", logFileMessage, cause.getLocalizedMessage(),
						"ERROR", cause, Level.ALL, null));
		// cause.printStackTrace();
	}

	/**
	 * Creates a temp file with a log of an Error
	 * @param message - error message
	 * @param cause - cause of the error
	 * @return path of the log file
	 * @throws IOException if writing the log file fails
	 */
	public static String writeErrorLog(final String message, final Throwable cause) throws IOException
	{
		File f = File.createTempFile("iPhoneAnalzyerErrorLog.", ".log");

		PrintStream printStream = new PrintStream(f);

		printStream.print("Date: ");
		printStream.print(new Date());
		printStream.print("\r\nOS: ");
		printStream.print(System.getProperty("os.name"));
		printStream.print("\r\nMessage: ");
		printStream.print(message);
		printStream.print("\r\n\r\nCause:\r\n");
		cause.printStackTrace(printStream);

		printStream.close();
		return f.getAbsolutePath();
	}
	
	@Override
	public void highlight(final Collection<Location> locations)
	{
		// Do nothing
	}

	@Override
	public void moveTo(final Location location)
	{
		// do nothing
	}

	private void addOpenBackupFileAction(final File backupDirectory,
			final JMenuItem parent)
	{
		parent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				OpenBackupActionListener.openBackupFile(MainFrame.this,
						MainFrame.this.mediator, backupDirectory);
			}
		});
	}

	private void createFileMenu(final JMenu fileMenu)
	{
		JMenuItem openFileMenuItem = new JMenuItem();
		fileMenu.add(openFileMenuItem);
		openFileMenuItem.setText("Open Backup directory");
		openFileMenuItem.addActionListener(new OpenBackupActionListener(this,
				this.mediator));
		
		JMenuItem openSshMenuItem = new JMenuItem();
		fileMenu.add(openSshMenuItem);
		openSshMenuItem.setText("Open over SSH (untested)");
		openSshMenuItem.addActionListener(new OpenSshActionListener(this,
				this.mediator));
		
		JMenuItem exportFileMenuItem = new JMenuItem();
		fileMenu.add(exportFileMenuItem);
		exportFileMenuItem.setText("Export Backup directory");
		exportFileMenuItem.addActionListener(new ExportBackupActionListener(
				this, this.mediator));
		JMenu defaultMenu = new JMenu("Default iTunes location");
		File defaultRoot = null;
		System.out.println("OS: " + System.getProperty("os.name"));
		
		if (OS.isWindows())
			defaultRoot = new File(
					System.getProperty("user.home")
							+ "\\Application Data\\Apple Computer\\MobileSync\\Backup\\");
		if (OS.isWindowsVista() || System.getProperty("os.name").equals("Windows 7"))
			defaultRoot = new File(
					System.getProperty("user.home")
							+ "\\AppData\\Roaming\\Apple Computer\\MobileSync\\Backup\\");
		if (OS.isMacOSX())
			defaultRoot = new File(System.getProperty("user.home")
					+ "/Library/Application Support/MobileSync/Backup/");

		// 1. Windows XP Backup Location:\Documents and
		// Settings\(username)\Application Data\Apple
		// Computer\MobileSync\Backup\
		// 2. Windows Vista Backup
		// Location:\Users\(username)\AppData\Roaming\Apple
		// Computer\MobileSync\Backup\
		// 3. OS X Backup Location:~/Library/Application
		// Support/MobileSync/Backup/

		if (defaultRoot != null && defaultRoot.exists())
		{
			for (File entry : defaultRoot.listFiles())
			{
				JMenuItem openDefault = new JMenuItem();
				defaultMenu.add(openDefault);
				try
				{
					openDefault.setText(BackupDirectoryParser
							.getBackupSummary(entry));
				} catch (FileParseException e1)
				{
					openDefault.setText("File problem: "
							+ entry.getAbsolutePath());
				} catch (IOException e1)
				{
					openDefault.setText("Access problem: "
							+ entry.getAbsolutePath());
				}
				addOpenBackupFileAction(entry, openDefault);
			}
			fileMenu.add(defaultMenu);
		}
		JMenu historyMenu = new JMenu("History");
		for (String backupDirectory : this.mediator.getPreferences()
				.getRecentBackupDirectories())
		{
			JMenuItem openHistorical = new JMenuItem();
			historyMenu.add(openHistorical);
			try
			{
				openHistorical.setText(BackupDirectoryParser
						.getBackupSummary(new File(backupDirectory)));
			} catch (FileParseException e1)
			{
				openHistorical.setText("File problem: " + backupDirectory);
			} catch (IOException e1)
			{
				openHistorical.setText("Access problem: " + backupDirectory);
			}
			addOpenBackupFileAction(new File(backupDirectory), openHistorical);
		}
		fileMenu.add(historyMenu);

		fileMenu.add(new JSeparator());
		JMenuItem openFileFileMenuItem = new JMenuItem();
		fileMenu.add(openFileFileMenuItem);
		openFileFileMenuItem.setText("Open file");
		openFileFileMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final JFileChooser fc;

				fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = fc.showOpenDialog(MainFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					MainFrame.this.mediator
							.fireSelectedFileChangeListeners(new PlainBackupFile(
									fc.getSelectedFile()));
				}

			}
		});
		fileMenu.add(new JSeparator());
		JMenuItem exitMenuItem = new JMenuItem();
		fileMenu.add(exitMenuItem);
		exitMenuItem.setText("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				System.exit(0);
			}
		});
	}

	private void initGUI()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// update view as Frames resize
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
		try
		{
			{
				this.searchResultsPane = new SearchResultsPane(this.mediator);
				this.searchResultsPane
						.setPreferredSize(new Dimension(800, 300));
				this.mediator.addHighlightChangeListener(this);
				mainVerticalSplit = new JSplitPane(
						JSplitPane.VERTICAL_SPLIT, true);

				mainVerticalSplit.add(new ProductMessage(), JSplitPane.BOTTOM);
				mainVerticalSplit.setResizeWeight(0.9f);
				mainVerticalSplit.setOneTouchExpandable(true);
				getContentPane().add(mainVerticalSplit, BorderLayout.CENTER);
				{
					{
						this.mainHorizontalSplit = new JSplitPane(
								JSplitPane.HORIZONTAL_SPLIT, true);
						JScrollPane navTreeScrollPane = new JScrollPane(
								ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
								ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
						JTabbedPane browsePanelTabs = new JTabbedPane();
						browsePanelTabs.add("Bookmarks", new TaskPane(
								this.mediator));
						browsePanelTabs.add("File System", navTreeScrollPane);
						JXTitledPanel browsePanel = new JXTitledPanel(
								"Browse Files", browsePanelTabs);
						browsePanel.setPreferredSize(new Dimension(200, 800));

						this.mainHorizontalSplit.add(browsePanel,
								JSplitPane.LEFT);
						{
							this.navTree = new NavigationTree(this.mediator);
							navTreeScrollPane.setViewportView(this.navTree);
						}
					}
				}
				mainVerticalSplit.add(this.mainHorizontalSplit, JSplitPane.TOP);
				{
					this.viewPane = new FilesViewingPane(this,this.mediator);
					JXTitledPanel examinePanel = new JXTitledPanel(
							"Examine Files", this.viewPane);
					this.mainHorizontalSplit
							.add(examinePanel, JSplitPane.RIGHT);
				}
			}
			Toolkit tk = Toolkit.getDefaultToolkit();
			int xSize = ((int) (tk.getScreenSize().getWidth() * 0.9f));
			int ySize = ((int) (tk.getScreenSize().getHeight() * 0.9f));
			setSize(xSize, ySize);
			{
				JMenuBar mainMenuBar = new JMenuBar();
				setJMenuBar(mainMenuBar);
				{
					final JMenu fileMenu = new JMenu();
					mainMenuBar.add(fileMenu);
					fileMenu.setText("File");
					{
						createFileMenu(fileMenu);
					}
					// update recent file entries if backup dirs change
					this.mediator
							.addSelectedBackupDirectoryChangeListener(new SelectedBackupDirectoryChangeListener() {
								@Override
								public void backupDirectoryChanged(
										final File directory,
										final IPhone backupDirectory)
								{
									fileMenu.removeAll();
									createFileMenu(fileMenu);
								}
							});
				}
				{
					JMenu searchMenu = new JMenu();
					mainMenuBar.add(searchMenu);
					searchMenu.setText("Search");
					{
						JMenuItem searchMenuItem = new JMenuItem();
						searchMenu.add(searchMenuItem);
						searchMenuItem.setText("Find");
						searchMenuItem.setAccelerator(KeyStroke.getKeyStroke(
								java.awt.event.KeyEvent.VK_F,
								java.awt.Event.CTRL_MASK));
						searchMenuItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(final ActionEvent e)
							{
								SearchDialog searchDialog = new SearchDialog(
										MainFrame.this, MainFrame.this.mediator);
								searchDialog
										.setLocationRelativeTo(MainFrame.this);
								searchDialog.setVisible(true);
								if (searchDialog.getResultsMap() != null)
								{
									MainFrame.this.searchResultsPane
											.updateSearchResults(searchDialog
													.getResultsMap());
									mainVerticalSplit
									.add(MainFrame.this.searchResultsPane, JSplitPane.BOTTOM);
								}
							}
						});
					}
				}
				// {
				// JMenu editMenu = new JMenu();
				// mainMenuBar.add(editMenu);
				// editMenu.setText("Edit");
				// {
				// JMenuItem cutMenuItem = new JMenuItem();
				// editMenu.add(cutMenuItem);
				// cutMenuItem.setText("Cut");
				// }
				// {
				// JMenuItem copyMenuItem = new JMenuItem();
				// editMenu.add(copyMenuItem);
				// copyMenuItem.setText("Copy");
				// }
				// {
				// JMenuItem pasteMenuItem = new JMenuItem();
				// editMenu.add(pasteMenuItem);
				// pasteMenuItem.setText("Paste");
				// }
				// {
				// editMenu.add(new JSeparator());
				// }
				// {
				// JMenuItem deleteMenuItem = new JMenuItem();
				// editMenu.add(deleteMenuItem);
				// deleteMenuItem.setText("Delete");
				// }
				// }
				{
					JMenu helpMenu = new JMenu();
					mainMenuBar.add(helpMenu);
					helpMenu.setText("Help");
					{
						JMenuItem helpMenuItem = new JMenuItem();
						helpMenu.add(helpMenuItem);
						helpMenuItem.setText("About");
						helpMenuItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(final ActionEvent arg0)
							{
								AboutDialog.showDialog(MainFrame.this);
							}
						});
					}
				}
			}
		} catch (Exception e)
		{
			this.mediator.displayErrorDialog("Problem configuring display", e);
		}
	}
}
