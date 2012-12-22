package com.crypticbit.ipa.entity.sqlite;

import java.util.List;

import com.crypticbit.ipa.entity.concept.Conceptable;
import com.crypticbit.ipa.entity.concept.wrapper.DescriptionTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhatTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhenTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhenTag.Field;
import com.crypticbit.ipa.entity.concept.wrapper.WhoTag;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlDatabaseKey;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlField;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlRelation;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlTable;
import com.crypticbit.ipa.io.util.PosixDate;

public interface MessageAfterIos6 extends List<MessageAfterIos6.Message> {

    @WhatTag(name = "Message")
    @SqlTable(tableName = "MESSAGE")
    public interface Message extends Conceptable {
	// @WhoTag(tag = "recipient", field = WhoTag.Field.PHONE_NUMBER)
	@SqlField("handle_id")
	public int getHandleId();

	@SqlRelation(primary = "getRowId", foreign = "getHandleId")
	public Handle getHandle();

	@WhenTag(tag = "sent", field = Field.DATE)
	@SqlField("date")
	public PosixDate getDate();

	@DescriptionTag
	@SqlField("text")
	public String getText();
	
	@SqlField("is_from_me")
	public MessageDirection getDirection();

	@SqlField("service")
	public String getService();
    }

    @SqlTable(tableName = "HANDLE")
    public interface Handle extends Conceptable {
	@SqlDatabaseKey
	public int getRowId();
	
	@SqlField("id")
	public String getId();

	@SqlField("service")
	public String getService();
    }

}