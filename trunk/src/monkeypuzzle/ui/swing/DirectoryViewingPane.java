package monkeypuzzle.ui.swing;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import monkeypuzzle.central.BackupFileType;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.ui.swing.eventhandler.ExportFilesActionListener;

public class DirectoryViewingPane extends ViewingPane
{

	public DirectoryViewingPane(JFrame mainFrame, String directory, final Mediator mediator)
	{
		final List<BackupFile> files = mediator.getBackupDirectory()
				.getBackupFileByType(BackupFileType.MEDIA);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(new ThumbnailView(mediator,files),
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel exportPane = new JPanel();
		JButton exportButton = new JButton("Export all");
		exportButton.addActionListener(new ExportFilesActionListener(mainFrame,mediator,files));
		exportPane.add(exportButton);
		panel.add(scrollPane,BorderLayout.CENTER);
		panel.add(exportPane,BorderLayout.SOUTH);
		this.add("Images", panel);
	}

	@Override
	public void cleanUp()
	{
		// do nothing.

	}

}
