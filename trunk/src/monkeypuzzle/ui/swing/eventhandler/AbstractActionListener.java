package monkeypuzzle.ui.swing.eventhandler;

import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import monkeypuzzle.ui.swing.Mediator;

public abstract class AbstractActionListener implements ActionListener
{
	protected final JFrame mainFrame;
	protected final Mediator mediator;

	/**
	 * @param mainFrame2
	 */
	public AbstractActionListener(final JFrame mainFrame2,
			final Mediator mediator)
	{
		this.mainFrame = mainFrame2;
		this.mediator = mediator;
	}
	
	protected File showSaveDialog()
	{
		final JFileChooser fc = new JFileChooser();
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fc.showSaveDialog(this.mainFrame) == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile();
		else
			return null;
	}
}
