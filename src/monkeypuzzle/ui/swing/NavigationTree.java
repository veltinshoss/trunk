/**
 * 
 */
package monkeypuzzle.ui.swing;

import java.io.File;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import monkeypuzzle.central.IPhone;
import monkeypuzzle.ui.swing.BackupDirectoryTreeModel.Node;
import monkeypuzzle.ui.swing.Mediator.SelectedBackupDirectoryChangeListener;

@SuppressWarnings("serial")
final class NavigationTree extends JTree implements
		SelectedBackupDirectoryChangeListener
{
	private final Mediator mediator;

	NavigationTree(final Mediator mediator)
	{
		super(new DefaultMutableTreeNode());
		this.mediator = mediator;
		addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(final TreeSelectionEvent e)
			{
				if (((Node) e.getPath().getLastPathComponent()).getBfd() != null)
				{
					mediator.fireSelectedFileChangeListeners(((Node) e
							.getPath().getLastPathComponent()).getBfd());
				}

			}
		});
		setRootVisible(false);
		setShowsRootHandles(true);
		mediator.addSelectedBackupDirectoryChangeListener(this);
	}

	@Override
	public void backupDirectoryChanged(final File directory,
			final IPhone backupDirectory)
	{
		setModel(new BackupDirectoryTreeModel(this.mediator));
		this.repaint();
	}
}