package com.crypticbit.ipa.ui.swing;

import javax.swing.JComponent;
import javax.swing.JTable;

public class CSVTransferHandler extends StringTransferHandler {
    private int[] rows = null;
    
	@Override
	protected String exportString(JComponent c) {
        JTable table = (JTable)c;
        rows = table.getSelectedRows();
        int colCount = table.getColumnCount();
        
        StringBuffer buff = new StringBuffer();
        
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < colCount; j++) {
                Object val = table.getValueAt(rows[i], j);
                buff.append('"');
                if(val != null)
                {
                	String cleanStr = val.toString().replace('"', '”'); //replace double quote delimeter with other double quotes
                	buff.append(cleanStr); 
                }
                buff.append('"');
                if (j != colCount - 1) {
                    buff.append(",");
                }
            }
            if (i != rows.length - 1) {
                buff.append("\n");
            }
        }
        
        return buff.toString();
	}

	@Override
	protected void importString(JComponent c, String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cleanup(JComponent c, boolean remove) {
		// TODO Auto-generated method stub
		
	}

}
