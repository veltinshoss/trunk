package monkeypuzzle;

import java.io.File;
import java.io.IOException;

import monkeypuzzle.central.IPhoneFactory;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.io.parser.plist.PListArray;
import monkeypuzzle.io.parser.plist.PListDict;
import monkeypuzzle.io.parser.plist.PListResults;
import monkeypuzzle.licence.UnlimitedLicenceValidator;
import monkeypuzzle.results.ContentType;
import monkeypuzzle.results.Location;
import monkeypuzzle.results.ParsedData;
import monkeypuzzle.results.TextSearchAlgorithm;

/**
 * Demo code to show some basic functionality
 * 
 * @author Leo
 * 
 */
public class FileExample
{

	public interface Root
	{
		public interface PreferenceSpecifiers
		{
			// pick some random fields
			public boolean getIsSecure();

			public String getTitle();

			public String getType();
		}

		// tjis returns an array
		public PreferenceSpecifiers[] getPreferenceSpecifiers();
	}

	public static File createTempDir(final String suffix) throws IOException
	{
		// create temp dir
		File tempDir = File.createTempFile("test", suffix);
		// create a file
		tempDir.delete(); // delete it
		tempDir.mkdir(); // replace it with a dir
		tempDir.deleteOnExit(); // set to be deleted when we exit.
		return tempDir;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception
	{
		IPhoneFactory factory = new IPhoneFactory(
				UnlimitedLicenceValidator.DEFAULT_INSTANCE);
		BackupFile bf = factory
				.createIPhoneFileFromEncoded(new File(
						"testData/firmware-2.0/01/835306ba2667437df8cb8781a25d97c49a783e83/0a0f3c65288f9b412dbfc0d8166806a605ad61db.mdbackup"));

		System.out.println("The original filename was : "
				+ bf.getCompleteOriginalFileName());
		System.out.println("It was parsed as a : " + bf.getParserType());

		// we could do
		// bf.restoreFile(directory)
		// if we wanted to restore the file

		ParsedData data = bf.getParsedData();

		System.out.println("The summary content is : " + data.getSummary());
		System.out.println("The full content is : " + data.getContents());

		PListResults pListData = (PListResults) data;

		// we can access data in a non typesafe way
		PListArray array = (PListArray) ((PListDict) pListData
				.getRootContainer()).get("PreferenceSpecifiers");
		PListDict dict = (PListDict) array.get(1);
		System.out.println("PreferenceSpecifiers.1.Key = " + dict.get("Key"));

		// or we describe the position we are looking for:
		for (Location l : data.match(ContentType.PLIST
				.getMatcher("PreferenceSpecifiers/1/Key")))
		{
			System.out.println(l + "=" + l.getLocationExtract());
		}

		// which can hit multiple entries

		for (Location l : data.match(ContentType.PLIST
				.getMatcher("PreferenceSpecifiers/#/Key")))
		{
			System.out.println(l + "=" + l.getLocationExtract());
		}

		// or we can access in a typesafe way
		Root root = pListData.getRootContainer().getAsInterface(Root.class);
		System.out.println("PreferenceSpecifiers.1.IsSecure = "
				+ root.getPreferenceSpecifiers()[1].getIsSecure());
		System.out.println("PreferenceSpecifiers.1.Title = "
				+ root.getPreferenceSpecifiers()[1].getTitle());

		// or we can search

		for (Location l : data.search(TextSearchAlgorithm.CASE_INSENSITIVE,
				"psTextFieldSpecifier"))
		{
			System.out.println("Found at: " + l + " containing "
					+ l.getLocationExtract());
		}

		for (Location l : data.search(TextSearchAlgorithm.REGULAR_EXPRESSION,
				".*(na|NA).*"))
		{
			System.out.println("Found at: " + l + " containing "
					+ l.getLocationExtract());
		}
	}

}
