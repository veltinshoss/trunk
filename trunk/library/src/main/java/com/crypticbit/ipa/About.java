package com.crypticbit.ipa;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import com.crypticbit.ipa.io.util.IoUtils;

/**
 * Provides support for help / about
 * 
 * @author Leo
 * 
 */
public class About
{

	public static String getAll()
	{
		return getCopyright() + "\n" + getVersion() + "\n" + getCredits();
	}

	public static String getCopyright()
	{
		return "Copyright 2008-2012 crypticbit.com";
	}


	public static URL getCredits()
	{
		return About.class.getResource("/credits.txt");
	}

	
	public static URL getDependencies()
	{
		return About.class.getResource("/dependencies.html"); 
	}
	
	public static URL getLicence()
	{
		return About.class.getResource("/licence.html"); 
	}

	public static boolean showMessage()
	{
		return false;
	}

	public static String getVersion()
	{
		String implementationVersion = About.class.getPackage().getImplementationVersion();
		return implementationVersion == null ? "unknown" : implementationVersion;
	}
}
