/**
 * 
 */
package com.crypticbit.ipa.ui.swing.plist;

import javax.swing.tree.TreeNode;

import com.crypticbit.ipa.io.parser.plist.PListContainer;

abstract class DefaultTreeNode implements TreeNode
{
	private DefaultTreeNode parent;

	private String value;

	private String context;

	DefaultTreeNode(final String value)
	{
		this.value = value;
	}

	public void addContextToDisplay(final String context)
	{
		this.context = context;
	}

	public DefaultTreeNode findNode(final PListContainer element)
	{
		if (getContents() == element)
		{
			return this;
		} else
			for (int loop = 0; loop < this.getChildCount(); loop++)
			{
				DefaultTreeNode a = ((DefaultTreeNode) this.getChildAt(loop))
						.findNode(element);
				if (a != null)
					return a;

			}
		return null;
	}

	@Override
	public TreeNode getParent()
	{
		return getParentNode();
	}

	@Override
	public String toString()
	{
		if (this.context == null)
			return this.value;
		else
			return this.context + ": " + this.value;
	}

	protected abstract PListContainer getContents();

	/**
	 * @return the parent
	 */
	protected DefaultTreeNode getParentNode()
	{
		return this.parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	protected void setParentNode(final DefaultTreeNode parent)
	{
		this.parent = parent;
	}

	String getContext()
	{
		return context;
	}
}