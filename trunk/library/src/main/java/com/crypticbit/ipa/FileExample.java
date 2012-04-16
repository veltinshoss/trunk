package com.crypticbit.ipa;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import com.crypticbit.ipa.central.IPhoneFactory;
import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.io.parser.plist.PListArray;
import com.crypticbit.ipa.io.parser.plist.PListDict;
import com.crypticbit.ipa.io.parser.plist.PListResults;
import com.crypticbit.ipa.licence.UnlimitedLicenceValidator;
import com.crypticbit.ipa.results.ContentType;
import com.crypticbit.ipa.results.Location;
import com.crypticbit.ipa.results.ParsedData;
import com.crypticbit.ipa.results.TextSearchAlgorithm;


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

		LogFactory.getLogger().log(Level.INFO,"The original filename was : "
				+ bf.getCompleteOriginalFileName());
		LogFactory.getLogger().log(Level.INFO,"It was parsed as a : " + bf.getParserType());

		// we could do
		// bf.restoreFile(directory)
		// if we wanted to restore the file

		ParsedData data = bf.getParsedData();

		LogFactory.getLogger().log(Level.INFO,"The summary content is : " + data.getSummary());
		LogFactory.getLogger().log(Level.INFO,"The full content is : " + data.getContents());

		PListResults pListData = (PListResults) data;

		// we can access data in a non typesafe way
		PListArray array = (PListArray) ((PListDict) pListData
				.getRootContainer()).get("PreferenceSpecifiers");
		PListDict dict = (PListDict) array.get(1);
		LogFactory.getLogger().log(Level.INFO,"PreferenceSpecifiers.1.Key = " + dict.get("Key"));
//
//		// or we describe the position we are looking for:
//		for (Location l : data.match(ContentType.PLIST
//				.getMatcher("PreferenceSpecifiers/1/Key")))
//		{
//			LogFactory.getLogger().log(Level.INFO,l + "=" + l.getLocationExtract());
//		}

		// which can hit multiple entries

//		for (Location l : data.match(ContentType.PLIST
//				.getMatcher("PreferenceSpecifiers/#/Key")))
//		{
//			LogFactory.getLogger().log(Level.INFO,l + "=" + l.getLocationExtract());
//		}

		// or we can access in a typesafe way
		Root root = pListData.getRootContainer().getAsInterface(Root.class);
		LogFactory.getLogger().log(Level.INFO,"PreferenceSpecifiers.1.IsSecure = "
				+ root.getPreferenceSpecifiers()[1].getIsSecure());
		LogFactory.getLogger().log(Level.INFO,"PreferenceSpecifiers.1.Title = "
				+ root.getPreferenceSpecifiers()[1].getTitle());

		// or we can search

		for (Location l : data.search(TextSearchAlgorithm.CASE_INSENSITIVE,
				"psTextFieldSpecifier"))
		{
			LogFactory.getLogger().log(Level.INFO,"Found at: " + l + " containing "
					+ l.getLocationExtract());
		}

		for (Location l : data.search(TextSearchAlgorithm.REGULAR_EXPRESSION,
				".*(na|NA).*"))
		{
			LogFactory.getLogger().log(Level.INFO,"Found at: " + l + " containing "
					+ l.getLocationExtract());
		}
	}

}
