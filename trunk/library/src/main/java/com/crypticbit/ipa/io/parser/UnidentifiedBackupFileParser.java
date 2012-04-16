package com.crypticbit.ipa.io.parser;

import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.results.ParsedData;
import com.crypticbit.ipa.results.ParsedDataImpl;


/**
 * Uses Null Object Pattern to represent backup file types that aren't
 * recognised
 * 
 */

public class UnidentifiedBackupFileParser implements
		BackupFileParser<ParsedData>
{

	private BackupFile bfd;

	public UnidentifiedBackupFileParser(final BackupFile bfd)
	{
		this.bfd = bfd;
	}

	@Override
	public ParsedData getParsedData()
	{
		return new ParsedDataImpl() {
			@Override
			public BackupFile getBackupFile()
			{
				return UnidentifiedBackupFileParser.this.bfd;
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
				return "unidentified";
			}

			@Override
			public String getSummary()
			{
				return "unidentified";
			}

		};

	}
}
