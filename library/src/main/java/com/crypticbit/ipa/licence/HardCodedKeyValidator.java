package com.crypticbit.ipa.licence;

import com.crypticbit.ipa.entity.status.Info;

public class HardCodedKeyValidator implements LicenceValidator
{
	// 0000000000111111111122222222223333333333
	// 0123456789012345678901234567890123456789
	// 00000000c0cb39719b403dd262b9eb862e5a8ca6
	// 00000000c0cba9719b403dd262b1eb862e5a8ca6 - isecpartners.com o1=12,c1=a,o2=27,c2=1
	private static final String licencePart1 = "00000000c0cb39719b403dd262b9eb862e5a8ca6";
	private static final int offset1 = 12;
	private static char char1 = 'a';
	private static final int offset2 = 27;
	private static char char2 = '1';

	private ValidatorUi ui;

	public HardCodedKeyValidator(final ValidatorUi ui)
	{
		this.ui = ui;
	}

	@Override
	public void checkLicence(final Info info) throws NotLicencedException
	{
		String userProductKey = this.ui.getCustomerNumber();
		StringBuilder buff = new StringBuilder(licencePart1);

		// badly obfuscated way of replacing chars at offsets

		for (int i = 0; i < buff.length(); i++)
		{
			if (i == offset1)
			{
				buff.setCharAt(i, char1);
			} else if (i == offset2)
			{
				buff.setCharAt(offset2, char2);
			}
		}

		if (!buff.toString().equals(userProductKey))
			throw new NotLicencedException("Invalid Licence Key");
	}

}
