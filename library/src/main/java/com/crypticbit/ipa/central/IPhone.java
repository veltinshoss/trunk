package com.crypticbit.ipa.central;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.entity.concept.ConceptException;
import com.crypticbit.ipa.entity.concept.Conceptable;
import com.crypticbit.ipa.entity.concept.wrapper.ConceptFactory;
import com.crypticbit.ipa.entity.concept.wrapper.impl.EventList;
import com.crypticbit.ipa.entity.settings.DateTimePrefs;
import com.crypticbit.ipa.entity.sqlite.AddressBook;
import com.crypticbit.ipa.entity.sqlite.AddressBook.ContactLabel.ContactType;
import com.crypticbit.ipa.entity.sqlite.AddressBook.Person;
import com.crypticbit.ipa.entity.sqlite.AddressBook.Person.ContactItem;
import com.crypticbit.ipa.io.parser.BadFileFormatException;
import com.crypticbit.ipa.io.util.TreeMapContainingSet;
import com.crypticbit.ipa.results.Location;
import com.crypticbit.ipa.results.ParsedData;
import com.crypticbit.ipa.results.TextSearchAlgorithm;

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
public class IPhone {
	// key details of included backup files
	private Map<BackupFile, BackupFileType> backupFileInstanceToType = new HashMap<BackupFile, BackupFileType>();
	private List<BackupFile> backupFiles = new ArrayList<BackupFile>();
	private Map<String, Object> globalVars;
	private BackupConfigurationElements configElements;
	private ConceptFactory conceptFactory;

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
	 * 		LogFactory.getLogger().log(Level.INFO,bfd+&quot; ==&gt; &quot;+backupDir.findBackupFileParser(bfd).getSummary());
	 * 	}
	 * 	
	 * 	// list the breakdown of files by type
	 * 	LogFactory.getLogger().log(Level.INFO,backupDir.getCountOfBackupFileTypes());
	 * 	
	 * 	// list the contacts - or alternatively get them and use the API to display them in any other way
	 * 	LogFactory.getLogger().log(Level.INFO,backupDir.getContacts().getContents());
	 * 	..
	 * 	LogFactory.getLogger().log(Level.INFO,backupDir.getSMS().getContents());
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
			throws IOException, FileParseException, IPhoneParseException {
		this.globalVars = globalVars;
		this.configElements = configElements;
	}

	public EventList getAllEvents(final ProgressIndicator progressIndicator) {

		if (events == null) {
			events = new EventList();
			int loop = 0;
			int size = this.backupFileInstanceToType.keySet().size();
			for (BackupFile bf : this.backupFileInstanceToType.keySet()) {

				progressIndicator.progressUpdate(loop++, size,
						bf.getOriginalFileName());

				ParsedData pd = bf.getParsedData();
				// if (pd != null)
				for (Class i : pd.getSubInterfaces()) {
					if (Conceptable.class.isAssignableFrom(i))
						try {
							events.addAll(getConceptFactory().createWrapper(
									(Collection<Conceptable>) pd
											.getRecordsByInterface(i)));
						} catch (FileParseException e) {
							// FIXME
							// TODO Auto-generated catch block
							LogFactory.getLogger().log(Level.SEVERE,
									"Exception", e);
						} catch (ConceptException e) {
							// TODO Auto-generated catch block
							LogFactory.getLogger().log(Level.SEVERE,
									"Exception", e);
						}
				}
			}

		}
		return events;
	}

	private ConceptFactory getConceptFactory() {
		if (conceptFactory == null)
			conceptFactory = new ConceptFactory(getDefaultLocale());
		return conceptFactory;
	}

	public <I> Collection<I> getRecordsByInterface(Class<I> clazz) {
		BackupFileView bfv = BackupFileView.findByInterface(clazz);
		if (bfv != null) {
			ParsedData parsedDataForType = getParsedDataForType(bfv);
			try {
				return parsedDataForType == null ? null : parsedDataForType
						.getRecordsByInterface(clazz);
			} catch (BadFileFormatException e) {
				LogFactory.getLogger().log(Level.SEVERE, "Exception", e);
				LogFactory.getLogger().log(Level.INFO,
						"Ignoring badly formed file");
				return null;
			} catch (FileParseException e) {
				e.printStackTrace();
				return null;
			}
		} else
			return null;
	}

	public <I> I getByInterface(Class<I> clazz) {
		BackupFileView bfv = BackupFileView.findByInterface(clazz);
		if (bfv != null) {
			ParsedData parsedDataForType = getParsedDataForType(bfv);
			try {
				return parsedDataForType == null ? null : parsedDataForType
						.getContentbyInterface(clazz);
			} catch (BadFileFormatException e) {
				LogFactory.getLogger().log(Level.SEVERE, "Exception", e);
				LogFactory.getLogger().log(Level.INFO,
						"Ignoring badly formed file");
				return null;
			}
		} else
			return null;
	}

	public List<BackupFile> getBackupFileByType(final BackupFileType bf) {
		List<BackupFile> result = new ArrayList<BackupFile>();
		for (Entry<BackupFile, BackupFileType> e : this.backupFileInstanceToType
				.entrySet()) {
			if (e.getValue() == bf) {
				result.add(e.getKey());
			}
		}
		return result;
	}

	/**
	 * Find all files which are displayed in a given view
	 * 
	 * @param backupFileView
	 * @return list of backup files for this view
	 */
	public List<BackupFile> getBackupFileByType(
			final BackupFileView backupFileView) {

		List<BackupFile> result = new ArrayList<BackupFile>();

		for (Entry<BackupFile, BackupFileType> e : this.backupFileInstanceToType
				.entrySet()) {
			BackupFile currentFile = e.getKey();
			BackupFileType currentType = currentFile.getParserType(); // PLIST,
																		// MEDIA,
																		// SQL
																		// etc

			// LogFactory.getLogger().log(Level.INFO,"CurrentFile: " +
			// currentFile.getOriginalFileName() + ", currentType: " +
			// currentType);
			BackupFileView testView = BackupFileView.find(currentFile);
			// LogFactory.getLogger().log(Level.INFO,"targetView:" +
			// backupFileView + ", testView:"
			// + testView);
			if (testView == backupFileView)
			// if (currentFile.getParsedData().getViews() == backupFileView)
			// calling currentFile.getParsedData() in this loop meant reading in
			// every file on the device which killed performance.
			{
				result.add(currentFile);
			}
		}
		return result;
	}

	public BackupFile getBackupFileFromName(final String name) {
		for (BackupFile bf : this.backupFiles)
			if (bf.getCompleteOriginalFileName().equals(name))
				return bf;
		return null;
	}

	public BackupConfigurationElements getConfigElements() {
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
	public Map<BackupFileType, Integer> getCountOfBackupFileTypes() {
		Map<BackupFileType, Integer> result = new HashMap<BackupFileType, Integer>();
		for (Entry<BackupFile, BackupFileType> e : this.backupFileInstanceToType
				.entrySet()) {
			if (result.get(e.getValue()) == null) {
				result.put(e.getValue(), 1);
			} else {
				result.put(e.getValue(), result.get(e.getValue()) + 1);
			}
		}
		return result;
	}

	public Object getGlobalVariable(final String key) {
		if (this.globalVars.containsKey(key))
			return this.globalVars.get(key);
		else
			return "Unknown variable '" + key + '\'';
	}

	/**
	 * Get information about the parsed backup files
	 * 
	 * @return A collection containing a description of every file parsed -
	 *         including it's real name (the original one held on the device)
	 *         and a link to the unwrapped contents
	 */
	public Collection<BackupFile> getParsedFiles() {
		return new HashSet<BackupFile>(this.backupFiles);
	}

	public void recreateBackupDirectory(final File directory) {
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
		// LogFactory.getLogger().log(Level.SEVERE,"Exception",e);
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
	public File restoreDirectory() throws IOException {
		// to hold files as they were on the original device
		File deviceRootDir = com.crypticbit.ipa.io.util.IoUtils
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
	public void restoreDirectory(final File directory) throws IOException {
		for (BackupFile bf : this.backupFiles) {
			try {
				bf.restoreFile(directory);
			} catch (IOException e) {
				LogFactory.getLogger().log(
						Level.WARNING,
						"Didn't export file " + bf.getOriginalFileName()
								+ " because " + e.getMessage());
			}

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
			final String searchString) {
		Set<Location> result = new HashSet<Location>();
		for (BackupFile bf : this.backupFiles) {
			try {
				result.addAll(bf.getParsedData().search(searchType,
						searchString));
			} catch (Exception e) {
				// FIXME
				e.printStackTrace();
			}
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
			throws NavigateException {
		return groupResults(search(searchType, searchString));
	}

	void addBackupFile(final BackupFile backupFile) {
		this.backupFiles.add(backupFile);
		this.backupFileInstanceToType.put(backupFile,
				backupFile.getParserType());
	}

	private ParsedData getParsedDataForType(final BackupFileView bf) {
		List<BackupFile> temp = getBackupFileByType(bf);
		if (temp.size() >= 1)
			return temp.get(0).getParsedData();
		else
			return null;
	}

	private Map<BackupFile, Set<Location>> groupResults(
			final Set<Location> results) {
		final TreeMapContainingSet<BackupFile, Location> files = new TreeMapContainingSet<BackupFile, Location>();
		for (Location location : results) {
			files.putSet(location.getBackupFile(), location);
		}
		return files;
	}

	public void restoreFiles(Collection<BackupFile> files, File directory)
			throws IOException {
		for (BackupFile bf : files) {
			bf.restoreFile(directory);
		}

	}

	private Map<String, String> peopleLookup;
	private EventList events;

	public String lookupNumber(String address) {
		if (peopleLookup == null) {
			peopleLookup = new HashMap<String, String>();
			AddressBook ab;
			try {
				ab = getParsedDataForType(BackupFileView.ADDRESS_BOOK)
						.getContentbyInterface(AddressBook.class);
			} catch (BadFileFormatException e) {
				return address;
			}
			for (Person p : ab)
				for (ContactItem c : p.getContactDetails())
					if (c.getContactType() == ContactType.PHONE_NUMBER)
						peopleLookup.put(
								telephoneSuffix(c.getValue()),
								(p.getFirstName() == null ? "" : p
										.getFirstName() + " ")
										+ (p.getLastName() == null ? "" : p
												.getLastName()));
		}

		if (peopleLookup.containsKey(telephoneSuffix(address)))
			return peopleLookup.get(telephoneSuffix(address));
		else
			return address;

	}

	private String telephoneSuffix(String telephoneNumber) {
		if (telephoneNumber == null)
			return null;
		return telephoneNumber.substring(Math.max(0,
				telephoneNumber.length() - 6));
	}

	public boolean isEventsLoaded() {
		return events != null;
	}

	private String locale = null;

	// FIXME use and improve
	public String getDefaultLocale() {
		if (locale == null) {
			LogFactory.getLogger().log(Level.INFO,
					"Trying DateTime prefs to get locale");
			try {
				ParsedData parsedDataForDateTime = getParsedDataForType(BackupFileView.DATETIME_PREFS);
				if (parsedDataForDateTime != null) {
					DateTimePrefs x = parsedDataForDateTime
							.getContentbyInterface(DateTimePrefs.class);
					locale = x.getDateTime().getLocaleCode();
				}
			} catch (Exception e1) {
				LogFactory.getLogger().log(Level.WARNING,
						"Problem finding locale : " + locale, e1);
			}

			if (locale == null) {
				LogFactory.getLogger().log(Level.INFO,
						"Trying Java Locale to get locale");
				locale = Locale.getDefault().getCountry();
			}

			if (locale == null) {
				locale = "XX";

				LogFactory.getLogger().log(Level.WARNING,
						"Setting device locale to XX as could not identify");
			}

			LogFactory.getLogger().log(Level.INFO,
					"Assuming device locale is: " + locale);
		}

		return locale;
	}
}
