/**
 * 
 */
package com.crypticbit.ipa.ui.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.crypticbit.ipa.ui.swing.ImageCache.SingleImageCache;


public class ImagePanel extends JPanel {

	public interface ImageSource {
		public InputStream getInputStream() throws IOException;
	}

	private int imageHeight = 0;
	private int imageWidth = 0;
	private Dimension preferred;
	private ImageSource source;
	private ExecutorService executor;
	private ImageCache thumnailImageCache;
	private ImageCache originalImageCache;
	
	public ImagePanel(final ImageSource source, Dimension preferred,
			ExecutorService executor, ImageCache thumnailImageCache, ImageCache originalImageCache) {
		super();
		this.source = source;
		this.executor = executor;
		this.preferred = preferred;
		this.thumnailImageCache = thumnailImageCache;
		this.originalImageCache = originalImageCache;
		setLayout(new GridLayout(1, 1));

	}

	public ImagePanel(final ImageSource source) {
		this(source, (Dimension) null, Executors.newSingleThreadExecutor(),
				new SingleImageCache(),new SingleImageCache());
	}

	@Override
	public Dimension getPreferredSize() {
		if (preferred == null)
			return new Dimension(imageWidth, imageHeight);
		else

			return preferred;
	}

	// override paintComponent
	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		final Image temp = thumnailImageCache.getImage();
		if (temp == null) {
			executor.execute(new Runnable() {
				public void run() {
					try {
						g.drawImage(getScaledImage(), 0, 0, ImagePanel.this);
						ImagePanel.this.repaint();
					} catch (IOException e) {
						// do nothing
					}
				}
			});
		} else
			g.drawImage(temp, 0, 0, this);
	}

	private Image getScaledImage() throws IOException {
		Image temp = thumnailImageCache.getImage();
		if (temp == null) {
			temp = calculateScaledImage(getOriginalImage());
			cacheScaledImage(temp);
		}
		return temp;
	}

	private void cacheScaledImage(Image temp) {
		thumnailImageCache.storeImage(temp);
	}

	// e.g., containing frame might call thi s from formComponentResized
	public void scaleImage() {
		try {
			calculateScaledImage(getOriginalImage());
		} catch (IOException e) {
			// do nothing
		}
	}

	private Image calculateScaledImage(Image image) throws IOException {
		// use floats so division below won't round
		float iw = this.imageWidth;
		float ih = this.imageHeight;
		float pw = getWidth(); // panel width
		float ph = getHeight(); // panel height

		if ((pw < iw) || (ph < ih)) {

			/*
			 * compare some ratios and then decide which side of image to anchor
			 * to panel and scale the other side (this is all based on empirical
			 * observations and not at all grounded in theory)
			 */

			if ((pw / ph) > (iw / ih)) {
				iw = -1;
				ih = ph;
			} else {
				iw = pw;
				ih = -1;
			}

			// prevent errors if panel is 0 wide or high
			if (iw == 0) {
				iw = -1;
			}
			if (ih == 0) {
				ih = -1;
			}

			return getOriginalImage().getScaledInstance(
					new Float(iw).intValue(), new Float(ih).intValue(),
					Image.SCALE_DEFAULT);
		} else {
			return getOriginalImage();
		}
	}

	private Image getOriginalImage() throws IOException {
		Image temp = originalImageCache.getImage();
		if (temp == null) {
			temp = loadImage();
			if (preferred == null)
				setImage(temp);
		}

		return temp;
	}

	private void setImage(Image image) {
originalImageCache.storeImage(image);
	}

	private Image loadImage() throws IOException {
		InputStream is = source.getInputStream();
		Image temp = ImageIO.read(is);
		is.close();
		this.imageHeight = temp.getHeight(null);
		this.imageWidth = temp.getWidth(null);
		return temp;

	}

}