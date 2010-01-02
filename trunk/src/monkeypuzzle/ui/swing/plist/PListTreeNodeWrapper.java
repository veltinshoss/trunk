package monkeypuzzle.ui.swing.plist;

import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import monkeypuzzle.io.parser.plist.PListArray;
import monkeypuzzle.io.parser.plist.PListContainer;
import monkeypuzzle.io.parser.plist.PListDict;
import monkeypuzzle.io.parser.plist.PListPrimitive;
import monkeypuzzle.io.parser.plist.PListWrapper;

public class PListTreeNodeWrapper implements PListWrapper<DefaultTreeNode>
{
	private class ArrayTreeNode extends ChildrenTreeNode
	{
		ArrayTreeNode(final PListArray array)
		{
			super(array, "Array");
			for (PListContainer c : array)
			{
				this.children.add(PListTreeNodeWrapper.addParent(this, c
						.wrap(PListTreeNodeWrapper.this)));
			}
		}
	}

	private class DictTreeNode extends ChildrenTreeNode
	{
		DictTreeNode(final PListDict dict)
		{
			super(dict, "Dict");
			for (Map.Entry<String, PListContainer> o : dict.entrySet())
			{
				final String key = o.getKey();
				final PListContainer value = o.getValue();
				final DefaultTreeNode child = PListTreeNodeWrapper.addParent(
						this, value.wrap(PListTreeNodeWrapper.this));
				this.children.add(child);
				child.addContextToDisplay(key);
			}
		}

	}

	private class PrimitiveTreeNode extends DefaultTreeNode
	{
		private final Vector<PListPrimitive> EMPTY_VECTOR = new Vector<PListPrimitive>();
		private PListPrimitive primitive;

		PrimitiveTreeNode(final PListPrimitive primitive)
		{
			super(primitive.toString());
			this.primitive = primitive;
		}

		@Override
		public Enumeration<PListPrimitive> children()
		{
			return this.EMPTY_VECTOR.elements();
		}

		@Override
		public boolean getAllowsChildren()
		{
			return false;
		}

		@Override
		public TreeNode getChildAt(final int childIndex)
		{
			return null;
		}

		@Override
		public int getChildCount()
		{
			return 0;
		}

		@Override
		public int getIndex(final TreeNode node)
		{
			return -1;
		}

		@Override
		public boolean isLeaf()
		{
			return true;
		}

		@Override
		protected PListContainer getContents()
		{
			return this.primitive;
		}

	}

	public static DefaultTreeNode addParent(final DefaultTreeNode parent,
			final DefaultTreeNode wrap)
	{
		wrap.setParentNode(parent);
		return wrap;
	}

	@Override
	public DefaultTreeNode wrap(final PListArray array)
	{
		return new ArrayTreeNode(array);
	}

	@Override
	public DefaultTreeNode wrap(final PListDict dict)
	{
		return new DictTreeNode(dict);
	}

	@Override
	public DefaultTreeNode wrap(final PListPrimitive primitive)
	{
		return new PrimitiveTreeNode(primitive);
	}
}
