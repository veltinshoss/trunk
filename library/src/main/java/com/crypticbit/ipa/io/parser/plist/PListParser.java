package com.crypticbit.ipa.io.parser.plist;

import java.io.IOException;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.io.parser.BackupFileParser;


/**
 * Represents backup files that contain a PList (the iPhone way of representing
 * property files) but which we don't yet have a particular parser for. This
 * will simply turn it into a textual form which can be read.
 * 
 */

public class PListParser implements BackupFileParser<PListResults<NullReturn>>
{
	BackupFile bfd;
	PListContainer dict;

	public PListParser(final BackupFile bfd) throws FileParseException
	{
		this.bfd = bfd;
		try
		{
			this.dict = PListFactory.createParser(bfd)
					.getRootContainer();

		} catch (IOException e)
		{
			throw new FileParseException("Problem parsing \""
					+ bfd.getCompleteOriginalFileName() + "\".", e);
		}
	}

	@Override
	public PListResults<NullReturn> getParsedData()
	{
		return new PListResultsImpl(this.bfd, this.dict);
	}
}

interface NullReturn
{

}
