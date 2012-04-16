package com.crypticbit.ipa.entity.sqlite;

import java.util.List;

import com.crypticbit.ipa.entity.concept.Conceptable;
import com.crypticbit.ipa.entity.concept.wrapper.DescriptionTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhatTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhoTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhoTag.Field;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlField;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlTable;


/** Manually hacked from Documents/friends.db */
public interface FacebookFriends extends List<FacebookFriends.FacebookFriend>
{

	@WhatTag(name="Facebook Friend")
	@SqlTable(tableName = "friend")
	public interface FacebookFriend extends Conceptable
	{
		@SqlField("uid")
		public Number getUid();

		@WhoTag(field = Field.FIRST_NAME)
		@SqlField("first_name")
		public String getFirstName();
		
		@WhoTag(field = Field.SURNAME)
		@SqlField("last_name")
		public String getLastName();
		
		@DescriptionTag
		@WhoTag(field = Field.FULL_NAME)
		@SqlField("name")
		public String getFullName();
		
		@SqlField("pic_square")
		public String getProfilePic();
	}

}