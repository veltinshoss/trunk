/**
 * 
 */
package monkeypuzzle.ui.swing;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.results.ContentType;
import monkeypuzzle.results.Location;
import monkeypuzzle.ui.swing.Mediator.HighlightChangeListener;
import monkeypuzzle.ui.swing.plist.PListView;

class FileViewingPane extends ViewingPane implements
HighlightChangeListener
{
	private final class InitRunnable implements Runnable {
		private View view;
		public InitRunnable(View view) {
			this.view = view;
		}

		@Override
		public void run() {
			try {
				view.init();
				view.revalidate();
			} catch (Exception e) {
				mediator.displayErrorDialog("Can not create view "+view.getSupportedContentView().toString(), e);
			}
		}
	}

	private BackupFile backupFile;
	private Map<ContentType, View> views;
	private Executor executor = Executors.newFixedThreadPool (1);
	private Mediator mediator;
	
	FileViewingPane(final BackupFile bfd, final Mediator mediator)
			throws IOException, FileParseException
	{
		super();
		this.backupFile = bfd;
		this.mediator = mediator;

		// check for presence of data file (may be missing in corrupted
		// backups)
		if (bfd.getContentsFile().exists())
		{
			this.views = new TreeMap<ContentType, View>();
			{
				this.views.put(ContentType.TEXT,
						new TextView(bfd, mediator));
				this.views.put(ContentType.HEX, new HexView(bfd, mediator));
				this.views.put(ContentType.PLIST, new PListView(bfd,
						mediator));
				this.views.put(ContentType.IMAGE, new ImageView(bfd,
						mediator));
				this.views.put(ContentType.SQL, new SqlView(bfd, mediator));
			}
			SpecialViewType specialViewType = SpecialViewType
					.getSpecialViewType(bfd.getParsedData().getViews());
			if (specialViewType != null)
			{
				this.add(specialViewType.getName(), specialViewType
						.createSpecialView(mediator).getComponent());
			}
			for (Map.Entry<ContentType, View> view : this.views.entrySet())
			{
				if (view.getValue().shouldBeVisible())
				{
					executor.execute (new InitRunnable(view.getValue()));
					this.add(view.getKey().toString(), view.getValue());
				}
			}
		} else
		{
			JTextPane message = new JTextPane();
			message
					.setText("The data associated with this backup file is missing, and should have been in a file called: "
							+ bfd.getContentsFile().getName());
			JScrollPane scrollPane = new JScrollPane(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setViewportView(message);
			this.add("Data Missing", scrollPane);
		}
	}

	@Override
	public void cleanUp()
	{
		if (this.views != null)
		{
			for (View view : this.views.values())
			{
				view.cleanUp();
			}
		}
	}

	@Override
	public void clearHighlighting()
	{
		// Do nothing
	}

	@Override
	public void highlight(final Collection<Location> locations)
	{
		// Do nothing
	}

	@Override
	public void moveTo(final Location location)
	{
		// If location is this file, switch to correct view
		BackupFile locationBackupFile = location.getBackupFile();
		if (this.backupFile.getCompleteOriginalFileName().equals(
				locationBackupFile.getCompleteOriginalFileName()))
		{
			// it's the right file, select right tab
			ContentType cv = location.getContentType();
			int tabIndex = this.indexOfTab(cv.toString());
			if (tabIndex != -1)
			{
				setSelectedIndex(tabIndex);
			}
		}
	}

}