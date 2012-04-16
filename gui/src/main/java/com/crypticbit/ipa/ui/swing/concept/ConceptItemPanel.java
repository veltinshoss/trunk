package com.crypticbit.ipa.ui.swing.concept;

import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.crypticbit.ipa.entity.concept.Event;
import com.crypticbit.ipa.entity.concept.GeoLocation;
import com.crypticbit.ipa.entity.concept.Who;
import com.crypticbit.ipa.entity.concept.wrapper.Tag;
import com.crypticbit.ipa.entity.concept.wrapper.WhoTag.Field;
import com.crypticbit.ipa.results.Location;
import com.crypticbit.ipa.ui.swing.GuiUtils;
import com.crypticbit.ipa.ui.swing.Mediator;

public class ConceptItemPanel extends JScrollPane implements ConceptPanel {

	private ConceptDataModel conceptTableModel;
	private JTree tree = new JTree();
	


	public ConceptItemPanel(final Mediator mediator,
			ConceptDataModel conceptTableModel) {
		tree.getActionMap().put(
				TransferHandler.getCopyAction().getValue(Action.NAME),
				TransferHandler.getCopyAction());
		this.conceptTableModel = conceptTableModel;
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		tree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {

				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();
				if (node != null) {
					if (node.getUserObject() instanceof Location) {
						Location loc = (Location) node.getUserObject();
						mediator.fireHighlightChangeListeners(loc);
					}
				}

			}
		});
		fireSelectChange();
		this.setViewportView(tree);

	}

	@Override
	public void fireFilterChange(Filter filter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fireHighlightChange() {

	}

	@Override
	public void fireSelectChange() {
		Set<Event> events = conceptTableModel.getSelectedEvents();
		DefaultMutableTreeNode all = new DefaultMutableTreeNode(events.size()
				+ " selected events");
		if (events != null && events.size() != 0) {
			for (Event event : events) {
				DefaultMutableTreeNode top = new DefaultMutableTreeNode(
						event.getWhat()+(event.getDescription() == null ? "" : (":"+event.getDescription())));
				DefaultMutableTreeNode locations = new DefaultMutableTreeNode(
						"Locations");
				for (Entry<Tag, ? extends GeoLocation> loc : event
						.getLocations().entrySet()) {
					DefaultMutableTreeNode locationKey = new DefaultMutableTreeNode(
							loc.getKey().toString());
					locationKey.add(new DefaultMutableTreeNode(loc.getValue()
							.toString()));
					locations.add(locationKey);
				}
				if (!locations.isLeaf())
					top.add(locations);
				DefaultMutableTreeNode times = new DefaultMutableTreeNode(
						"Times");
				for (Entry<Tag, ? extends Date> when : event.getWhen()
						.entrySet()) {
					DefaultMutableTreeNode whenKey = new DefaultMutableTreeNode(
							when.getKey().toString());
					whenKey.add(new DefaultMutableTreeNode(when.getValue()
							.toString()));
					times.add(whenKey);
				}
				if (!times.isLeaf())
					top.add(times);
				DefaultMutableTreeNode identities = new DefaultMutableTreeNode(
						"Identities");
				for (Entry<Tag, ? extends Who> who : event.getWho().entrySet()) {
					DefaultMutableTreeNode whoKey = identities;
					// if the value is blank then skip this layer
					if (who.getKey() != null
							&& who.getKey().toString().length() > 0) {
						whoKey = new DefaultMutableTreeNode(who.getKey()
								.toString());
					}

					DefaultMutableTreeNode identity = new DefaultMutableTreeNode(
							who.getValue().toString());

					whoKey.add(identity);
					for (Entry<Field, String> a : who.getValue()) {
						identity.add(new DefaultMutableTreeNode(a.getValue()
								+ " (" + a.getKey() + ")"));
					}
					if (identities != whoKey)
						identities.add(whoKey);
				}
				if (!identities.isLeaf())
					top.add(identities);
				top.add(new DefaultMutableTreeNode(event.getFileLocation()));
				all.add(top);

			}
		}
		tree.setModel(new DefaultTreeModel(all));
		GuiUtils.expandJTree(tree, true);
		revalidate();
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
