package com.crypticbit.ipa.io.parser.sqlite;

import java.io.IOException;
import java.sql.SQLException;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.io.parser.BackupFileParser;


/**
 * Parses a file that represents a AddressBook and exposes the results
 * 
 */
public class SqlParser implements BackupFileParser<SqlResults>
{

	private BackupFile bfd;
	private SqlDataSource sqlDataSource;

	public SqlParser(final BackupFile bfd) throws FileParseException
	{
		this.bfd = bfd;
		try
		{
			this.sqlDataSource = new SqlDataSource(bfd,bfd.getContentsFile());

		} catch (SQLException e)
		{
			throw new FileParseException(e);
		} catch (IOException e)
		{
			throw new FileParseException(e);

		}

	}

	@Override
	public SqlResults getParsedData()
	{
		return new SqlResultsImpl(this.bfd, this.sqlDataSource);
	}
}
