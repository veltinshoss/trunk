/**
 * 
 */
package com.crypticbit.ipa.io.parser.plist.xml;

import java.util.ArrayList;
import java.util.List;

import net.n3.nanoxml.IXMLElement;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.io.parser.plist.PListArrayImpl;
import com.crypticbit.ipa.io.parser.plist.PListContainer;
import com.crypticbit.ipa.io.parser.plist.PListLocation;

class XmlArray extends PListArrayImpl implements PListContainer
{
	private IXMLElement elem;
	private List<PListContainer> list;

	XmlArray(PListLocation location,final IXMLElement elem) throws ArrayIndexOutOfBoundsException,
			FileParseException
	{
		super(location);
		this.elem = elem;
		this.list = getAsList();
	}

	@Override
	public PListContainer get(final int index)
	{
		return this.list.get(index);
	}

	@Override
	public int size()
	{
		return this.list.size();
	}

	@Override
	public String toString()
	{
		return "Array: " + this.list;
	}

	@Override
	public IXMLElement toXml()
	{
		return this.elem;
	}

	/**
	 * Worker function - never call other than from constructor
	 * 
	 * @return the current XML element converted into an array
	 * @throws FileParseException
	 * @throws ArrayIndexOutOfBoundsException
	 */
	private List<PListContainer> getAsList()
			throws ArrayIndexOutOfBoundsException, FileParseException
	{
		List<PListContainer> list = new ArrayList<PListContainer>();
		for (int index = 0; index < this.elem.getChildrenCount();)
		{
			list
					.add(XmlPListParser.getItem(getLocation().getBackupFile(), this.elem
							.getChildAtIndex(index++)));
		}

		return list;
	}

}