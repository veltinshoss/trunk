/**
 * 
 */
package com.crypticbit.ipa.io.parser.plist;

import net.n3.nanoxml.IXMLElement;

public interface PListContainer
{

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


	public <T> T wrap(PListWrapper<T> wrapper);

	public void visitLeafs(LeafVisitor leafVisitor);
	
	public PListLocation getLocation();	
	
	public interface LeafVisitor
	{
		void visitLeaf(PListPrimitive leaf);
	}
}