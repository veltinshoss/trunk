package monkeypuzzle.ui.swing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import monkeypuzzle.central.BackupFileView;

public enum SpecialViewType
{
	ADDRESS_BOOK
	{
		@Override
		public SpecialView createSpecialView(final Mediator mediator)
		{
			return new AddressBookView(mediator);
		}

		@Override
		public String getName()
		{
			return "Address Book";
		}

	},
	SMS
	{
		@Override
		public SpecialView createSpecialView(final Mediator mediator)
		{
			return new SmsView(mediator);
		}

		@Override
		public String getName()
		{
			return "Messages";
		}

	};

	private static final Map<BackupFileView, SpecialViewType> lookup = new HashMap<BackupFileView, SpecialViewType>();
	static
	{
		// fixme - must do better
		lookup.put(BackupFileView.ADDRESS_BOOK, SpecialViewType.ADDRESS_BOOK);
		
		//TODO commented out as it was failing 
		//lookup.put(BackupFileView.ADDRESS_BOOK_IMAGES, SpecialViewType.ADDRESS_BOOK);
		lookup.put(BackupFileView.SMS, SpecialViewType.SMS);
	}

	public static SpecialViewType getSpecialViewType(final BackupFileView type)
	{
		return lookup.get(type);
	}

	public abstract SpecialView createSpecialView(Mediator mediator);

	public List<BackupFileView> getBackupFileType()
	{
		List<BackupFileView> results = new ArrayList<BackupFileView>();
		for (Map.Entry<BackupFileView, SpecialViewType> entry : lookup
				.entrySet())
			if (entry.getValue() == this)
			{
				results.add(entry.getKey());
			}
		return results;
	}

	public abstract String getName();
}
