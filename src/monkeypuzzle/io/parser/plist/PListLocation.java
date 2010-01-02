package monkeypuzzle.io.parser.plist;

import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.results.AbstractLocation;
import monkeypuzzle.results.ContentType;
import monkeypuzzle.results.Location;
import monkeypuzzle.results.Matcher;
import monkeypuzzle.results.MatcherException;

/**
 * Represents a Location of a node within a PList. The two ways to get a
 * location are either:
 * <ol>
 * <li>starting at the root, as navigating there one step at a time using
 * <code>createChildLocation</code>
 * <li>restoring from serialised form
 * </ol>
 * 
 * @author Leo
 * 
 */
public class PListLocation extends AbstractLocation
{
	static interface PListPath extends Comparable<PListPath>
	{
		PListNode getLastComponent();

		String getMatcherForm();
	}

	static class PListPathImpl implements PListPath
	{
		private PListNode nextStep;
		private PListPath parent;

		PListPathImpl(final PListPath parent, final PListNode nextStep)
		{
			this.parent = parent;
			this.nextStep = nextStep;
		}

		@Override
		public int compareTo(final PListPath o)
		{
			if (o instanceof PListPathImpl)
			{
				int parentCompare = this.parent
						.compareTo(((PListPathImpl) o).parent);
				if (parentCompare == 0)
					return this.nextStep.toString().compareTo(
							((PListPathImpl) o).nextStep.toString());
				else
					return parentCompare;
			} else
				return 1;
		}

		@Override
		public PListNode getLastComponent()
		{
			return this.nextStep;
		}

		public String getMatcherForm()
		{
			return this.parent.getMatcherForm() != null ? this.parent
					.getMatcherForm()
					+ PListLocation.PLIST_ELEMENT_SEP
					+ this.nextStep.getMatcherForm() : this.nextStep
					.getMatcherForm();
		}

		@Override
		public String toString()
		{
			return this.parent.toString() + "." + this.nextStep;
		}
	}

	private static class PListPathRoot implements PListPath
	{
		@Override
		public int compareTo(final PListPath o)
		{
			return this == o ? 0 : -1;
		}

		@Override
		public PListNode getLastComponent()
		{
			return new PListNode() {
				@Override
				public int compareTo(final PListNode o)
				{
					return toString().compareTo(o.toString());
				}

				@Override
				public String getMatcherForm()
				{
					return null;
				}

				@Override
				public PListContainer getNode()
				{
					// the only node that can do this.
					return null;
				}

				@Override
				public MatchType match(final String argument)
				{
					return MatchType.IGNORE;
				}

				@Override
				public String toString()
				{
					return "ROOT";
				}
			};
		}

		@Override
		public String getMatcherForm()
		{
			return getLastComponent().getMatcherForm();
		}

		@Override
		public String toString()
		{
			return getLastComponent().toString();
		}
	}

	public static final String PLIST_ELEMENT_SEP = "/";
	public static final String PLIST_ESCAPE = "\\";

	/**
	 * The root element
	 */
	private static final PListPath ROOT = new PListPathRoot();

	private BackupFile bfd;

	private PListPath path;

	/**
	 * Create a PList location at the root
	 * 
	 * @param bfd
	 *            the file to create the root at
	 */
	public PListLocation(final BackupFile bfd)
	{
		this(bfd, ROOT);
	}

	/**
	 * 
	 * @param bfd
	 * @param path
	 */
	private PListLocation(final BackupFile bfd, final PListPath path)
	{
		assert bfd != null;
		this.bfd = bfd;
		this.path = path;
	}

	@Override
	public int compareTo(final Location location)
	{
		if (!(location instanceof PListLocation))
			return getClass().getName()
					.compareTo(location.getClass().getName());
		PListLocation loc = (PListLocation) location;
		if (this.bfd != loc.bfd)
			return this.bfd.compareTo(loc.bfd);
		return this.path.compareTo(loc.path);
	}

	/**
	 * Create a Location representing one of the children of the current
	 * location
	 * 
	 * @param nextStep
	 *            a description of the next step to take
	 * @return a location representing one of the children of the current
	 *         location based on the navigation description provided
	 */
	public PListLocation createChildLocation(final PListNode nextStep)
	{
		PListLocation child = new PListLocation(this.bfd, new PListPathImpl(
				this.path, nextStep));
		return child;
	}

	@Override
	public BackupFile getBackupFile()
	{
		return this.bfd;
	}

	/**
	 * Gets the instance of the container that this PList describes
	 * 
	 * @return
	 */
	public PListContainer getContainer()
	{
		return this.path.getLastComponent().getNode();
	}

	@Override
	public ContentType getContentType()
	{
		return ContentType.PLIST;
	}

	public PListNode getLastComponent()
	{
		return this.path.getLastComponent();
	}

	@Override
	public String getLocationDescription()
	{
		return this.bfd.getCompleteOriginalFileName() + ": " + this.path;
	}

	@Override
	public String getLocationExtract()
	{
		return getContainer() == null ? "null" : getContainer().toString();
	}

	public Matcher getMatcher() throws MatcherException
	{
		return ContentType.PLIST.getMatcher(this.path.getMatcherForm());
	}

}
