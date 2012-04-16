/**
 * 
 */
package com.crypticbit.ipa.ui.swing.eventhandler;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.ui.swing.Mediator;


public final class ExportFilesActionListener extends AbstractActionListener
{
	private Collection<BackupFile> files;

	public ExportFilesActionListener(Mediator mediator, Collection<BackupFile> files)
	{
		super(mediator);
		this.files = files;
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		File directory = showSaveDialog(true);
		if (directory != null )
		{
			try
			{
				this.mediator.getBackupDirectory().restoreFiles(files, directory);
			} catch (IOException ioe)
			{
				this.mediator.displayErrorDialog(
						"Unable to write files to directory", ioe);
			}
		}
	}
}