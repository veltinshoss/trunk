package monkeypuzzle.report;

public enum TagType
{
	SEARCH("query"), VAR("variable"), LOCATE("locate");

	public static TagType findByName(final String name) throws TagException
	{
		for (TagType result : values())
			if (result.typeString.equals(name))
				return result;
		throw new TagException("Can not find tag: " + name);
	}

	String typeString;

	TagType(final String s)
	{
		this.typeString = s;
	}

}