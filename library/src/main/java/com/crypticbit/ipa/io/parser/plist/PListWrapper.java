package com.crypticbit.ipa.io.parser.plist;

public interface PListWrapper<T>
{
	public T wrap(PListArray array);

	public T wrap(PListDict dict);

	public T wrap(PListPrimitive primitive);

}
