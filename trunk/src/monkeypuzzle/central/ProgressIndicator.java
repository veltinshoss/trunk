package monkeypuzzle.central;

public interface ProgressIndicator
{
	public final ProgressIndicator nullProgressIndicator = new ProgressIndicator() {

		@Override
		public void progressUpdate(final int entry, final int outOf,
				final String description)
		{
			// do nothing
		}
	};

	public void progressUpdate(int entry, int outOf, String description);
}
