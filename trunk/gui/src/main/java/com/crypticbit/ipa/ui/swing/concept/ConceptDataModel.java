package com.crypticbit.ipa.ui.swing.concept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import com.crypticbit.ipa.entity.concept.Event;
import com.crypticbit.ipa.entity.concept.wrapper.impl.EventList;

@SuppressWarnings("serial")
public final class ConceptDataModel extends AbstractTableModel
{

	private static final String[] COLUMN_NAMES = { "What", "When", "Where",
			"Who" };

	private final EventList events;
	private final Set<Filter> filters = new HashSet<Filter>();
	private Set<Event> selectedEvents = new HashSet<Event>();;
	private Event highlightEvent;
	private Map<Filter, Set<Event>> filteredEvents = new HashMap<Filter, Set<Event>>();
	private final EventSet changeListeners = new EventSet();

	ConceptDataModel(EventList events)
	{
		this.events = events;

	}

	public String getColumnName(int column)
	{
		return COLUMN_NAMES[column];
	}

	public void addChangeListener(ConceptPanel cp)
	{
		changeListeners.add(cp);

	}

	public void addFilter(Filter filter)
	{
		filters.add(filter);
		triggerFilterUpdate(filter);
	}

	public Event eventAt(int row)
	{
		return getFilteredEvents().get(row);
	}

	@Override
	public int getColumnCount()
	{
		return 4;
	}

	public Set<Event> getFilteredEvents(Filter exclude)
	{
		Set<Event> filtered = new HashSet<Event>(events);
		for (Entry<Filter, Set<Event>> a : filteredEvents.entrySet())
		{
			if (a.getKey() != exclude)
			{
				filtered.retainAll(a.getValue());
			}
		}
		return filtered;
	}

	private List<Event> allFilteredEvents;

	public List<Event> getFilteredEvents()
	{
		if (allFilteredEvents == null)
			allFilteredEvents = new ArrayList<Event>(getFilteredEvents(null));
		return allFilteredEvents;
	}

	@Override
	public int getRowCount()
	{
		return getFilteredEvents().size();
	}

	public EventList getUnfilteredEvents()
	{
		return events;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		final Event e = getFilteredEvents().get(rowIndex);
		switch (columnIndex)
		{
		case 0:
			return e.getWhat()
					+ (e.getDescription() == null ? "" : (":" + e
							.getDescription()));
		case 1:
			return e.getWhen();
		case 2:
			return e.getLocations();
		default:
			return e.getWho();
		}
	}

	public boolean isHighlighted(Event event)
	{
		return highlightEvent == event;
	}

	public boolean isSelected(Event event)
	{
		return selectedEvents.contains(event);
	}

	public void triggerFilterUpdate(Filter filter)
	{
		Set<Event> filtered = new HashSet<Event>();
		for (Event e : events)
		{
			if (filter.accept(e))
				filtered.add(e);
		}
		if (!filtered.equals(filteredEvents.get(filter)))
		{
			filteredEvents.put(filter, filtered);
			allFilteredEvents = null;
			changeListeners.fireFilterChange(filter);
		}
	}

	public void triggerHighlightEvent(Event highlightEvent)
	{
		this.highlightEvent = highlightEvent;
		changeListeners.fireHighlightChange();
	}

	public void triggerSelectionUpdated(int[] selected)
	{
		final Set<Event> result = new HashSet<Event>();
		for (final int loop : selected)
			result.add(getFilteredEvents().get(loop));
		triggerSelectionUpdated(result);

	}

	public void triggerSelectionUpdated(Set<Event> selectedEvents)
	{
		this.selectedEvents = selectedEvents;
		changeListeners.fireSelectChange();
	}

	private static final class EventSet extends HashSet<ConceptPanel>
	{
		public void fireFilterChange(Filter filter)
		{
			for (final ConceptPanel cp : this)
				cp.fireFilterChange(filter);
		}

		public void fireHighlightChange()
		{
			for (final ConceptPanel cp : this)
				cp.fireHighlightChange();
		}

		public void fireSelectChange()
		{
			for (final ConceptPanel cp : this)
				cp.fireSelectChange();
		}
	}

	public Event getHighlightedEvent()
	{
		return highlightEvent;
	}

	public Set<Event> getSelectedEvents()
	{
		return selectedEvents;
	}

}