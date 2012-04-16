package com.crypticbit.ipa.io.parser.plist;

import com.crypticbit.ipa.central.FileParseException;

public interface PListHeader
{
	public PListContainer getRootContainer() throws FileParseException;
}
