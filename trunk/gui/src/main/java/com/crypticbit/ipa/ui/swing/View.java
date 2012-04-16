package com.crypticbit.ipa.ui.swing;

import java.io.IOException;

import javax.swing.JPanel;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.results.ContentType;
import com.crypticbit.ipa.results.Location;
import com.crypticbit.ipa.ui.swing.Mediator.HighlightChangeListener;


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
		mediator.addHighlightChangeListener(this);

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
