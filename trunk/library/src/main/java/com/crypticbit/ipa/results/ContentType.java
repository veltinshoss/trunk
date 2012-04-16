package com.crypticbit.ipa.results;


public enum ContentType
{
	IMAGE("Image"), PLIST("Plist"), SQL("Sql"), TEXT("Text"), HEX("Hex");
	
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

	@Override
	public String toString()
	{
		return this.name;
	}
}
