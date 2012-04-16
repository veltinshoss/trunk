package com.crypticbit.ipa.entity.status;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.io.parser.plist.PListContainer;
import com.crypticbit.ipa.io.parser.plist.PListFactory;


public class ManifestDecoder
{
	private PListContainer manifest;

	public ManifestDecoder(final byte[] data) throws FileParseException,
			IOException
	{
		this.manifest = PListFactory.createParser(null,
				new ByteArrayInputStream(data)).getRootContainer();

	}

	public PListContainer getContainer()
	{
		return this.manifest;
	}

	public Manifest.ManifestEntry getManifestEntry()
	{
		return this.manifest.getAsInterface(Manifest.ManifestEntry.class);
	}
}