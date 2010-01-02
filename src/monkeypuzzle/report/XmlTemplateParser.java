package monkeypuzzle.report;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.CharacterCodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import monkeypuzzle.central.IPhone;
import monkeypuzzle.central.NavigateException;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.results.ContentType;
import monkeypuzzle.results.Location;
import monkeypuzzle.results.LocationMatcher;
import monkeypuzzle.results.MatcherException;
import monkeypuzzle.results.TextSearchAlgorithm;
import monkeypuzzle.util.Base64;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLException;
import net.n3.nanoxml.XMLParserFactory;
import net.n3.nanoxml.XMLWriter;

public class XmlTemplateParser
{
	static final int CONTEXT_CHARS_AFTER = 20;
	static final int CONTEXT_CHARS_BEFORE = 10;
	static final String QUERY_PREFIX = "cbiq";
	public static final String RESULT_PREFIX = "cbir";
	static final char TAG_PREFIX_SEPARATOR = '.';
	private static final int MAX_EXTRACT_LENGTH = 30;

	public static String filterNonAscii(final String input)
			throws CharacterCodingException
	{
		// Is there a quicker way?
		String result = input.replaceAll("[^\\p{Print}]", "");
		return result;
	}

	private IPhone backupDir;
	private IXMLParser parser;

	private IXMLElement xml;

	/**
	 * Create a new XmlTemplateParser
	 * 
	 * @param srcTemplate
	 *            - the XML template to use
	 * @throws XMLException
	 *             - if template isn't valid XML
	 */
	public XmlTemplateParser(final Reader srcTemplate) throws XMLException
	{
		IXMLReader reader = new StdXMLReader(srcTemplate);
		try
		{
			this.parser = XMLParserFactory.createDefaultXMLParser();
		} catch (Exception e)
		{
			throw new XMLException("Unable to set up XML parser", 0, e);
		}
		this.parser.setReader(reader);
	}

	/**
	 * Generate a report using this Template
	 * 
	 * @param backupDirectory
	 *            - the backup directory to report on
	 * @param output
	 *            - the writer to write the report to
	 * @throws IOException
	 *             - if there is some problem writing the report to the writer
	 * @throws XMLException
	 *             - if there is a problem with the
	 * @throws NavigateException
	 */
	public void generateReport(final IPhone backupDirectory, final Writer output)
			throws IOException, XMLException, NavigateException
	{
		// get fresh template xml
		this.xml = (IXMLElement) this.parser.parse();
		this.backupDir = backupDirectory;
		// process the xml tree in place - replacing specified queries with
		// values
		this.xml = processTree(this.xml);
		// write modified xml to output
		if (this.xml != null)
		{
			XMLWriter xWriter = new XMLWriter(output);
			xWriter.write(this.xml, true);
		}
	}

	private IXMLElement processLocate(final IXMLElement currentNode)
			throws TagException
	{
		// get content to locate within, e.g. PLIST
		String locateTypeString = currentNode.getAttribute("type",
				ContentType.PLIST.toString());
		try
		{
			ContentType locateInType = ContentType.findByName(locateTypeString);
			String fileString = currentNode.getAttribute("files", "**");
			String locateString = currentNode.getAttribute("locate", null);
			if ((locateString != null) && (locateString.length() != 0))
			{
				IXMLElement resultNode = new XMLElement(RESULT_PREFIX
						+ TAG_PREFIX_SEPARATOR + "resultset");
				resultNode.setAttribute("query", locateString);
				resultNode.setAttribute("query-type", locateInType.toString());
				resultNode.setAttribute("description",
						currentNode.getContent() == null ? locateString
								: currentNode.getContent());
				// search all files
				try
				{
					for (Map.Entry<BackupFile, Set<Location>> result : this.backupDir
							.locateGrouped(
									LocationMatcher.parse(fileString,
											locateInType, locateString))
							.entrySet())
					{
						resultNode.addChild(resultsToXml(result.getValue(),
								result.getKey()));
					}
				} catch (MatcherException e)
				{
					throw new TagException("Bad matcher in current node ("
							+ currentNode.toString() + "). Caused error:"
							+ e.getMessage());
				}
				return resultNode;
			} else
				throw new TagException(
						"no locate attribute supplied, or blank query");
		} catch (EnumConstantNotPresentException e)
		{
			// unknown search type
			StringBuilder errStr = new StringBuilder();
			errStr.append("Unknown locate type '");
			errStr.append(locateTypeString);
			errStr.append('\'');
			errStr.append(" expected one of: ");
			for (ContentType cv : ContentType.values())
			{
				errStr.append(cv.name());
				errStr.append(", ");
			}
			throw new TagException(errStr.toString());
		}
	}

