package monkeypuzzle.io.parser.plist;

import monkeypuzzle.results.ParsedData;

public interface PListResults<T> extends ParsedData, PListHeader
{

	public T getEntry();

}