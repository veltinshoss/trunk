package com.crypticbit.ipa.ui.swing;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class NotImplementedYetPanel extends JPanel
{
	NotImplementedYetPanel()
	{
		setLayout(new BorderLayout());
		this.add(new JLabel("This feature is not yet implemented"),
				BorderLayout.CENTER);
	}
}
