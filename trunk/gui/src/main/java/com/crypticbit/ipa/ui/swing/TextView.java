package com.crypticbit.ipa.ui.swing;

import java.awt.GridLayout;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.results.ContentType;
import com.crypticbit.ipa.results.Location;
import com.crypticbit.ipa.results.TextLocation;
import com.crypticbit.ipa.ui.swing.Mediator.HighlightChangeListener;


@SuppressWarnings("serial")
public class TextView extends View implements HighlightChangeListener
{
	private static Highlighter.HighlightPainter HIGHLIGHT_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(
			Constants.HIGHLIGHT_COLOUR);

	private JTextArea textPane = new JTextArea();
	private DisplayConverter displayConverter;
	
	TextView(final BackupFile bfd, final Mediator mediator) throws IOException,
			FileParseException
	{
		super(bfd, mediator);
		displayConverter = mediator.getDisplayConverter();
		setLayout(new GridLayout(1, 1));
		this.textPane.setEditable(false);
		this.textPane.setLineWrap(false);
		this.add(new JScrollPane(this.textPane));
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
	protected void init() throws FileParseException
	{
		getMediator().addHighlightChangeListener(this);
		this.textPane.setText(displayConverter.convertString(getBackupFile().getParsedData().getContents()));
		this.textPane.setCaretPosition(0);
	}

	@Override
	protected boolean shouldBeVisible()
	{
		return true;
	}

}
