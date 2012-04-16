package com.crypticbit.ipa.ui.swing;

import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public abstract class ViewingPane extends JTabbedPane 
{
	protected ViewingPane()
	{
		super(SwingConstants.BOTTOM);
	}
	
	public void resetDefaultTab()
	{
		setSelectedIndex(0);
	}

	public abstract void cleanUp();
}
