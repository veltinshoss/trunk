package monkeypuzzle.licence;

import monkeypuzzle.entity.status.Info;

public interface LicenceValidator
{

	public void checkLicence(Info info) throws NotLicencedException;

}
