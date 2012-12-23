package com.crypticbit.ipa.entity.sqlite;

import java.util.List;

import com.crypticbit.ipa.entity.concept.Conceptable;
import com.crypticbit.ipa.entity.concept.wrapper.DescriptionTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhatTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhenTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhenTag.Field;
import com.crypticbit.ipa.entity.concept.wrapper.WhoTag;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlValidateFieldsPresent;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlDatabaseKey;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlField;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlRelation;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlTable;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlVersionOptions;
import com.crypticbit.ipa.io.util.IphoneDate;
import com.crypticbit.ipa.io.util.PosixDate;

public interface Messages extends List<Messages.Message> {

    @SqlVersionOptions({MessageAfterIos6.class, MessageBeforeIos6.class})
    @WhatTag(name = "Message")
    @SqlTable(tableName = "MESSAGE")
    public interface Message extends Conceptable {
	@DescriptionTag
	@SqlField("text")
	public String getText();
    }


    @WhatTag(name = "Message")
    @SqlTable(tableName = "MESSAGE")
    @SqlValidateFieldsPresent
    public interface MessageAfterIos6 extends Message {
	@SqlField("handle_id")
	public int getHandleId();

	@WhenTag(tag = "sent", field = Field.DATE)
	@SqlField("date")
	public IphoneDate getDate();
	
	@WhoTag(tag = "recipient", field = WhoTag.Field.PHONE_NUMBER)
	@SqlRelation(primary = "getRowId", foreign = "getHandleId")
	public Handle getHandle();

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

    @WhatTag(name = "Message")
    @SqlTable(tableName = "MESSAGE")
    @SqlValidateFieldsPresent
    public interface MessageBeforeIos6 extends Message {
	
	@WhenTag(tag = "sent", field = Field.DATE)
	@SqlField("date")
	public PosixDate getDate();
	
	@WhoTag(tag = "recipient", field = WhoTag.Field.PHONE_NUMBER)
	@SqlField("address")
	public String getAddress();

	@SqlField("association_id")
	public int getAssociationId();

	@SqlField("flags")
	public MessageDirection getDirection();

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

	@SqlField("UIFlags")
	public int getUIFlags();

	@SqlField("version")
	public int getVersion();
    }

}