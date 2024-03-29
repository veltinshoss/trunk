package com.crypticbit.ipa.entity.sqlite;

import java.util.List;

import com.crypticbit.ipa.entity.concept.Conceptable;
import com.crypticbit.ipa.entity.concept.wrapper.DescriptionTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhatTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhenTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhoTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhoTag.Field;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlField;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlTable;
import com.crypticbit.ipa.io.util.PosixDate;


/** Autogenerated from Library/CallHistory/call_history.db */
public interface CallHistory extends List<CallHistory.Call>
{

	@WhatTag(name="Call")
	@SqlTable(tableName = "CALL")
	public interface Call extends Conceptable
	{
		@DescriptionTag
		@WhoTag(field = Field.PHONE_NUMBER)
		@SqlField("address")
		public String getPhoneNumber();

		@WhenTag
		@SqlField("date")
		public PosixDate getDate();

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
	public static enum Flags {

		INCOMING(4), OUTGOING(5), BLOCKED(8), UNKNOWN(-1);

		// convert.put(1507333, "unknown [1]");
		// convert.put(1769477, "unknown [2]");

		public static Flags convert(final Integer value) {
			for (Flags flag : Flags.values())
				if (flag.id == value)
					return flag;
			return UNKNOWN;
		}

		private int id;

		Flags(final int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

	}

}