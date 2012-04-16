package com.crypticbit.ipa.ui.swing.concept;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.WaypointPainter;

import com.crypticbit.ipa.entity.concept.Event;
import com.crypticbit.ipa.entity.concept.GeoLocation;

public class EventPainter<T extends JXMapViewer> extends WaypointPainter<T>
{

	private final com.crypticbit.ipa.ui.swing.concept.ConceptDataModel conceptTableModel;
	private HeatMap heatMap;

	public EventPainter(ConceptDataModel conceptTableModel)
	{
		this.conceptTableModel = conceptTableModel;
	}

	@Override
	protected void doPaint(Graphics2D g, T map, int width, int height)
	{
		// figure out which waypoints are within this map viewport
		// so, get the bounds
		final Rectangle viewportBounds = map.getViewportBounds();
		final int zoom = map.getZoom();
		final Dimension sizeInTiles = map.getTileFactory().getMapSize(zoom);
		final int tileSize = map.getTileFactory().getTileSize(zoom);
		final Dimension sizeInPixels = new Dimension(sizeInTiles.width
				* tileSize, sizeInTiles.height * tileSize);

		double vpx = viewportBounds.getX();
		// normalize the left edge of the viewport to be positive
		while (vpx < 0)
		{
			vpx += sizeInPixels.getWidth();
		}
		// normalize the left edge of the viewport to no wrap around the world
		while (vpx > sizeInPixels.getWidth())
		{
			vpx -= sizeInPixels.getWidth();
		}

		// create two new viewports next to eachother
		final Rectangle2D vp2 = new Rectangle2D.Double(vpx,
				viewportBounds.getY(), viewportBounds.getWidth(),
				viewportBounds.getHeight());
		// final Rectangle2D vp3 = new Rectangle2D.Double(
		// vpx - sizeInPixels.getWidth(),
		// viewportBounds.getY(),
		// viewportBounds.getWidth(),
		// viewportBounds.getHeight());

		if (zoom > 5)
		{

			heatMap = new HeatMap(width, height, 21);
			for (final Event event : conceptTableModel.getFilteredEvents())
			{

				for (final GeoLocation location : event.getLocations().values())
				{
					final Point2D point = map.getTileFactory().geoToPixel(
							new GeoPosition(location.getLatitude(),
									location.getLongitude()), map.getZoom());

					heatMap.add(point, vp2);
				}
			}
			double longitudeDegreeWidthInPixels = map.getTileFactory()
					.getInfo().getLongitudeDegreeWidthInPixels(zoom);
			heatMap.render(g, longitudeDegreeWidthInPixels);
		} else
		{

			// do in this order to ensure we don't overwrite
			for (final Event event : conceptTableModel.getFilteredEvents())
				if (!conceptTableModel.isSelected(event)
						&& !conceptTableModel.isHighlighted(event))
					renderPoints(g, map, vp2, null, event);

			for (final Event event : conceptTableModel.getFilteredEvents())
				if (conceptTableModel.isSelected(event)
						&& !conceptTableModel.isHighlighted(event))
					renderPoints(g, map, vp2, null, event);

			for (final Event event : conceptTableModel.getFilteredEvents())
				if (conceptTableModel.isHighlighted(event))
					renderPoints(g, map, vp2, null, event);
		}
	}

	private void paintWaypoint(Event event, GeoLocation location, Graphics2D g)
	{
		Color c;
		g.setStroke(new BasicStroke(3f));
		if (conceptTableModel.isHighlighted(event))
			c = Color.RED;
		else if (conceptTableModel.isSelected(event))
			c = Color.BLUE;
		else
			c = Color.GRAY;
		if (location.getAccuracy() == null)
		{
			g.setColor(c);
		}
		g.setStroke(new BasicStroke(1f));
		g.drawLine(-5, 0, 5, 0);
		g.drawLine(0, -5, 0, 5);
		g.drawOval(-3, -3, 6, 6);
	}

	public static Color findOppositeColor(Color color)
	{
		if (color == null)
			return null;
		int red = color.getRed();
		red = findOppositeNumber(red);
		int green = color.getGreen();
		green = findOppositeNumber(green);
		int blue = color.getBlue();
		blue = findOppositeNumber(blue);
		return new Color(red, green, blue);
	}

