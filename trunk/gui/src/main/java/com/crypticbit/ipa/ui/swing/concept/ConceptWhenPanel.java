package com.crypticbit.ipa.ui.swing.concept;

import java.awt.Dimension;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdesktop.swingx.JXTitledPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.crypticbit.ipa.entity.concept.Event;
import com.crypticbit.ipa.ui.swing.Mediator;

public class ConceptWhenPanel extends JXTitledPanel implements ConceptPanel {

	private DateAxis domainAxis;
	private final ConceptDataModel conceptTableModel;
	private Date earliestDate;
	private Date latestDate;
	private Date earliestFilterDate;
	private Date latestFilterDate;
	private final Filter filter = new Filter() {

		@Override
		public boolean accept(Event e) {
			if (domainAxis == null)
				return true;
			if (e.getWhen() == null || e.getWhen().size() == 0)
				return true;
			for (final Date d : e.getWhen().values())
				if (d.after(earliestFilterDate) && d.before(latestFilterDate))
					return true;
			return false;

		}
	};

	public ConceptWhenPanel(Mediator mediator,
			ConceptDataModel conceptTableModel) {
		super("When");
		this.setPreferredSize(new Dimension(140, 100));
		this.conceptTableModel = conceptTableModel;
		resetDates(conceptTableModel);
		this.setContentContainer(new DateChart());
		conceptTableModel.addFilter(filter);
	}

	private JFreeChart createChart(Map<Day, Integer> dates) {

		final TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		final TimeSeries series = createSerie(dates);
		timeSeriesCollection.addSeries(series);

		domainAxis = new DateAxis("Time") {
			// workaround to stop mouse zoom changing range axis beyond our
			// preferred bounds
			@Override
			public void resizeRange2(double factor, double anchorX) {
				super.resizeRange2(factor, anchorX);
				// if (earliestDate != null && latestDate != null) {
				if (domainAxis.getMinimumDate().before(earliestDate)
						|| domainAxis.getMinimumDate().after(latestDate))
					domainAxis.setMinimumDate(earliestDate);
				if (domainAxis.getMaximumDate().after(latestDate)
						|| domainAxis.getMaximumDate().before(earliestDate))
					domainAxis.setMaximumDate(latestDate);
				// }
			}
		};
		domainAxis.addChangeListener(new AxisChangeListener() {
			@Override
			public void axisChanged(AxisChangeEvent arg0) {
				conceptTableModel.triggerFilterUpdate(filter);
			}
		});
		final NumberAxis rangeAxis = new NumberAxis("Events") {
			// workaround to stop mouse zooming changing range axis
			@Override
			public void resizeRange2(double factor, double anchorX) {
				// do nothing - effectively reject the change
			}
		};
		// rangeAxis.set
		rangeAxis.setAutoRangeIncludesZero(true);
		final XYBarRenderer renderer = new XYBarRenderer();
		renderer.setShadowVisible(false);
		final XYPlot plot = new XYPlot(timeSeriesCollection, domainAxis,
				rangeAxis, renderer);
		final JFreeChart chart = new JFreeChart(plot);
		chart.setAntiAlias(false);
		chart.removeLegend();
		chart.addChangeListener(new ChartChangeListener() {

			@Override
			public void chartChanged(ChartChangeEvent event) {
				if (event.getType() == ChartChangeEventType.GENERAL) {
					earliestFilterDate = domainAxis.getMinimumDate();
					latestFilterDate = domainAxis.getMaximumDate();
				}
			}
		});
		return chart;
	}

	private TimeSeries createSerie(Map<Day, Integer> dates) {
		final TimeSeries timeSeries = new TimeSeries("");
		for (final Map.Entry<Day, Integer> entry : dates.entrySet())
			timeSeries.add(entry.getKey(), entry.getValue());

		return timeSeries;
	}

	@Override
	public void fireFilterChange(Filter filter) {
		if (filter != this.filter) {
			this.setContentContainer(new DateChart());
			revalidate();
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

	private Map<Day, Integer> getDates(ConceptDataModel conceptTableModel) {
		final Map<Day, Integer> dates = new HashMap<Day, Integer>();
		final List<Event> events = conceptTableModel.getFilteredEvents();
		for (final Event e : events) {
			if (e.getWhen() != null)
				for (final Date dd : e.getWhen().values()) {
					final Day d = new Day(dd);
					if (dates.containsKey(d))
						dates.put(d, dates.get(d) + 1);
					else
						dates.put(d, 1);
				}

		}
		return dates;
	}

	@Override
	public void registerToUpdateOnSelectionChange() {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerToUpddateOnMouseOverChange() {
		// TODO Auto-generated method stub

	}

	private void resetDates(ConceptDataModel conceptTableModel) {
		final List<Event> events = conceptTableModel.getUnfilteredEvents();
		for (final Event e : events) {
			if (e.getWhen() != null)
				for (final Date dd : e.getWhen().values()) {
					if (earliestDate == null || dd.before(earliestDate))
						earliestDate = dd;
					if (latestDate == null || dd.after(latestDate))
						latestDate = dd;
				}

		}
		if (earliestFilterDate == null)
			earliestFilterDate = earliestDate;
		if (latestFilterDate == null)
			latestFilterDate = latestDate;
	}

	public class DateChart extends ChartPanel {

		public DateChart() {
			super(null);
			if (conceptTableModel.getFilteredEvents().size() == 0)
				setChart(null);
			else {
				setChart(createChart(getDates(conceptTableModel)));
				setDomainZoomable(true);
				setMouseWheelEnabled(true);
				setRangeZoomable(false);
				setPopupMenu(null);
			}
		}
	}

}
