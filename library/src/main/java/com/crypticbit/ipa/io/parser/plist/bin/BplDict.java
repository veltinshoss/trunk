package com.crypticbit.ipa.io.parser.plist.bin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;

import com.crypticbit.ipa.io.parser.plist.PListContainer;
import com.crypticbit.ipa.io.parser.plist.PListDict;
import com.crypticbit.ipa.io.parser.plist.PListDictImpl;
import com.crypticbit.ipa.io.parser.plist.PListLocation;
import com.crypticbit.ipa.io.parser.plist.PListPrimitive;

/**
 * Holder for a binary PList dict element.
 */
public class BplDict extends PListDictImpl implements PListDict
{
	private int[] keyref;

	private ArrayList<PListContainer> objectTable;
	private int[] objref;

	public BplDict(PListLocation location,final ArrayList<PListContainer> objectTable,
			final int[] keyref, final int[] objref)
	{
		super(location);
		this.objectTable = objectTable;
		this.keyref = keyref;
		this.objref = objref;
	}

	@Override
	public Set<Map.Entry<String, PListContainer>> entrySet()
	{
		return getAsMap().entrySet();
	}

	public Map<String, PListContainer> getAsMap()
	{
		Map<String, PListContainer> dictAsMap = new HashMap<String, PListContainer>();
		for (int i = 0; i < this.keyref.length; i++)
		{
			dictAsMap.put(getKey(i), getValue(i));
		}
		return dictAsMap;
	}

	public String getKey(final int i)
	{
		return ((PListPrimitive) this.objectTable.get(this.keyref[i]))
				.getPrimitive().toString();
	}

	public PListContainer getValue(final int i)
	{
		return this.objectTable.get(this.objref[i]);
	}

	@Override
	public String toString()
	{
		StringBuffer buf = new StringBuffer("BplDict{");
		for (int i = 0; i < this.keyref.length; i++)
		{
			if (i > 0)
			{
				buf.append(',');
			}
			if ((this.keyref[i] < 0)
					|| (this.keyref[i] >= this.objectTable.size()))
			{
				buf.append("#" + this.keyref[i]);
			} else if (this.objectTable.get(this.keyref[i]) == this)
			{
				buf.append("*" + this.keyref[i]);
			} else
			{
				buf.append(this.objectTable.get(this.keyref[i]));
				// buf.append(keyref[i]);
			}
			buf.append(":");
			if ((this.objref[i] < 0)
					|| (this.objref[i] >= this.objectTable.size()))
			{
				buf.append("#" + this.objref[i]);
			} else if (this.objectTable.get(this.objref[i]) == this)
			{
				buf.append("*" + this.objref[i]);
			} else
			{
				buf.append(this.objectTable.get(this.objref[i]));
				// buf.append(objref[i]);
			}
		}
		buf.append('}');
		return buf.toString();
	}

	@Override
	public IXMLElement toXml()
	{
		XMLElement elem = new XMLElement(); // parent.createAnotherElement();

		elem.setName("dict");
		for (int i = 0; i < this.keyref.length; i++)
		{
			// XMLElement entry = new XMLElement();
			// entry.setName("entry");
			XMLElement key = new XMLElement(); // parent.createAnotherElement();
			key.setName("key");
			key.setContent(getKey(i));
			elem.addChild(key);
			elem.addChild(getValue(i).toXml());
			// elem.addChild(entry);
		}

		return elem;
	}

}
