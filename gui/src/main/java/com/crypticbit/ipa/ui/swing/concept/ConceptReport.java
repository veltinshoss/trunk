package com.crypticbit.ipa.ui.swing.concept;

import java.util.logging.Level;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.ui.swing.Mediator;

public class ConceptReport extends JScrollPane {

	public ConceptReport(Mediator mediator,
			final ConceptDataModel conceptTableModel) {
		final JTextPane text = new JTextPane();
		text.setContentType("text/html");
		text.setText("<html>Loading...</html>");
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					text.setText(conceptTableModel.getUnfilteredEvents()
							.asHtml());
				} catch (Exception e) {
					text.setText("Problem loading report");
					LogFactory.getLogger().log(Level.WARNING,
							"problem creating report", e);
				}
			}
		});
		this.setViewportView(text);

	}

}
