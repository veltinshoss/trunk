package monkeypuzzle.entity.sqlite;

import java.util.List;

import monkeypuzzle.entity.sqlite.AddressBook.ContactLabel.ContactType;
import monkeypuzzle.io.parser.sqlite.dynamicproxy.SqlDatabaseKey;
import monkeypuzzle.io.parser.sqlite.dynamicproxy.SqlField;
import monkeypuzzle.io.parser.sqlite.dynamicproxy.SqlJoin;
import monkeypuzzle.io.parser.sqlite.dynamicproxy.SqlRelation;
import monkeypuzzle.io.parser.sqlite.dynamicproxy.SqlTable;
import monkeypuzzle.io.util.IphoneDate;

public interface AddressBook extends List<AddressBook.Person>
{

	// FIXME - The set of Enums needs developing - only partial
	public static enum ContactLabel
	{
		CUSTOM_LABEL(9),
		HOME(2),
		HOMEPAGE(10),
		MOBILE(3),
		OTHER(7),
		UNKNOWN(-1),
		WORK(1);

		// FIXME - The set of Enums needs developing - only partial
		public static enum ContactType
		{
			ADDRESS(5), EMAIL(4), PHONE_NUMBER(3), UNKNOWN(-1), WEB(22);

			public static ContactType convert(final Integer value)
			{
				for (ContactType type : ContactType.values())
					if (type.id == value)
						return type;
				return UNKNOWN;
			}

			private int id;

			ContactType(final int id)
			{
				this.id = id;
			}

			public int getId()
			{
				return id;
			}
			
			public String toString() {
				return this.name().replace("_", " ").toLowerCase();
			}
		}

		public static ContactLabel convert(final Integer value)
		{
			for (ContactLabel type : ContactLabel.values())
				if (type.id == value)
					return type;
			return UNKNOWN;
		}

		private int id;

		ContactLabel(final int id)
		{
			this.id = id;
		}

		public int getId()
		{
			return id;
		}

		public String toString() {
			return this.name().replace("_", " ").toLowerCase();
		}
		
	}

	@SqlTable(tableName = "ABPERSON")
	public interface Person
	{
		@SqlTable(tableName = "ABPERSONCHANGES")
		public interface Changes
		{
			@SqlField("record")
			public int getRecord();

			@SqlField("type")
			public int getType();
		}

		@SqlJoin(firstKey = "getLabel", secondKey = "getRowId")
		public interface ContactItem extends MultiValue, MultiValueLabel
		{

			@SqlRelation(primary = "getParentId", foreign = "getUID")
			List<MultiLineContactItem> getMultiLineItems();

		}

		@SqlTable(tableName = "ABGROUP")
		public interface Group
		{
			@SqlField("Name")
			public String getName();

			@SqlField("ROWID")
			public int getROWID();
		}

		@SqlTable(tableName = "ABGROUPCHANGES")
		public interface GroupChanges
		{
			@SqlField("record")
			public int getRecord();

			@SqlField("type")
			public int getType();
		}

		@SqlTable(tableName = "ABGROUPMEMBERS")
		public interface GroupMembers
		{
			@SqlField("group_id")
			public int getGroupId();

			@SqlField("member_id")
			public int getMemberId();

			@SqlField("member_type")
			public int getMemberType();

			@SqlField("UID")
			public int getUID();
		}

		@SqlJoin(firstKey = "getKey", secondKey = "getRowId")
		public interface MultiLineContactItem extends MultiValueEntry,
				MultiValueEntryKey
		{
		}

		@SqlTable(tableName = "ABMULTIVALUE")
		public interface MultiValue
		{
			@SqlField("property")
			public ContactType getContactType();

			@SqlField("identifier")
			public int getIdentifier();

			@SqlField("label")
			public ContactLabel getLabel();

			@SqlField("record_id")
			public int getRecordId();

			@SqlField("UID")
			public int getUID();

			@SqlField("value")
			public String getValue();
		}

		@SqlTable(tableName = "ABMULTIVALUEENTRY")
		public interface MultiValueEntry
		{
			@SqlField("key")
			public int getKey();

			@SqlField("parent_id")
			public int getParentId();

			@SqlField("value")
			public String getValue();
		}

		@SqlTable(tableName = "ABMULTIVALUEENTRYKEY")
		public interface MultiValueEntryKey
		{
			@SqlDatabaseKey
			public String getRowId();

			@SqlField("value")
			public String getValue();
		}

		@SqlTable(tableName = "ABMULTIVALUELABEL")
		public interface MultiValueLabel
		{
			@SqlField("value")
			public String getLabelValue();

			@SqlDatabaseKey
			public String getRowId();
		}

		@SqlField("Birthday")
		public IphoneDate getBirthday();

		@SqlField("CompositeNameFallback")
		public String getCompositeNameFallback();

		@SqlRelation(primary = "getRecordId", foreign = "getRowId")
		public List<ContactItem> getContactDetails();

		@SqlField("CreationDate")
		public IphoneDate getCreationDate();

		@SqlField("Department")
		public String getDepartment();

		@SqlField("First")
		public String getFirstName();

		// @SqlField("FirstSort")
		// public String getFirstSort();
		//
		// @SqlField("LastSort")
		// public String getLastSort();

		@SqlField("FirstPhonetic")
		public String getFirstPhonetic();

		@SqlField("JobTitle")
		public String getJobTitle();

		@SqlField("Kind")
		public int getKind();

		@SqlField("Last")
		public String getLastName();

		@SqlField("LastPhonetic")
		public String getLastPhonetic();

		@SqlField("Middle")
		public String getMiddle();

		@SqlField("MiddlePhonetic")
		public String getMiddlePhonetic();

		@SqlField("ModificationDate")
		public IphoneDate getModificationDate();

		@SqlField("Nickname")
		public String getNickname();

		@SqlField("Note")
		public String getNote();

		@SqlField("Organization")
		public String getOrganization();

		@SqlField("Prefix")
		public String getPrefix();

		// @SqlTable(tableName = "ABPHONELASTFOUR")
		// public interface ABPHONELASTFOUR
		// {
		// @SqlField("multivalue_id")
		// public int getMultivalueId();
		//
		// @SqlField("value")
		// public String getValue();
		// }

		// @SqlTable(tableName = "ABPERSONMULTIVALUEDELETES")
		// public interface ABPERSONMULTIVALUEDELETES
		// {
		// @SqlField("record_id")
		// public int getRecordId();
		//
		// @SqlField("property_id")
		// public int getPropertyId();
		//
		// @SqlField("identifier")
		// public int getIdentifier();
		// }

		// @SqlTable(tableName = "ABRECENT")
		// public interface ABRECENT
		// {
		// @SqlField("date")
		// public int getDate();
		//
		// @SqlField("property")
		// public int getProperty();
		// }

		@SqlDatabaseKey
		public int getRowId();

		@SqlField("Suffix")
		public String getSuffix();

	}
}