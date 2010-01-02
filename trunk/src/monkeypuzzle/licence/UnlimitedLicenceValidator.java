package monkeypuzzle.licence;

import monkeypuzzle.entity.status.Info;

public class UnlimitedLicenceValidator implements LicenceValidator
{

	public static final LicenceValidator DEFAULT_INSTANCE = new UnlimitedLicenceValidator();

	@Override
	public void checkLicence(final Info info) throws NotLicencedException
	{
		// do nothing
	}

}
