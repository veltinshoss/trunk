package monkeypuzzle.central;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.entity.settings.MailAccounts;
import monkeypuzzle.entity.settings.SafariBookmarks;
import monkeypuzzle.entity.sqlite.AddressBook;
import monkeypuzzle.entity.sqlite.AddressBookImages;
import monkeypuzzle.entity.sqlite.CallHistory;
import monkeypuzzle.entity.sqlite.Messages;
import monkeypuzzle.entity.sqlite.Notes;
import monkeypuzzle.entity.sqlite.TimeBasedInformation;
import monkeypuzzle.io.util.TreeMapContainingSet;
import monkeypuzzle.results.Location;
import monkeypuzzle.results.LocationMatcher;
import monkeypuzzle.results.ParsedData;
import monkeypuzzle.results.TextSearchAlgorithm;

/**
 * The main class for parsing iPhone backup files. Given a directory containing
 * parsed files this class will scan each file, extracting all the information
 * it can and exposing it through a simple API. Data can either be accessed in a
 * non-typesafe way (i.e. you can iterate through everything and dump the
 * content without understanding about the filetype), or alternatively using
 * typesafe method calls.
 * 
 * A <code>BackupDirectory</code> relies on four important concepts to enable it
 * to be used:
 * <ul>
 * <li><code>BackupFile</code> - a description of the file that used to be
 * contained on the iPhone including its original name and a reference to the
 * unwrapped content</li>
 * <li><code>BackupFileType</code> - an enumeration that shows what type the
 * backup file is, e.g. Contacts, SMS Store, etc.</li>
 * <li><code>BackupFileParser</code> - the Parser/DAO that parsed the backup
 * file and can expose its content.</li>
 * </ul>
 * Important method calls to manipulate these include:
 * <ul>
 * <li>getParsedFiles - to get all the <code>BackupFile</code>s
 * <li>findBackupFileParser - to get the <code>BackupFileParser</code> given a
 * <code>BackupFile</code>
 * <li>getXyz - to get a <code>BackupFileParser</code> for a particular hard
 * coded type
 * </ul>
 */
public class IPhone
{
	// key details of included backup files
	private Map<BackupFile, BackupFileType> backupFileInstanceToType = new HashMap<BackupFile, BackupFileType>();
	private List<BackupFile> backupFiles = new ArrayList<BackupFile>();
	private Map<String, Object> globalVars;
	private BackupConfigurationElements configElements;

	/**
	 * Causes the given directory to be parsed and its contents made available
	 * through typesafe and non-typesafe APIs.
	 * 
	 * A typical example might be something like:
	 * 
	 * <pre>
	 * 
	 * BackupDirectory backupDir = new BackupDirectory(f);
	 * 	
	 * 	// list the status of all backup files parses
	 * 	for(BackupFileA bfd : backupDir.getParsedFiles()) {
	 * 		System.out.println(bfd+&quot; ==&gt; &quot;+backupDir.findBackupFileParser(bfd).getSummary());
	 * 	}
	 * 	
	 * 	// list the breakdown of files by type
	 * 	System.out.println(backupDir.getCountOfBackupFileTypes());
	 * 	
	 * 	// list the contacts - or alternatively get them and use the API to display them in any other way
	 * 	System.out.println(backupDir.getContacts().getContents());
	 * 	..
	 * 	System.out.println(backupDir.getSMS().getContents());
	 * </pre>
	 * 
	 * @param backupDirectory
	 *            the directory to parse
	 * @throws IOException
	 *             if there are problems reading the directory containing backup
	 *             files
	 * @throws FileParseException
	 * @throws IPhoneParseException
	 */
	public IPhone(final Map<String, Object> globalVars,
			final BackupConfigurationElements configElements)
			throws IOException, FileParseException, IPhoneParseException
	{
		this.globalVars = globalVars;
		this.configElements = configElements;
	}

	/**
	 * Get Contact details extracted from the parsed backup files
	 * 
	 * @return Contact Details
	 */
	public AddressBook getAddressBook()
	{
		return getParsedDataForType(BackupFileView.ADDRESS_BOOK)
				.getContentbyInterface(AddressBook.class);
	}

