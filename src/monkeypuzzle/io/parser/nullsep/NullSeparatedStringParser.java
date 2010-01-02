package monkeypuzzle.io.parser.nullsep;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.io.parser.BackupFileParser;
import monkeypuzzle.results.Location;
import monkeypuzzle.results.Matcher;
import monkeypuzzle.results.ParsedData;
import monkeypuzzle.results.ParsedDataImpl;

public class NullSeparatedStringParser implements BackupFileParser<ParsedData>
{

	private BackupFile bfd;
	private List<String> wordlist;

	public NullSeparatedStringParser(final BackupFile bfd)
			throws FileParseException
	{
		this.bfd = bfd;
		this.wordlist = new LinkedList<String>();

		// Read backup file
		File backupFile = null;
		StringBuilder wordBuffer = new StringBuilder();
		byte[] byteBuffer = new byte[20];
		int bytesRead;
		try
		{

			backupFile = bfd.getContentsFile();
			FileInputStream fis = new FileInputStream(backupFile);

			while ((bytesRead = fis.read(byteBuffer)) != -1)
			{
				for (int i = 0; i < bytesRead; i++)
				{
					if (byteBuffer[i] == '\00')
					{
						// end of a word
						if (wordBuffer.length() > 0)
						{
							// add current word to wordlist
							this.wordlist.add(wordBuffer.toString());
							// move to next word
							wordBuffer = new StringBuilder();
						}
					} else
					{
						// char from word, add to current word
						wordBuffer.append((char) byteBuffer[i]);
					}
				}
			}
		} catch (IOException e)
		{
			String path = "null file";
			if (backupFile != null)
			{
				path = backupFile.getAbsolutePath();
			}

			throw new FileParseException("Unable to parse file: " + path, e);
		}
	}

	@Override
	public ParsedData getParsedData()
	{
		return new ParsedDataImpl() {
			@Override
			public BackupFile getBackupFile()
			{
				return NullSeparatedStringParser.this.bfd;
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
				StringBuilder buff = new StringBuilder();
				for (String w : NullSeparatedStringParser.this.wordlist)
				{
					buff.append(w).append('\n');
				}

				return buff.toString();
			}

			@Override
			public String getSummary()
			{
				return "Number of entries in wordlist: "
						+ NullSeparatedStringParser.this.wordlist.size();
			}

			@Override
			public Set<Location> match(final Matcher matcher)
			{
				// TODO - implement match
				return Collections.emptySet();
			}

		};
	}

}
