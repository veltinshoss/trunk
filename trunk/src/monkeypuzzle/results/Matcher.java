/**
 * 
 */
package monkeypuzzle.results;

import java.io.Serializable;
import java.util.Set;

import monkeypuzzle.central.backupfile.BackupFile;

public interface Matcher extends Serializable
{
	public Set<Location> match(BackupFile bfd, Object objectToMatch);
}