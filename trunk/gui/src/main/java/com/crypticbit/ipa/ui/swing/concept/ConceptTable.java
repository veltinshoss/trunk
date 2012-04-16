package com.crypticbit.ipa.ui.swing.concept;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;

import com.crypticbit.ipa.ui.swing.Mediator;

public class ConceptTable extends JScrollPane implements ConceptPanel
{

	private final ConceptDataModel conceptTableModel;
	private final JTable table;

	public ConceptTable(Mediator mediator, ConceptDataModel conceptTableModel)
	{
		super();
		table = new JTable(conceptTableModel);
		table.getActionMap().put(
				TransferHandler.getCopyAction().getValue(Action.NAME),
				TransferHandler.getCopyAction());
		setViewportView(table);
		this.conceptTableModel = conceptTableModel;
		final TableColumnModel tcm = table.getColumnModel();

		// //worth experimenting with the constant below once we have the layout
		// proportions sorted...
		// table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// int margin = 2;
		//
		// //iterate over all but last column
		// for( int c=0; c<table.getColumnCount(); c++)
		// {
		// JTableUtils.packColumn(table, c, margin);
		// }

	}

	public void fireChange()
	{
		conceptTableModel.fireTableDataChanged();
		this.revalidate();

	}

	@Override
	public void fireFilterChange(Filter filter)
	{
		fireChange();

	}

	@Override
	public void fireHighlightChange()
	{
	}

	@Override
	public void fireSelectChange()
	{
	}

	@Override
	public void registerToUpdateOnSelectionChange()
	{
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e)
					{
						if (!e.getValueIsAdjusting())
						{
							conceptTableModel.triggerSelectionUpdated(table
									.getSelectedRows());
						}
					}
				});

	}

	@Override
	public void registerToUpddateOnMouseOverChange()
	{
		table.addMouseMotionListener(new MouseMotionAdapter() {
			private int row;

			@Override
			public void mouseMoved(MouseEvent e)
			{
				final int tRow = table.rowAtPoint(e.getPoint());
				if (row != tRow)
				{
					row = tRow;
					conceptTableModel.triggerHighlightEvent(conceptTableModel
							.eventAt(row));
				}
			}

		});

	}
}
