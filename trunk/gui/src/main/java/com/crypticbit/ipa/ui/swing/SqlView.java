package com.crypticbit.ipa.ui.swing;

import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;

import org.tmatesoft.sqljet.browser.core.BrowserComponentManager;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.io.parser.sqlite.SqlResults;
import com.crypticbit.ipa.results.ContentType;
import com.crypticbit.ipa.results.Location;
import com.crypticbit.ipa.ui.swing.Mediator.HighlightChangeListener;

@SuppressWarnings("serial")
public class SqlView extends View implements HighlightChangeListener {

	private Collection<Location> highlights = new ArrayList<Location>();
	private BackupFile bfd;

	SqlView(final BackupFile bfd, final Mediator mediator) throws IOException,
			FileParseException {
		super(bfd, mediator);
		this.bfd = bfd;
		setLayout(new GridLayout(1, 1));
	}

	@Override
	public void clearHighlighting() {
		this.highlights = new ArrayList<Location>();
	}

	@Override
	public void highlight(final Collection<Location> locations) {
		// FIXME
		// for (Location l : locations)
		// {
		// if ((l.getContentType() == ContentType.SQL)
		// && (l.getBackupFile() == getBackupFile()))
		// {
		// this.highlights.add(l);
		// }
		// }
	}

	@Override
	public void moveTo0(final Location location) {
		// int tabIndex = this.tabbedPane.indexOfTab((((SqlLocation) location)
		// .getTable()));
		// if (tabIndex != -1)
		// {
		// this.tabbedPane.setSelectedIndex(tabIndex);
		// }
	}

	@Override
	protected ContentType getSupportedContentView() {
		return ContentType.SQL;
	}

	@Override
	protected void init() {
		BrowserComponentManager manager = BrowserComponentManager
				.create(new JFrame());
		this.add(manager.getComponent());
		try {
			manager.open(bfd.getContentsFile());
		} catch (IOException e) {
			getMediator().displayErrorDialog(
					"Unable to access database file: "
							+ bfd.getOriginalFileName(), e);
		}
	}

	@Override
	protected boolean shouldBeVisible() {
		return getBackupFile().getParsedData() instanceof SqlResults;
	}
}
