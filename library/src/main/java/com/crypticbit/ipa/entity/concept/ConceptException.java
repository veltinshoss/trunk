package com.crypticbit.ipa.entity.concept;

public class ConceptException extends Exception {

	public ConceptException(String message, Throwable reason) {
		super(message, reason);
	}

	public ConceptException(String message) {
		super(message);
	}
}
