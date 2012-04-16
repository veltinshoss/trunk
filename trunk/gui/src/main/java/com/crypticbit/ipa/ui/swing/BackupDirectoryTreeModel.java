/**
 * 
 */
package com.crypticbit.ipa.ui.swing;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.io.util.IoUtils;


public final class BackupDirectoryTreeModel implements TreeModel
{
	class Node implements Comparable<Node>
	{
		private BackupFile bfd;
		private String description;

		Node(final String description)
		{
			setDescription(description);
		}

		Node(final String description, final BackupFile bfd)
		{
			setBfd(bfd);
			setDescription(description);
		}

		/**
		 * return node in sorted order - by filename with directories first and
		 * files second
		 */
		@Override
		public int compareTo(final Node o)
		{
			if ((getBfd() == null) == (o.getBfd() == null))
				return this.description.compareTo(o.description);
			else
				return (getBfd() == null) ? -1 : 1;
		}

		public Node createNextLevelNode(final String nextComponent)
		{
			return new Node((this.description == null ? "" : this.description
					+ IoUtils.IPHONE_PATH_SEP)
					+ nextComponent);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Node other = (Node) obj;
			if (getDescription() == null)
			{
				if (other.getDescription() != null)
					return false;
			} else if (!getDescription().equals(other.getDescription()))
				return false;
			return true;
		}

		public String[] getComponentParts()
		{
			return getDescription() == null ? new String[] {}
					: getDescription().split(IoUtils.IPHONE_PATH_SEP);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((getDescription() == null) ? 0 : getDescription()
							.hashCode());
			return result;
		}

		@Override
		public String toString()
		{
			if (this.description == null)
				return "";
			String tempDescription = getDescription();
			if (tempDescription.endsWith(IoUtils.IPHONE_PATH_SEP))
			{
				tempDescription = tempDescription.substring(0, tempDescription
						.length() - 1);
			}
			return tempDescription.lastIndexOf(IoUtils.IPHONE_PATH_SEP) >= 0 ? tempDescription
					.substring(tempDescription
							.lastIndexOf(IoUtils.IPHONE_PATH_SEP) + 1)
					: tempDescription;
		}

		BackupFile getBfd()
		{
			return this.bfd;
		}

		String getDescription()
		{
			return this.description;
		}

		private void setBfd(final BackupFile bfd)
		{
			this.bfd = bfd;
		}

		private void setDescription(final String description)
		{
			this.description = description;
		}
	}

	private List<BackupFile> list;

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private final Mediator mediator;

	/**
	 * @param mainFrame
	 */
	public BackupDirectoryTreeModel(final Mediator mediator)
	{
		this.mediator = mediator;
		this.list = new ArrayList<BackupFile>(mediator.getBackupDirectory()
				.getParsedFiles());
	}

	@Override
	public void addTreeModelListener(final TreeModelListener l)
	{
		// do nothing
	}

	@Override
	public Object getChild(final Object parent, final int index)
	{
		return processToList((Node) parent).get(index);
	}

	@Override
	public int getChildCount(final Object parent)
	{
		return processToList((Node) parent).size();
	}

	@Override
	public int getIndexOfChild(final Object parent, final Object child)
	{
		return processToList((Node) parent).indexOf(child);
	}

	@Override
	public Object getRoot()
	{
		return new Node(null);
	}

	@Override
	public boolean isLeaf(final Object node)
	{
		return ((Node) node).getBfd() != null;
	}

	@Override
	public void removeTreeModelListener(final TreeModelListener l)
	{
		// do nothing
	}

	@Override
	public void valueForPathChanged(final TreePath path, final Object newValue)
	{
		// do nothing
	}

	private List<Node> processToList(final Node root)
	{
		String[] rootComponents = root.getComponentParts();
		Set<Node> s = new TreeSet<Node>();
		for (BackupFile bfd : this.list)
		{
			String[] currentComponents = bfd.getCompleteOriginalFileName().split(
					IoUtils.IPHONE_PATH_SEP);
			// finds the last matching index position of the two string arrays
			int lastMatch = -1;
			for (int nextMatch = 0; (nextMatch < currentComponents.length)
					&& (nextMatch < rootComponents.length)
					&& currentComponents[nextMatch]
							.equals(rootComponents[nextMatch]); nextMatch++)
			{
				lastMatch = nextMatch;
			}
			if (lastMatch + 1 == rootComponents.length)
			{
				if (currentComponents.length == rootComponents.length + 1)
				{
					s.add(new Node(currentComponents[rootComponents.length],
							bfd));
				} else
				{
					s
							.add(root
									.createNextLevelNode(currentComponents[rootComponents.length]));
				}
			}
		}
		return new ArrayList<Node>(s);
	}
}