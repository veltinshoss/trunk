package monkeypuzzle.ui.swing;

import java.util.Map;
import java.util.Set;

import monkeypuzzle.central.IPhone;
import monkeypuzzle.central.NavigateException;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.results.Location;
import monkeypuzzle.results.MatcherException;

public interface SearchPane
{

	Map<BackupFile, Set<Location>> search(IPhone backupDir)
			throws MatcherException, NavigateException;

}
