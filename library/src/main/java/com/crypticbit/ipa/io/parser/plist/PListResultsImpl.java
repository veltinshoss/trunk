/**
 * 
 */
package com.crypticbit.ipa.io.parser.plist;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import net.n3.nanoxml.XMLWriter;

import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.io.parser.plist.PListContainer.LeafVisitor;
import com.crypticbit.ipa.results.Location;
import com.crypticbit.ipa.results.ParsedDataImpl;
import com.crypticbit.ipa.results.TextSearchAlgorithm;

public class PListResultsImpl<T> extends ParsedDataImpl implements
		PListResults<T>
{


	private final BackupFile bfd;
	private final PListContainer root;

	public PListResultsImpl(final BackupFile bfd, final PListContainer dict)
	{
		this.bfd = bfd;
		this.root = dict;
	}

	@Override
	public BackupFile getBackupFile()
	{
		return this.bfd;
	}

	@Override
	public <I> I getContentbyInterface(final Class<I> interfaceDef)
	{
		return this.root.getAsInterface(interfaceDef);
	}

	@Override
	public String getContents()
	{
		Writer c = new CharArrayWriter();
		try
		{
			new XMLWriter(c).write(this.root.toXml(), true);
		} catch (IOException e)
		{
			try
			{
				c.write("Problems creating XML view of data\n");
			} catch (IOException e1)
			{
				LogFactory.getLogger().log(Level.SEVERE,"Exception",e1);
			}
			e.printStackTrace(new PrintWriter(c));
		}
		return c.toString();
	}

	@Override
	public T getEntry()
	{
		return null;
	}

	@Override
	public PListContainer getRootContainer()
	{
		return this.root;
	}

	@Override
	public String getSummary()
	{
		if (this.root.toXml().getFirstChildNamed("root") != null)
			return this.root.toXml().getFirstChildNamed("root")
					.getChildrenCount()
					+ " entries of unknown type";
		else
			return "unknown entries of unknown type";
	}


	@Override
	public Set<Location> search(final TextSearchAlgorithm searchType,
			final String searchString)
	{
		final Set<Location> result = new HashSet<Location>();
		this.root.visitLeafs(new LeafVisitor() {
			@Override
			public void visitLeaf(final PListPrimitive leaf)
			{
				if ((leaf.getPrimitive() != null)
						&& (searchType.search(searchString,
								leaf.getPrimitive().toString()).size() > 0))
				{
					result.add(leaf.getLocation());
				}
			}
		});
		return result;
	}

	@Override
	public String toString()
	{
		return getContents();
	}
}