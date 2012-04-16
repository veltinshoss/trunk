package com.crypticbit.ipa.io.parser.manifest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.filters.NoExtension40DigitHexFilenameFilter;
import com.crypticbit.ipa.licence.WebValidator;

public class ManifestParser
{

	private Map<String, MbdbRecord> result = new HashMap<String, MbdbRecord>();
	private boolean failed;
	private File rootDirectory;

	public ManifestParser(File rootDirectory)
	{
		this.rootDirectory = rootDirectory;
		try
		{
			// details from
			// http://stackoverflow.com/questions/6569004/how-to-parse-the-manifest-mbdb-file-in-an-ios-5-0-beta-2-without-manifest-mbdx
			// Mbdx mbdx = new Mbdx(new FileInputStream(new File(rootDirectory,
			// "Manifest.mbdx")));
			Mbdb mbdb = new Mbdb(new FileInputStream(new File(rootDirectory,
					"Manifest.mbdb")));

			MessageDigest md = MessageDigest.getInstance("SHA-1");

			File[] files = rootDirectory
					.listFiles(new NoExtension40DigitHexFilenameFilter());

			Map<String, MbdbRecord> hashLookup = new HashMap<String, MbdbRecord>();
			for (MbdbRecord record : mbdb)
			{
				String s = record.getDomain() + "-" + record.getPath();
				String d = WebValidator.convertToSha1(s);
				hashLookup.put(d, record);
			}

			for (File f : files)
			{
				MbdbRecord x = hashLookup.get(f.getName());
				result.put(f.getName(), x);
			}

		} catch (IOException ioe)
		{
			failed = true;
		} catch (FileParseException e)
		{
			failed = true;
		} catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean succeeded()
	{
		return !failed & result.size() > 0;
	}

	public String getRealFilePath(File file)
	{
		return getRealFilePath(file.getAbsolutePath().substring(
				rootDirectory.getAbsolutePath().length() + 1));
	}

	public String getRealFilePath(String fileName)
	{
		MbdbRecord record = result.get(fileName);
		if (record == null)
			return null;
		return record.getPath();
	}

}
