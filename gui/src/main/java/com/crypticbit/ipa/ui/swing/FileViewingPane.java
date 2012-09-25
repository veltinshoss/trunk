/**
 * 
 */
package com.crypticbit.ipa.ui.swing;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.results.ContentType;
import com.crypticbit.ipa.results.Location;
import com.crypticbit.ipa.ui.swing.Mediator.HighlightChangeListener;
import com.crypticbit.ipa.ui.swing.plist.PListView;

class FileViewingPane extends ViewingPane implements HighlightChangeListener {

	BackupFile backupFile;
	private CfTreeMap views;

	FileViewingPane(final BackupFile bfd, final Mediator mediator, final Callback callback)
			throws IOException, FileParseException {
		super();
		this.backupFile = bfd;

		// check for presence of data file (may be missing in corrupted
		// backups)
		if (bfd.getContentsFile().exists()) {
			this.views = new CfTreeMap();
			{
				this.views.put(ContentType.IMAGE, new ImageView(bfd, mediator));
				this.views.put(ContentType.PLIST, new PListView(bfd, mediator));
				this.views.put(ContentType.SQL, new SqlView(bfd, mediator));
				this.views.put("Deleted Fragments", new SQLUndeleteview(bfd,
						mediator));
				this.views.put(ContentType.TEXT, new TextView(bfd, mediator));
				this.views.put(ContentType.HEX, new HexView(bfd, mediator));

			}
			SpecialViewType specialViewType = SpecialViewType
					.getSpecialViewType(bfd.getParsedData().getViews());
			if (specialViewType != null) 
			{
				try
				{
					String name = specialViewType.getName();
					SpecialView createSpecialView = specialViewType
							.createSpecialView(mediator);
					if( createSpecialView != null)
					{
						JComponent component = createSpecialView.getComponent();
						this.add(name, component);
					}
				}
				catch(Exception e)
				{
					LogFactory.getLogger().log(Level.WARNING, "Failed to create special view for " + bfd.getOriginalFileName() + ", " + specialViewType, e);
				}
			}
			for (final Map.Entry<String, View> view : this.views) {

				if (view.getValue().shouldBeVisible()) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							try {
								view.getValue().init();
							} catch (Throwable t) {
								LogFactory.getLogger().log(Level.SEVERE,
										"Exception", t);
								LogFactory.getLogger().log(
										Level.INFO,
										"Error initialising view: "
												+ view.getClass());
							}
						}
					});
					add(view.getKey(), view.getValue());
				}

			}
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run()
				{
					callback.callback(FileViewingPane.this);
					
				}});

		} else {
			JTextPane message = new JTextPane();
			message.setText("The data associated with this backup file is missing, and should have been in a file called: "
					+ bfd.getContentsFile().getName());
			JScrollPane scrollPane = new JScrollPane(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setViewportView(message);
			this.add("Data Missing", scrollPane);
		}
	}

	@Override
	public void cleanUp() {
		if (this.views != null) {
			for (Map.Entry<String, View> view : views) {
				view.getValue().cleanUp();
			}
		}
	}

	@Override
	public void clearHighlighting() {
		// Do nothing
	}

	@Override
	public void highlight(final Collection<Location> locations) {
		// Do nothing
	}

	@Override
	public void moveTo(final Location location) {
		// If location is this file, switch to correct view
		BackupFile locationBackupFile = location.getBackupFile();
		if (this.backupFile.getCompleteOriginalFileName().equals(
				locationBackupFile.getCompleteOriginalFileName())) {
			// it's the right file, select right tab
			ContentType cv = location.getContentType();
			int tabIndex = this.indexOfTab(cv.toString());
			if (tabIndex != -1) {
				setSelectedIndex(tabIndex);
			}
		}
	}

}

class CfTreeMap extends LinkedList<Map.Entry<String, View>> {
	public void put(ContentType ct, View v) {
		this.put(ct.toString(), v);
	}

	public void put(String key, View value) {
		this.add(new AbstractMap.SimpleEntry<String, View>(key, value));
	}

}
