package com.crypticbit.ipa.ui.swing.map;

import java.util.Set;
import java.util.logging.Level;

import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.ui.swing.Mediator;
import com.crypticbit.ipa.ui.swing.prefs.Preferences;
import com.crypticbit.ipa.ui.swing.prefs.Preferences.PrefType;
import com.crypticbit.ipa.ui.swing.prefs.Preferences.PrefsChangeListener;

@SuppressWarnings("serial")
public class MapPanel extends JXMapKit implements PrefsChangeListener {

	private final static int MIN_MAP_ZOOM = 1;
	private final static int MAX_MAP_ZOOM = 16;
	private Preferences prefs;

	//zoom is reversed in open street maps, so we need to take MAX rather than MIN and work back from the actual min zoom
	private final static int DEFAULT_ZOOM = MAX_MAP_ZOOM - 2;
	
	public MapPanel(Mediator mediator) {

		prefs = mediator.getPreferences();
		configureMapFromPreferences();
		prefs.addWeakPreferenceChangeListener(this);

		setZoom(MAX_MAP_ZOOM);
		this.getMainMap().setHorizontalWrapped(false);

	}

	private void configureMapFromPreferences() {
		boolean useOfflineMaps = prefs.getUseOfflineMaps();
		LogFactory.getLogger().log(Level.INFO,
				"Using Offline maps: " + useOfflineMaps);
		if (useOfflineMaps) {
			TileFactoryInfo localTileFactoryInfo;
			localTileFactoryInfo = new TileFactoryInfo(

					MIN_MAP_ZOOM, // minimumZoomLevel
					MAX_MAP_ZOOM - 1, // maximumZoomLevel
					MAX_MAP_ZOOM, // totalMapZoom
					256, // tile size
					true, true, // x/y orientation is normal
					prefs.getOfflineMapDir().toURI().toASCIIString(), // baseUrl
					// "file:/home/mat/Downloads/maptiles/"
					"x", "y", "z" // URL params for x, y, z
			) {
				public String getTileUrl(int x, int y, int zoom) {
					zoom = MAX_MAP_ZOOM - zoom;
					return this.baseURL + '/' + zoom + '/' + x + '/' + y
							+ ".png";
				}
			};

			localTileFactoryInfo.setDefaultZoomLevel(DEFAULT_ZOOM);
			TileFactory localTileFactory = new DefaultTileFactory(
					localTileFactoryInfo);
			setTileFactory(localTileFactory);
		} else {
			setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
		}
		
		setZoom(DEFAULT_ZOOM);
	}

	public void zoomToPoints(Set<GeoPosition> points) {
		getMainMap().setZoom(MAX_MAP_ZOOM);
		moveToCentre(points);
		getMainMap().calculateZoomFrom(points);
	}

	private void moveToCentre(Set<GeoPosition> points) {
		if (points == null || points.size() <= 0)
			return;
		double minLat, maxLat, minLong, maxLong;
		minLat = maxLat = points.iterator().next().getLatitude();
		minLong = maxLong = points.iterator().next().getLongitude();

		for (GeoPosition p : points) {
			if (p.getLatitude() < minLat)
				minLat = p.getLatitude();
			if (p.getLatitude() > maxLat)
				maxLat = p.getLatitude();
			if (p.getLongitude() < minLong)
				minLong = p.getLongitude();
			if (p.getLongitude() > maxLong)
				maxLong = p.getLongitude();
		}
		getMainMap().setCenterPosition(
				new GeoPosition((minLat + maxLat) / 2.0,
						(minLong + maxLong) / 2.0));
	}

	@Override
	public void preferenceUpdated(PrefType prefType) {
		if (prefType == PrefType.Mapping)
			configureMapFromPreferences();

	}

}
