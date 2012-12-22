package com.crypticbit.ipa.ui.swing;

// code based on that from http://www.java2s.com/Code/Java/Database-SQL-JDBC/DatabaseBrowser.htm

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.io.parser.sqlite.SqlDataSource;

@SuppressWarnings("serial")
public class DatabaseBrowser extends JPanel {

    private JTable table = new JTable();
    private SqlDataSource sqlDataSource;
    private JComboBox tableBox;

    public DatabaseBrowser() throws Exception {
	this.setLayout(new BorderLayout());
	tableBox = new JComboBox();

	tableBox.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent event) {
		refreshTable();
	    }
	});
    }

    protected void populateTableBox() {
	try {
	    Set<String> tables = sqlDataSource.getTables();
	    tableBox.setModel(new DefaultComboBoxModel(tables.toArray()));
	    tableBox.setEnabled(tables.size() > 0);
	} catch (Exception e) {
	    tableBox.setEnabled(false);
	}
    }

    protected void refreshTable() {
	String tableName = (String) tableBox.getSelectedItem();
	if (tableName == null) {
	    table.setModel(new DefaultTableModel());
	    return;
	}
	table.setModel(new ResultSetTableModel(sqlDataSource, tableName));
    }

    public void open(BackupFile bfd) throws SQLException, IOException {

	this.sqlDataSource = new SqlDataSource(bfd, bfd.getContentsFile());
	populateTableBox();
	add(tableBox, BorderLayout.NORTH);
	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	refreshTable();
	add(new JScrollPane(table), BorderLayout.CENTER);
    }

    @SuppressWarnings("serial")
    static class ResultSetTableModel extends AbstractTableModel {

	private List<String> columns;
	private Object[][] data;

	ResultSetTableModel(SqlDataSource sqlDataSource, String tableName) {
	    try {
		columns = sqlDataSource.getColumns(tableName);
		data = sqlDataSource.getData(tableName, columns);
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}

	public int getColumnCount() {
	    return columns.size();
	}

	public int getRowCount() {
	    return data.length;
	}

	public Object getValueAt(int row, int column) {
	    return data[row][column];
	}

	public boolean isCellEditable(int row, int column) {
	    return false;
	}

	public String getColumnName(int column) {
	    return (String) (columns.get(column));
	}

    }
}
