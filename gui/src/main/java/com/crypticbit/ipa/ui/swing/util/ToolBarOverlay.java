package com.crypticbit.ipa.ui.swing.util;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;

public class ToolBarOverlay extends OverlayComponent
{

	public ToolBarOverlay(JComponent under, JButton... buttons)
	{
		super(under, createToolBar(buttons));
	}

	private static JToolBar createToolBar(JComponent... buttons)
	{
		JToolBar toolBar = new JToolBar("Test", JToolBar.VERTICAL);
		for (JComponent c : buttons)
			toolBar.add(c);
		toolBar.setRollover(true);
		toolBar.setBackground(new Color(255, 255, 255, 128));
		toolBar.setOpaque(true);
		toolBar.setFloatable(false);
		return toolBar;
	}

}