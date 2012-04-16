package com.crypticbit.ipa.io.parser.plist;

import java.util.AbstractMap;
import java.util.Map;

import com.crypticbit.ipa.io.parser.plist.PListContainer.LeafVisitor;
import com.crypticbit.ipa.io.parser.plist.dynamicproxy.PListDynamicProxy;
import com.crypticbit.ipa.util.RegEx;


public abstract class PListDictImpl extends AbstractMap<String, PListContainer>
		implements PListDict
{

	private PListLocation location;
	protected PListDictImpl(PListLocation location) {
		this.location = location;
		location.setContainer(this);
	}
	public PListLocation getLocation() {return location;}
	
	public <T> T getAsInterface(final Class<T> interface1)
	{
		return PListDynamicProxy.newInstance(interface1, this);
	}

	public void visitLeafs(LeafVisitor leafVisitor) {
		for(PListContainer c : this.values())
			c.visitLeafs(leafVisitor);
	}
	
	
	public <T> T wrap(final PListWrapper<T> wrapper)
	{
		return wrapper.wrap(this);
	}

}
