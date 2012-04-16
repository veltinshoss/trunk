package com.crypticbit.ipa.ui.swing.util;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;

public class OverlayComponent extends JLayeredPane
{

	OverlayComponent(final JComponent under, final JComponent over)
	{
		add(under, JLayeredPane.DEFAULT_LAYER);
		add(over, new Integer(100));
		this.setPreferredSize(new Dimension(500, 500));
		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e)
			{
				under.setBounds(0, 0, getWidth(), getHeight());
				over.setBounds(getWidth()
						- (int) over.getPreferredSize().getWidth(), 0,
						(int) over.getPreferredSize().getWidth(),
						(int) over.getPreferredSize().getHeight());
			}

		});
	}

}