package com.crypticbit.ipa.ui.swing.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;

import com.crypticbit.ipa.entity.sqlite.LocationD;
import com.crypticbit.ipa.entity.sqlite.LocationD.Location;
import com.crypticbit.ipa.ui.swing.Mediator;
import com.crypticbit.ipa.ui.swing.SpecialView;
import com.crypticbit.ipa.ui.swing.map.KmlHandler.KmlWaypoint;
import com.crypticbit.ipa.ui.swing.map.KmlHandler.WaypointProvider;
import com.crypticbit.ipa.ui.swing.util.ToolBarOverlay;

public class MapView implements SpecialView
{

	private Mediator mediator;
	private Set<OurWayPoint> waypoints;

	public MapView(Mediator mediator)
	{
		this.mediator = mediator;
	}

	@Override
	public JComponent getComponent()
	{

		MapPanel mapPanel = new MapPanel(mediator);

		waypoints = new HashSet<OurWayPoint>();

		Collection<Location> cellLocations = mediator.getBackupDirectory()
				.getRecordsByInterface(LocationD.Location.class);
		if (cellLocations != null)
			for (LocationD.Location l : cellLocations)
			{
				waypoints.add(new OurWayPoint(l.getLatitude(),
						l.getLongitude(), "cell",l.getDate().toGMTString(), Color.RED));
			}

		Collection<Location> wifiLocations = mediator.getBackupDirectory()
				.getRecordsByInterface(LocationD.Location.class);
		if (wifiLocations != null)
			for (LocationD.Location l : wifiLocations)
			{
				waypoints.add(new OurWayPoint(l.getLatitude(),
						l.getLongitude(), "wifi",l.getDate().toGMTString(), Color.BLUE));
			}

		WaypointPainter painter = new WaypointPainter();
		painter.setRenderer(new WaypointRenderer() {

			@Override
			public boolean paintWaypoint(Graphics2D g, JXMapViewer map,
					Waypoint waypoint)
			{
				g.setStroke(new BasicStroke(3f));
				g.setColor(((OurWayPoint) waypoint).getColor());
				g.drawOval(-10, -10, 20, 20);
				g.setStroke(new BasicStroke(1f));
				g.drawLine(-10, 0, 10, 0);
				g.drawLine(0, -10, 0, 10);
				return false;
			}

		});
		painter.setWaypoints(waypoints);
		mapPanel.getMainMap().setOverlayPainter(painter);
		KmlHandler kmlHandler = new KmlHandler(mediator, new WaypointProvider() {
			
			@Override
			public Collection<? extends KmlWaypoint> getWaypoints()
			{
				return waypoints;
			}
		});

		return new ToolBarOverlay(mapPanel, kmlHandler.createExport(),
				kmlHandler.createOpen());

	}

	class OurWayPoint extends Waypoint implements KmlWaypoint
	{
		private String name;
		private Color color;
		private String date;

		OurWayPoint(Double latitude, Double longitude, String name, String date, Color color)
		{
			super(latitude, longitude);
			this.name = name;
			this.color = color;
			this.date = date;
		}

		@Override
		public double getLatitude()
		{
			return this.getPosition().getLatitude();
		}

		@Override
		public double getLongitude()
		{
			return this.getPosition().getLongitude();
		}

		@Override
		public String getName()
		{
			return name;
		}

		@Override
		public Color getColor()
		{
			return color;
		}

		@Override
		public String getDescription()
		{
			return date;
		}

	}

}
