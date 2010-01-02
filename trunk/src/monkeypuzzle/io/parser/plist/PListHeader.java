package monkeypuzzle.io.parser.plist;

import monkeypuzzle.central.FileParseException;

public interface PListHeader
{
	public PListContainer getRootContainer() throws FileParseException;
}
