package com.crypticbit.ipa.entity.concept.wrapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.MatchType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

@Retention(RetentionPolicy.RUNTIME)
public @interface WhoTag
{

	String tag() default "";

	Field field() default Field.LOOKUP;

	String fieldMethod() default "";

	public interface WhoSetter
	{
		public void addIdentifier(Field field, String value);
	}

	public static enum Field
	{

		LOOKUP,
		FIRST_NAME,
		SURNAME,
		FULL_NAME,
		NAME_EXTRACT,
		PHONE_NUMBER(
				true)
		{
			private PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil
					.getInstance();

			public boolean compare(String a, String b, String defaultCountryCode)
			{
				try
				{
					if (!phoneNumberUtil
							.isPossibleNumber(a, defaultCountryCode)
							|| !phoneNumberUtil.isPossibleNumber(b,
									defaultCountryCode))
						return false;
					PhoneNumber numberA = phoneNumberUtil.parse(a,
							defaultCountryCode);
					PhoneNumber numberB = phoneNumberUtil.parse(b,
							defaultCountryCode);
					MatchType numberMatch = phoneNumberUtil.isNumberMatch(
							numberA, numberB);
					boolean match = numberMatch == MatchType.EXACT_MATCH
							|| numberMatch == MatchType.NSN_MATCH
							|| numberMatch == MatchType.SHORT_NSN_MATCH;
					return match;
				} catch (Exception e)
				{
					return false;
				}

			}

			/* Use last four characters to do hash */
			public int hashCode(String entry)
			{
				String trim = entry.trim();
				if (trim.length() < 4)
					return 0;
				return trim.substring(trim.length() - 4).hashCode();
			}
		},
		EMAIL(
				true),
		OTHER_INFO,
		OTHER_IDENTFIER(
				true);
		private boolean key;

		private Field()
		{
			this.key = false;
		}

		private Field(boolean key)
		{
			this.key = key;
		}

		public void setValue(WhoSetter gs, String value)
		{
			gs.addIdentifier(this, value);
		}

		public boolean compare(String a, String b, String defaultCountryCode)
		{
			return a.equals(b);
		}

		public int hashCode(String entry)
		{
			return entry.hashCode();
		}

		public boolean isKey()
		{
			return key;
		}
	}

	public interface WhoConceptable
	{
		public WhoTag.Field getConceptType();
	}
}
