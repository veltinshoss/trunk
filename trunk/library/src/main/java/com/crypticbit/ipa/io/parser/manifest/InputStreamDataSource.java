/**
 * 
 */
package com.crypticbit.ipa.io.parser.manifest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

class InputStreamDataSource extends DataSource {
	private BufferedInputStream bis;

	InputStreamDataSource(InputStream is) {
		bis = new BufferedInputStream(is);
	}

	@Override
	public int getNext_() throws IOException {
		return bis.read();
	}

	@Override
	public boolean hasNext() {
		try {
			return bis.available() > 0;
		} catch (IOException e) {
			return false;
		}
	}
}