	public AddressBookImages getAddressBookImages()
	{
		return getParsedDataForType(BackupFileView.ADDRESS_BOOK_IMAGES)
				.getContentbyInterface(AddressBookImages.class);
	}

	public List<BackupFile> getBackupFileByType(final BackupFileType bf)
	{
		List<BackupFile> result = new ArrayList<BackupFile>();
		for (Entry<BackupFile, BackupFileType> e : this.backupFileInstanceToType
				.entrySet())
		{
			if (e.getValue() == bf)
			{
				result.add(e.getKey());
			}
		}
		return result;
	}

	public List<BackupFile> getBackupFileByType(final BackupFileView bf)
	{
		List<BackupFile> result = new ArrayList<BackupFile>();
		for (Entry<BackupFile, BackupFileType> e : this.backupFileInstanceToType
				.entrySet())
		{
			if (e.getKey().getParsedData().getViews() == bf)
			{
				result.add(e.getKey());
			}
		}
		return result;
	}

	public BackupFile getBackupFileFromName(final String name)
	{
		for (BackupFile bf : this.backupFiles)
			if (bf.getCompleteOriginalFileName().equals(name))
				return bf;
		return null;
	}

	/**
	 * Get Calendar details extracted from the parsed backup files
	 * 
	 * @return Calendar Details
	 */
	public TimeBasedInformation getCalendar()
	{
		return getParsedDataForType(BackupFileView.CALENDARS)
				.getContentbyInterface(TimeBasedInformation.class);
	}

	/**
	 * Get the call history extracted from the parsed backup files
	 * 
	 * @return the call history
	 */
	public CallHistory getCallHistory()
	{
		return getParsedDataForType(BackupFileView.CALL_HISTORY)
				.getContentbyInterface(CallHistory.class);
	}

	public BackupConfigurationElements getConfigElements()
	{
		return this.configElements;
	}

	/**
	 * Provides a breakdown of the types of backup file found - returned as a
	 * Map containing a descriptor of the type and a count of its occurance. The
	 * types are provided by the BackupFileType enumeration.
	 * 
	 * @see BackupFileType
	 * 
	 * @return a map containing a BackupFileType for each found type of parsed
	 *         file (the key) and a count of its occurance (value)
	 */
	public Map<BackupFileType, Integer> getCountOfBackupFileTypes()
	{
		Map<BackupFileType, Integer> result = new HashMap<BackupFileType, Integer>();
		for (Entry<BackupFile, BackupFileType> e : this.backupFileInstanceToType
				.entrySet())
		{
			if (result.get(e.getValue()) == null)
			{
				result.put(e.getValue(), 1);
			} else
			{
				result.put(e.getValue(), result.get(e.getValue()) + 1);
			}
		}
		return result;
	}

	public Object getGlobalVariable(final String key)
	{
		if (this.globalVars.containsKey(key))
			return this.globalVars.get(key);
		else
			return "Unknown variable '" + key + '\'';
	}

	public MailAccounts getMailAccounts()
	{
		return getParsedDataForType(BackupFileView.MAIL_ACCOUNTS)
				.getContentbyInterface(MailAccounts.class);
	}

	/**
	 * Get SMSs extracted from the parsed backup files
	 * 
	 * @return SMSs
	 */
	public Messages getMessages()
	{
		return getParsedDataForType(BackupFileView.SMS).getContentbyInterface(
				Messages.class);
	}

	/**
	 * Get the notes stored in this backup
	 * 
	 * @return the note folder containing all notes
	 */
	public Notes getNotes()
	{
		return getParsedDataForType(BackupFileView.NOTES)
				.getContentbyInterface(Notes.class);
	}

	/**
	 * Get information about the parsed backup files
	 * 
	 * @return A collection containing a description of every file parsed -
	 *         including it's real name (the original one held on the device)
	 *         and a link to the unwrapped contents
	 */
	public Collection<BackupFile> getParsedFiles()
	{
		return new HashSet<BackupFile>(this.backupFiles);
	}