	private IXMLElement processSearch(final IXMLElement currentNode)
			throws TagException, NavigateException
	{
		// get search type (using CASE_INSENSITIVE as the default)
		String searchTypeStr = currentNode.getAttribute("type",
				TextSearchAlgorithm.CASE_INSENSITIVE.name());
		try
		{
			TextSearchAlgorithm searchType = TextSearchAlgorithm
					.valueOf(searchTypeStr);
			String queryString = currentNode.getAttribute("query", null);
			if ((queryString != null) && (queryString.length() != 0))
			{
				IXMLElement resultNode = new XMLElement(RESULT_PREFIX
						+ TAG_PREFIX_SEPARATOR + "resultset");
				resultNode.setAttribute("query", queryString);
				resultNode.setAttribute("query-type", searchType.name());
				resultNode.setAttribute("description",
						currentNode.getContent() == null ? queryString
								: currentNode.getContent());
				// search all files
				for (Map.Entry<BackupFile, Set<Location>> result : this.backupDir
						.searchGrouped(searchType, queryString).entrySet())
				{
					resultNode.addChild(resultsToXml(result.getValue(), result
							.getKey()));
				}
				return resultNode;
			} else
				throw new TagException(
						"no query attribute supplied, or blank query");
		} catch (IllegalArgumentException e)
		{
			// unknown search type
			StringBuilder errStr = new StringBuilder();
			errStr.append("Unknown search type '");
			errStr.append(searchTypeStr);
			errStr.append('\'');
			errStr.append(" expected one of: ");
			for (TextSearchAlgorithm tsa : TextSearchAlgorithm.values())
			{
				errStr.append(tsa.name());
				errStr.append(", ");
			}
			throw new TagException(errStr.toString());
		}
	}

	/**
	 * Recursive method to process the xml tree
	 * 
	 * @param currentNode
	 *            - the root of the nodes to process
	 * @throws NavigateException
	 * @throws IOException
	 *             - if there is an issue writing to the writer
	 */
	private IXMLElement processTree(final IXMLElement currentNode)
			throws NavigateException
	{
		String fullTagName = currentNode.getName();
		String[] nameParts = fullTagName.split("\\.", 2);
		if (nameParts[0].equals(QUERY_PREFIX) && (nameParts.length == 2))
		{
			// It's an interesting tag
			try
			{
				TagType tagType = TagType.findByName(nameParts[1]);
				switch (tagType)
				{
				case VAR:
					return processVar(currentNode, nameParts);
				case SEARCH:
					return processSearch(currentNode);
				case LOCATE:
					return processLocate(currentNode);
				}
			} catch (TagException te)
			{
				IXMLElement errorNode = new XMLElement(RESULT_PREFIX
						+ TAG_PREFIX_SEPARATOR + "error");
				errorNode.setContent(te.getMessage());
				return errorNode;
			}
		} else
		{
			// it's not an interesting tag, process children
			List<IXMLElement> children = currentNode.getChildren();
			int childCount = children.size();
			for (int i = 0; i < childCount; i++)
			{
				IXMLElement child = children.get(i);
				IXMLElement processedChild = processTree(child);
				if (processedChild == null)
				{
					children.remove(i);
					i--;
					childCount--;
				} else
				{
					children.set(i, processedChild);
				}
			}
		}
		return currentNode;
	}

	private IXMLElement processVar(final IXMLElement currentNode,
			final String[] nameParts)
	{
		// get variable name
		String varName = currentNode.getAttribute("name", null);
		if (varName != null)
		{
			Object o = this.backupDir.getGlobalVariable(varName);
			String value = "NULL";
			if (o != null)
			{
				value = o.toString();
			}
			// replace this xml tag with result tag
			IXMLElement resultNode = new XMLElement(RESULT_PREFIX
					+ TAG_PREFIX_SEPARATOR + nameParts[1]);
			resultNode.setAttribute("name", varName);
			resultNode.setAttribute("value", value);
			return resultNode;
		}
		return currentNode;
	}

	/**
	 * Takes a results Map and produces XML for output
	 * 
	 * @param locations
	 *            the set of locations founding within bf
	 * @param bf
	 *            - the backupFile queried to get the results
	 * @return - the results in XML form
	 */
	private IXMLElement resultsToXml(final Set<Location> locations,
			final BackupFile bf)
	{
		IXMLElement fileResultNode = new XMLElement(RESULT_PREFIX
				+ TAG_PREFIX_SEPARATOR + "file-result");
		// write data which applies to all results in this Set
		fileResultNode.setAttribute("file-name", bf.getCompleteOriginalFileName());
		fileResultNode.setAttribute("hit-count", String.valueOf(locations
				.size()));
		// write each result out
		for (Location location : locations)
		{
			/*
			 * <hit file.offset='5' matched.text='sat'> the cat sat on the mat
			 * </hit>
			 */
			IXMLElement hitNode = new XMLElement(RESULT_PREFIX
					+ TAG_PREFIX_SEPARATOR + "hit");
			hitNode.setAttribute("location-description", (location
					.getLocationDescription()));
			hitNode.setAttribute("location-matcher", Base64
					.encodeObject(location.getLocationMatcher()));
			try
			{
				hitNode.setContent(filterNonAscii(location.getLocationExtract()
						.length() > MAX_EXTRACT_LENGTH ? location
						.getLocationExtract().substring(0, MAX_EXTRACT_LENGTH)
						: location.getLocationExtract()));
			} catch (Exception e)
			{
				// never happen
				e.printStackTrace();
			}
			fileResultNode.addChild(hitNode);
		}
		return fileResultNode;
	}
}
