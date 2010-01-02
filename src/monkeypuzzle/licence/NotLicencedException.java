package monkeypuzzle.licence;

public class NotLicencedException extends Exception
{

	public NotLicencedException(final String message)
	{
		super(message);
	}

	public NotLicencedException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

}
