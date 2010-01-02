package monkeypuzzle.results;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import monkeypuzzle.central.backupfile.BackupFile;

/**
 * Used to locate arbitary places in files by wildcard. Describes a pattern to
 * describe whcih files can contain the element, and then which set of paths
 * also locate the element/elements
 * 
 * @author Leo
 * 
 */
public class LocationMatcher implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static LocationMatcher parse(final String fileMatcherString,
			final ContentType type, final String typeMatchString)
			throws MatcherException, MatcherException
	{
		return new LocationMatcher(new FileMatcher(fileMatcherString), type
				.getMatcher(typeMatchString));
	}

	public static LocationMatcher parse(final String fileMatcherString,
			final String type, final String typeMatchString)
			throws MatcherException
	{
		return parse(fileMatcherString, ContentType.findByName(type),
				typeMatchString);
	}

	/* Generics and empty sets went funny without this */
	private static Set<Location> emptySet()
	{
		return Collections.emptySet();
	}

	private FileMatcher fileMatcher;

	private Matcher matcher;

	public LocationMatcher(final FileMatcher fileMatcher, final Matcher matcher)
	{
		this.matcher = matcher;
		this.fileMatcher = fileMatcher;
	}

	public Matcher getTypeMatcher()
	{
		return this.matcher;
	}

	public Set<Location> match(final BackupFile bf)
	{
		return this.fileMatcher.match(bf.getCompleteOriginalFileName()) ? bf
				.getParsedData().match(this.matcher) : emptySet();
	}
}
