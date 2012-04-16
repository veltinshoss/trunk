/**
 * 
 */
package com.crypticbit.ipa.ui.swing.plist;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import com.crypticbit.ipa.io.parser.plist.PListContainer;


public class ChildrenTreeNode extends DefaultTreeNode
{

	protected List<DefaultTreeNode> children = new ArrayList<DefaultTreeNode>();
	protected PListContainer currentNode;

	ChildrenTreeNode(final PListContainer currentNode, final String value)
	{
		super(value);
		this.currentNode = currentNode;

	}

	@Override
	public Enumeration<TreeNode> children()
	{
		return new Vector<TreeNode>(this.children).elements();
	}

	@Override
	public DefaultTreeNode findNode(final PListContainer element)
	{
		if (getContents() == element) {
			return this;
		}
		else
		{
			for (DefaultTreeNode c : this.children)
			{
				DefaultTreeNode result = c.findNode(element);
				if (result != null)
					return result;
			}
		}
		return null;

	}

	@Override
	public boolean getAllowsChildren()
	{
		return false;
	}

	@Override
	public TreeNode getChildAt(final int childIndex)
	{
		return this.children.get(childIndex);
	}

	@Override
	public int getChildCount()
	{
		return this.children.size();
	}

	@Override
	public int getIndex(final TreeNode node)
	{
		return this.children.indexOf(node);
	}

	@Override
	public boolean isLeaf()
	{
		return false;
	}

	@Override
	protected PListContainer getContents()
	{
		return this.currentNode;
	}
}