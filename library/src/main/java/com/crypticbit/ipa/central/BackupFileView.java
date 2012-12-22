package com.crypticbit.ipa.central;

import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.entity.settings.DateTimePrefs;
import com.crypticbit.ipa.entity.settings.MailAccounts;
import com.crypticbit.ipa.entity.settings.MailAccounts.DeliveryAccounts;
import com.crypticbit.ipa.entity.settings.MapsHistory;
import com.crypticbit.ipa.entity.settings.SafariBookmarks;
import com.crypticbit.ipa.entity.sqlite.AddressBook;
import com.crypticbit.ipa.entity.sqlite.AddressBook.Person;
import com.crypticbit.ipa.entity.sqlite.AddressBookImages;
import com.crypticbit.ipa.entity.sqlite.CallHistory;
import com.crypticbit.ipa.entity.sqlite.CallHistory.Call;
import com.crypticbit.ipa.entity.sqlite.FacebookFriends;
import com.crypticbit.ipa.entity.sqlite.FacebookFriends.FacebookFriend;
import com.crypticbit.ipa.entity.sqlite.LocationD;
import com.crypticbit.ipa.entity.sqlite.LocationD.Location;
import com.crypticbit.ipa.entity.sqlite.LocationD.WifiLocation;
import com.crypticbit.ipa.entity.sqlite.Notes;
import com.crypticbit.ipa.entity.sqlite.Notes.Note;
import com.crypticbit.ipa.entity.sqlite.MessageAfterIos6;
import com.crypticbit.ipa.entity.sqlite.MessageAfterIos6.Message;
import com.crypticbit.ipa.entity.sqlite.TimeBasedInformation;
import com.crypticbit.ipa.entity.sqlite.TimeBasedInformation.Alarm;
import com.crypticbit.ipa.entity.sqlite.TimeBasedInformation.Event;
import com.crypticbit.ipa.entity.sqlite.Voicemails;
import com.crypticbit.ipa.entity.sqlite.Voicemails.Voicemail;
import com.crypticbit.ipa.io.parser.media.MetadataI;

public enum BackupFileView
{
	ADDRESS_BOOK(
			"Library/AddressBook/AddressBook.sqlitedb",
			AddressBook.class,
			Person.class),
	ADDRESS_BOOK_IMAGES(
			"Library/AddressBook/AddressBookImages.sqlitedb",
			AddressBookImages.class),
	LOCATIONS(
			"Library/Caches/locationd/consolidated.db",
			LocationD.class,
			Location.class,
			WifiLocation.class),
	CALENDARS(
			"Library/Calendar/Calendar.sqlitedb",
			TimeBasedInformation.class,
			Alarm.class,
			Event.class),
	CALL_HISTORY(
			"Library/CallHistory/call_history.db",
			CallHistory.class,
			Call.class),
	MAIL_ACCOUNTS(
			"Library/Mail/Accounts.plist",
			MailAccounts.class,
			DeliveryAccounts.class,
			MailAccounts.class),
	NOTES(
			"Library/Notes/notes.db",
			Notes.class,
			Note.class),
	SAFARI_BOOKMARKS(
			"Library/Safari/Bookmarks.plist",
			SafariBookmarks.class),
	SMS(
			"Library/SMS/sms.db",
			MessageAfterIos6.class ,
			Message.class),
	VOICEMAIL(
			"Library/Voicemail/voicemail.db",
			Voicemails.class,
			Voicemail.class),
	MEDIA(
			BackupFileType.MEDIA,
			MetadataI.class,
			MetadataI.class),
	FACEBOOK(
			"Documents/friends.db",
			FacebookFriends.class,
			FacebookFriend.class),
	MAPS_HISTORY(
			"Library/Maps/History.plist",
			MapsHistory.class,
			MapsHistory.class),
	DATETIME_PREFS(
			"Library/Preferences/com.apple.preferences.datetime.plist",
			DateTimePrefs.class,
			DateTimePrefs.class);

	// FIXME repeated with other enum
	private interface BackupFileAcceptor
	{
		public boolean match(BackupFile bf);

	}

	public static BackupFileView find(final BackupFile bfd)
	{
		for (BackupFileView bf : BackupFileView.values())
		{
			if (bf.acceptor.match(bfd))
			{
				return bf;
			}
		}
		return null;
	}

	private final BackupFileAcceptor acceptor;
	private final Class mainInterface;
	private final Class[] subInterfaces;

	private BackupFileView(final BackupFileAcceptor acceptor,
			final Class mainInterface, final Class... subInterfaces)
	{
		this.acceptor = acceptor;
		this.mainInterface = mainInterface;
		this.subInterfaces = subInterfaces;
	}

	private BackupFileView(final java.util.regex.Pattern pattern,
			final Class mainInterface, final Class... subInterfaces)
	{
		this(new BackupFileAcceptor() {
			@Override
			public boolean match(final BackupFile bf)
			{
				return pattern.matcher(bf.getCompleteOriginalFileName())
						.matches();
			}

		}, mainInterface, subInterfaces);
	}

	private BackupFileView(final BackupFileType type,
			final Class mainInterface, final Class... subInterfaces)
	{
		this(new BackupFileAcceptor() {
			@Override
			public boolean match(final BackupFile bf)
			{
				return bf.getParserType() == type;
			}

		}, mainInterface, subInterfaces);
	}

	private BackupFileView(final String matchFilename,
			final Class mainInterface, final Class... subInterfaces)
	{
		this(new BackupFileAcceptor() {
			@Override
			public boolean match(final BackupFile bf)
			{
				return bf.getCompleteOriginalFileName().endsWith(matchFilename);
			}
		}, mainInterface, subInterfaces);
	}

	public Class getMainInterface()
	{
		return mainInterface;
	}

	public Class[] getSubInterfaces()
	{
		return subInterfaces;
	}

	public static BackupFileView findByInterface(Class clazz)
	{
		for (BackupFileView bfv : values())
		{
			if (bfv.getMainInterface().equals(clazz))
				return bfv;
			for (Class c : bfv.getSubInterfaces())
			{
				if (c.equals(clazz))
					return bfv;
			}
		}
		return null;
	}
}
