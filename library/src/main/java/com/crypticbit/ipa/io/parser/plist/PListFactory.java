package com.crypticbit.ipa.io.parser.plist;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.io.parser.plist.bin.BplParser;
import com.crypticbit.ipa.io.parser.plist.xml.XmlPListParser;

public class PListFactory
{
	public static PListHeader createParser(BackupFile bfd) throws IOException,
			FileParseException
	{
		return createParser(bfd, bfd.getContentsInputStream());
	}

	public static PListHeader createParser(BackupFile bfd, InputStream is)
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
			return new BplParser(bfd, bis);
		else
			return new XmlPListParser(bfd, bis);
	}

}
