package com.crypticbit.ipa.io.parser.plist.xml;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLParserFactory;
import net.n3.nanoxml.XMLWriter;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.io.parser.plist.PListContainer;
import com.crypticbit.ipa.io.parser.plist.PListHeader;
import com.crypticbit.ipa.io.parser.plist.PListLocation;

public class XmlPListParser implements PListHeader
{
	private BackupFile bfd;
	
	static PListContainer getItem(BackupFile backupFile, final IXMLElement element)
			throws FileParseException
	{
		if (element.getName().equals("plist"))
			return getItem(backupFile,element.getChildAtIndex(0));
		else if (element.getName().equals("dict"))
			return new XmlDict(new PListLocation(backupFile,element),element);
		else if (element.getName().equals("array"))
			return new XmlArray(new PListLocation(backupFile,element),element);
		else
			return new XmlPrimitive(new PListLocation(backupFile, element),element);
	}

	private IXMLElement xml;

	public XmlPListParser(BackupFile bfd, final InputStream is) throws FileParseException, IOException
	{
		this.bfd = bfd;
		parse(is);
		is.close();
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
		return getItem(bfd,this.xml);
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