	public static int findOppositeNumber(int num)
	{
		// toggle the msb
		return (num ^ 0x80) & 0xff;
	}

	private void renderPoint(Graphics2D g, T map, Rectangle2D rectangle,
			Event event, GeoLocation location, Point2D point)
	{
		if (rectangle.contains(point))
		{
			final int x = (int) (point.getX() - rectangle.getX());
			final int y = (int) (point.getY() - rectangle.getY());
			g.translate(x, y);
			paintWaypoint(event, location, g);
			g.translate(-x, -y);
		}
	}

	private void renderPoints(Graphics2D g, T map, Rectangle2D vp2,
			Rectangle2D vp3, Event event)
	{

		// calculate the pixel per metre scaling factor
		double scale = (360.0 / 6371000.0)
				* map.getTileFactory().getInfo()
						.getLongitudeDegreeWidthInPixels(map.getZoom());
		for (final GeoLocation location : event.getLocations().values())
		{
			final Point2D point = map.getTileFactory().geoToPixel(
					new GeoPosition(location.getLatitude(),
							location.getLongitude()), map.getZoom());

			renderPoint(g, map, vp2, event, location, point);
			// renderPoint(g, map, vp3, event, location, point, scale);
		}

	}

	public class HeatMap
	{
		private int[][] lookup;
		private int gridSize;

		HeatMap(int width, int height, int gridSize)
		{
			lookup = new int[width / gridSize + 1][height / gridSize + 1];
			this.gridSize = gridSize;
		}

		public void add(Point2D point, Rectangle2D rectangle)
		{
			final int x = (int) (point.getX() - rectangle.getX());
			final int y = (int) (point.getY() - rectangle.getY());

			try
			{
				lookup[x / gridSize][y / gridSize]++;
			} catch (Exception e)
			{
				// System.err.println(x + "," + y);
			}
		}

		public void render(Graphics2D g, double scale)
		{
			int[][] colorLookup = new int[lookup.length][lookup[0].length];
			for (int x = 0; x < lookup.length; x++)
				for (int y = 0; y < lookup[x].length; y++)
				{
					int value = lookup[x][y];
					if (value != 0)
					{
						int t = lookup(value * scale * scale);
						colorLookup[x][y] = t;

					}
				}
			int span = gridSize / 3;
			// loop through all grids
			for (int x = 0; x < lookup.length; x++)
				for (int y = 0; y < lookup[x].length; y++)
				{
					int value = lookup[x][y];
					 int t = colorLookup[x][y];

					 // then break down into a sub 3x3 grid so we can blend colours
					for (int xx = 0; xx < 3; xx++)
						for (int yy = 0; yy < 3; yy++)
						{

							int count = 0;
							int sum = 0;
							// now count up the colours on adjacent squares
							for (int ax = 0; ax < 3; ax++)
								for (int ay = 0; ay < 3; ay++)
								{
									int xxx = x;
									if(ax == 0 && xx == 0)
										xxx--;
									if(ax == 2 && xx == 2)
										xxx++;
									int yyy = y;
									if(ay == 0 && yy == 0)
										yyy--;
									if(ay == 2 && yy == 2)
										yyy++;
									
									if (xxx >= 0 && xxx < lookup.length
											&& yyy >= 0
											&& yyy < lookup[0].length)
									{
										count++;
										sum += colorLookup[xxx][yyy];
									}
								}

						
							g.setColor(new Color(255 , 0, 0,
									sum / count));

							g.fillRect(x * gridSize + (span * xx), y * gridSize
									+ (span * yy),
									xx == 2 ? (gridSize - (2 * span)) : span,
									yy == 2 ? (gridSize - (2 * span)) : span);
						}

				}

		}

		private final int STEPS = 20;
		private final int MIN_T = 20;
		private final int MAX_T = 160;

		private int lookup(double value)
		{
			for (int x = 0; x < STEPS; x++)
			{
				if (value < (2 << x))
				{
					return ((MAX_T - MIN_T) / STEPS * x) + MIN_T;
				}
			}
			return MAX_T;
		}

	}
}
