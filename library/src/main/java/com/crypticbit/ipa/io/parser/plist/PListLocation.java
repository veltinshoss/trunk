package com.crypticbit.ipa.io.parser.plist;

import net.n3.nanoxml.IXMLElement;

import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.results.AbstractLocation;
import com.crypticbit.ipa.results.ContentType;

/**
 * Represents a Location of a node within a PList. The two ways to get a
 * location are either:
 * <ol>
 * <li>starting at the root, as navigating there one step at a time using
 * <code>createChildLocation</code>
 * <li>restoring from serialised form
 * </ol>
 * 
 * @author Leo
 * 
 */
public class PListLocation extends AbstractLocation
{

	private int i;
	private String xmlPath;
	private PListContainer container;

	public PListLocation(BackupFile bfd, IXMLElement element)
	{
		super(bfd);
		xmlPath = element.getFullName();
	}

	public PListLocation(BackupFile bfd, int i)
	{
		super(bfd);
		this.i = i;
	}

	@Override
	public ContentType getContentType()
	{
		return ContentType.PLIST;
	}

	public PListContainer getContainer()
	{
		return container;
	}

	@Override
	public String getLocationDescription()
	{
		if (xmlPath == null)
			return "Binary: " + i;
		else
			return "XML: " + xmlPath;
	}

	@Override
	public String getLocationExtract()
	{
		return null;
	}

	public void setContainer(PListContainer container)
	{
		this.container  = container;
		
	}

}
