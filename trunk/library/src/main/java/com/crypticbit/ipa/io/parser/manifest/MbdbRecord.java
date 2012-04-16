package com.crypticbit.ipa.io.parser.manifest;

import java.util.HashMap;
import java.util.Map;

public class MbdbRecord {

	private String domain;
	private String path; // Path
	private String linkTarget; // LinkTarget absolute path
	private String dataHash;
	private long fileLength;
	private short propertyCount;
	
	private Map<String,String> properties = new HashMap<String, String>();

	public MbdbRecord(String domain, String path, // Path
			String linkTarget, // LinkTarget absolute path
			String dataHash, // DataHash SHA.1 (some files only)
			String unknown, // unknown always N/A
			int mode,// Mode same as mbdx.Mode
			long unknown1, // unknown always 0
			long unknown2,// unknown
			long userId, // UserId
			long groupId,// GroupId mostly 501 for apps
			long time1, // Time1 relative to unix epoch (e.g
			// time_t)
			long time2, // Time2 Time1 or Time2 is the former
			// ModificationTime
			long time3, // Time3
			long fileLength, // FileLength always 0 for link or
			// directory
			short flag,// Flag 0 if special (link, directory),
			// otherwise unknown
			short propertyCount // PropertyCount number of properties
	// following
	) {
		this.domain = domain;
		this.path = path; // Path
		this.linkTarget = linkTarget; // LinkTarget absolute path
		this.dataHash = dataHash;
		this.fileLength = fileLength;
		this.propertyCount = propertyCount;
	}

	public String getPath() {return path; }
	public String getLinkTarget() { return linkTarget; }

	public String getDataHash() { return dataHash; }
	public long getFileLength() { return fileLength; }
	public short getPropertyCount() { return propertyCount;}
	
	public String toString() {
		return getPath()+" ("+getFileLength()+") "+properties.toString();
	}

	public void addProperty(String key, String value) {
		properties.put(key, value);
		
	}

	public String getDomain()
	{
		return domain;
	}
	
}
