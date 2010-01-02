package monkeypuzzle.io.parser.plist;

import java.io.IOException;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.io.parser.BackupFileParser;

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
			this.dict = PListFactory.createParser(bfd.getContentsInputStream())
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
