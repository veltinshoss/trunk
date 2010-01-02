package monkeypuzzle.entity.sqlite;

import java.util.List;

import monkeypuzzle.io.parser.sqlite.dynamicproxy.SqlField;
import monkeypuzzle.io.parser.sqlite.dynamicproxy.SqlTable;

public interface AddressBookImages extends
		List<AddressBookImages.AddressBookImage>
{
	@SqlTable(tableName = "ABIMAGE")
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
