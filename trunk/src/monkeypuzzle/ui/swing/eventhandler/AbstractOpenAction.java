package monkeypuzzle.ui.swing.eventhandler;

import java.awt.Cursor;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.central.IPhone;
import monkeypuzzle.central.IPhoneParseException;
import monkeypuzzle.central.ProgressIndicator;
import monkeypuzzle.licence.NotLicencedException;
import monkeypuzzle.ui.swing.Mediator;

public class AbstractOpenAction {

	public interface Creator {
		IPhone getIPhone() throws IPhoneParseException, IOException,
				FileParseException, NotLicencedException;

		void postCreate(Mediator mediator, IPhone iPhone);

	}

	static void openDataSource(final JFrame frame, final Mediator mediator,
			final Creator c) {
		{
			new SwingWorker<Void, Void>() {
				private ProgressMonitor progressMonitor = null;

				@Override
				public Void doInBackground() {
					try {
						frame.setCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));
						mediator.getIPhoneFactory().setProgressIndicator(
								new ProgressIndicator() {
									@Override
									public void progressUpdate(final int entry,
											final int outOf,
											final String description) {
										if (progressMonitor == null) {
											progressMonitor = new ProgressMonitor(
													frame,
													"Loading backup files...",
													description, 0, outOf);
											progressMonitor
													.setMillisToPopup(200);
											progressMonitor
													.setMillisToDecideToPopup(50);
										} else {
											progressMonitor.setProgress(entry);
											progressMonitor
													.setNote(description);
										}
									}
								});
						IPhone newIPhoneState = c.getIPhone();
						if (this.progressMonitor != null) {
							this.progressMonitor
									.setNote("Files loaded. Now scanning...");
						}
						c.postCreate(mediator, newIPhoneState);
					} catch (NotLicencedException nle) {
						mediator.getPreferences().removeCustomerId();
						mediator
								.displayErrorDialog(
										"Unable to open the backup directory because of a licence issue. If you have a valid Licence Key please try again.",
										nle);
					} catch (Throwable e1) {
						mediator.displayErrorDialog(
								"Problem opening directory", e1);
					} finally {
						frame.setCursor(Cursor
								.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						if (this.progressMonitor != null) {
							this.progressMonitor.close();
						}
						this.progressMonitor = null;
					}
					return null;
				}
			}.execute();
		}
	}

	protected final Mediator mediator;

	public AbstractOpenAction(Mediator mediator) {
		this.mediator = mediator;
	}

}