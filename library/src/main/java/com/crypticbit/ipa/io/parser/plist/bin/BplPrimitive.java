/**
 * 
 */
package com.crypticbit.ipa.io.parser.plist.bin;

import java.util.Date;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;

import com.crypticbit.ipa.io.parser.plist.PListLocation;
import com.crypticbit.ipa.io.parser.plist.PListPrimitive;
import com.crypticbit.ipa.io.parser.plist.PListPrimitiveImpl;
import com.crypticbit.ipa.util.Base64;

class BplPrimitive extends PListPrimitiveImpl implements PListPrimitive
{
	BplPrimitive(PListLocation location, final Object object)
	{
		super(location, object);
	}

	public IXMLElement toXml()
	{
		XMLElement elem = new XMLElement(); // parent.createAnotherElement();
		if (this.object instanceof String)
		{
			elem.setName("string");
			elem.setContent((String) this.object);
		} else if (this.object instanceof Integer)
		{
			elem.setName("integer");
			elem.setContent(this.object.toString());
		} else if (this.object instanceof Long)
		{
			elem.setName("integer");
			elem.setContent(this.object.toString());
		} else if (this.object instanceof Float)
		{
			elem.setName("real");
			elem.setContent(this.object.toString());
		} else if (this.object instanceof Double)
		{
			elem.setName("real");
			elem.setContent(this.object.toString());
		} else if (this.object instanceof Boolean)
		{
			elem.setName("boolean");
			elem.setContent(this.object.toString());
		} else if (this.object instanceof byte[])
		{
			elem.setName("data");
			elem.setContent(Base64.encodeBytes((byte[]) this.object));
		} else if (this.object instanceof Date)
		{
			elem.setName("date");
			elem.setContent(ISO_8601_DATE_FORMAT.format((Date) this.object));
		} else
		{
			elem.setName("unsupported");
			elem.setContent(this.object.toString());
		}
		return elem;
	}

}