package monkeypuzzle.entity.sqlite;

import java.util.List;

import monkeypuzzle.io.parser.sqlite.dynamicproxy.SqlField;
import monkeypuzzle.io.parser.sqlite.dynamicproxy.SqlTable;
import monkeypuzzle.io.util.PosixDate;

/** Autogenerated from Library/SMS/sms.db */
public interface Messages extends List<Messages.Message>
{

	@SqlTable(tableName = "MSG_GROUP")
	public interface Group
	{
		@SqlField("newest_message")
		public int getNewestMessage();

		@SqlField("ROWID")
		public int getRowId();

		@SqlField("type")
		public int getType();

		@SqlField("unread_count")
		public int getUnreadCount();
	}

	@SqlTable(tableName = "GROUP_MEMBER")
	public interface GroupMember
	{
		@SqlField("address")
		public String getAddress();

		@SqlField("group_id")
		public int getGroupId();

		@SqlField("ROWID")
		public int getRowId();
	}

	@SqlTable(tableName = "MESSAGE")
	public interface Message
	{
		@SqlField("address")
		public String getAddress();

		@SqlField("association_id")
		public int getAssociationId();

		@SqlField("date")
		public PosixDate getDate();

		@SqlField("flags")
		public MessageType getFlags();

		@SqlField("group_id")
		public int getGroupId();

		@SqlField("height")
		public int getHeight();

		@SqlField("replace")
		public int getReplace();

		@SqlField("ROWID")
		public int getRowId();

		@SqlField("svc_center")
		public String getSvcCenter();

		@SqlField("text")
		public String getText();

		@SqlField("UIFlags")
		public int getUIFlags();

		@SqlField("version")
		public int getVersion();
	}

	// FIXME - The set of Enums needs developing - only partial
	public static enum MessageType
	{
		RECEIVED(2), SENT(3), UNKNOWN(-1);

		public static MessageType convert(final Integer value)
		{
			for (MessageType type : MessageType.values())
				if (type.id == value)
					return type;
			return UNKNOWN;
		}

		private int id;

		MessageType(final int id)
		{
			this.id = id;
		}

		public int getId()
		{
			return id;
		}
	}
}