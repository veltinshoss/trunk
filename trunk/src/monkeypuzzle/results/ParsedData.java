package monkeypuzzle.results;

import java.util.Collection;
import java.util.Set;

import monkeypuzzle.central.BackupFileView;
import monkeypuzzle.central.NavigateException;
import monkeypuzzle.central.backupfile.BackupFile;

public interface ParsedData
{
	public Collection<Class<?>> getAvailableInterfaces();

	public <I> I getContentbyInterface(Class<I> interfaceDef);

	/**
	 * Generate a verbose textual view of the parsed file
	 * 
	 * @return a verbose textual view of the parsed file
	 */
	public String getContents();

	/**
	 * Generate a one line textual summary of the parsed file
	 * 
	 * @return the summary
	 */
	public String getSummary();

	public BackupFileView getViews();

	public Set<Location> match(Matcher matcher);

	/**
	 * Search for the given String amongst all different views (text, hex,
	 * metadata, pList, etc. that might exist for this result type
	 * 
	 * @param searchString
	 *            string to search for
	 * @return a set containing all the found locatins
	 * @throws NavigateException
	 * 
	 */
	public Set<Location> search(TextSearchAlgorithm searchType,
			String searchString) throws NavigateException;

	BackupFile getBackupFile();
}
