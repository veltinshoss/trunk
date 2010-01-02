package monkeypuzzle.results;

import monkeypuzzle.central.backupfile.BackupFile;

public interface Location extends Comparable<Location>
{
	/**
	 * Gets the backup file which this location relates to
	 * 
	 * @return the backup file which this location relates to
	 */
	public BackupFile getBackupFile();

	/**
	 * Get the type of the content this describes, e.g. PLIST
	 * 
	 * @return an element from the ContentType enumeration
	 */
	public ContentType getContentType();

	/**
	 * Get a human readable description of the location
	 * 
	 * @return a human readable description of the location
	 */
	public String getLocationDescription();

	/**
	 * A textual view of what the location points at
	 * 
	 * @return a string which can be used to show the user what the location
	 *         describes
	 */
	public String getLocationExtract();

	/**
	 * Create a location matcher that will be able to restore this location
	 * later
	 */
	public LocationMatcher getLocationMatcher();

	Matcher getMatcher() throws MatcherException;

}
