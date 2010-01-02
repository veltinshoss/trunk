package monkeypuzzle.central;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import monkeypuzzle.central.backupfile.BackupFile;

abstract class IPhoneParser
{

	protected static final class NullProgressIndicator implements
			ProgressIndicator
	{
		@Override
		public void progressUpdate(final int entry, final int outOf,
				final String description)
		{
			// do nothing
		}
	}

	private ProgressIndicator progressIndicator;
	protected Map<BackupFile, BackupFileType> backupFileInstanceToType = new HashMap<BackupFile, BackupFileType>();
	protected List<BackupFile> backupFiles = new ArrayList<BackupFile>();

	protected Map<String, Object> globalVars = new HashMap<String, Object>();

	protected IPhoneParser(final ProgressIndicator progressIndicator)
			throws IOException
	{
		// to hold processed files
		this.progressIndicator = progressIndicator;
	}

	public abstract IPhone getIphoneConfiguration();

	protected ProgressIndicator getProgressIndicator()
	{
		return this.progressIndicator;
	}

}