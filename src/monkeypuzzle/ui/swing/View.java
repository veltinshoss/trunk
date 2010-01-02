package monkeypuzzle.ui.swing;

import java.io.IOException;

import javax.swing.JPanel;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.results.ContentType;
import monkeypuzzle.results.Location;
import monkeypuzzle.ui.swing.Mediator.HighlightChangeListener;

/**
 * Lifecycle is instantiate, call <code>shouldBeVisible</code> and if that
 * returns <code>true</code> then to call <code>init</code>
 * 
 * @author Leo
 * 
 */
public abstract class View extends JPanel implements HighlightChangeListener
{
	private final BackupFile bfd;
	private final Mediator mediator;

	protected View(final BackupFile bfd, final Mediator mediator)
			throws IOException, FileParseException
	{
		this.mediator = mediator;
		this.bfd = bfd;
	}

	public void cleanUp()
	{
		this.mediator.removeHighlightChangeListener(this);
	}

	@Override
	public final void moveTo(final Location location)
	{

		if ((location.getBackupFile() == this.bfd)
				&& (location.getContentType() == getSupportedContentView()))
		{
			moveTo0(location);
		}
	}

	protected BackupFile getBackupFile()
	{
		return this.bfd;
	}

	protected Mediator getMediator()
	{
		return this.mediator;
	}

	protected abstract ContentType getSupportedContentView();

	protected abstract void init() throws IOException, FileParseException;

	protected abstract void moveTo0(Location location);

	protected abstract boolean shouldBeVisible();

}
