/**
 * 
 */
package monkeypuzzle.io.parser.plist;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.results.Location;
import monkeypuzzle.results.Matcher;

public class PListMatcher implements Matcher
{
	private final String[] matchComponent;

	public PListMatcher(final String[] matchComponent)
	{
		this.matchComponent = matchComponent;
	}

	public Set<Location> match(final BackupFile bfd, final Object objectToMatch)
	{
		if (!(objectToMatch instanceof PListContainer))
			return Collections.emptySet();
		final Set<Location> results = new HashSet<Location>();
		PListContainer root = (PListContainer) objectToMatch;
		root.visitChildrenRecursively(new PListLocation(bfd),
				new PListMatcherVisitor(0, this.matchComponent, results));
		return results;
	}
}