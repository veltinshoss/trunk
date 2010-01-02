package monkeypuzzle.ui.swing;

import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class GuiUtils
{

	/**
	 * Fully expands or collapses a JTree
	 * 
	 * @param tree
	 *            - tree to work on
	 * @param expand
	 *            - true to expand, false to collapse
	 */
	public static void expandJTree(final JTree tree, final boolean expand)
	{
		TreeNode root = (TreeNode) tree.getModel().getRoot();

		// Traverse tree from root
		expandAll(tree, new TreePath(root), expand);
	}

	private static void expandAll(final JTree tree, final TreePath parent,
			final boolean expand)
	{
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0)
		{
			for (Enumeration e = node.children(); e.hasMoreElements();)
			{
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}

		// Expansion or collapse must be done bottom-up
		if (expand)
		{
			tree.expandPath(parent);
		} else
		{
			tree.collapsePath(parent);
		}
	}

}
