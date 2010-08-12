package monkeypuzzle.ui.swing;

import java.awt.GridLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import monkeypuzzle.central.NavigateException;
import monkeypuzzle.report.XmlTemplateParser;
import monkeypuzzle.results.Location;
import monkeypuzzle.results.LocationMatcher;
import monkeypuzzle.util.Base64;
import net.n3.nanoxml.XMLException;
import net.sf.saxon.FeatureKeys;

@SuppressWarnings("serial")
public class ReportView extends JPanel
{
	private static final String DEFAULT_REPORT_TEMPLATE_RESOURCE = "/defaultReportTemplate.xml";
	private static final String DEFAULT_XSLT_RESOURCE = "/defaultReportStyleSheet.xsl";
	private JEditorPane htmlPane;
	private JScrollPane scrollPane;

	ReportView(final Mediator mediator)
	{
		try
		{
			System.out.println("Starting reportview");
			System.setProperty("javax.xml.transform.TransformerFactory",
					"net.sf.saxon.TransformerFactoryImpl");
			setLayout(new GridLayout(1, 1));
			String htmlString = "<html><head></head><body>Default HTML</body></html>";
			// 1. get report xml
			// read in report template
			Reader reportTemplateReader;
			InputStream is = XmlTemplateParser.class
					.getResourceAsStream(DEFAULT_REPORT_TEMPLATE_RESOURCE);
			if (is == null)
			{
				reportTemplateReader = new StringReader(
						"<error>Report template "
								+ DEFAULT_REPORT_TEMPLATE_RESOURCE
								+ " could not be found.</error>");
			} else
			{
				reportTemplateReader = new InputStreamReader(is);
			}
			XmlTemplateParser templateParser = null;
			try
			{
				templateParser = new XmlTemplateParser(reportTemplateReader);
			} catch (XMLException e)
			{
				htmlString = exceptionToHtml("Unable to Parse Report Template",
						e);
			}
			// generate report xml using prepared report template
			if (templateParser != null)
			{
				Writer reportXmlWriter = new StringWriter();
				try
				{
					templateParser.generateReport(
							mediator.getBackupDirectory(), reportXmlWriter);
					String reportXmlString = reportXmlWriter.toString();
					// 2. transform report xml to html
					// read in xslt resource
					Reader xsltReader;
					is = XmlTemplateParser.class
							.getResourceAsStream(DEFAULT_XSLT_RESOURCE);
					if (is == null)
					{
						xsltReader = new StringReader("<error>xslt "
								+ DEFAULT_XSLT_RESOURCE
								+ " could not be found.</error>");
					} else
					{
						xsltReader = new InputStreamReader(is);
					}
					// Get transformer template
					TransformerFactory transformerFactory = TransformerFactory
							.newInstance();
					transformerFactory.setAttribute(
							FeatureKeys.SCHEMA_VALIDATION, new Integer(
									net.sf.saxon.om.Validation.SKIP));
					Templates x = transformerFactory
							.newTemplates(new StreamSource(xsltReader));
					// get transformer
					Transformer transformer = x.newTransformer();
					StringReader reportXmlReader = new StringReader(
							reportXmlString);
					StringWriter outputHtmlWriter = new StringWriter();
					transformer.transform(new StreamSource(reportXmlReader),
							new StreamResult(outputHtmlWriter));
					htmlString = outputHtmlWriter.toString();
					// String htmlString =
					// "<html><head><title>title</title></head><body><h1>Heading</h1><p>hello
					// world</p></body></html>";
				} catch (IOException e)
				{
					htmlString = exceptionToHtml("Unable to generate report", e);
				} catch (XMLException e)
				{
					htmlString = exceptionToHtml("Unable to generate report", e);
				} catch (TransformerConfigurationException e)
				{
					htmlString = exceptionToHtml(
							"Unable to generate report - problem setting up XML transform",
							e);
				} catch (TransformerException e)
				{
					htmlString = exceptionToHtml(
							"Unable to generate report - problem performing XML transform",
							e);
				} catch (NavigateException e)
				{
					htmlString = exceptionToHtml(
							"Unable to generate report - problem navigating data",
							e);

				}
			}
			this.htmlPane = new JEditorPane("text/html", htmlString);
			this.htmlPane.setEditable(false);
			this.scrollPane = new JScrollPane(this.htmlPane);
			this.htmlPane.addHyperlinkListener(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(final HyperlinkEvent e)
				{
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
					{
						mediator.fireRemoveHighlightChangeListeners();
						Location location = mediator.getBackupDirectory()
								.locate(
										(LocationMatcher) Base64
												.decodeToObject(e
														.getDescription()))
								.iterator().next();
						mediator.fireHighlightChangeListeners(location
								.getBackupFile(), Arrays
								.asList(new Location[] { location }));
						mediator.fireMoveToChangeListeners(location);
					}
				}
			});
			this.add(this.scrollPane);
		} catch (Error e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Ending reportview");
	}

	private String exceptionToHtml(final String message, final Exception e)
	{
		e.printStackTrace();
		String html = "<html><head><title>Error</title></head><body><h1>Error</h1><h3>"
				+ message
				+ "</h3><code>"
				+ e.getMessage()
				+ "</code></body></html>";
		return html;
	}
}
