package monkeypuzzle.results;

@SuppressWarnings("serial")
public class MatcherException extends Exception
{

	public MatcherException(final String message)
	{
		super(message);
	}

	public MatcherException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
