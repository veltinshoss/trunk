package com.crypticbit.ipa.ui.swing.app.fileviewer;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;

import javax.swing.JApplet;
import javax.swing.JFrame;

import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.ui.swing.ErrorHandler;
import com.crypticbit.ipa.ui.swing.LicenceRequestor;
import com.crypticbit.ipa.ui.swing.MainTabbedPane;
import com.crypticbit.ipa.ui.swing.Mediator;


@SuppressWarnings("serial")
public class MainFrame extends JApplet implements ErrorHandler
{

	public static void main(final String[] args)
	{
		// Create an instance of the applet class.
		MainFrame applet = new MainFrame();

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
