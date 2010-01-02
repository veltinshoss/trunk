/**
 * 
 */
package monkeypuzzle.ui.swing.eventhandler;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import monkeypuzzle.ui.swing.Mediator;

public final class ExportBackupActionListener extends AbstractActionListener 
{


	public ExportBackupActionListener(JFrame mainFrame, Mediator mediator)
	{
		super(mainFrame, mediator);
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		File returnVal = showSaveDialog();
		if (returnVal != null)
		{
			try
			{
				this.mediator.getBackupDirectory().restoreDirectory(
						returnVal);
			} catch (IOException ioe)
			{
				this.mediator.displayErrorDialog(
						"Unable to write files to directory", ioe);
			}
		}
	}


}