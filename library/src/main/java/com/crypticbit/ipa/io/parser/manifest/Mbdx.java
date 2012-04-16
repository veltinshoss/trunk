package com.crypticbit.ipa.io.parser.manifest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.crypticbit.ipa.central.FileParseException;


public class Mbdx implements Iterable<MbdxRecord> {

	private static char[] EXPECTED_HEADER = new char[] { 'm', 'b', 'd', 'x', '\2', '\0' };
	
	private DataSource ds;
	private long recordCount;
	private List<MbdxRecord> records = new ArrayList<MbdxRecord>();

	public Mbdx(final InputStream is) throws FileParseException, IOException {
		this.ds = new InputStreamDataSource(is);

		readHeader();
		for (int loop = 0; loop < recordCount; loop++)
			readRecord();
	}

	// record (fixed size of 26 bytes)
	//
	// uint8[20] the Key of the file, it's also the filename in the backup
	// directory
	// It's the same key as 9.1 backups.
	// uint32 offset of file record in .mbdb file
	// Offsets are counted from the 7th byte. So you have to add 6 to this
	// number to get the absolute position in the file.
	// uint16 file mode
	// Axxx symbolic link
	// 4xxx directory
	// 8xxx regular file
	// The meaning of xxx is unknown to me, it corresponds to the Mode field in
	// the old backup data.
	private void readRecord() throws IOException {
		MbdxRecord record = new MbdxRecord(ds.readHexCharArray(20), // the Key
				// of the
				// file
				ds.readUnsignedInt(4), // uint32 offset of file record
				(int) ds.readUnsignedInt(2) // file mode
		);
		records.add(record);
	}

	// header
	//
	// uint8[6] 'mbdx\2\0'
	// uint32 record count in the file
	//
	private void readHeader() throws FileParseException, IOException {
		char[] header = ds.readCharArray(6);
			
		if (!Arrays
				.equals(header, EXPECTED_HEADER))
			throw new FileParseException(
					"mdbx did not start with corrected header. Started with: "
							+ new String(header));
		recordCount = ds.readUnsignedInt(4);
	}

	public String toString() {
		return recordCount+": "+records.toString();
	}


	@Override
	public Iterator<MbdxRecord> iterator() {
		return records.iterator();
	}

}
