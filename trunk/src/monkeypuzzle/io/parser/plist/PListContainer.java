/**
 * 
 */
package monkeypuzzle.io.parser.plist;

import java.util.Set;

import net.n3.nanoxml.IXMLElement;

public interface PListContainer
{
	public static interface LeafVisitor
	{
		public void visitLeaf(PListLocation location, PListPrimitive leaf);
	}

	public static interface PathVisitor extends LeafVisitor
	{
		/**
		 * 
		 * @param location
		 *            the location of the node
		 * @param node
		 *            the node to visit
		 * @return a set of visitors to use for nodes further down this branch.
		 *         an empty set stops traversal, return a set of one with the
		 *         current visitor continues as-is and returning a different set
		 *         can be used to simulate recursive bEhviour.
		 */
		public Set<PathVisitor> visitNodeOnWay(PListLocation location,
				PListContainer node);
	}

	/**
	 * Exposes the node through the typesafe java interface T.
	 * 
	 * @param <T>
	 *            the type of the interface
	 * @param interface1
	 *            the interface to expose it as
	 * @return a dynamic proxy implementing interface1 that exposes teh data in
	 *         this node
	 */
	public <T> T getAsInterface(Class<T> interface1);

	/**
	 * Converts this node recursively to XML
	 * 
	 * @return an XML representation of this node
	 */
	public IXMLElement toXml();

	/**
	 * Visit every node on a depth-first traversal, offering the visitor the
	 * opportunity at every node to abort the traversal of that branch.
	 * 
	 * @param parent
	 * @param visitor
	 */
	public void visitChildrenRecursively(PListLocation location,
			PathVisitor visitor);

	/**
	 * Visit every leaf
	 * 
	 * @param parent
	 * @param visitor
	 */
	public void visitLeafs(PListLocation location, LeafVisitor visitor);

	public <T> T wrap(PListWrapper<T> wrapper);
}