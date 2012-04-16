package com.crypticbit.ipa.io.parser.plist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.io.parser.plist.PListContainer.LeafVisitor;
import com.crypticbit.ipa.util.TypeFormatter;

public abstract class PListPrimitiveImpl implements PListPrimitive
{

	private PListLocation location;

	public PListLocation getLocation()
	{
		return location;
	}

	protected Object object;

	protected PListPrimitiveImpl(PListLocation location, final Object o)
	{
		this.object = o;
		this.location = location;
		location.setContainer(this);
	}

	@SuppressWarnings("unchecked")
	// correct behaviour - need runtime fail
	@Override
	public <T> T getAsInterface(final Class<T> interface1)
	{
		if ((this.object instanceof byte[]) && interface1.isInterface())
		{
			try
			{
				return PListFactory
						.createParser(null,
								new ByteArrayInputStream((byte[]) this.object))
						.getRootContainer().getAsInterface(interface1);
			} catch (FileParseException e)
			{
				// FIXME error handling
				// LogFactory.getLogger().log(Level.SEVERE,"Exception",e);
			} catch (IOException e)
			{
				// FIXME error handling
				LogFactory.getLogger().log(Level.SEVERE, "Exception", e);
			}
		}

		return (T) this.object;
	}

	public Object getPrimitive()
	{
		return this.object;
	}

	@Override
	public String toString()
	{
		return TypeFormatter.formatTypeAsString(this.object);
	}

	public <T> T wrap(final PListWrapper<T> wrapper)
	{
		return wrapper.wrap(this);
	}

	public void visitLeafs(LeafVisitor leafVisitor)
	{
		leafVisitor.visitLeaf(this);
	}

}
