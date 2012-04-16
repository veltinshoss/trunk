package com.crypticbit.ipa.central;

@SuppressWarnings("serial")
public class NotaBackupDirectoryException extends IPhoneParseException {
	public NotaBackupDirectoryException(final String message)
	{
		super(message);
	}

	public NotaBackupDirectoryException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
