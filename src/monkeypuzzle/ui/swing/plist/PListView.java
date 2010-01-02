package monkeypuzzle.ui.swing.plist;

import java.awt.GridLayout;
import java.io.IOException;
import java.util.Collection;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.io.parser.plist.PListResults;
import monkeypuzzle.results.ContentType;
import monkeypuzzle.results.Location;
import monkeypuzzle.ui.swing.Mediator;
import monkeypuzzle.ui.swing.View;
import monkeypuzzle.ui.swing.Mediator.HighlightChangeListener;

@SuppressWarnings("serial")
public class PListView extends View implements HighlightChangeListener
{

	private PListPanel panel;

	public PListView(final BackupFile bfd, final Mediator mediator)
			throws IOException, FileParseException
	{
		super(bfd, mediator);
		setLayout(new GridLayout(1, 1));
	}

	@Override
	public void clearHighlighting()
	{
		this.panel.clearHighlighting();

	}

	@Override
	public void highlight(final Collection<Location> locations)
	{
		this.panel.highlight(locations);
	}

	@Override
	protected ContentType getSupportedContentView()
	{
		return ContentType.PLIST;
	}

	@Override
	protected void init() throws IOException, FileParseException
	{
		PListResults<?> plp = (PListResults<?>) getBackupFile().getParsedData();
		this.panel = new PListPanel(plp.getRootContainer(), getMediator());
		this.add(this.panel);
		getMediator().addHighlightChangeListener(this);
	}

	@Override
	protected void moveTo0(final Location location)
	{
		this.panel.moveTo(location);

	}

	@Override
	protected boolean shouldBeVisible()
	{
		return getBackupFile().getParsedData() instanceof PListResults;
	}
}
