package com.crypticbit.ipa.io.parser.manifest;

public class MbdxRecord {

	private String fileName;
	private long offset;
	private int fileMode;
	MbdxRecord(String fileName, long offset, int fileMode) {
		this.fileName = fileName;
		this.offset = offset;
		this.fileMode = fileMode;
	}
	public String getFileName() {
		return fileName;
	}
	public long getOffset() {
		return offset;
	}
	public int getFileMode() {
		return fileMode;
	}
	
	public String toString() {
		return getFileName()+" @"+getOffset()+" ["+getFileMode()+"]";
	}
}
