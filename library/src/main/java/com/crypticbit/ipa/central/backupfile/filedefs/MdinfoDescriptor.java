package com.crypticbit.ipa.central.backupfile.filedefs;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.io.parser.plist.PListFactory;


public interface MdinfoDescriptor
{
	interface MdinfoMetadataDescriptor
	{
		public String getDomain();

		public String getGreylist();

		public String getPath();

		public String getVersion();
	}

	static class MdinfoMetadataDescriptorConverter
	{
		// FIXME repeated code from MqanifestDecoder
		private MdinfoDescriptor.MdinfoMetadataDescriptor metadata;

		public MdinfoMetadataDescriptorConverter(final byte[] data)
				throws FileParseException, IOException
		{
			metadata = PListFactory
					.createParser(null,new ByteArrayInputStream(data))
					.getRootContainer().getAsInterface(
							MdinfoDescriptor.MdinfoMetadataDescriptor.class);
		}

		public MdinfoDescriptor.MdinfoMetadataDescriptor getMetadataEntry()
		{
			return metadata;
		}

	}

	public MdinfoMetadataDescriptorConverter getMetadata();

	public String getPath();
}
