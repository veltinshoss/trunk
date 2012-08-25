package com.crypticbit.ipa.ui.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.text.DefaultEditorKit;

import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.error.ErrorInfo;

import com.crypticbit.ipa.About;
import com.crypticbit.ipa.central.BackupDirectoryParser;
import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.IPhone;
import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.central.backupfile.BackupScanner;
import com.crypticbit.ipa.central.backupfile.PlainBackupFile;
import com.crypticbit.ipa.results.Location;
import com.crypticbit.ipa.ui.swing.Mediator.HighlightChangeListener;
import com.crypticbit.ipa.ui.swing.Mediator.SelectedBackupDirectoryChangeListener;
import com.crypticbit.ipa.ui.swing.eventhandler.ExportBackupActionListener;
import com.crypticbit.ipa.ui.swing.eventhandler.OpenBackupActionListener;
import com.crypticbit.ipa.ui.swing.eventhandler.OpenSshActionListener;
import com.crypticbit.ipa.ui.swing.util.TransferActionListener;

@SuppressWarnings("serial")
public class MainFrame extends javax.swing.JFrame implements ErrorHandler,
		HighlightChangeListener {
	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainFrame inst = new MainFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	private JSplitPane mainHorizontalSplit;
	private Mediator mediator = new Mediator(this, this, new LicenceRequestor(
			this));
	private JTree navTree;
	private SearchResultsPane searchResultsPane;

	private JTabbedPane viewPane;
	private JSplitPane mainVerticalSplit;
	private OpenBackupActionListener openBackupActionListener;
	private TransferActionListener actionListener;

	public MainFrame() {
		super("iPhone Analyzer");
		openBackupActionListener = new OpenBackupActionListener(MainFrame.this,
				mediator, false);
		initGUI();
	}

	@Override
	public void clearHighlighting() {
		// Do nothing
	}

	@Override
	public void displayErrorDialog(String message, final Throwable cause) {
		StringBuilder logFileMessage = new StringBuilder();
		String title = "Error";
		String category = "ERROR";
		Level level = Level.ALL;

		logFileMessage
				.append("An unexpected problem has occurred and been logged.\n\nPlease");

		logFileMessage
				.append(" email the following file to support@crypticbit.com to allow the issue to be identified and fixed:\n ");

		// public ErrorInfo(String title, String basicErrorMessage, String
		// detailedErrorMessage, String category, Throwable errorException,
		// Level errorLevel, Map<String, String> state)(Code)

		String basicErrorMessage;
		String detailedErrorMessage = cause.getLocalizedMessage();

		try {
			logFileMessage.append(writeErrorLog(message, cause));
			basicErrorMessage = logFileMessage.toString();
		} catch (IOException e) {
			// use original message
			basicErrorMessage = message;
		}
		org.jdesktop.swingx.JXErrorPane.showDialog(getContentPane(),
				new ErrorInfo(title, basicErrorMessage, detailedErrorMessage,
						category, cause, level, null));
		// causLogFactory.getLogger().log(Level.SEVERE,"Exception",e);
	}

	/**
	 * Creates a temp file with a log of an Error
	 * 
	 * @param message
	 *            - error message
	 * @param cause
	 *            - cause of the error
	 * @return path of the log file
	 * @throws IOException
	 *             if writing the log file fails
	 */
	public static String writeErrorLog(final String message,
			final Throwable cause) throws IOException {
		File f = File.createTempFile("iPhoneAnalzyerErrorLog.", ".log");
		LogFactory.getLogger().log(Level.WARNING,
				"Found issue, logged it to :" + f.getPath());
		PrintStream printStream = new PrintStream(f);

		printStream.print("Date: ");
		printStream.print(new Date());
		printStream.print("\r\nOS Name: ");
		printStream.print(System.getProperty("os.name"));
		printStream.print("\r\nOS Version: ");
		printStream.print(System.getProperty("os.version"));
		printStream.print("\r\nJava Vendor: ");
		printStream.print(System.getProperty("java.vendor"));
		printStream.print("\r\nJava Version: ");
		printStream.print(System.getProperty("java.version"));
		printStream.print("\r\nProduct Version: ");
		printStream.print(About.getVersion());
		printStream.print("\r\nMessage: ");
		printStream.print(message);
		printStream.print("\r\n\r\nCause:\r\n");
		for (Throwable e = cause; e != null; e = e.getCause()) {
			e.printStackTrace(printStream);
		}
		printStream.close();
		return f.getAbsolutePath();
	}

	@Override
	public void highlight(final Collection<Location> locations) {
		// Do nothing
	}

	@Override
	public void moveTo(final Location location) {
		// do nothing
	}

	private void addOpenBackupFileAction(final File backupDirectory,
			final JMenuItem parent) {
		parent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				openFile(backupDirectory);
			}
		});
	}

	private JMenuItem createOpenBackupFileMenu(final File entry) {

		JMenuItem openDefault = new JMenuItem();
		openDefault.setText(getBackupName(entry));

		addOpenBackupFileAction(entry, openDefault);
		return openDefault;
	}

	public String getBackupName(final File entry) {
		try {
			return BackupDirectoryParser.getBackupSummary(entry);
		} catch (FileParseException e1) {
			return "File problem: " + entry.getAbsolutePath();
		} catch (IOException e1) {
			return "Access problem: " + entry.getAbsolutePath();
		}
	}

	private void createFileMenu(final JMenu fileMenu) {
		JMenuItem openFileMenuItem = new JMenuItem();
		fileMenu.add(openFileMenuItem);
		openFileMenuItem.setText("Open: New Backup directory");
		openFileMenuItem.addActionListener(new OpenBackupActionListener(this,
				this.mediator, false));

		JMenu defaultMenu = new JMenu("Open: Default iTunes location");
		File defaultRoot = BackupScanner.getDefaultRoot();

		if (defaultRoot != null && defaultRoot.exists()) {
			for (File entry : defaultRoot.listFiles()) {
				try {
					defaultMenu.add(createOpenBackupFileMenu(entry));
				} catch (Exception e) {
					defaultMenu.add("Unable to create entry for "
							+ entry.getAbsolutePath());
					LogFactory.getLogger().log(
							Level.WARNING,
							"Problem creating entry for "
									+ entry.getAbsolutePath() + " with error "
									+ e.getMessage(), e);
				}
			}
			fileMenu.add(defaultMenu);
		}
		JMenu historyMenu = new JMenu("Open: History");
		for (String backupDirectory : this.mediator.getPreferences()
				.getRecentBackupDirectories()) {
			historyMenu
					.add(createOpenBackupFileMenu(new File(backupDirectory)));
		}
		fileMenu.add(historyMenu);

		JMenu specialOpenMenu = new JMenu("Open: Other");

		JMenuItem openUnpackedFileMenuItem = new JMenuItem();
		specialOpenMenu.add(openUnpackedFileMenuItem);
		openUnpackedFileMenuItem.setText("Open pre-extracted directory");
		openUnpackedFileMenuItem
				.addActionListener(new OpenBackupActionListener(this,
						this.mediator, true));

		JMenuItem openSshMenuItem = new JMenuItem();
		specialOpenMenu.add(openSshMenuItem);
		openSshMenuItem.setText("Open over SSH (beta)");
		openSshMenuItem.addActionListener(new OpenSshActionListener(
				this.mediator));

		JMenuItem openFileFileMenuItem = new JMenuItem();
		specialOpenMenu.add(openFileFileMenuItem);
		openFileFileMenuItem.setText("Open individual file");
		openFileFileMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final JFileChooser fc;

				fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = fc.showOpenDialog(MainFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					MainFrame.this.mediator
							.fireSelectedFileChangeListeners(new PlainBackupFile(
									fc.getSelectedFile()));
				}

			}
		});

		fileMenu.add(specialOpenMenu);

		fileMenu.addSeparator();

		JMenu prefsMenu = new JMenu("Preferences");
		fileMenu.add(prefsMenu);
		JMenuItem mapPrefsMenuItem = new JMenuItem("Preferences");
		prefsMenu.add(mapPrefsMenuItem);
		mapPrefsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				PrefsDialog prefsDialog = new PrefsDialog(MainFrame.this,
						MainFrame.this.mediator);
				prefsDialog.setLocationRelativeTo(MainFrame.this);
				prefsDialog.setVisible(true);
			}
		});

		//

		fileMenu.addSeparator();

		JMenu exportFileMenu = new JMenu("Export");
		fileMenu.add(exportFileMenu);

		JMenuItem exportFileMenuItem = new JMenuItem();
		exportFileMenu.add(exportFileMenuItem);
		exportFileMenuItem.setText("Export all files");
		exportFileMenuItem.addActionListener(new ExportBackupActionListener(
				this.mediator));

		fileMenu.addSeparator();

		JMenuItem exitMenuItem = new JMenuItem();
		fileMenu.add(exitMenuItem);
		exitMenuItem.setText("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				System.exit(0);
			}
		});
	}

	private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look
			// and feel.
		}

		// update view as Frames resize
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
		actionListener = new TransferActionListener();
		try {
			{
				this.searchResultsPane = new SearchResultsPane(this.mediator);
				this.searchResultsPane
						.setPreferredSize(new Dimension(800, 300));
				this.mediator.addHighlightChangeListener(this);
				mainVerticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
						true);
				if (About.showMessage())
					mainVerticalSplit.add(new ProductMessage(),
							JSplitPane.BOTTOM);
				mainVerticalSplit.setResizeWeight(0.9f);
				mainVerticalSplit.setOneTouchExpandable(true);
				final JPanel cards = new JPanel(new CardLayout());
				cards.add(new OpenPanel(mediator, this), "Open");
				cards.add(mainVerticalSplit, "View");
				mediator.addSelectedBackupDirectoryChangeListener(new SelectedBackupDirectoryChangeListener() {

					@Override
					public void backupDirectoryChanged(File directory,
							IPhone backupDirectory) {
						CardLayout cl = (CardLayout) (cards.getLayout());
						cl.show(cards, "View");

					}
				});

				getContentPane().add(cards, BorderLayout.CENTER);
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
					this.viewPane = new MainTabbedPane(this, this.mediator);
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
					fileMenu.setMnemonic(KeyEvent.VK_F);
					{
						createFileMenu(fileMenu);
					}
					// update recent file entries if backup dirs change
					this.mediator
							.addSelectedBackupDirectoryChangeListener(new SelectedBackupDirectoryChangeListener() {
								@Override
								public void backupDirectoryChanged(
										final File directory,
										final IPhone backupDirectory) {
									fileMenu.removeAll();
									createFileMenu(fileMenu);
								}
							});
				}
				mainMenuBar.add(createMenuBar());
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
							public void actionPerformed(final ActionEvent e) {
								SearchDialog searchDialog = new SearchDialog(
										MainFrame.this, MainFrame.this.mediator);
								searchDialog
										.setLocationRelativeTo(MainFrame.this);
								searchDialog.setVisible(true);
								if (searchDialog.getResultsMap() != null) {
									MainFrame.this.searchResultsPane
											.updateSearchResults(searchDialog
													.getResultsMap());
									mainVerticalSplit.add(
											MainFrame.this.searchResultsPane,
											JSplitPane.BOTTOM);
								}
							}
						});
					}
				}

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
							public void actionPerformed(final ActionEvent arg0) {
								AboutDialog.showDialog(MainFrame.this);
							}
						});
						JMenuItem logMenuItem = new JMenuItem();
						helpMenu.add(logMenuItem);
						logMenuItem.setText("Log");
						logMenuItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(final ActionEvent arg0) {
								LogDialog.showDialog(MainFrame.this);
							}
						});
						JMenuItem clearRegMenuItem = new JMenuItem();
						helpMenu.add(clearRegMenuItem);
						clearRegMenuItem.setText("Clear Registration Details");
						clearRegMenuItem
								.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(
											final ActionEvent arg0) {
										Frame frame = (Frame) mediator
												.getMainFrame();

										// default icon, custom title
										int value = JOptionPane
												.showConfirmDialog(
														frame,
														"Do you really want to unregister iPhone Analyzer?",
														"Unregister?",
														JOptionPane.YES_NO_OPTION);

										if (value == JOptionPane.YES_OPTION) {
											mediator.getPreferences()
													.clearRegistrationDetails();
											JOptionPane
													.showMessageDialog(frame,
															"Registration details cleared, this will take effect at next restart.");
										}
									}
								});
					}
				}
			}
		} catch (Exception e) {
			this.mediator.displayErrorDialog("Problem configuring display", e);
		}
	}

	/**
	 * Create an Edit menu to support cut/copy/paste.
	 */
	private JMenuBar createMenuBar() {
		JMenuItem menuItem = null;
		JMenuBar menuBar = new JMenuBar();

		JMenu mainMenu = new JMenu("Edit");
		mainMenu.setMnemonic(KeyEvent.VK_E);

		menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
		menuItem.setActionCommand((String) TransferHandler.getCopyAction()
				.getValue(Action.NAME));
		menuItem.setText("Copy");
		menuItem.setMnemonic(KeyEvent.VK_C);
		menuItem.addActionListener(actionListener);
		mainMenu.add(menuItem);

		menuBar.add(mainMenu);
		return menuBar;
	}

	@Override
	public void displayWarningDialog(String message, Throwable cause) {
		org.jdesktop.swingx.JXErrorPane.showDialog(getContentPane(),
				new ErrorInfo("Warning", message, message, "Warning", cause,
						Level.INFO, null));

	}

	public void openFile(final File backupDirectory) {
		openBackupActionListener.openBackupFile(backupDirectory);
	}

}
