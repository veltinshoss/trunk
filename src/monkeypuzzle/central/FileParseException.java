package monkeypuzzle.central;

@SuppressWarnings("serial")
public class FileParseException extends Exception
{

	public FileParseException(final String msg)
	{
		super(msg);
	}

	public FileParseException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public FileParseException(final Throwable cause)
	{
		super(cause);
	}

}
