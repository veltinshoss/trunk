package monkeypuzzle.io.parser;

import java.util.Collections;
import java.util.Set;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.results.Location;
import monkeypuzzle.results.Matcher;
import monkeypuzzle.results.ParsedData;
import monkeypuzzle.results.ParsedDataImpl;

public class ErrorResults extends ParsedDataImpl implements ParsedData
{

	private BackupFile bfd;
	private FileParseException pe;

	public ErrorResults(final BackupFile bfd, final FileParseException pe)
	{
		this.bfd = bfd;
		this.pe = pe;
	}

	@Override
	public BackupFile getBackupFile()
	{
		return this.bfd;
	}

	@Override
	public <I> I getContentbyInterface(final Class<I> interfaceDef)
	{
		throw new UnsupportedOperationException(
				"Not possible to get this content as interface: "
						+ getSummary());
	}

	@Override
	public String getContents()
	{
		return getSummary();
	}

	@Override
	public String getSummary()
	{
		return "the file was not succesfully parsed because: "
				+ this.pe.getMessage();
	}

	@Override
	public Set<Location> match(final Matcher matcher)
	{
		return Collections.emptySet();
	}

}