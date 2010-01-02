package monkeypuzzle.central.backupfile.filedefs;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.io.parser.plist.PListFactory;

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
					.createParser(new ByteArrayInputStream(data))
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
