/**
 * 
 */
package com.crypticbit.ipa.ui.swing.eventhandler;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import com.crypticbit.ipa.ui.swing.Mediator;


public final class ExportBackupActionListener extends AbstractActionListener 
{


	public ExportBackupActionListener(Mediator mediator)
	{
		super(mediator);
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		File returnVal = showSaveDialog(true);
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