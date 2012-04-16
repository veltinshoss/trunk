package com.crypticbit.ipa.io.parser.plist.bin;

import java.util.ArrayList;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;

import com.crypticbit.ipa.io.parser.plist.PListArray;
import com.crypticbit.ipa.io.parser.plist.PListArrayImpl;
import com.crypticbit.ipa.io.parser.plist.PListContainer;
import com.crypticbit.ipa.io.parser.plist.PListLocation;

/**
 * Holder for a binary PList array element.
 */
class BplArray extends PListArrayImpl implements PListArray
{
	private ArrayList<PListContainer> objectTable;
	private int[] objref;

	public BplArray(PListLocation location,final ArrayList<PListContainer> objectTable,
			final int[] objref)
	{
		super(location);
		this.objectTable = objectTable;
		this.objref = objref;
	}

	@Override
	public PListContainer get(final int index)
	{
		return getValue(index);
	}

	@SuppressWarnings("unchecked")
	// newInstance doesn't handle generic well
	// enough to d
	@Override
	public <T> T getAsInterface(final Class<T> interface1)
	{
		T[] result = (T[]) java.lang.reflect.Array.newInstance(
				interface1.getComponentType(), this.objref.length);
		for (int loop = 0; loop < this.objref.length; loop++)
		{
			result[loop] = (T) getValue(loop).getAsInterface(
					interface1.getComponentType());
		}
		return (T) result;
	}

	@Override
	public int size()
	{
		return this.objref.length;
	}

	@Override
	public IXMLElement toXml()
	{
		XMLElement elem = new XMLElement(); // parent.createAnotherElement();

		elem.setName("array");
		for (int i = 0; i < this.objref.length; i++)
		{
			elem.addChild(getValue(i).toXml());
		}

		return elem;
	}

	private PListContainer getValue(final int i)
	{
		return this.objectTable.get(this.objref[i]);
	}

}