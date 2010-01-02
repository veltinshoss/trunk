package monkeypuzzle.central.backupfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import monkeypuzzle.central.backupfile.filedefs.MdinfoDescriptor;
import monkeypuzzle.central.filters.MddataFilenameFilter;
import monkeypuzzle.central.filters.MdinfoFilenameFilter;
import monkeypuzzle.io.parser.plist.bin.BplParser;
import monkeypuzzle.io.util.IoUtils;

public class Version2BackupFile extends BackupFile
{
	public static final String UNKNOWN_FAKE_PATH = " UNKNOWN/";
	public static final String MDDATA_EXTENSION = ".mddata";
	public static final String MDINFO_EXTENSION = ".mdinfo";

	private static final MdinfoFilenameFilter MDINFO_FILENAME_FILTER = new MdinfoFilenameFilter();
	private static final MddataFilenameFilter MDDATA_FILENAME_FILTER = new MddataFilenameFilter();

	// TODO check that the 2 are a pair based on filename and decide what to do
	// if they're not

	public static File getCorrespondinfInfoFile(final File dataFile)
	{
		String infoFilename = IoUtils.getFilenameNoExtension(dataFile)
				+ MDINFO_EXTENSION;
		return new File(dataFile.getParent(), infoFilename);
	}

	public static File getCorrespondingDataFile(final File infoFile)
	{
		String dataFilename = IoUtils.getFilenameNoExtension(infoFile)
				+ MDDATA_EXTENSION;
		return new File(infoFile.getParent(), dataFilename);
	}

	private File contentsFile;

	public Version2BackupFile(final File dataOrInfoFile)
			throws FileNotFoundException, IOException
	{

		File infoFile = null;

		if (dataOrInfoFile == null)
			// nothing to work with
			throw new IOException("Unable to create backupFile from null File");

		String filename = dataOrInfoFile.getName();
		if (MDINFO_FILENAME_FILTER.accept(null, filename))
		{
			// it's an info file
			process(getCorrespondingDataFile(infoFile), dataOrInfoFile);

		} else if (MDDATA_FILENAME_FILTER.accept(null, filename))
		{
			// deduce matching info file
			String infoFilename = IoUtils
					.getFilenameNoExtension(dataOrInfoFile)
					+ MDINFO_EXTENSION;
			infoFile = new File(dataOrInfoFile.getParent(), infoFilename);
			// it's a data file
			process(dataOrInfoFile, infoFile);
		} else
			throw new IOException(
					"Unable to create backup file from the file provided: "
							+ dataOrInfoFile.getAbsolutePath());

	}

	public Version2BackupFile(final File dataFile, final File infoFile)
			throws FileNotFoundException, IOException
	{
		process(dataFile, infoFile);
	}

	@Override
	public File getContentsFile() throws IOException
	{
		return this.contentsFile;
	}

	@Override
	public void restoreFile(final File directory) throws IOException
	{
		// TODO support restore file
	}

	@Override
	protected byte[] createByteArrayFromBackupFile() throws IOException,
			FileNotFoundException
	{
		return IoUtils.getBytesFromFile(this.contentsFile);
	}

	private void process(final File dataFile, final File infoFile)
			throws FileNotFoundException, IOException
	{
		// Parse info file as binary plist
		this.contentsFile = dataFile;
		if ((infoFile != null) && infoFile.exists())
		{
			BplParser bplParser = new BplParser(new FileInputStream(infoFile));
			MdinfoDescriptor c = bplParser.getRootContainer().getAsInterface(
					MdinfoDescriptor.class);

			this.originalFileName = c.getPath();
			if (this.originalFileName == null)
			{
				this.originalFileName = c.getMetadata().getMetadataEntry()
						.getPath();
			}
		} else
		{
			this.originalFileName = UNKNOWN_FAKE_PATH
					+ this.contentsFile.getName();
		}
	}
}
