/**
 * 
 */
package monkeypuzzle.io.parser.plist;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import monkeypuzzle.io.parser.plist.PListContainer.PathVisitor;
import monkeypuzzle.results.Location;

/**
 * Immutable class to represent a single state of checking whether a description
 * of a location (matchLocation) can be fulfilled by the actual location at a
 * given index
 * 
 * @author Leo
 * 
 */
public class PListMatcherVisitor implements PathVisitor
{
	private final int index;
	private final String[] matchComponent;
	private final Set<Location> results;

	public PListMatcherVisitor(final int index, final String[] matchComponent,
			final Set<Location> results)
	{
		this.index = index;
		this.matchComponent = matchComponent;
		this.results = results;
	}

	@Override
	public void visitLeaf(final PListLocation location,
			final PListPrimitive leaf)
	{
		// check all actions have been consumed
		if (this.index == this.matchComponent.length)
		{
			this.results.add(location);
		}
	}

	@Override
	public Set<PathVisitor> visitNodeOnWay(final PListLocation location,
			final PListContainer node)
	{
		// overun (i.e we're matching an element that doesn't exist)
		if (this.index + 1 > this.matchComponent.length)
			return Collections.emptySet();

		switch (location.getLastComponent().match(
				this.matchComponent[this.index]))
		{
		case NO:
			return Collections.emptySet();
		case GREEDY:
			{
				Set<PathVisitor> visitors = new HashSet<PathVisitor>();
				visitors.add(this);
				visitors.add(cloneWithIndex(this.index + 1));
				return visitors;
			}
		case NORMAL:
			{
				Set<PathVisitor> visitors = new HashSet<PathVisitor>();
				visitors.add(cloneWithIndex(this.index + 1));
				return visitors;
			}
		case IGNORE:
			{
				Set<PathVisitor> visitors = new HashSet<PathVisitor>();
				visitors.add(cloneWithIndex(this.index));
				return visitors;
			}
		default:
			throw new Error("An unknown match case was returned");
		}
	}

	PListMatcherVisitor cloneWithIndex(final int newIndex)
	{
		return new PListMatcherVisitor(newIndex, this.matchComponent,
				this.results);
	}

}