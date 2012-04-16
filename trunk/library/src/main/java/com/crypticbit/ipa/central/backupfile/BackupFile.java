package com.crypticbit.ipa.central.backupfile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

import com.crypticbit.ipa.central.BackupDirectoryParser;
import com.crypticbit.ipa.central.BackupFileType;
import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.io.util.IoUtils;
import com.crypticbit.ipa.results.ParsedData;

/**
 * 
 * Represents a backed-up file, allowing access to both the content and original
 * name of the file
 * 
 */
public abstract class BackupFile implements Comparable<BackupFile>
{

	private SoftReference<byte[]> data;
	protected String originalFileName;
	private ParsedData parsedData;
	private BackupFileType parserType;

	protected BackupFile()
	{
	}

	@Override
	public int compareTo(final BackupFile o)
	{
		return this.originalFileName.compareTo(o.originalFileName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final BackupFile other = (BackupFile) obj;
		if (this.originalFileName == null)
		{
			if (other.originalFileName != null)
				return false;
		} else if (!this.originalFileName.equals(other.originalFileName))
			return false;
		return true;
	}

	public byte[] getContentsAsByteArray() throws IOException,
			FileNotFoundException
	{
		byte[] result = this.data == null ? null : this.data.get();
		if (result == null)
		{
			result = createByteArrayFromBackupFile();
			this.data = new SoftReference<byte[]>(result);
		}
		return result;
	}

	/**
	 * Return the File containing the original data for this backup file. This
	 * file may not exist if the backup is incomplete.
	 * 
	 * @return A File which provides access to the data, if it exists.
	 * @throws IOException
	 */
	public abstract File getContentsFile() throws IOException;

	public InputStream getContentsInputStream() throws IOException
	{
		return new ByteArrayInputStream(getContentsAsByteArray());
	}

	/**
	 * Get the filename of the backup file on the original device
	 * 
	 * @return Get the filename of the backup file on the original device
	 * @throws IOException
	 */
	public String getCompleteOriginalFileName()
	{
		return this.originalFileName;
	}

	public String getOriginalFileNameSufix()
	{
		return (this.originalFileName.lastIndexOf(".") > 0) ? this.originalFileName
				.substring(this.originalFileName.lastIndexOf(".") + 1) : "";
	}

	public String getOriginalFileName()
	{
		return (this.originalFileName.lastIndexOf(IoUtils.IPHONE_PATH_SEP) > 0) ? this.originalFileName
				.substring(this.originalFileName
						.lastIndexOf(IoUtils.IPHONE_PATH_SEP) + 1)
				: this.originalFileName;
	}

	public ParsedData getParsedData()
	{
		if (this.parsedData == null)
		{
			this.parsedData = getParserType().getParsedData(this);
		}
		return this.parsedData;
	}

	public BackupFileType getParserType()
	{
		if (this.parserType == null)
		{
			this.parserType = BackupFileType.find(this);
		}
		return this.parserType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.originalFileName == null) ? 0 : this.originalFileName
						.hashCode());
		return result;
	}

	public File restoreFile(final File deviceRootDir) throws IOException
	{
		// create file in deviceRoot
		File destFile = null;
		if (this.originalFileName != null)
		{
			destFile = new File(deviceRootDir, this.originalFileName);
			destFile.getParentFile().mkdirs(); // create sub dirs in
			// deviceRoot
		}

		// write file to deviceRoot directory
		writeData(destFile, getContentsAsByteArray());
		return destFile;
	}

	protected void writeData(final File file, final byte[] data)
			throws FileNotFoundException, IOException
	{
		// Write file to working dir
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(data);
		fos.close();
	}

	public static String getBackupName(final File entry)
	{
		try
		{
			return BackupDirectoryParser.getBackupSummary(entry);
		} catch (FileParseException e1)
		{
			return "File problem: " + entry.getAbsolutePath();
		} catch (IOException e1)
		{
			return "Access problem: " + entry.getAbsolutePath();
		}
	}

	@Override
	public String toString()
	{
		return "backup File: " + this.originalFileName;
	}

	protected abstract byte[] createByteArrayFromBackupFile()
			throws IOException;

}
