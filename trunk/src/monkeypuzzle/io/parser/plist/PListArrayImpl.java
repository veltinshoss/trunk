package monkeypuzzle.io.parser.plist;

import java.util.AbstractList;

public abstract class PListArrayImpl extends AbstractList<PListContainer>
		implements PListArray
{
	/*
	 * Believe this is never called
	 * 
	 * @see
	 * monkeypuzzle.io.parser.plist.PListContainer#getAsInterface(java.lang.
	 * Class)
	 */
	public <T> T getAsInterface(final Class<T> interface1)
	{
		return null;
	}

	@Override
	public void visitChildrenRecursively(final PListLocation location,
			final PathVisitor visitor)
	{
		for (PathVisitor v : visitor.visitNodeOnWay(location, this))
		{
			int index = 0;
			for (PListContainer child : this)
			{
				child.visitChildrenRecursively(location
						.createChildLocation(getNextStep(child, index++)), v);
			}
		}
	}

	public void visitLeafs(final PListLocation location,
			final LeafVisitor visitor)
	{
		int index = 0;
		for (PListContainer child : this)
		{
			child.visitLeafs(location.createChildLocation(getNextStep(child,
					index++)), visitor);
		}
	}

	public <T> T wrap(final PListWrapper<T> wrapper)
	{
		return wrapper.wrap(this);
	}

	private PListNode getNextStep(final PListContainer child, final int index)
	{
		return new PListNode() {
			@Override
			public int compareTo(final PListNode o)
			{
				return toString().compareTo(o.toString());
			}

			@Override
			public String getMatcherForm()
			{
				return "" + index;
			}

			@Override
			public PListContainer getNode()
			{
				return child;
			}

			@Override
			public MatchType match(final String argument)
			{
				if ("**".equals(argument))
					return MatchType.GREEDY;
				if ("#".equals(argument))
					return MatchType.NORMAL;
				if (argument.equals(Integer.toString(index)))
					return MatchType.NORMAL;
				else
					return MatchType.NO;
			}

			@Override
			public String toString()
			{
				return "[" + index + "]";
			}
		};
	}
}
