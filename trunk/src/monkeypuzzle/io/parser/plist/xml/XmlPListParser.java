package monkeypuzzle.io.parser.plist.xml;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.io.parser.plist.PListContainer;
import monkeypuzzle.io.parser.plist.PListHeader;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLParserFactory;
import net.n3.nanoxml.XMLWriter;

public class XmlPListParser implements PListHeader
{
	static PListContainer getItem(final IXMLElement element)
			throws FileParseException
	{
		if (element.getName().equals("plist"))
			return getItem(element.getChildAtIndex(0));
		else if (element.getName().equals("dict"))
			return new XmlDict(element);
		else if (element.getName().equals("array"))
			return new XmlArray(element);
		else
			return new XmlPrimitive(element);
	}

	private IXMLElement xml;

	public XmlPListParser(final InputStream is) throws FileParseException
	{
		parse(is);
	}

	public String getContents()
	{
		Writer c = new CharArrayWriter();
		try
		{
			new XMLWriter(c).write(this.xml, true);
		} catch (IOException e)
		{
			return "Unable to produce XML representation (" + e.getMessage()
					+ ")";
		}
		return c.toString();
	}

	public PListContainer getRootContainer() throws FileParseException
	{
		return getItem(this.xml);
	}

	public IXMLElement getXml()
	{
		return this.xml;
	}

	private void parse(final InputStream is) throws FileParseException
	{
		try
		{
			IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
			// parser.setResolver(new MyResolver());
			IXMLReader reader = new StdXMLReader(is);
			parser.setReader(reader);
			this.xml = (IXMLElement) parser.parse();
		} catch (Exception e)
		{
			throw new FileParseException("Unable to parse the XML PList", e);
		}
	}
}
