package com.crypticbit.ipa.ui.swing;

import java.util.Map;
import java.util.Set;

import com.crypticbit.ipa.central.IPhone;
import com.crypticbit.ipa.central.NavigateException;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.results.Location;


public interface SearchPane
{

	Map<BackupFile, Set<Location>> search(IPhone backupDir)
			throws NavigateException;

}
