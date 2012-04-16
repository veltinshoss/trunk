package com.crypticbit.ipa.ui.swing.concept;

import java.awt.BorderLayout;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactory;

import com.crypticbit.ipa.entity.concept.Event;
import com.crypticbit.ipa.entity.concept.GeoLocation;
import com.crypticbit.ipa.ui.swing.Mediator;
import com.crypticbit.ipa.ui.swing.map.KmlHandler;
import com.crypticbit.ipa.ui.swing.map.KmlHandler.KmlWaypoint;
import com.crypticbit.ipa.ui.swing.map.KmlHandler.WaypointProvider;
import com.crypticbit.ipa.ui.swing.map.MapPanel;
import com.crypticbit.ipa.ui.swing.util.ToolBarOverlay;

public class ConceptWherePanel extends JPanel implements ConceptPanel
{

	private final class OurFilter implements Filter
	{
		private final TileFactory tf = getMainMap().getTileFactory();
		private final JXMapViewer map = getMainMap();

		@Override
		public boolean accept(Event e)
		{
			if (map.getViewportBounds().getWidth() == 0
					|| map.getViewportBounds().getHeight() == 0)
				return true;
			if (e.getLocations() == null || e.getLocations().size() == 0)
				return true;
			for (final GeoLocation d : e.getLocations().values())
				if (currentlyVisible(d))
					return true;
			return false;
		}

		private boolean currentlyVisible(GeoLocation d)
		{
			return map.getViewportBounds().contains(
					tf.geoToPixel(
							new GeoPosition(d.getLatitude(), d.getLongitude()),
							getMainMap().getZoom()));
		}
	}

	private MapPanel mapPanel;

	private final Filter filter;

	private final ConceptDataModel conceptTableModel;

	public ConceptWherePanel(Mediator mediator,
			final ConceptDataModel conceptTableModel)
	{
		this.setLayout(new BorderLayout());
		mapPanel = new MapPanel(mediator);
		filter = new OurFilter();
		this.conceptTableModel = conceptTableModel;
		final EventPainter eventPainter = new EventPainter(conceptTableModel);
		getMainMap().setOverlayPainter(eventPainter);
		conceptTableModel.addFilter(filter);
		this.getMainMap().addPropertyChangeListener(
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt)
					{
						if (evt.getPropertyName().equals("zoom")
								|| evt.getPropertyName().equals("center"))
							conceptTableModel.triggerFilterUpdate(filter);
					}
				});

		//
		// fireChange();
		// conceptTableModel.triggerFilterUpdate(filter);

		KmlHandler kmlHandler = new KmlHandler(mediator,
				new WaypointProvider() {

					@Override
					public Collection<? extends KmlWaypoint> getWaypoints()
					{
						final List<KmlWaypoint> locations = new LinkedList<KmlWaypoint>();
						for (final Event event : conceptTableModel
								.getFilteredEvents())
						{

							for (final GeoLocation g : event.getLocations()
									.values())
								locations.add(new KmlWaypoint() {

									@Override
									public double getLatitude()
									{
										return g.getLatitude();
									}

									@Override
									public double getLongitude()
									{
										return g.getLongitude();
									}

									@Override
									public String getName()
									{
										return event.getWhat();
									}

									@Override
									public String getDescription()
									{
										return event.getDescription();
									}

									@Override
									public Color getColor()
									{
										return null;
									}
								});

						}
						return locations;
					}
				});

		this.add(new ToolBarOverlay(mapPanel, kmlHandler.createExport(),
				kmlHandler.createOpen()), BorderLayout.CENTER);
	}

	private JXMapViewer getMainMap()
	{
		return mapPanel.getMainMap();
	}

	private void fireChange()
	{
		// do nothing as this simply zooms, which causes other problems and
		// affects performance

		// final Set<GeoPosition> locations = new HashSet<GeoPosition>();
		// for (final Event event : conceptTableModel.getFilteredEvents()) {
		// if (conceptTableModel.isSelected(event)) {
		// for (final GeoLocation g : event.getLocations().values())
		// locations.add(new GeoPosition(g.getLatitude(), g
		// .getLongitude()));
		// }
		// }
		// mapPanel.zoomToPoints(locations);
		// this.repaint();
	}

	@Override
	public void fireFilterChange(Filter filter)
	{
		if (this.filter != filter)
			fireChange();
	}

	@Override
	public void fireHighlightChange()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void fireSelectChange()
	{
		fireChange();
	}

	@Override
	public void registerToUpdateOnSelectionChange()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void registerToUpddateOnMouseOverChange()
	{
		// TODO Auto-generated method stub

	}

}
