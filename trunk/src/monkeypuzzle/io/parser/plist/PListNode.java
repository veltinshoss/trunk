package monkeypuzzle.io.parser.plist;

public interface PListNode extends Comparable<PListNode>
{

	enum MatchType
	{
		GREEDY, IGNORE, NO, NORMAL
	}

	public String getMatcherForm();

	public PListContainer getNode();

	public MatchType match(String argument);
}
