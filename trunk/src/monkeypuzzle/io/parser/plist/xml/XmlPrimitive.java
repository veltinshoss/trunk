/**
 * 
 */
package monkeypuzzle.io.parser.plist.xml;

import java.util.HashMap;
import java.util.Map;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.io.parser.plist.PListPrimitive;
import monkeypuzzle.io.parser.plist.PListPrimitiveImpl;
import monkeypuzzle.util.Base64;
import net.n3.nanoxml.IXMLElement;

class XmlPrimitive extends PListPrimitiveImpl implements PListPrimitive
{

	public enum XmlElementType
	{
		// <!ELEMENT data (#PCDATA)> <!-- Contents interpreted as Base-64
		// encoded -->
		DATA("data")
		{
			@Override
			Object parseElement(final IXMLElement elem)
			{
				return elem.getContent() == null ? null : Base64.decode(elem
						.getContent());
			}
		},
		// <!ELEMENT date (#PCDATA)> <!-- Contents should conform to a
		// subset of
		// ISO 8601 (in particular, YYYY '-' MM '-' DD 'T' HH ':' MM ':' SS
		// 'Z'.
		// Smaller units may be omitted with a loss of precision) -->
		DATE("date")
		{
			@Override
			Object parseElement(final IXMLElement elem)
					throws FileParseException
			{
				try
				{
					return ISO_8601_DATE_FORMAT.parse(elem.getContent());
				} catch (java.text.ParseException e)
				{
					throw new FileParseException(e);
				}
			}
		},
		// <!ELEMENT false EMPTY> <!-- Boolean constant false -->
		FALSE("false")
		{
			@Override
			Object parseElement(final IXMLElement elem)
			{
				return false;
			}
		},

		// <!-- Numerical primitives -->

		// <!ELEMENT integer (#PCDATA)> <!-- Contents should represent a
		// (possibly signed) integer number in base 10 -->
		INTEGER("integer")
		{
			@Override
			Object parseElement(final IXMLElement elem)
			{
				return Integer.parseInt(elem.getContent());
			}
		},

		// <!ELEMENT real (#PCDATA)> <!-- Contents should represent a
		// floating
		// point number matching ("+" | "-")? d+ ("."d*)? ("E" ("+" | "-")
		// d+)?
		// where d is a digit 0-9. -->
		REAL("real")
		{
			@Override
			Object parseElement(final IXMLElement elem)
			{
				String elemContent = elem.getContent();
				if (elemContent.toLowerCase().equals("nan"))
					return Double.NaN;
				// TODO write test cases for +ve and -ve infinity and deal with
				// them
				// TODO deal with NumberFormatExceptions generally.
				return Double.parseDouble(elemContent);

			}
		},

		// <!ELEMENT string (#PCDATA)>
		STRING("string")
		{
			@Override
			Object parseElement(final IXMLElement elem)
			{
				return elem.getContent();
			}
		},
		// <!ELEMENT true EMPTY> <!-- Boolean constant true -->
		TRUE("true")
		{
			@Override
			Object parseElement(final IXMLElement elem)
			{
				return true;
			}
		};

		private String tagName;

		XmlElementType(final String tagName)
		{
			this.tagName = tagName;
		}

		abstract Object parseElement(IXMLElement elem)
				throws FileParseException;

	}

	private static Map<String, XmlPrimitive.XmlElementType> types = new HashMap<String, XmlPrimitive.XmlElementType>();

	static
	{
		for (XmlPrimitive.XmlElementType type : XmlElementType.values())
		{
			types.put(type.tagName, type);
		}
	}

	private static Object xmlElementToObject(final IXMLElement elem)
			throws FileParseException
	{
		XmlPrimitive.XmlElementType type = types.get(elem.getName());
		if (type == null)
			throw new FileParseException("Unknown XML primitive type: "
					+ elem.getName());
		else
			return type.parseElement(elem);
	}

	private IXMLElement elem;

	XmlPrimitive(final IXMLElement elem) throws FileParseException
	{
		super(xmlElementToObject(elem));
		this.elem = elem;
	}

	@Override
	public IXMLElement toXml()
	{
		return this.elem;
	}

}