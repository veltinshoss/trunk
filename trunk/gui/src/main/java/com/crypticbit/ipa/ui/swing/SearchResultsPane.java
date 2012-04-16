package com.crypticbit.ipa.ui.swing;

import java.awt.GridLayout;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXTitledPanel;

import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.results.Location;

@SuppressWarnings("serial")
public class SearchResultsPane extends JXTitledPanel
{
	private final class LocationTreeNode extends DefaultMutableTreeNode
	{
		private LocationTreeNode(final Location location)
		{
			super(location);
		}

		public BackupFile getBackupFile()
		{
			return getLocation().getBackupFile();
		}

		public Location getLocation()
		{
			return ((Location) getUserObject());
		}
	}

	static final String defaultTitle = "Search Results";

	private Mediator mediator;

	private JScrollPane scrollPane = new JScrollPane();

	SearchResultsPane(final Mediator mediator)
	{
		super(defaultTitle);
		this.mediator = mediator;
		getContentContainer().setLayout(new GridLayout(1, 1));
		getContentContainer().add(this.scrollPane);
	}

	public void updateSearchResults(final Map<BackupFile, Set<Location>> results)
	{

		int docCount = 0; // the number of documents containing hits
		int hitCount = 0; // the total number of hits, maybe more than 1 per
		// doc

		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		for (Map.Entry<BackupFile, Set<Location>> entry : results.entrySet())
		{
			DefaultMutableTreeNode element = new DefaultMutableTreeNode(entry
					.getKey().getCompleteOriginalFileName());
			for (Location location : entry.getValue())
			{
				element.add(new LocationTreeNode(location));
				hitCount++;
			}
			root.add(element);
		}

		docCount = root.getChildCount();

		setTitle(defaultTitle + " - Found " + hitCount + " hits in " + docCount
				+ " files" + ".");

		final JTree tree = new JTree(root);
		tree.getActionMap().put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(final TreeSelectionEvent e)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();

				if (node != null)
				{
					if (node instanceof LocationTreeNode)
					{
						SearchResultsPane.this.mediator
								.fireHighlightChangeListeners(
										((LocationTreeNode) node)
												.getBackupFile(), results
												.get(((LocationTreeNode) node)
														.getBackupFile()),((LocationTreeNode) node)
														.getLocation());

					}
				}

			}
		});
		this.scrollPane.setViewportView(tree);
	}
}
