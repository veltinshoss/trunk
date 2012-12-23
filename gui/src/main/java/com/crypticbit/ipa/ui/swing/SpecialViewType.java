package com.crypticbit.ipa.ui.swing;

import java.util.Arrays;
import java.util.logging.Level;

import com.crypticbit.ipa.central.BackupFileView;
import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.entity.settings.MapsHistory;
import com.crypticbit.ipa.entity.sqlite.CallHistory;
import com.crypticbit.ipa.entity.sqlite.MessageDirection;
import com.crypticbit.ipa.ui.swing.map.MapView;

public enum SpecialViewType
{
	ADDRESS_BOOK(BackupFileView.ADDRESS_BOOK)
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
	SMS(BackupFileView.SMS)
	{
		@Override
		public SpecialView createSpecialView(final Mediator mediator)
		{
			return new SmsView(mediator);
		}

		@Override
		public Event[] getOptions()
		{
			return new Event[] { new Event("All", new Callback() {
				@Override
				void callback(final ViewingPane viewingPane)
				{
					((SmsView) viewingPane.getComponentAt(0)).clearFilter();
				}
			}), new Event("Sent", new Callback() {
				@Override
				void callback(final ViewingPane viewingPane)
				{
					((SmsView) viewingPane.getComponentAt(0))
							.setFilter(MessageDirection.SENT);
				}
			}), new Event("Received", new Callback() {
				@Override
				void callback(final ViewingPane viewingPane)
				{
					((SmsView) viewingPane.getComponentAt(0))
							.setFilter(MessageDirection.RECEIVED);
				}
			}) };
		}

		@Override
		public String getName()
		{
			return "Messages";
		}

	},
	CALLS(BackupFileView.CALL_HISTORY)
	{
		@Override
		public SpecialView createSpecialView(final Mediator mediator)
		{
			return new CallRecordView(mediator);
		}

		@Override
		public String getName()
		{
			return "Calls";
		}

		@Override
		public Event[] getOptions()
		{
			return new Event[] { new Event("All", new Callback() {
				@Override
				void callback(final ViewingPane viewingPane)
				{
					((CallRecordView) viewingPane.getComponentAt(0))
							.clearFilter();
				}
			}), new Event("Incoming", new Callback() {
				@Override
				void callback(final ViewingPane viewingPane)
				{
					((CallRecordView) viewingPane.getComponentAt(0))
							.setFilter(CallHistory.Flags.INCOMING);
				}
			}), new Event("Outgoing", new Callback() {
				@Override
				void callback(final ViewingPane viewingPane)
				{
					((CallRecordView) viewingPane.getComponentAt(0))
							.setFilter(CallHistory.Flags.OUTGOING);
				}
			}) };
		}

	},
	LOCATIOND(BackupFileView.LOCATIONS)
	{
		@Override
		public SpecialView createSpecialView(final Mediator mediator)
		{
			return new MapView(mediator);
		}

		@Override
		public String getName()
		{
			return "Location Map";
		}

	},

	VOICEMAIL(BackupFileView.VOICEMAIL)
	{

		@Override
		public SpecialView createSpecialView(Mediator mediator)
		{
			return new VoiceMailView(mediator);
		}

		@Override
		public String getName()
		{
			return "Voicemail";
		}

	},
	FACEBOOK(BackupFileView.FACEBOOK)
	{
		@Override
		public SpecialView createSpecialView(Mediator mediator)
		{
			return null;
		}

		@Override
		public String getName()
		{
			return "Facebook Friends";
		}

	},
	MAPS_HISTORY(BackupFileView.MAPS_HISTORY)
	{
		@Override
		public SpecialView createSpecialView(Mediator mediator)
		{
			return null;
		}

		@Override
		public String getName()
		{
			return "Maps history";
		}

	};

	private BackupFileView bfv;

	private SpecialViewType(BackupFileView bfv)
	{
		this.bfv = bfv;
	}

	public Event[] getOptions()
	{
		return null;
	}

	public static SpecialViewType getSpecialViewType(final BackupFileView type)
	{
		for (SpecialViewType svt : values())
		{
			if (svt.bfv == type)
				return svt;
		}
		return null;
	}

	public abstract SpecialView createSpecialView(Mediator mediator);

	public BackupFileView getBackupFileType()
	{
		return bfv;
	}

	public abstract String getName();

	public static class Event
	{
		private String name;
		private Callback callback;

		private Event(String name, Callback callback)
		{
			this.name = name;
			this.callback = callback;
		}

		public String getName()
		{
			return name;
		}

		public Callback getCallback()
		{
			return callback;
		}
	}

	public boolean isEnabled(Mediator mediator)
	{
		try {
		return mediator.getBackupDirectory().getByInterface(
				getBackupFileType().getMainInterface()) != null;
		} catch (Exception e) {
			LogFactory.getLogger().log(Level.WARNING,"Error trying to access file",e);
			return false;
			
		}
	}
}