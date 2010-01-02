package monkeypuzzle.results;

import monkeypuzzle.io.parser.plist.PListLocation;
import monkeypuzzle.io.parser.plist.PListMatcher;
import monkeypuzzle.io.parser.sqlite.SqlMatcher;

public enum ContentType
{
	IMAGE("Image")
	{
		@Override
		public Matcher getMatcher(final String expression)
		{
			return null;
		}
	},
	PLIST("Plist")
	{
		@Override
		public Matcher getMatcher(final String expression)
		{
			final String[] matchComponent = expression
					.split(PListLocation.PLIST_ELEMENT_SEP);
			return new PListMatcher(matchComponent);
		}
	},
	SQL("Sql")
	{
		@Override
		public Matcher getMatcher(final String expression)
				throws MatcherException
		{
			return SqlMatcher.parseMatcher(expression);

		}
	},
	TEXT("Text")
	{
		@Override
		public Matcher getMatcher(final String expression)
		{
			return null;
		}
	},
	HEX("Hex")
	{
		@Override
		public Matcher getMatcher(final String expression)
		{
			return null;
		}
	};
	public static ContentType findByName(final String name)
	{
		for (ContentType result : values())
			if (result.name.equals(name))
				return result;
		throw new java.lang.EnumConstantNotPresentException(ContentType.class,
				name);
	}

	private String name;

	private ContentType(final String name)
	{
		this.name = name;
	}

	public abstract Matcher getMatcher(String expression)
			throws MatcherException;

	@Override
	public String toString()
	{
		return this.name;
	}
}
