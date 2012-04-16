package com.crypticbit.ipa.entity.concept.wrapper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.entity.concept.ConceptException;
import com.crypticbit.ipa.entity.concept.Conceptable;
import com.crypticbit.ipa.entity.concept.Event;
import com.crypticbit.ipa.entity.concept.wrapper.WhoSet.WhoEntry;
import com.crypticbit.ipa.entity.concept.wrapper.impl.EventImpl;
import com.crypticbit.ipa.entity.concept.wrapper.impl.EventImpl.Concept;
import com.crypticbit.ipa.entity.concept.wrapper.impl.WhoImpl;

/**
 * This class processes a Conceptable class so that we can extract Events from
 * it
 */
public class ConceptFactory
{

	private String defaultLocale;
	public ConceptFactory(String defaultLocale)
	{
		this.defaultLocale = defaultLocale;
	}

	/**
	 * This method takes a Conceptable object (the interface is just a marker -
	 * no methods) and processes it's annotation to extract events. There are
	 * two types of interface; collection and member. Collection (currently only
	 * ConceptIterator) simply navigates to another collection of classes which
	 * are recursively processed whilst member (WhereTag, WhenTag, etc.) will
	 * extract data from that member method and add it to the current event.
	 * <p>
	 * The pattern for each member annotation is common. They take two
	 * parameters; tag and field. Tag is a human readable tag to define the
	 * activity with (e.g. "start call") and field sets the type of date
	 * expected, e.g. latitude or longitude. having a repeated tag is fine if
	 * the fields are different (e.g. tag is home, and fields are latitude and
	 * longitude.)
	 * <p>
	 * This class uses reflection - so be warned.
	 * 
	 * @param object
	 *            a Conceptable object to process
	 * @return a collection of events
	 */
	@SuppressWarnings("unchecked")
	public Collection<Event> createWrapper(Conceptable object)
			throws ConceptException
	{

		Class iface = findConceptableInterface(object.getClass());
		if (iface == null)
			// FIXME proper error handling
			throw new ConceptException(
					"You tried to wrap a non-concpetable class: "
							+ object.getClass());

		return createWrapper(iface, object);
	}

	private Collection<Event> createWrapper(Class iface, Conceptable object)
			throws ConceptException
	{
		return createWrapper(iface, object,
				new EventImpl(object.getFileLocation(), this),
				new LinkedList<Event>(), new Tag(null));
	}

	private Collection<Event> createWrapper(Class iface, Conceptable object,
			final EventImpl event, final Collection<Event> events, Tag tagPrefix)
			throws ConceptException
	{
		WhatTag annot = (WhatTag) iface.getAnnotation(WhatTag.class);
		if (annot != null)
			event.setWhat(annot.name());

		try
		{
			for (Method method : iface.getMethods())
			{

				for (EventImpl.Concept concept : Concept.values())
				{
					Object annotation = method
							.getAnnotation(concept.annotationClass);
					if (annotation != null)
					{
						event.add(concept, annotation, method.invoke(object),
								tagPrefix, object);
					}
				}

				ConceptIterator ci = method
						.getAnnotation(ConceptIterator.class);
				if (ci != null)
				{
					Object invokeResult = method.invoke(object);
					if (invokeResult instanceof Iterable
							|| invokeResult instanceof Object[])
					{
						Iterator i = coerceToIterator(invokeResult);
						while (i.hasNext())
						{
							Conceptable c = (Conceptable) i.next();
							diveIntoItem(event, events, tagPrefix, ci, c);

						}
					} else if (invokeResult instanceof Conceptable)
						diveIntoItem(event, events, tagPrefix, ci,
								(Conceptable) invokeResult);
					else
						LogFactory.getLogger().log(
								Level.INFO,
								"Ignored " + method + " on " + iface
										+ " because " + invokeResult.getClass()
										+ " not Conceptable or Iteratable");
				}
			}

			if (!event.isEmpty())
				events.add(event);

			return events;
		} catch (Exception e)
		{
			throw new ConceptException("Reflection problem whilst parsing: "
					+ iface.getName(), e);
		}
	}

	private void diveIntoItem(final EventImpl event,
			final Collection<Event> events, Tag tagPrefix, ConceptIterator ci,
			Conceptable c) throws ConceptException
	{
		Class returnType = findConceptableInterface(c.getClass());
		if (ci.type() == ConceptIterator.Type.RECURSE)
		{
			createWrapper(returnType, c, event, events,
					tagPrefix.add(ci.tagPrefix()));
		} else
			events.addAll(createWrapper(returnType, c));
	}

	private static Iterator coerceToIterator(Object invokeResult)
	{
		if (invokeResult instanceof Iterable)
			return ((Iterable) invokeResult).iterator();
		if (invokeResult instanceof Object[])
			return Arrays.asList((Object[]) invokeResult).iterator();
		throw new Error("Badly typed param: can't cast to Iterator");
	}

	private static Class findConceptableInterface(Class c)
	{
		for (Class t : c.getInterfaces())
		{
			if (t == Conceptable.class)
				return c;
			else
			{
				Class tt = findConceptableInterface(t);
				if (tt != null)
					return tt;
			}
		}
		return null;
	}

	public Collection<? extends Event> createWrapper(
			Collection<Conceptable> conceptables) throws ConceptException
	{
		List<Event> result = new LinkedList<Event>();
		for (Conceptable i : conceptables)
		{
			try
			{
				result.addAll(createWrapper(i));
			} catch (ConceptException ce)
			{
				LogFactory
						.getLogger()
						.log(Level.WARNING,
								"Skipping scanning of "
										+ i.getClass()
										+ " because of a problem (probably mismatched version)");
			}
		}
		return result;
	}

	private Map<WhoImpl, WhoSet> whoEntries = new IdentityHashMap<WhoImpl, WhoSet>();

	public void add(WhoImpl who, WhoEntry entry)
	{
		WhoSet foundFromWho = whoEntries.get(who);
		if (foundFromWho == null)
		{
			WhoSet foundFromEntry = find(entry);
			if (foundFromEntry == null)
			{
				WhoSet set = new WhoSet();
				whoEntries.put(who, set);
				set.add(entry);
			} else
			{
				whoEntries.put(who, foundFromEntry);
			}
		} else
		{
			WhoSet foundFromEntry = find(entry);
			if (foundFromEntry != null && foundFromEntry != foundFromWho)
			{
				foundFromWho.addAll(foundFromEntry);
				for (Entry<WhoImpl, WhoSet> e : whoEntries.entrySet())
				{
					if (e.getValue() == foundFromEntry)
					{
						e.setValue(foundFromWho);
					}
				}
			}
			foundFromWho.add(entry);
			whoEntries.put(who, foundFromWho);
		}
	}

	private WhoSet find(WhoEntry entry)
	{
		for (WhoSet ws : new HashSet<WhoSet>(whoEntries.values()))
		{
			if (ws.contains(entry))
				return ws;
		}
		return null;
	}

	public WhoSet get(WhoImpl who)
	{
		return whoEntries.get(who);
	}

	public String getDefaultTelephoneLocale()
	{
		return defaultLocale;
	}

}
