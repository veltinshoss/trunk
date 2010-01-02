package monkeypuzzle.io.parser.plist;

import java.util.AbstractMap;
import java.util.Map;

import monkeypuzzle.io.parser.plist.dynamicproxy.PListDynamicProxy;
import monkeypuzzle.util.RegEx;

public abstract class PListDictImpl extends AbstractMap<String, PListContainer>
		implements PListDict
{
	public static final class PListDictNode implements PListNode
	{
		private final PListContainer entry;
		private final String key;

		PListDictNode(final PListContainer entry, final String key)
		{
			this.key = key;
			this.entry = entry;
		}

		@Override
		public int compareTo(final PListNode o)
		{
			return toString().compareTo(o.toString());
		}

		@Override
		public String getMatcherForm()
		{
			return this.key;
		}

		@Override
		public PListContainer getNode()
		{
			return this.entry;
		}

		@Override
		public MatchType match(final String argument)
		{
			if ("**".equals(argument))
				return MatchType.GREEDY;
			// shortcut for doing the rest as it's quite common
			if ("*".equals(argument))
				return MatchType.NORMAL;
			String pattern = RegEx.defaultRegEx.encode(argument);
			if (this.key.matches(pattern))
				return MatchType.NORMAL;
			else
				return MatchType.NO;
		}

		@Override
		public String toString()
		{
			return "<" + this.key + ">";
		}
	}

	public <T> T getAsInterface(final Class<T> interface1)
	{
		return PListDynamicProxy.newInstance(interface1, this);
	}

	@Override
	public void visitChildrenRecursively(final PListLocation location,
			final PathVisitor visitor)
	{
		for (PathVisitor v : visitor.visitNodeOnWay(location, this))
		{
			for (Map.Entry<String, PListContainer> entry : entrySet())
			{
				entry.getValue().visitChildrenRecursively(
						location.createChildLocation(getNextStep(entry
								.getValue(), entry.getKey())), v);
			}
		}
	}

	public void visitLeafs(final PListLocation location,
			final LeafVisitor visitor)
	{
		for (Map.Entry<String, PListContainer> entry : entrySet())
		{
			entry.getValue().visitLeafs(
					location.createChildLocation(getNextStep(entry.getValue(),
							entry.getKey())), visitor);
		}
	}

	public <T> T wrap(final PListWrapper<T> wrapper)
	{
		return wrapper.wrap(this);
	}

	private PListNode getNextStep(final PListContainer entry, final String key)
	{
		return new PListDictNode(entry, key);
	}
}
