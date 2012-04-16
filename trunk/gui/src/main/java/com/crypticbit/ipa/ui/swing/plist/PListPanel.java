package com.crypticbit.ipa.ui.swing.plist;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.backupfile.PlainBackupFile;
import com.crypticbit.ipa.io.parser.plist.PListContainer;
import com.crypticbit.ipa.io.parser.plist.PListLocation;
import com.crypticbit.ipa.io.parser.plist.PListPrimitive;
import com.crypticbit.ipa.io.util.IoUtils;
import com.crypticbit.ipa.results.ContentType;
import com.crypticbit.ipa.results.Location;
import com.crypticbit.ipa.ui.swing.Constants;
import com.crypticbit.ipa.ui.swing.GuiUtils;
import com.crypticbit.ipa.ui.swing.Mediator;
import com.crypticbit.ipa.ui.swing.Mediator.HighlightChangeListener;
import com.crypticbit.ipa.ui.swing.plist.PListTreeNodeWrapper.PrimitiveTreeNode;

public class PListPanel extends JPanel implements HighlightChangeListener
{

	private Set<PListContainer> highlightLocation = new HashSet<PListContainer>();
	private DefaultTreeNode rootTreeNode;
	private JTree tree = new JTree(new Object[] {});
	private DefaultTreeModel treeModel;
	private Mediator mediator;

	public PListPanel(final PListContainer plist, final Mediator mediator)
			throws IOException, FileParseException
	{
		this.mediator = mediator;
		tree.getActionMap().put(
				TransferHandler.getCopyAction().getValue(Action.NAME),
				TransferHandler.getCopyAction());
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
						.getTreeCellRendererComponent(tree,
								leaf ? mediator.getDisplayConverter()
										.convertString(value.toString())
										: value, sel, expanded, leaf, row,
								hasFocus);
				if ((PListPanel.this.highlightLocation != null)
						&& (node.getContents() != null)
						&& PListPanel.this.highlightLocation.contains(node
								.getContents()))
				{
					c.setBackgroundNonSelectionColor(Constants.HIGHLIGHT_COLOUR);
					c.setForeground(Color.RED);

				} else
				{
					c.setBackgroundNonSelectionColor(this.defaultBackgroundColor);
				}
				return c;
			}
		});
		setLayout(new GridLayout(1, 1));
		this.rootTreeNode = plist.wrap(new PListTreeNodeWrapper());
		this.treeModel = new DefaultTreeModel(this.rootTreeNode);
		this.tree.setModel(this.treeModel);
		this.tree.setEditable(false);

		MouseListener popupListener = new PopupListener();
		tree.addMouseListener(popupListener);
		GuiUtils.expandJTree(this.tree, true);

		this.add(new JScrollPane(this.tree));
	}

	/**
	 * Add the cut/copy/paste actions to the action map.
	 */
	private void setMappings(JList list)
	{
		ActionMap map = list.getActionMap();

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

	class PopupListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e)
		{
			if (e.isPopupTrigger())
			{
				TreePath path = tree.getPathForLocation(e.getX(), e.getY());
				final TreeNode node = (TreeNode) path.getLastPathComponent();
				if (node != null)
				{
					if (node instanceof PrimitiveTreeNode)
					{
						final PrimitiveTreeNode prn = (PrimitiveTreeNode) node;
						if (((PListPrimitive) prn.getContents()).getPrimitive() instanceof byte[])
						{
							JPopupMenu popupMenu = new JPopupMenu();
							JMenuItem mi = new JMenuItem("Open "
									+ prn.getContext() + " as file");
							popupMenu.add(mi);
							mi.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent e)
								{
									FileOutputStream fos = null;
									try
									{
										File f = IoUtils.getExtractFile(prn
												.getContext());
										fos = new FileOutputStream(f);
										fos.write((byte[]) ((PListPrimitive) prn
												.getContents()).getPrimitive());
										mediator.fireSelectedFileChangeListeners(new PlainBackupFile(
												f));
									} catch (Exception ee)
									{
										mediator.displayWarningDialog(
												"Unable to extract or open file",
												ee);
									} finally
									{
										if (fos != null)
											try
											{
												fos.close();
											} catch (IOException e1)
											{
												// do nothing
											}
									}
								}
							});
							popupMenu
									.show(e.getComponent(), e.getX(), e.getY());
						}
					}
				}
			}
		}
	}

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
}
