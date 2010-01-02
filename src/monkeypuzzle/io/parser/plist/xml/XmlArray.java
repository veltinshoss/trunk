/**
 * 
 */
package monkeypuzzle.io.parser.plist.xml;

import java.util.ArrayList;
import java.util.List;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.io.parser.plist.PListArrayImpl;
import monkeypuzzle.io.parser.plist.PListContainer;
import net.n3.nanoxml.IXMLElement;

class XmlArray extends PListArrayImpl implements PListContainer
{
	private IXMLElement elem;
	private List<PListContainer> list;

	XmlArray(final IXMLElement elem) throws ArrayIndexOutOfBoundsException,
			FileParseException
	{
		this.elem = elem;
		this.list = getAsList();
	}

	@Override
	public PListContainer get(final int index)
	{
		return this.list.get(index);
	}

	@Override
	public int size()
	{
		return this.list.size();
	}

	@Override
	public String toString()
	{
		return "Array: " + this.list;
	}

	@Override
	public IXMLElement toXml()
	{
		return this.elem;
	}

	/**
	 * Worker function - never call other than from constructor
	 * 
	 * @return the current XML element converted into an array
	 * @throws FileParseException
	 * @throws ArrayIndexOutOfBoundsException
	 */
	private List<PListContainer> getAsList()
			throws ArrayIndexOutOfBoundsException, FileParseException
	{
		List<PListContainer> list = new ArrayList<PListContainer>();
		for (int index = 0; index < this.elem.getChildrenCount();)
		{
			list
					.add(XmlPListParser.getItem(this.elem
							.getChildAtIndex(index++)));
		}

		return list;
	}

}