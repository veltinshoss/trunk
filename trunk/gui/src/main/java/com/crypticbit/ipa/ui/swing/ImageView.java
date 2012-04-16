package com.crypticbit.ipa.ui.swing;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.io.parser.media.MediaParser;
import com.crypticbit.ipa.results.ContentType;
import com.crypticbit.ipa.results.Location;
import com.crypticbit.ipa.ui.swing.Mediator.HighlightChangeListener;
import com.drew.metadata.Metadata;

@SuppressWarnings("serial")
public class ImageView extends View implements HighlightChangeListener {

	/*
	 * ImagePanel.java
	 * 
	 * Copyright (C) 2007 Scott Carpenter (scottc at movingtofreedom dot org)
	 * 
	 * This program is free software: you can redistribute it and/or modify it
	 * under the terms of the GNU General Public License as published by the
	 * Free Software Foundation, either version 3 of the License, or (at your
	 * option) any later version.
	 * 
	 * This program is distributed in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
	 * Public License for more details.
	 * 
	 * You should have received a copy of the GNU General Public License along
	 * with this program. If not, see http://www.gnu.org/licenses/.
	 * 
	 * Created on November 9, 2007, 4:07 PM
	 */

	private ImagePanel imagePanel;

	private JSplitPane splitPane;;

	ImageView(final BackupFile bfd, final Mediator mediator)
			throws IOException, FileParseException {
		super(bfd, mediator);
		setLayout(new GridLayout(1, 1));
		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		this.splitPane.setOneTouchExpandable(true);
		this.add(this.splitPane);
	}

	@Override
	public void clearHighlighting() {
		// TODO - Support highlighting

	}

	@Override
	public void highlight(final Collection<Location> locations) {
		// TODO - Support highlighting

	}

	@Override
	public void moveTo0(final Location location) {
		// TODO - Support highlighting

	}

	@Override
	protected ContentType getSupportedContentView() {
		return ContentType.IMAGE;
	}

	@Override
	protected void init() {

		this.imagePanel = new ImagePanel(new BackupFileImageAdapter(
				getBackupFile()));
		if (getMetadata() != null) {
			JScrollPane metadataPane = new JScrollPane(new ImageMetaDataPanel(
					getMetadata()));
			metadataPane.setMinimumSize(new Dimension(150, 0));
			this.splitPane.add(metadataPane, JSplitPane.RIGHT);
		}
			this.splitPane.add(this.imagePanel, JSplitPane.LEFT);
		
			this.splitPane.setResizeWeight(1.0);
			this.splitPane
					.setDividerLocation(this.splitPane.getSize().width - 150);
			revalidate();
			this.imagePanel.scaleImage();
			this.repaint();
			this.imagePanel.addComponentListener(new ComponentListener() {

				@Override
				public void componentHidden(final ComponentEvent e) {
					// do nothing

				}

				@Override
				public void componentMoved(final ComponentEvent e) {
					// do nothing

				}

				@Override
				public void componentResized(final ComponentEvent e) {
					if(imagePanel != null) 
						ImageView.this.imagePanel.scaleImage();
				}

				@Override
				public void componentShown(final ComponentEvent e) {
					// do nothing

				}
			});
		

	}

	@Override
	protected boolean shouldBeVisible() {
		return (ImageIO.getImageWritersBySuffix(getBackupFile()
				.getOriginalFileNameSufix()).hasNext());

	}

	private Metadata getMetadata() {
		if (getBackupFile().getParsedData() instanceof MediaParser.MediaResults)
			return ((MediaParser.MediaResults) getBackupFile().getParsedData())
					.getMetadata();
		else
			return null;
	}

}
