package monkeypuzzle.entity.sqlite;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import monkeypuzzle.io.parser.sqlite.dynamicproxy.SqlField;
import monkeypuzzle.io.parser.sqlite.dynamicproxy.SqlTable;
import monkeypuzzle.io.util.IphoneDate;

/** Autogenerated from Library/CallHistory/call_history.db */
public interface CallHistory extends List<CallHistory.Call>
{

	@SqlTable(tableName = "CALL")
	public interface Call
	{
		@SqlField("address")
		public String getAddress();

		@SqlField("date")
		public IphoneDate getDate();

		@SqlField("duration")
		public int getDuration();

		@SqlField("flags")
		public Flags getFlags();

		@SqlField("id")
		public int getId();

		@SqlField("ROWID")
		public int getRowId();
	}

	/*
	 * Flags http://code.google.com/p/iphonelogd/wiki/CallHistoryDatabase
	 * 
	 * <ul> <li>4 - incoming</li> <li>5 - outgoing <li>8 - Blocked (don't know
	 * what this means) <li>1507333 - seen in 2.0/01 testData <li>1769477 - seen
	 * in 2.0/01 testData</ul>
	 */
	public static class Flags
	{
		private static Map<Integer, String> convert = new HashMap<Integer, String>();
		static
		{
			convert.put(4, "incoming");
			convert.put(5, "outgoing");
			convert.put(8, "blocked");
			convert.put(1507333, "unknown [1]");
			convert.put(1769477, "unknown [2]");
		}
		private int flags;

		public Flags(final Integer flags)
		{
			this.flags = flags;
		}

		@Override
		public String toString()
		{
			return ""
					+ flags
					+ (convert.get(flags) == null ? "" : ("("
							+ convert.get(flags) + ")"));
		}
	}

}