package monkeypuzzle.ui.swing;

import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.io.parser.sqlite.SqlResults;
import monkeypuzzle.results.ContentType;
import monkeypuzzle.results.Location;
import monkeypuzzle.ui.swing.Mediator.HighlightChangeListener;

import org.tmatesoft.sqljet.browser.core.BrowserComponentManager;

@SuppressWarnings("serial")
public class SqlView extends View implements HighlightChangeListener {

	private Collection<Location> highlights = new ArrayList<Location>();
	private BackupFile bfd;

	SqlView(final BackupFile bfd, final Mediator mediator) throws IOException,
			FileParseException {
		super(bfd, mediator);
		this.bfd = bfd;
		setLayout(new GridLayout(1, 1));
		getMediator().addHighlightChangeListener(this);
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
