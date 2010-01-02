package monkeypuzzle.results;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import monkeypuzzle.central.BackupFileView;
import monkeypuzzle.central.NavigateException;

public abstract class ParsedDataImpl implements ParsedData
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see monkeypuzzle.results.ParsedData#getAvailableInterfaces()
	 */
	@Override
	public Collection<Class<?>> getAvailableInterfaces()
	{
		if ((getViews() == null)
				|| (getViews().getAvailableInterface() == null))
			return Collections.emptySet();
		else
			return Arrays.asList(new Class<?>[] { getViews()
					.getAvailableInterface() });
	}

	public BackupFileView getViews()
	{
		return BackupFileView.find(getBackupFile());
	}

	@Override
	public Set<Location> search(final TextSearchAlgorithm searchType,
			final String searchString) throws NavigateException
	{
		Set<Location> result = new HashSet<Location>();
		for (Map.Entry<Integer, String> entry : searchType.search(searchString,
				getContents()).entrySet())
		{
			result.add(new TextLocation(this, getBackupFile(),
					entry.getValue(), entry.getKey()));
		}
		return result;
	}

}
