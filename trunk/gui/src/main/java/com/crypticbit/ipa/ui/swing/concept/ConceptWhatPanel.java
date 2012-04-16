package com.crypticbit.ipa.ui.swing.concept;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXTitledPanel;

import com.crypticbit.ipa.entity.concept.Event;
import com.crypticbit.ipa.ui.swing.Mediator;
import com.crypticbit.ipa.ui.swing.SpringUtilities;

public class ConceptWhatPanel extends JXTitledPanel implements ConceptPanel {

	private final Set<String> notSelected = new HashSet<String>();
	private final ConceptDataModel conceptTableModel;
	private final Filter filter = new Filter() {

		@Override
		public boolean accept(Event e) {
			return !notSelected.contains(e.getWhat());
		}
	};
	private final Map<String, JCheckBox> checkBoxes = new HashMap<String, JCheckBox>();

	public ConceptWhatPanel(Mediator mediator,
			ConceptDataModel conceptTableModel) {
		super("What");
		this.conceptTableModel = conceptTableModel;
		conceptTableModel.addFilter(filter);
		init();
	}

	@Override
	public void fireFilterChange(Filter filter) {
		if (filter != this.filter) {
			for (final JCheckBox c : checkBoxes.values()) {
				c.setEnabled(false);
			}
			for (final Event e : conceptTableModel.getFilteredEvents(this.filter)) {
				checkBoxes.get(e.getWhat()).setEnabled(true);
			}
		}
	}

	@Override
	public void fireHighlightChange() {
		// do nothing

	}

	@Override
	public void fireSelectChange() {
		// do nothing

	}

	private void init() {
		final Set<String> whats = new TreeSet<String>();
		for (final Event e : conceptTableModel.getUnfilteredEvents()) {
			whats.add(e.getWhat());
		}
		final JPanel panel = new JPanel(new SpringLayout());
		for (final String what : whats) {
			final JLabel l = new JLabel(what, SwingConstants.TRAILING);
			panel.add(l);
			final JCheckBox x = new JCheckBox();
			checkBoxes.put(what, x);
			x.setAction(new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (x.isSelected())
						notSelected.remove(what.toString());
					else
						notSelected.add(what.toString());
					conceptTableModel.triggerFilterUpdate(filter);
				}

			});
			x.setSelected(!notSelected.contains(what));
			l.setLabelFor(x);
			panel.add(x);
		}

		SpringUtilities.makeCompactGrid(panel, whats.size(), 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		// this.removeAll();
		this.setContentContainer(new JScrollPane(panel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
	}

	@Override
	public void registerToUpdateOnSelectionChange() {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerToUpddateOnMouseOverChange() {
		// TODO Auto-generated method stub

	}

}
