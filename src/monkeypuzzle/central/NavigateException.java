package monkeypuzzle.central;

@SuppressWarnings("serial")
public class NavigateException extends Exception
{
	public NavigateException(final String message, final Throwable t)
	{
		super(message, t);
	}
}
