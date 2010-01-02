package monkeypuzzle.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.ui.swing.ImageCache.DiscardImageCache;
import monkeypuzzle.ui.swing.ImageCache.MaxSizedImageCacheFactory;

public class ThumbnailView extends JPanel {
	private final class RespondMouseListener implements MouseListener {
		private BackupFile bfd;

		public RespondMouseListener(BackupFile bfd) {
			this.bfd = bfd;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			((JPanel) e.getComponent()).setBorder(createBorder(bfd, true));

		}

		@Override
		public void mouseExited(MouseEvent e) {
			((JPanel) e.getComponent()).setBorder(createBorder(bfd, false));
		}

		@Override
		public void mousePressed(MouseEvent e) {
			mediator.fireSelectedFileChangeListeners(bfd);

		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}

	private GridLayout layout;
	private Mediator mediator;

	public ThumbnailView(Mediator mediator, List<BackupFile> list) {
		this.mediator = mediator;
		this.setBorder(new EmptyBorder(4, 8, 4, 8));
		layout = new GridLayout(0, 4);
		adjustColumns();
		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent e) {
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				adjustColumns();
			}

			@Override
			public void componentShown(ComponentEvent e) {
			}
		});
		ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 1,
				TimeUnit.SECONDS, new ArrayBlockingQueue(20),
				new ThreadPoolExecutor.DiscardOldestPolicy());
		MaxSizedImageCacheFactory cacheThumnailFactory = new MaxSizedImageCacheFactory(
				40);
		this.setLayout(layout);
		DiscardImageCache discardImageCache = new DiscardImageCache();
		for (BackupFile bfd : list) {
			JPanel temp = new JPanel(new GridBagLayout());
			temp.addMouseListener(new RespondMouseListener(bfd));
			ImagePanel ip = new ImagePanel(new BackupFileImageAdapter(bfd),
					new Dimension(100, 100), executor, cacheThumnailFactory
							.createNewElement(), discardImageCache);
			temp.setBorder(createBorder(bfd, false));
			temp.add(ip);
			this.add(temp);
		}
	}

	private Border createBorder(BackupFile bfd, boolean highlight) {
		return new TitledBorder(highlight ? new LineBorder(Color.BLUE)
				: new LineBorder(Color.WHITE), bfd.getOriginalFileName(),
				TitledBorder.CENTER, TitledBorder.BOTTOM, null,
				highlight ? Color.BLUE : null);
	}

	private void adjustColumns() {
		int numCols = (int) Math.floor(ThumbnailView.this.getWidth() / 150);
		if (numCols >= 1)
			layout.setColumns(numCols);
	}
}
