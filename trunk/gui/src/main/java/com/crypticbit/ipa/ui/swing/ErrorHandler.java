package com.crypticbit.ipa.ui.swing;

public interface ErrorHandler
{

	public void displayErrorDialog(String message, Throwable cause);
	public void displayWarningDialog(String message, Throwable cause);

}
