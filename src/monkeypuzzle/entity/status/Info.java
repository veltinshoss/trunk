package monkeypuzzle.entity.status;

import monkeypuzzle.io.parser.plist.dynamicproxy.PListAnnotationEntry;

/** Autogenerated from Info.plist */
public interface Info
{
	public interface ITunesFiles
	{
		@PListAnnotationEntry("iTunesPrefs")
		public byte[] getITunesPrefs();

		@PListAnnotationEntry("PhotosFolderAlbums")
		public byte[] getPhotosFolderAlbums();

		@PListAnnotationEntry("PhotosFolderPrefs")
		public byte[] getPhotosFolderPrefs();
	}

	@PListAnnotationEntry("Build Version")
	public java.lang.String getBuildVersion();

	@PListAnnotationEntry("Device Name")
	public java.lang.String getDeviceName();

	@PListAnnotationEntry("Display Name")
	public java.lang.String getDisplayName();

	@PListAnnotationEntry("GUID")
	public java.lang.String getGUID();

	@PListAnnotationEntry("iTunes Files")
	public ITunesFiles getITunesFiles();

	@PListAnnotationEntry("iTunes Version")
	public java.lang.String getITunesVersion();

	@PListAnnotationEntry("Last Backup Date")
	public java.util.Date getLastBackupDate();

	@PListAnnotationEntry("Phone Number")
	public java.lang.String getPhoneNumber();

	@PListAnnotationEntry("Product Type")
	public java.lang.String getProductType();

	@PListAnnotationEntry("Product Version")
	public java.lang.String getProductVersion();

	@PListAnnotationEntry("Serial Number")
	public java.lang.String getSerialNumber();

	@PListAnnotationEntry("Target Identifier")
	public java.lang.String getTargetIdentifier();

	@PListAnnotationEntry("Target Type")
	public java.lang.String getTargetType();

	@PListAnnotationEntry("Unique Identifier")
	public java.lang.String getUniqueIdentifier();
}