package com.crypticbit.ipa.licence;

import com.crypticbit.ipa.entity.status.Info;

public interface LicenceValidator
{

	public void checkLicence(Info info) throws NotLicencedException;

}
