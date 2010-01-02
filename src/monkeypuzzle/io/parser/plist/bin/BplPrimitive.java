/**
 * 
 */
package monkeypuzzle.io.parser.plist.bin;

import java.util.Date;

import monkeypuzzle.io.parser.plist.PListPrimitive;
import monkeypuzzle.io.parser.plist.PListPrimitiveImpl;
import monkeypuzzle.util.Base64;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;

class BplPrimitive extends PListPrimitiveImpl implements PListPrimitive
{
	BplPrimitive(final Object object)
	{
		super(object);
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