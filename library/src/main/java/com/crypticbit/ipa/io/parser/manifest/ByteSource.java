package com.crypticbit.ipa.io.parser.manifest;

import java.io.IOException;

public interface ByteSource {

	public boolean hasNext();
	public int getNext() throws IOException;
	
}
