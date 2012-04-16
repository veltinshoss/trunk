package com.crypticbit.ipa.io.parser.media;

import java.util.Date;

import com.crypticbit.ipa.entity.concept.Conceptable;
import com.crypticbit.ipa.entity.concept.wrapper.WhatTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhenTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhereTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhereTag.Field;


@WhatTag(name="media or image metadata")
public interface MetadataI extends Conceptable {

	@WhereTag(field = Field.LONGITUDE)
	public Double getLong();

	@WhereTag(field = Field.LATITUDE)
	public Double getLat();

	@WhenTag
	public Date getWhen();

}
