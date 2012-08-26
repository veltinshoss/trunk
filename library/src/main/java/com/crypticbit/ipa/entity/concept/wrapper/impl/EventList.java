package com.crypticbit.ipa.entity.concept.wrapper.impl;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.crypticbit.ipa.entity.concept.Event;
import com.crypticbit.ipa.entity.concept.wrapper.Tag;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.TraxSource;

public class EventList extends ArrayList<Event> {

	private XStream getStream() {
		XStream xstream = new XStream(new DomDriver()); // does not require XPP3
		// library

		xstream.omitField(EventImpl.class, "fileLocation");
		xstream.omitField(EventImpl.class, "empty");
		xstream.omitField(WhereImpl.class, "twoDecimalPlacesFormat");
		

		xstream.addImplicitCollection(EventImpl.class, "when");
		xstream.addImplicitCollection(EventImpl.class, "where");
		xstream.addImplicitCollection(EventImpl.class, "who");

		xstream.alias("events", EventList.class);
		xstream.alias("when", WhenImpl.class);
		xstream.alias("where", WhereImpl.class);
		xstream.alias("who", WhoImpl.class);
		xstream.alias("event", EventImpl.class);
		xstream.alias("tag", Tag.class);

		xstream.useAttributeFor(Tag.class, "tag");
		xstream.registerConverter(new AbstractSingleValueConverter() {

			@Override
			public boolean canConvert(Class arg0) {
				return false;
			}

			@Override
			public Object fromString(String arg0) {
				return null;
			}

		});
		xstream.registerConverter(new Converter() {

			@Override
			public boolean canConvert(Class clazz) {
				return Date.class.isAssignableFrom(clazz);
			}

			@Override
			public Object unmarshal(HierarchicalStreamReader arg0,
					UnmarshallingContext arg1) {
				return null;
			}

			@Override
			public void marshal(Object value, HierarchicalStreamWriter writer,
					MarshallingContext arg2) {
				Date d = (Date) value;
				writer.setValue(value.toString());
			}
		});
		return xstream;
	}

	public String asXml() {

		return getStream().toXML(this);

	}

	public String asHtml() throws TransformerFactoryConfigurationError, TransformerException {
		TraxSource traxSource = new TraxSource(this, getStream());
		Writer buffer = new StringWriter();
		StreamResult streamResult = new StreamResult(buffer);
		
		StreamSource streamSource = new StreamSource(this.getClass().getClassLoader().getResourceAsStream("report.xsl"));
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer(streamSource);
		transformer.transform(traxSource, streamResult);
		return buffer.toString();
	}

}
