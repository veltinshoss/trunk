package com.crypticbit.ipa.io.parser;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.results.ParsedData;

public interface BackupFileParser<T extends ParsedData>
{
	/**
	 * Return an array of all entries parsed from the backup file. The type will
	 * obviously depend on the file contents but the overridden class return
	 * types should make this clear.
	 * 
	 * @return an array of strongly typed entries representing the contents of
	 *         the parsed backup file. An empty array means the file has no
	 *         entries and null means that this parser doesn't support this
	 *         method. (Not very OO so this method may change at some point)
	 * @throws FileParseException
	 *             if the backup file can't properly be parsed
	 */
	// public Object[] getAllEntries() throws FileParseException;
	public T getParsedData();

}
