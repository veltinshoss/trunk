package monkeypuzzle.io.parser.plist;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.util.TypeFormatter;

public abstract class PListPrimitiveImpl implements PListPrimitive
{

	protected Object object;

	public PListPrimitiveImpl(final Object o)
	{
		this.object = o;
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
				return PListFactory.createParser(
						new ByteArrayInputStream((byte[]) this.object))
						.getRootContainer().getAsInterface(interface1);
			} catch (FileParseException e)
			{
				// FIXME error handling e.printStackTrace();
			} catch (IOException e)
			{
				// FIXME error handling
				e.printStackTrace();
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

	@Override
	public void visitChildrenRecursively(final PListLocation location,
			final PathVisitor visitor)
	{
		for (PathVisitor v : visitor.visitNodeOnWay(location, this))
		{
			v.visitLeaf(location, this);
		}
	}

	public void visitLeafs(final PListLocation location,
			final LeafVisitor visitor)
	{
		visitor.visitLeaf(location, this);
	}

	public <T> T wrap(final PListWrapper<T> wrapper)
	{
		return wrapper.wrap(this);
	}

}
