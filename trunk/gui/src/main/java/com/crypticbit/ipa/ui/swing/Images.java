package com.crypticbit.ipa.ui.swing;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

public enum Images
{

	NetworkConnected("NetworkConnected.png"), NetworkDisconnected(
			"NetworkDisconnected.png"), Quit("icons/Open24.gif");

	String imagefilename;

	Images(final String name)
	{

		this.imagefilename = name;
	}

	Icon getIcon()
	{

		return new ImageIcon(getImage());

	}

	Icon getIcon(final int width, final int height)
	{

		return new ImageIcon(getImage(width, height));

	}

	BufferedImage getImage()
	{

		try
		{

			return ImageIO.read(ClassLoader
					.getSystemResourceAsStream(this.imagefilename));

		}

		catch (IOException e)
		{

			return null;

		}

	}

	BufferedImage getImage(final int width, final int height)
	{

		return GraphicsUtilities.createThumbnail(getImage(), width, height);

	}

}// end enum Images

