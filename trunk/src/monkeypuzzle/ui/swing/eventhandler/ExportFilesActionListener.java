/**
 * 
 */
package monkeypuzzle.ui.swing.eventhandler;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JFrame;

import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.ui.swing.Mediator;

public final class ExportFilesActionListener extends AbstractActionListener
{
	private Collection<BackupFile> files;

	public ExportFilesActionListener(JFrame mainFrame, Mediator mediator, Collection<BackupFile> files)
	{
		super(mainFrame, mediator);
		this.files = files;
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		File directory = showSaveDialog();
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