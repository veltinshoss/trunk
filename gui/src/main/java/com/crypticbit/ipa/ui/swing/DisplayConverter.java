package com.crypticbit.ipa.ui.swing;

import org.apache.commons.lang.StringUtils;

public interface DisplayConverter
{

	String convertString(String string);

	String convertNumber(String number);

	public DisplayConverter NULL = new NullDisplayConverter();
	public DisplayConverter OBSCURED = new ObscuredDisplayConverter();

	public class NullDisplayConverter implements DisplayConverter
	{

		public String convertString(String string)
		{
			return string==null ? "" : string;
		}

		public String convertNumber(String number)
		{
			return number==null ? "" : number;
		}
	}

	public class ObscuredDisplayConverter implements DisplayConverter
	{

		public String convertString(String string)
		{
			if (string == null)
				return null;
			return string.replaceAll("\\w", "*");
		}

		public String convertNumber(String number)
		{
			if (number == null)
				return null;
			int cut = Math.max(number.length() - 3, 0);
			return number.substring(0, cut)
					+ StringUtils.repeat("#", number.length() - cut);
		}
	}
}
