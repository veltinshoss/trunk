package com.crypticbit.ipa.ui.swing;

import java.awt.Image;
import java.lang.ref.WeakReference;
import java.util.concurrent.ArrayBlockingQueue;

public interface ImageCache {
	public void storeImage(Image image);

	public Image getImage();

	public final static class SingleImageCache implements ImageCache {
		private Image image;

		@Override
		public Image getImage() {
			return image;
		}

		@Override
		public void storeImage(Image image) {
			this.image = image;

		}
	}

	public final static class MaxSizedImageCacheFactory {
		private ArrayBlockingQueue<Image> queue;

		public MaxSizedImageCacheFactory(int size) {
			queue = new ArrayBlockingQueue<Image>(size);
		}

		private MaxSizedImageCacheFactory(ArrayBlockingQueue queue) {
			this.queue = queue;
		}

		public MaxSizedImageCache createNewElement() {
			return new MaxSizedImageCache(queue);
		}
	}

	public final static class MaxSizedImageCache implements ImageCache {
		private ArrayBlockingQueue<Image> queue;

		MaxSizedImageCache(ArrayBlockingQueue<Image> queue) {
			this.queue = queue;
		}

		private WeakReference<Image> weak;

		@Override
		public Image getImage() {
			return weak == null ? null : weak.get();
		}

		@Override
		public void storeImage(Image image) {
			weak = new WeakReference(image);
			while (!queue.offer(image)) {
				queue.poll();
			}
		}
	}

	public final static class DiscardImageCache implements ImageCache {

		@Override
		public Image getImage() {
			return null;
		}

		@Override
		public void storeImage(Image image) {
		}
	}
}
