package monkeypuzzle.io.dynamicproxy;

@SuppressWarnings("serial")
public class DynamicProxyException extends Exception
{
	public DynamicProxyException(final String message)
	{
		super(message);
	}

	DynamicProxyException(final String message, final Exception e)
	{
		super(message, e);
	}
}