	public SafariBookmarks getSafariBookmarks()
	{
		return getParsedDataForType(BackupFileView.SAFARI_BOOKMARKS)
				.getContentbyInterface(SafariBookmarks.class);

	}

	public Set<Location> locate(final LocationMatcher locationMatcher)
	{
		Set<Location> results = new HashSet<Location>();
		for (BackupFile bf : this.backupFiles)
		{
			results.addAll(bf.getParsedData().match(
					locationMatcher.getTypeMatcher()));
		}
		return results;
	}

	public Map<BackupFile, Set<Location>> locateGrouped(
			final LocationMatcher parse)
	{
		return groupResults(locate(parse));
	}

	public void recreateBackupDirectory(final File directory)
	{
		throw new UnsupportedOperationException(
				"Can not reform backup directory");
		// for (EncodedBackupFile bf : backupFiles)
		// {
		// try
		// {
		// MessageDigest md = MessageDigest.getInstance("SHA");
		// md.update(toChapter1);
		// MessageDigest tc1 = md.clone();
		// byte[] toChapter1Digest = tc1.digest();
		// } catch (CloneNotSupportedException cnse)
		// {
		// throw new DigestException(
		// "couldn't make digest of partial content");
		// }
		// catch (NoSuchAlgorithmException e)
		// {
		// e.printStackTrace();
		// }
		// }
	}

	/**
	 * Restore the entire directory to an unspecified temporary directory which
	 * is returned
	 * 
	 * @return the temporary location all files were unpacked to
	 * @throws IOException
	 */
	public File restoreDirectory() throws IOException
	{
		// to hold files as they were on the original device
		File deviceRootDir = monkeypuzzle.io.util.IoUtils
				.createTempDir(".deviceRoot");
		restoreDirectory(deviceRootDir);
		return deviceRootDir;
	}

	/**
	 * Restore the entire directory to a specified directory
	 * 
	 * @param directory
	 *            the directory to restore the backup folder to
	 * @throws IOException
	 */
	public void restoreDirectory(final File directory) throws IOException
	{
		for (BackupFile bf : this.backupFiles)
		{
			bf.restoreFile(directory);
		}
	}

	/**
	 * Search for the given String amongst all different views (text, hex,
	 * metadata, pList, etc. that might exist for all results
	 * 
	 * @param searchString
	 *            string to search for
	 * @return a set returning results for every type of view possible on this
	 *         data
	 * @throws NavigateException
	 */
	public Set<Location> search(final TextSearchAlgorithm searchType,
			final String searchString) throws NavigateException
	{
		Set<Location> result = new HashSet<Location>();
		for (BackupFile bf : this.backupFiles)
		{
			result.addAll(bf.getParsedData().search(searchType, searchString));
		}
		return result;
	}

	/**
	 * As search but return the locations grouped by the file they are in
	 * 
	 * @param searchType
	 * @param searchString
	 * @return
	 * @throws NavigateException
	 */
	public Map<BackupFile, Set<Location>> searchGrouped(
			final TextSearchAlgorithm searchType, final String searchString)
			throws NavigateException
	{
		return groupResults(search(searchType, searchString));
	}

	void addBackupFile(final BackupFile backupFile)
	{
		this.backupFiles.add(backupFile);
		this.backupFileInstanceToType.put(backupFile, backupFile
				.getParserType());
	}

	private ParsedData getParsedDataForType(final BackupFileView bf)
	{
		List<BackupFile> temp = getBackupFileByType(bf);
		if (temp.size() >= 1)
			return temp.get(0).getParsedData();
		else
			return null;
	}

	private Map<BackupFile, Set<Location>> groupResults(
			final Set<Location> results)
	{
		final TreeMapContainingSet<BackupFile, Location> files = new TreeMapContainingSet<BackupFile, Location>();
		for (Location location : results)
		{
			files.put(location.getBackupFile(), location);
		}
		return files;
	}

	public void restoreFiles(Collection<BackupFile> files, File directory) throws IOException
	{
		for (BackupFile bf : files)
		{
			bf.restoreFile(directory);
		}
		
	}

}
