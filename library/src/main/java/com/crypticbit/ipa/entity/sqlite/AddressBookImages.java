package com.crypticbit.ipa.entity.sqlite;

import java.util.List;

import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlField;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlTable;


public interface AddressBookImages extends
		List<AddressBookImages.AddressBookImage>
{
	@SqlTable(tableName="", tableNames = {"ABIMAGE","ABTHUMBNAILIMAGE"})
	public interface AddressBookImage
	{
		@SqlField("crop_width")
		public int crop_width();

		@SqlField("crop_height")
		public int getCropHeight();

		@SqlField("crop_x")
		public int getCropX();

		@SqlField("crop_y")
		public int getCropY();

		@SqlField("format")
		public int getFormat();

		@SqlField("data")
		public byte[] getImageData();

		@SqlField("record_id")
		public int getRecordId();
	}
}
