package com.crypticbit.ipa.results;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crypticbit.ipa.central.BackupFileView;
import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.NavigateException;


public abstract class ParsedDataImpl implements ParsedData {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.crypticbit.ipa.results.ParsedData#getAvailableInterfaces()
	 */
	@Override
	public Collection<Class<?>> getSubInterfaces() {
		if ((getViews() == null)
				|| (getViews().getMainInterface() == null))
			return Collections.emptySet();
		else
			return Arrays.asList((Class<?>[])getViews()
					.getSubInterfaces());
	}

	public BackupFileView getViews() {
		return BackupFileView.find(getBackupFile());
	}

	@Override
	public Set<Location> search(final TextSearchAlgorithm searchType,
			final String searchString) throws NavigateException,
			FileParseException {
		Set<Location> result = new HashSet<Location>();

		for (Map.Entry<Integer, String> entry : searchType.search(searchString,
				getContents()).entrySet()) {
			result.add(new TextLocation(this, getBackupFile(),
					entry.getValue(), entry.getKey()));
		}
		return result;
	}

	@Override
	public <T> List<T> getRecordsByInterface(final Class<T> interfaceDef)
			throws FileParseException {
		List<T> list = new ArrayList<T>();
		list.add(getContentbyInterface(interfaceDef));
		return list;
	}

}