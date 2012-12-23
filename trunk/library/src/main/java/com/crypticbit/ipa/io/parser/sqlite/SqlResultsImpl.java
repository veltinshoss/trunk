/**
 * 
 */
package com.crypticbit.ipa.io.parser.sqlite;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.central.NavigateException;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.io.parser.BadFileFormatException;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlDynamicProxy;
import com.crypticbit.ipa.results.Location;
import com.crypticbit.ipa.results.ParsedDataImpl;
import com.crypticbit.ipa.results.TextSearchAlgorithm;


public class SqlResultsImpl extends ParsedDataImpl implements
		com.crypticbit.ipa.results.ParsedData, SqlResults {
	private BackupFile bfd;
	private SqlDataSource sqlDataSource;

	/**
	 * @param sqlParser
	 */
	SqlResultsImpl(final BackupFile bfd, final SqlDataSource sqlDataSource) {
		this.bfd = bfd;
		this.sqlDataSource = sqlDataSource;
	}

	@Override
	public BackupFile getBackupFile() {
		return this.bfd;
	}

	@Override
	public <I> I getContentbyInterface(final Class<I> interfaceDef) throws BadFileFormatException {
		try {
			return SqlDynamicProxy.loadRootData(interfaceDef,
					this.sqlDataSource);
		} catch (Exception e) {
			throw new BadFileFormatException("Unable to load data into type \""
					+ interfaceDef.getName() + "\"", e);
		}
	}
	

	@Override
	public <T> List<T> getRecordsByInterface(final Class<T> interfaceDef)
			throws FileParseException {
		try {
			return SqlDynamicProxy.loadData(interfaceDef, this.sqlDataSource);

		} catch (Exception e) {
			throw new FileParseException("Failed to get Records for \""
					+ interfaceDef.getName() + "\"", e);
		}
	}

	public String getContents() throws FileParseException {
		StringBuffer buff = new StringBuffer();
		for (Class<?> interfaceDef : getSubInterfaces()) {
			buff.append("== ");
			buff.append(com.crypticbit.ipa.util.StringTools
					.getClassNameNoPackage(interfaceDef));
			buff.append(" Entries ==");
			buff.append(com.crypticbit.ipa.io.util.Util.SYSTEM_LINE_SEPARATOR);

			for (Object p : getRecordsByInterface(interfaceDef)) {
				buff.append("= Entry =").append(
						com.crypticbit.ipa.io.util.Util.SYSTEM_LINE_SEPARATOR);
				buff.append(p.toString());
				buff.append(com.crypticbit.ipa.io.util.Util.SYSTEM_LINE_SEPARATOR);
				buff.append(com.crypticbit.ipa.io.util.Util.SYSTEM_LINE_SEPARATOR);
			}

		}
		return buff.toString();
	}

	public SqlMetaData getMetaData() {
		return this.sqlDataSource;
	}


	public String getSummary() {
		StringBuffer result = new StringBuffer();
		for (Class<?> interfaceDef : getSubInterfaces()) {
			try {
				result.append(getRecordsByInterface(interfaceDef).size()
						+ " entries of type " + interfaceDef.getSimpleName()
						+ ". ");
			} catch (FileParseException e) {
				result.append("<error parsing data for \"" + interfaceDef
						+ "\">");
			}
		}
		return result.toString();
	}

	@Override
	public Set<Location> search(final TextSearchAlgorithm searchType,
			final String searchString) throws NavigateException {
		Set<Location> result = new HashSet<Location>();
		try {
			for (String table : this.sqlDataSource.getTables()) {
				String[][] data = this.sqlDataSource.getData(table,
						this.sqlDataSource.getColumns(table));
				for (int x = 0; x < data.length; x++) {
					for (int y = 0; y < data[x].length; y++)
						if ((data[x][y] != null)
								&& (searchType.search(searchString, data[x][y])
										.size() > 0)) {
							result.add(new SqlLocation(this.bfd, data[x][y],
									table, this.sqlDataSource.getColumns(table)
											.get(y), x));
						}
				}
			}
		} catch (SQLException e) {
			throw new NavigateException("unable to access SQL element on file "
					+ bfd.getCompleteOriginalFileName()
					+ " which is described as " + sqlDataSource.toString(), e);
		}
		return result;
	}

	@Override
	public String toString() {
		try {
			return getContents();
		} catch (FileParseException e) {
			e.printStackTrace();
			return "<error>";
			
		
		}
	}
}