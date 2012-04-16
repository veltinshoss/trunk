package com.crypticbit.ipa.entity.concept.wrapper.impl;

import java.util.Date;

public class WhenImpl extends Date implements com.crypticbit.ipa.entity.concept.wrapper.WhenTag.WhenSetter {

	public void setDate(Date date) {
		this.setTime(date.getTime());
	}


	
}
