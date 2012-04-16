package com.crypticbit.ipa.ui.swing.app.fileviewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.central.backupfile.PlainBackupFile;
import com.crypticbit.ipa.ui.swing.ErrorHandler;
import com.crypticbit.ipa.ui.swing.LicenceRequestor;
import com.crypticbit.ipa.ui.swing.MainTabbedPane;
import com.crypticbit.ipa.ui.swing.Mediator;


@SuppressWarnings("serial")
public class FileViewerApplet extends JApplet implements ErrorHandler
{

	/**
	 * @author Leo
	 */
	private enum Button
	{

		OPEN("Open", "Open", "Open", "Open Iphone Files")
		{
			@Override
			public void act(final Mediator mediator)
			{
				File lastSelectedDir = null;
				final JFileChooser fc;
				if (lastSelectedDir == null)
				{
					fc = new JFileChooser();
				} else
				{
					fc = new JFileChooser(lastSelectedDir);
				}

				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					try
					{
						lastSelectedDir = fc.getSelectedFile();
						start(mediator, lastSelectedDir);
					} catch (Exception e1)
					{
						mediator.displayErrorDialog(
								"Problem opening directory", e1);
					}
				}

			}
		};
		private String actionCommand;
		private String iconName;
		private String name;
		private String toolTipText;

		Button(final String name, final String iconName,
				final String actionCommand, final String toolTipText)
		{
			this.name = name;
			this.iconName = iconName;
			this.actionCommand = actionCommand;
			this.toolTipText = toolTipText;
		}

		public abstract void act(Mediator mediator);

		String getActionCommand()
		{
			return this.actionCommand;
		}

		String getIconName()
		{
			return this.iconName;
		}

		String getName()
		{
			return this.name;
		}

		String getToolTipText()
		{
			return this.toolTipText;
		}
	}

	public static void main(final String[] args)
	{
		// Create an instance of the applet class.
		FileViewerApplet applet = new FileViewerApplet();

		// Send the applet an init() message.
		applet.init();

		// Construct a JFrame.
		final JFrame frame = new JFrame("IPhone File Viewer");

		// Transfer the applet's context pane to the JFrame.
		frame.setContentPane(applet.getContentPane());

		// Transfer the applet's menu bar into the JFrame.
		// This line can be omitted if the applet
		// does not create a menu bar.
		frame.setJMenuBar(applet.getJMenuBar());

		// Add a window listener to the frame for shutting
		// down the application.
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e)
			{
				frame.dispose();
				System.exit(0);
			}
		});

		// Set the size of the frame.
		// To pack the frame as tightly as possible
		// replace the setSize() message with the following.
		// frame.pack();
		frame.setSize(600, 600);

		// Show the frame.
		frame.setVisible(true);

		// Invoke the applet's start() method.
		// This line can be omitted if the applet
		// does not define a start method.
		applet.start();

	}

	public static void start(final Mediator mediator, final File f)
	{
		java.security.AccessController
				.doPrivileged(new java.security.PrivilegedAction<Object>() {
					public Object run()
					{

						mediator
								.fireSelectedFileChangeListeners(new PlainBackupFile(
										f));
						return null;
					}
				});
	}

	private Mediator mediator;

	@Override
	public void displayErrorDialog(final String message, final Throwable cause)
	{
		// org.jdesktop.swingx.JXErrorPane.showDialog(this.getContentPane(),
		// new ErrorInfo("Error", message, cause.getLocalizedMessage(),
		// "ERROR", cause, Level.ALL, null));
		LogFactory.getLogger().log(Level.SEVERE,"Exception",cause);
	}

	/**
	 * Auto-generated main method to display this JFrame
	 */
	@Override
	public void init()
	{
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
		this.mediator = new Mediator(this,this, new LicenceRequestor(
				getParentFrame()));
		// FIXME null
		this.add(new MainTabbedPane(null,this.mediator));

		JToolBar toolBar = new JToolBar();
		addButtons(toolBar);
		add(toolBar, BorderLayout.PAGE_START);

	}

	protected void addButtons(final JToolBar toolBar)
	{
		JButton button = makeNavigationButton(Button.OPEN);
		toolBar.add(button);
	}

	protected JButton makeNavigationButton(final Button buttona)
	{
		// Look for the image.
		String imgLocation = "/icons/" + buttona.getIconName() + "24.gif";
		URL imageURL = FileViewerApplet.class.getResource(imgLocation);

		// Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(buttona.getActionCommand());
		button.setToolTipText(buttona.getToolTipText());
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0)
			{
				buttona.act(FileViewerApplet.this.mediator);
			}
		});

		if (imageURL != null)
		{ // image found
			button.setIcon(new ImageIcon(imageURL, buttona.getName()));
		} else
		{ // no image found
			button.setText(buttona.getName());
			LogFactory.getLogger().log(Level.WARNING,"Resource not found: " + imgLocation);
		}

		return button;
	}

	private Frame getParentFrame()
	{
		Component c = this; // Applet
		while ((c != null) && !(c instanceof Frame))
		{
			c = c.getParent();
		}
		return (Frame) c;
	}

	@Override
	public void displayWarningDialog(String message, Throwable cause) {
		LogFactory.getLogger().log(Level.SEVERE,"Exception",cause);
		
	}

}
