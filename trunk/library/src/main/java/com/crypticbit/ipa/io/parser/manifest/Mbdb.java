package com.crypticbit.ipa.io.parser.manifest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.crypticbit.ipa.central.FileParseException;


public class Mbdb implements Iterable<MbdbRecord> {

	private static char[] EXPECTED_HEADER = new char[] { 'm', 'b', 'd', 'b',
			'\5', '\0' };

	private DataSource ds;
	private Map<Integer,MbdbRecord> records = new HashMap<Integer,MbdbRecord>();

	public Mbdb(final InputStream is) throws FileParseException, IOException {
		this.ds = new InputStreamDataSource(is);
		readHeader();
		while (readRecord())
			; // do nothing
	}

	// string Domain
	// string Path
	// string LinkTarget absolute path
	// string DataHash SHA.1 (some files only)
	// string unknown always N/A
	// uint16 Mode same as mbdx.Mode
	// uint32 unknown always 0
	// uint32 unknown
	// uint32 UserId
	// uint32 GroupId mostly 501 for apps
	// uint32 Time1 relative to unix epoch (e.g time_t)
	// uint32 Time2 Time1 or Time2 is the former ModificationTime
	// uint32 Time3
	// uint64 FileLength always 0 for link or directory
	// uint8 Flag 0 if special (link, directory), otherwise unknown
	// uint8 PropertyCount number of properties following
	//
	// Property is a couple of strings:
	//
	// string name
	// string value can be a string or a binary content
	private boolean readRecord() throws IOException {
		if (!ds.hasNext())
			return false;
		int offset = ds.getCurrentOffset();
		MbdbRecord record = new MbdbRecord(ds.readString(), // Domain
				ds.readString(), // Path
				ds.readString(), // LinkTarget absolute path
				ds.readString(), // DataHash SHA.1 (some files only)
				ds.readString(), // unknown always N/A
				(int) ds.readUnsignedInt(2), // Mode same as mbdx.Mode
				ds.readUnsignedInt(4), // unknown always 0
				ds.readUnsignedInt(4), // unknown
				ds.readUnsignedInt(4), // UserId
				ds.readUnsignedInt(4), // GroupId mostly 501 for apps
				ds.readUnsignedInt(4), // Time1 relative to unix epoch (e.g
				// time_t)
				ds.readUnsignedInt(4), // Time2 Time1 or Time2 is the former
				// ModificationTime
				ds.readUnsignedInt(4), // Time3
				ds.readUnsignedInt(8), // FileLength always 0 for link or
				// directory
				(short) ds.readUnsignedInt(1),// Flag 0 if special (link,
												// directory),
				// otherwise unknown
				(short) ds.readUnsignedInt(1) // PropertyCount number of
												// properties
		// following

		);
		
		

		for (int loop = 0; loop < record.getPropertyCount(); loop++)
			record.addProperty(ds.readString(), ds.readString());

		records.put(offset,record);
		return true;
	}

	// header
	//
	// uint8[6] 'mbdb\5\0'
	//
	private void readHeader() throws FileParseException, IOException {
		char[] header = ds.readCharArray(6);

		if (!Arrays.equals(header, EXPECTED_HEADER))
			throw new FileParseException(
					"mdbx did not start with corrected header. Started with: "
							+ new String(header));
	}

	public String toString() {
		return records.toString();
	}
	
	public MbdbRecord getRecord(int offset) {
		return records.get(offset);
	}

	@Override
	public Iterator<MbdbRecord> iterator() {
		return records.values().iterator();
	}

}
