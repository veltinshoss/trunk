package monkeypuzzle.ui.swing;

import java.awt.GridLayout;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.results.ContentType;
import monkeypuzzle.results.Location;
import monkeypuzzle.ui.swing.Mediator.HighlightChangeListener;

import com.jhe.hexed.JHexEditor;

@SuppressWarnings("serial")
public class HexView extends View implements HighlightChangeListener
{
	private JHexEditor hexPane;
	private JScrollPane scrollPane = new JScrollPane(
			ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	HexView(final BackupFile bfd, final Mediator mediator) throws IOException,
			FileParseException
	{
		super(bfd, mediator);
		setLayout(new GridLayout(1, 1));
		add(this.scrollPane);

	}

	@Override
	public void clearHighlighting()
	{
		// TODO - Support highlighting

	}

	@Override
	public void highlight(final Collection<Location> locations)
	{
		// TODO - Support highlighting
	}

	@Override
	public void moveTo0(final Location location)
	{
		// TODO - Support highlighting

	}

	@Override
	protected ContentType getSupportedContentView()
	{
		return ContentType.HEX;
	}

	@Override
	protected void init() throws IOException
	{
		this.hexPane = new JHexEditor(getBackupFile().getContentsAsByteArray());
		this.scrollPane.setViewportView(this.hexPane);
		this.scrollPane.validate();
	}

	@Override
	protected boolean shouldBeVisible()
	{
		return true;
	}

}
