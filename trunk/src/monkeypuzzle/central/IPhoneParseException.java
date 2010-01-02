package monkeypuzzle.central;

@SuppressWarnings("serial")
public class IPhoneParseException extends Exception
{

	public IPhoneParseException(final String message)
	{
		super(message);
	}

	public IPhoneParseException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

}
