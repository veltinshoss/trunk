package monkeypuzzle;

import java.io.InputStream;

import monkeypuzzle.io.util.IoUtils;

/**
 * Provides support for help / about
 * 
 * @author Leo
 * 
 */
public class About
{

	public static final int MAJOR_VERSION = 0;
	public static final int MINOR_VERSION = 7;
	public static final String VERSION_MODIFIER = "alpha";

	public static String getAll()
	{
		return getCopyright() + "\n" + getVersion() + "\n" + getCredits();
	}

	public static String getCopyright()
	{
		return "Copyright 2008/2009 Mat Proud and Leo Crawford";
	}

	public static String getCredits()
	{
		InputStream is = About.class.getResourceAsStream("/credits.txt");
		if (is == null)
			return "credits.txt could not be found. Please check out documentation for credits.";
		else
			return IoUtils.convertStreamToString(is);
	}

	public static String getVersion()
	{
		return MAJOR_VERSION + "." + MINOR_VERSION + " " + VERSION_MODIFIER;
	}
}
