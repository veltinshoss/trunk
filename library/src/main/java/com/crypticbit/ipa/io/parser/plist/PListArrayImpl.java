package com.crypticbit.ipa.io.parser.plist;

import java.util.AbstractList;

import com.crypticbit.ipa.io.parser.plist.PListContainer.LeafVisitor;

public abstract class PListArrayImpl extends AbstractList<PListContainer>
		implements PListArray
{
	private PListLocation location;

	protected PListArrayImpl(PListLocation location)
	{
		this.location = location;
		location.setContainer(this);
	}

	public PListLocation getLocation()
	{
		return location;
	}

	/*
	 * Believe this is never called
	 * 
	 * @see
	 * com.crypticbit.ipa.io.parser.plist.PListContainer#getAsInterface(java
	 * .lang. Class)
	 */
	public <T> T getAsInterface(final Class<T> interface1)
	{
		return null;
	}

	public <T> T wrap(final PListWrapper<T> wrapper)
	{
		return wrapper.wrap(this);
	}

	public void visitLeafs(LeafVisitor leafVisitor)
	{
		for (PListContainer c : this)
			c.visitLeafs(leafVisitor);
	}

}
