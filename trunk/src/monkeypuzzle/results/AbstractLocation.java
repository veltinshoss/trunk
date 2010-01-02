package monkeypuzzle.results;

import monkeypuzzle.central.backupfile.BackupFile;

public abstract class AbstractLocation implements Location
{
	public static LocationMatcher createLocationMatcher(final BackupFile bf,
			final Location location)
	{
		try
		{
			return new LocationMatcher(new FileMatcher(bf), location
					.getMatcher());
		} catch (MatcherException me)
		{
			throw new java.lang.Error(
					"A Location and matcher are incompatable. In this case "
							+ location.getClass()
							+ " produced a matcher it could not read", me);
		}
	}

	@Override
	public LocationMatcher getLocationMatcher()
	{
		return createLocationMatcher(getBackupFile(), this);
	}

	@Override
	public String toString()
	{
		return getLocationDescription() + " - " + getLocationExtract();
	}

}
