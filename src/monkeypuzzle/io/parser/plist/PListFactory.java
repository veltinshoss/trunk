package monkeypuzzle.io.parser.plist;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.io.parser.plist.bin.BplParser;
import monkeypuzzle.io.parser.plist.xml.XmlPListParser;

public class PListFactory
{
	public static PListHeader createParser(final InputStream is)
			throws IOException, FileParseException
	{
		BufferedInputStream bis = new BufferedInputStream(is);
		bis.mark(100);
		// would be good to resuse byte buffer, but seems complicated
		ReadableByteChannel c = Channels.newChannel(bis);
		ByteBuffer buffer = ByteBuffer.allocate(100);
		c.read(buffer);
		buffer.clear();
		bis.reset();
		if (BplParser.checkHeader(buffer))
			return new BplParser(bis);
		else
			return new XmlPListParser(bis);
	}
}
