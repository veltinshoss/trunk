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


public interface Voicemails extends List<Voicemails.Voicemail> {

	@WhatTag(name = "voicemail")
	@SqlTable(tableName = "voicemail")
	public interface Voicemail extends Conceptable {
		@WhenTag
		@SqlField("date")
		public PosixDate getDateDate();

		@DescriptionTag
		@WhoTag(field = Field.PHONE_NUMBER)
		@SqlField("sender")
		public String getSender();

		@WhoTag(field = Field.PHONE_NUMBER)
		@SqlField("callback_num")
		public String getCallbackNumber();

		@SqlField("ROWID")
		public int getFilenameNumber();
	}
}