package monkeypuzzle.central;

import java.io.IOException;

import javax.imageio.ImageIO;

import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.io.parser.BackupFileParser;
import monkeypuzzle.io.parser.ErrorResults;
import monkeypuzzle.io.parser.UnidentifiedBackupFileParser;
import monkeypuzzle.io.parser.media.MediaParser;
import monkeypuzzle.io.parser.nullsep.NullSeparatedStringParser;
import monkeypuzzle.io.parser.plist.PListParser;
import monkeypuzzle.io.parser.sqlite.SqlParser;
import monkeypuzzle.results.ParsedData;

/*
 * This maintains an ordered list of possible parsers for a file using an enum. Each 
 * one can self identify whether it is a appropriate parser for a given filename.  
 */
public enum BackupFileType
{

	MEDIA(new FilenameAcceptor() {
		@Override
		public boolean match(final String filename)
		{
			return (ImageIO.getImageReadersBySuffix(getSuffix(filename))
					.hasNext());
		}

		private String getSuffix(final String filename)
		{
			String[] temp = filename.split("\\.");
			return temp[temp.length - 1];
		}
	})
	{
		@Override
		MediaParser createParser(final BackupFile bfd)
				throws FileParseException
		{
			try
			{
				return new MediaParser(bfd);
			} catch (IOException e)
			{
				throw new FileParseException(e);
			}
		}
	},
	CUSTOM_WORDS(java.util.regex.Pattern.compile(".*\\.dat"))
	{
		@Override
		NullSeparatedStringParser createParser(final BackupFile bfd)
				throws FileParseException
		{
			return new NullSeparatedStringParser(bfd);
		}
	},
	PLIST(java.util.regex.Pattern
			.compile(".*\\.plist|.*\\.mdinfo|.*GoogleMobile.*events\\.db"))
	{
		@Override
		PListParser createParser(final BackupFile bfd)
				throws FileParseException
		{
			return new PListParser(bfd);
		}
	},
	SQL(
			java.util.regex.Pattern
					.compile(".*\\.db|.*\\.sqlitedb|.*\\.sqlite3|.*\\.sql|.*\\.sqlite|.*\\.rdb"))
	{
		@Override
		SqlParser createParser(final BackupFile bfd) throws FileParseException
		{
			return new SqlParser(bfd);
		}
	},
	UNIDENTIFIED(java.util.regex.Pattern.compile(".*"))
	{
		@Override
		UnidentifiedBackupFileParser createParser(final BackupFile bfd)
		{
			return new UnidentifiedBackupFileParser(bfd);
		}
	};
	private interface FilenameAcceptor
	{
		public boolean match(String filename);
	}

	public static BackupFileType find(final BackupFile bfd)
	{
		return find(bfd.getCompleteOriginalFileName());
	}

	public static BackupFileType find(final String filename)
	{
		for (BackupFileType bf : BackupFileType.values())
		{
			if (bf.acceptor.match(filename))
				return bf;
		}
		// should never happen as UNIDENTIFIED should have catch all fall
		// through.
		return null;
	}

	private final FilenameAcceptor acceptor;

	private BackupFileType(final FilenameAcceptor acceptor)
	{
		this.acceptor = acceptor;
	}

	private BackupFileType(final java.util.regex.Pattern pattern)
	{
		this.acceptor = new FilenameAcceptor() {
			@Override
			public boolean match(final String filename)
			{
				return pattern.matcher(filename).matches();
			}
		};
	}

	private BackupFileType(final String matchFilename)
	{
		this.acceptor = new FilenameAcceptor() {
			@Override
			public boolean match(final String filename)
			{
				return matchFilename.equals(filename);
			}
		};
	}

	public ParsedData getParsedData(final BackupFile bfd)
	{
		try
		{
			return createParser(bfd).getParsedData();
		} catch (FileParseException pe)
		{
			try
			{
				//pe.printStackTrace();
				// FIXME better way?
				return new ErrorResults(bfd, pe);
			} catch (Exception e1)
			{
				// can never happen.
				//e1.printStackTrace();
				return null;
			}
		}
	}

	abstract <S extends ParsedData> BackupFileParser<S> createParser(
			BackupFile bfd) throws FileParseException;
}
