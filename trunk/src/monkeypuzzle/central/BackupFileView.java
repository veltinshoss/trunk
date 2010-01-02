package monkeypuzzle.central;

import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.entity.settings.MailAccounts;
import monkeypuzzle.entity.settings.SafariBookmarks;
import monkeypuzzle.entity.sqlite.AddressBookImages;
import monkeypuzzle.entity.sqlite.AddressBook.Person;
import monkeypuzzle.entity.sqlite.CallHistory.Call;
import monkeypuzzle.entity.sqlite.Messages.Message;
import monkeypuzzle.entity.sqlite.Notes.Note;
import monkeypuzzle.entity.sqlite.TimeBasedInformation.Event;

public enum BackupFileView
{
	ADDRESS_BOOK("Library/AddressBook/AddressBook.sqlitedb")
	{
		@Override
		public Class getAvailableInterface()
		{
			return Person.class;
		}
	},
	ADDRESS_BOOK_IMAGES("Library/AddressBook/AddressBookImages.sqlitedb")
	{

		@Override
		public Class getAvailableInterface()
		{
			return AddressBookImages.class;
		}
	},
	CALENDARS("Library/Calendar/Calendar.sqlitedb")
	{

		@Override
		public Class getAvailableInterface()
		{
			return Event.class;
		}
	},
	CALL_HISTORY("Library/CallHistory/call_history.db")
	{

		@Override
		public Class getAvailableInterface()
		{
			return Call.class;
		}
	},
	// CUSTOM_WORDS("Library/Keyboard/dynamic-text.dat")
	// {
	// @Override
	// NullSeparatedStringParser createParser(final BackupFile bfd)
	// throws FileParseException
	// {
	// // FIXME - do before?
	// return new NullSeparatedStringParser(bfd);
	// }
	// public Class getAvailableInterface()
	// {
	// return null;
	// }
	// },
	MAIL_ACCOUNTS("Library/Mail/Accounts.plist")
	{
		@Override
		public Class getAvailableInterface()
		{
			return MailAccounts.class;
		}
	},
	NOTES("Library/Notes/notes.db")
	{

		@Override
		public Class getAvailableInterface()
		{
			return Note.class;
		}
	},
	SAFARI_BOOKMARKS("Library/Safari/Bookmarks.plist")
	{

		@Override
		public Class getAvailableInterface()
		{
			return SafariBookmarks.class;
		}
	},
	SMS("Library/SMS/sms.db")
	{

		@Override
		public Class getAvailableInterface()
		{
			return Message.class;
		}
	};
	// FIXME repeated with other enum
	private interface FilenameAcceptor
	{
		public boolean match(String filename);
	}

	public static BackupFileView find(final BackupFile bfd)
	{
		return find(bfd.getCompleteOriginalFileName());
	}

	public static BackupFileView find(final String filename)
	{
		for (BackupFileView bf : BackupFileView.values())
		{
			if (bf.acceptor.match(filename))
				return bf;
		}
		// should never happen as UNIDENTIFIED should have catch all fall
		// through.
		return null;
	}

	private final FilenameAcceptor acceptor;

	private BackupFileView(final FilenameAcceptor acceptor)
	{
		this.acceptor = acceptor;
	}

	private BackupFileView(final java.util.regex.Pattern pattern)
	{
		this.acceptor = new FilenameAcceptor() {
			@Override
			public boolean match(final String filename)
			{
				return pattern.matcher(filename).matches();
			}
		};
	}

	private BackupFileView(final String matchFilename)
	{
		this.acceptor = new FilenameAcceptor() {
			@Override
			public boolean match(final String filename)
			{
				return matchFilename.equals(filename);
			}
		};
	}

	public abstract Class getAvailableInterface();

	Object createParser(final BackupFile bfd) throws FileParseException
	{
		return bfd.getParsedData().getContentbyInterface(
				getAvailableInterface());
	}
}
