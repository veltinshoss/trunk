/**
 * 
 */
package monkeypuzzle.results;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.util.RegEx;

public class FileMatcher implements Serializable
{
	private static final transient Map<OrderedString, String> ENCODE_MAPPING = new TreeMap<OrderedString, String>();
	static
	{
		ENCODE_MAPPING.put(new OrderedString("**", 0), ".*");
		ENCODE_MAPPING.put(new OrderedString("*", 1), "[^"
				+ monkeypuzzle.io.util.IoUtils.IPHONE_PATH_SEP + "]*");
		ENCODE_MAPPING.put(new OrderedString("?", 2), "[^"
				+ monkeypuzzle.io.util.IoUtils.IPHONE_PATH_SEP + "]?");
	}
	/*
	 * The regular expression pattern
	 * 
	 * @serial
	 */
	private Pattern pattern;
	/*
	 * When an exact match is needed. Saves figuring out escape characters
	 * 
	 * @serial
	 */
	private String exactMatch;
	private static RegEx regEx = new RegEx(ENCODE_MAPPING);

	public FileMatcher(final BackupFile bf)
	{
		this.exactMatch = bf.getCompleteOriginalFileName();
	}

	public FileMatcher(final String fileMatcherString)
	{
		this.pattern = Pattern.compile(regEx.encode(fileMatcherString));
	}

	public boolean match(final String originalFileName)
	{
		if (this.pattern != null)
			return this.pattern.matcher(originalFileName).matches();
		else
			return originalFileName.equals(this.exactMatch);
	}
}