package monkeypuzzle.ui.swing;

import java.awt.GridLayout;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.results.ContentType;
import monkeypuzzle.results.Location;
import monkeypuzzle.results.TextLocation;
import monkeypuzzle.ui.swing.Mediator.HighlightChangeListener;

@SuppressWarnings("serial")
public class TextView extends View implements HighlightChangeListener
{
	private static Highlighter.HighlightPainter HIGHLIGHT_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(
			Constants.HIGHLIGHT_COLOUR);

	private JTextArea textPane = new JTextArea();

	TextView(final BackupFile bfd, final Mediator mediator) throws IOException,
			FileParseException
	{
		super(bfd, mediator);
		setLayout(new GridLayout(1, 1));
		this.textPane.setEditable(false);
		this.textPane.setLineWrap(false);
		this.add(new JScrollPane(this.textPane));
		getMediator().addHighlightChangeListener(this);

	}

	@Override
	public void clearHighlighting()
	{
		this.textPane.getHighlighter().removeAllHighlights();
	}

	@Override
	public void highlight(final Collection<Location> locations)
	{
		for (Location location : locations)
		{
			if (location.getBackupFile().equals(getBackupFile())
					&& (location instanceof TextLocation))
			{
				TextLocation textLocation = ((TextLocation) location);
				highlight(textLocation.getStart(), textLocation.getLength());
			}

		}
	}

	public void highlight(final int start, final int length)
	{
		Highlighter highlighter = this.textPane.getHighlighter();
		try
		{
			highlighter.addHighlight(start, start + length, HIGHLIGHT_PAINTER);
		} catch (BadLocationException e)
		{
			getMediator()
					.displayErrorDialog(
							"Search results don't appear to correspond to a location in this file",
							e);
		}
	}

	@Override
	public void moveTo0(final Location location)
	{
		this.textPane.setCaretPosition(((TextLocation) location).getStart());
	}

	@Override
	protected ContentType getSupportedContentView()
	{
		return ContentType.TEXT;
	}

	@Override
	protected void init()
	{
		getMediator().addHighlightChangeListener(this);
		this.textPane.setText(getBackupFile().getParsedData().getContents());
		this.textPane.setCaretPosition(0);
	}

	@Override
	protected boolean shouldBeVisible()
	{
		return true;
	}

}
