package monkeypuzzle.io.parser.sqlite;

import java.util.List;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.results.ParsedData;

public interface SqlResults extends ParsedData
{
	public SqlMetaData getMetaData();

	public <T> List<T> getRecords(Class<T> interfaceDef)
			throws FileParseException;
}
