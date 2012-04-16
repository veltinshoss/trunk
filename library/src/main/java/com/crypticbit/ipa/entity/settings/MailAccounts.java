package com.crypticbit.ipa.entity.settings;

import com.crypticbit.ipa.io.parser.plist.dynamicproxy.PListAnnotationEntry;

/** Autogenerated from Library/Mail/Accounts */
public interface MailAccounts
{
	public interface DeliveryAccounts
	{
		@PListAnnotationEntry("AccountType")
		public java.lang.String getAccountType();

		@PListAnnotationEntry("AuthenticationScheme")
		public java.lang.String getAuthenticationScheme();

		@PListAnnotationEntry("Hostname")
		public java.lang.String getHostname();

		@PListAnnotationEntry("ShouldUseAuthentication")
		public java.lang.String getShouldUseAuthentication();

		@PListAnnotationEntry("uniqueId")
		public java.lang.String getUniqueId();

		@PListAnnotationEntry("Username")
		public java.lang.String getUsername();
	}

	public interface MailAccount
	{
		@PListAnnotationEntry("AccountPath")
		public java.lang.String getAccountPath();

		@PListAnnotationEntry("AccountType")
		public java.lang.String getAccountType();

		@PListAnnotationEntry("DraftsMailboxName")
		public java.lang.String getDraftsMailboxName();

		@PListAnnotationEntry("SentMessagesMailboxName")
		public java.lang.String getSentMessagesMailboxName();

		@PListAnnotationEntry("TrashMailboxName")
		public java.lang.String getTrashMailboxName();

		@PListAnnotationEntry("uniqueId")
		public java.lang.String getUniqueId();
	}

	@PListAnnotationEntry("DeliveryAccounts")
	public DeliveryAccounts[] getDeliveryAccounts();

	@PListAnnotationEntry("MailAccounts")
	public MailAccount[] getMailAccounts();
}