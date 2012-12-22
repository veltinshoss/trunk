package com.crypticbit.ipa.entity.sqlite;

// FIXME - The set of Enums needs developing - only partial
public enum MessageDirection {
    RECEIVED, SENT, UNKNOWN;

    public static MessageDirection convert(final Integer value) {
	if (value == 3 || value == 1)
	    return SENT;
	else if (value == 2 || value == 0)
	    return RECEIVED;
	else
	    return UNKNOWN;
    }

}