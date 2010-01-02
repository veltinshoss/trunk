package monkeypuzzle.entity.status;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.io.parser.plist.PListContainer;
import monkeypuzzle.io.parser.plist.PListFactory;

public class ManifestDecoder
{
	private PListContainer manifest;

	public ManifestDecoder(final byte[] data) throws FileParseException,
			IOException
	{
		this.manifest = PListFactory.createParser(
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