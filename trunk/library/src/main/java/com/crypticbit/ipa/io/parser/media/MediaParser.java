package com.crypticbit.ipa.io.parser.media;

import java.io.IOException;
import java.util.Iterator;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.io.parser.BackupFileParser;
import com.crypticbit.ipa.results.ContentType;
import com.crypticbit.ipa.results.FileOnlyLocation;
import com.crypticbit.ipa.results.ParsedData;
import com.crypticbit.ipa.results.ParsedDataImpl;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

/**
 * Parses backup files that represent known media types and renames them so they
 * can be accesses by the local OS as files of that type.
 * 
 */
public class MediaParser implements BackupFileParser<ParsedData>
{
	public final class MediaResults extends ParsedDataImpl implements
			ParsedData
	{
		private Metadata metadata;
		private String result;

		MediaResults()
		{
			this.result = init();
		}

		@Override
		public BackupFile getBackupFile()
		{
			return MediaParser.this.bfd;
		}

		@Override
		public <I> I getContentbyInterface(final Class<I> interfaceDef)
		{	
			if(interfaceDef.equals(MetadataI.class))
				return (I) new MetadataImpl(metadata, new FileOnlyLocation(getBackupFile(), ContentType.IMAGE));
			else
			throw new UnsupportedOperationException(
					"Not possible to get this content as interface: "
							+ getSummary());
		}

		@Override
		public String getContents()
		{
			return this.result;
		}

		/**
		 * @return the metadata
		 */
		public Metadata getMetadata()
		{
			return this.metadata;
		}

		@Override
		public String getSummary()
		{
			return "media image: " + MediaParser.this.bfd.getCompleteOriginalFileName();
		}

		private String init()
		{
			String originalFilename = MediaParser.this.bfd
					.getCompleteOriginalFileName();
			// let's try this and fail well rather than check that we can read
			// the meta data
			StringBuilder buffer = new StringBuilder();
			try
			{
				this.metadata = JpegMetadataReader
						.readMetadata(MediaParser.this.bfd
								.getContentsInputStream());
				// iterate through metadata directories
				Iterator<Directory> directories = this.metadata
						.getDirectoryIterator();
				while (directories.hasNext())
				{
					Directory directory = directories.next();
					Iterator<Tag> tags = directory.getTagIterator();
					while (tags.hasNext())
					{
						Tag tag = tags.next(); // use Tag.toString()
						buffer.append(tag.toString()).append('\n');
					}
				}
				return buffer.toString();
			} catch (JpegProcessingException e)
			{
				return ("Unable to extract EXIF data for image: ")
						+ originalFilename;
			} catch (IOException e)
			{
				return ("Unable access file to extract EXIF data for image: " + originalFilename);
			}
		}
	}

	private BackupFile bfd;

	public MediaParser(final BackupFile bfd) throws FileParseException,
			IOException
	{
		this.bfd = bfd;
	}

	@Override
	public ParsedData getParsedData()
	{
		return new MediaResults();
	}
}
