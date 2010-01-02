package monkeypuzzle.ui.swing.plist;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.io.parser.plist.PListContainer;
import monkeypuzzle.io.parser.plist.PListLocation;
import monkeypuzzle.results.ContentType;
import monkeypuzzle.results.Location;
import monkeypuzzle.ui.swing.Constants;
import monkeypuzzle.ui.swing.GuiUtils;
import monkeypuzzle.ui.swing.Mediator;
import monkeypuzzle.ui.swing.Mediator.HighlightChangeListener;

public class PListPanel extends JPanel implements HighlightChangeListener
{
	abstract class ReadOnlyAsbtractTreeModel implements TreeModel
	{
		private TreeNode root;

		ReadOnlyAsbtractTreeModel(final TreeNode root)
		{
			this.root = root;
		}

		@Override
		public void addTreeModelListener(final TreeModelListener l)
		{
		}

		@Override
		public Object getRoot()
		{
			return this.root;
		}

		@Override
		public void removeTreeModelListener(final TreeModelListener l)
		{
		}

		@Override
		public void valueForPathChanged(final TreePath path,
				final Object newValue)
		{
		}
	}

	private Set<PListContainer> highlightLocation = new HashSet<PListContainer>();
	private DefaultTreeNode rootTreeNode;
	private JTree tree = new JTree(new Object[] {});
	private DefaultTreeModel treeModel;

	public PListPanel(final PListContainer plist, final Mediator mediator)
			throws IOException, FileParseException
	{
		this.tree.setCellRenderer(new DefaultTreeCellRenderer() {
			private final Color defaultBackgroundColor = UIManager
					.getColor("Tree.textBackground");

			@Override
			public Component getTreeCellRendererComponent(final JTree tree,
					final Object value, final boolean sel,
					final boolean expanded, final boolean leaf, final int row,
					final boolean hasFocus)
			{
				DefaultTreeNode node = (DefaultTreeNode) value;
				DefaultTreeCellRenderer c = (DefaultTreeCellRenderer) super
						.getTreeCellRendererComponent(tree, value, sel,
								expanded, leaf, row, hasFocus);
				if ((PListPanel.this.highlightLocation != null)
						&& (node.getContents() != null)
						&& PListPanel.this.highlightLocation.contains(node
								.getContents()))
				{
					c
							.setBackgroundNonSelectionColor(Constants.HIGHLIGHT_COLOUR);
				} else
				{
					c
							.setBackgroundNonSelectionColor(this.defaultBackgroundColor);
				}
				return c;
			}
		});
		setLayout(new GridLayout(1, 1));
		this.rootTreeNode = plist.wrap(new PListTreeNodeWrapper());
		this.treeModel = new DefaultTreeModel(this.rootTreeNode);
		this.tree.setModel(this.treeModel);
		this.tree.setEditable(false);
		GuiUtils.expandJTree(this.tree, true);

		this.add(new JScrollPane(this.tree));
	}

	@Override
	public void clearHighlighting()
	{
		this.highlightLocation = new HashSet<PListContainer>();
	}

	@Override
	public void highlight(final Collection<Location> locations)
	{
		for (Location l : locations)
		{
			if ((l.getContentType() == ContentType.PLIST)
					&& (((PListLocation) l).getContainer() != null))
			{
				this.highlightLocation.add(((PListLocation) l).getContainer());
			}
		}
	}

	@Override
	public void moveTo(final Location location)
	{
		DefaultTreeNode node = this.rootTreeNode
				.findNode(((PListLocation) location).getContainer());
		this.tree.scrollPathToVisible(new TreePath(this.treeModel
				.getPathToRoot(node)));
	}

}
