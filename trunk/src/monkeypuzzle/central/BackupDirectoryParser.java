package monkeypuzzle.central;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.central.backupfile.BackupFileException;
import monkeypuzzle.central.filters.MdbackupFilenameFilter;
import monkeypuzzle.central.filters.MddataFilenameFilter;
import monkeypuzzle.entity.status.Info;
import monkeypuzzle.entity.status.Manifest;
import monkeypuzzle.entity.status.Status;
import monkeypuzzle.io.parser.plist.PListContainer;
import monkeypuzzle.io.parser.plist.PListFactory;

public class BackupDirectoryParser extends IPhoneParser {

	private static PListContainer findContainerForFile(final File file)
			throws IOException, FileParseException, FileNotFoundException {
		return PListFactory.createParser(new FileInputStream(file))
				.getRootContainer();
	}

	private File sourceDir;
	private IPhoneFactory factory;

	private IPhone bd;

	public static String getBackupSummary(File sourceDir)
			throws FileParseException, IOException {
		Info info = getConfiguration(sourceDir).getInfo();
		if (info == null)
			return "unknown backup (" + sourceDir.getAbsolutePath() + ")";
		else
			return info.getProductType() + ": " + info.getDeviceName() + " v"
					+ info.getProductVersion() + " - "
					+ info.getLastBackupDate();
	}

	BackupDirectoryParser(final IPhoneFactory factory, final File sourceDir,
			final ProgressIndicator progressIndicator)
			throws IPhoneParseException, FileParseException, IOException {
		super(progressIndicator);
		this.factory = factory;
		this.sourceDir = sourceDir;
		BackupConfigurationElements elements = getConfiguration(sourceDir);
		this.bd = new IPhone(getGlobalVars(elements), elements);
		try {
			// get list of mdbackup files
			File[] mdbackups = sourceDir
					.listFiles(new MdbackupFilenameFilter());
			File[] mddata = sourceDir.listFiles(new MddataFilenameFilter());

			if ((mdbackups == null) || (mddata == null))
				throw new IPhoneParseException("Problem accessing directory");

			if ((mdbackups.length == 0) && (mddata.length == 0))
				throw new IPhoneParseException(
						"No backup files found in directory");

			parseEncodedFiles(mdbackups);
			parseEncodedFiles(mddata);
		} catch (BackupFileException bfe) {
			throw new IPhoneParseException("Unable to parse directory \""
					+ sourceDir + "\"", bfe);
		}

	}

	public File getDirectory() {
		return this.sourceDir;
	}

	@Override
	public IPhone getIphoneConfiguration() {
		return this.bd;
	}

	private static BackupConfigurationElements getConfiguration(File sourceDir)
			throws FileParseException, IOException {

		Manifest manifest = null;
		Info info = null;
		Status status = null;

		try {
			info = findContainerForFile(new File(sourceDir, "Info.plist"))
					.getAsInterface(Info.class);
			// add all info fields to globalVars - ordered by method name
		} catch (FileNotFoundException e) {
			// fail silently
			System.err.println("Info.plist was not found");
		}
		try {
			manifest = findContainerForFile(
					new File(sourceDir, "Manifest.plist")).getAsInterface(
					Manifest.class);
		} catch (FileNotFoundException e) {
			// fail silently
			System.err.println("Manifest.plist was not found");
		}
		try {
			status = findContainerForFile(new File(sourceDir, "Status.plist"))
					.getAsInterface(Status.class);
		} catch (FileNotFoundException e) {
			// fail silently
			System.err.println("Status.plist was not found");
		}
		return new BackupConfigurationElements(manifest, status, info);
	}

	private Map<String, Object> getGlobalVars(
			final BackupConfigurationElements elements) {
		this.globalVars = new HashMap<String, Object>();
		this.globalVars.put("info.buildVersion", elements.getInfo()
				.getBuildVersion());
		this.globalVars.put("info.deviceName", elements.getInfo()
				.getDeviceName());
		this.globalVars.put("info.displayName", elements.getInfo()
				.getDisplayName());
		this.globalVars.put("info.guid", elements.getInfo().getGUID());
		this.globalVars.put("info.itunesVersion", elements.getInfo()
				.getITunesVersion());
		this.globalVars.put("info.lastBackupDate", elements.getInfo()
				.getLastBackupDate());
		this.globalVars.put("info.phoneNumber", elements.getInfo()
				.getPhoneNumber());
		this.globalVars.put("info.productType", elements.getInfo()
				.getProductType());
		this.globalVars.put("info.productVersion", elements.getInfo()
				.getProductVersion());
		this.globalVars.put("info.serialNumber", elements.getInfo()
				.getSerialNumber());
		this.globalVars.put("info.targetIdentifier", elements.getInfo()
				.getTargetIdentifier());
		this.globalVars.put("info.targetType", elements.getInfo()
				.getTargetType());
		this.globalVars.put("info.uniqueID", elements.getInfo()
				.getUniqueIdentifier());
		return this.globalVars;
	}

	private void parseEncodedFiles(final File[] files)
			throws BackupFileException, IOException

	{
		int count = 0;
		for (File f : files) {
			BackupFile bfd = this.factory.createIPhoneFileFromEncoded(f);
			this.bd.addBackupFile(bfd);
			getProgressIndicator().progressUpdate(count++, files.length,
					bfd.getCompleteOriginalFileName());
		}
	}

}
