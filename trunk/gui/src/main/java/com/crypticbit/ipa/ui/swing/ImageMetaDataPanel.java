package com.crypticbit.ipa.ui.swing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

@SuppressWarnings("serial")
public class ImageMetaDataPanel extends JTable
{
	private static TableModel convertToModel(final Metadata metadata)
	{

		final List<String> entries = new ArrayList<String>();

		Iterator<Directory> directories = metadata.getDirectoryIterator();

		while (directories.hasNext())
		{
			Directory directory = directories.next();
			Iterator<Tag> tags = directory.getTagIterator();
			while (tags.hasNext())
			{
				Tag tag = tags.next(); // use Tag.toString()
				entries.add(tag.toString());
			}
		}

		return new AbstractTableModel() {

			@Override
			public int getColumnCount()
			{
				return 1;
			}

			@Override
			public String getColumnName(final int columnIndex)
			{
				return "EXIF Data";
			}

			@Override
			public int getRowCount()
			{
				return entries.size();
			}

			@Override
			public Object getValueAt(final int rowIndex, final int columnIndex)
			{
				return entries.get(rowIndex);
			}

		};

	}

	public ImageMetaDataPanel(final Metadata metadata)
	{
		super(convertToModel(metadata));
		getActionMap().put(
				TransferHandler.getCopyAction().getValue(Action.NAME),
				TransferHandler.getCopyAction());
	}

}
