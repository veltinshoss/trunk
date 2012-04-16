package com.crypticbit.ipa.ui.swing;

import java.awt.Desktop;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import org.jdesktop.swingx.JXTitledPanel;

import com.crypticbit.ipa.About;
import com.crypticbit.ipa.central.LogFactory;

public class ProductMessage extends JXTitledPanel implements HyperlinkListener
{

	ProductMessage()
	{
		super("Product Message");
		getContentContainer().setLayout(new GridLayout(1, 1));
		JEditorPane tc;

		try
		{
			tc = new JEditorPane(About.getLicence());
			tc.addHyperlinkListener(this);
			tc.setEditable(false);
			this.add(tc);
		} catch (IOException e)
		{
			this.add(new JLabel("Licence file missing"));
		}

	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e)
	{
		if (e.getEventType() == EventType.ACTIVATED)
			try
			{
				Desktop.getDesktop().browse(e.getURL().toURI());
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				LogFactory.getLogger().log(Level.SEVERE, "Exception", e1);
			} catch (URISyntaxException e1)
			{
				// TODO Auto-generated catch block
				LogFactory.getLogger().log(Level.SEVERE, "Exception", e1);
			}

	}

}
