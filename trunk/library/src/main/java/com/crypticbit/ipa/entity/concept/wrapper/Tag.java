package com.crypticbit.ipa.entity.concept.wrapper;

public class Tag {

	private String tag;

	public Tag(String tag) {
		this.tag = tag;
		if (this.tag == null)
			this.tag = "";
	}
	

	public Tag add(String tag) {
		if (tag == null || tag.length() == 0)
			return this;
		if (this.tag.length() == 0) {
			return new Tag(tag);
		} else
			return new Tag(this.tag + "." + tag);
	}

	public String toString() {
		return tag;
	}

	@Override
	public int hashCode() {
		return tag.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof Tag))
			return false;
		else
		return tag.equals(((Tag)obj).tag);
		
	}

}
