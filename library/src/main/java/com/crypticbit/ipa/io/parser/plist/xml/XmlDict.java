/**
 * 
 */
package com.crypticbit.ipa.io.parser.plist.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.n3.nanoxml.IXMLElement;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.io.parser.plist.PListContainer;
import com.crypticbit.ipa.io.parser.plist.PListDict;
import com.crypticbit.ipa.io.parser.plist.PListDictImpl;
import com.crypticbit.ipa.io.parser.plist.PListLocation;

class XmlDict extends PListDictImpl implements PListDict
{

	private IXMLElement elem;
	private Map<String, PListContainer> map;

	XmlDict(PListLocation location,final IXMLElement elem) throws ArrayIndexOutOfBoundsException,
			FileParseException
	{
		super(location);
		this.elem = elem;
		this.map = getAsMap();
	}

	@Override
	public Set<Map.Entry<String, PListContainer>> entrySet()
	{
		return this.map.entrySet();
	}

	@Override
	public String toString()
	{
		return "Dict: " + this.map;
	}

	@Override
	public IXMLElement toXml()
	{
		return this.elem;
	}

	/**
	 * Worker function - never call other than from constructor
	 * 
	 * @return the current XML element converted into an dict
	 * @throws FileParseException
	 * @throws ArrayIndexOutOfBoundsException
	 */
	private Map<String, PListContainer> getAsMap()
			throws ArrayIndexOutOfBoundsException, FileParseException
	{
		Map<String, PListContainer> map = new HashMap<String, PListContainer>();
		for (int index = 0; index < this.elem.getChildrenCount();)
		{
			map.put((this.elem.getChildAtIndex(index++)).getContent(),
					XmlPListParser.getItem(getLocation().getBackupFile(), this.elem.getChildAtIndex(index++)));
		}
		return map;
	}

}