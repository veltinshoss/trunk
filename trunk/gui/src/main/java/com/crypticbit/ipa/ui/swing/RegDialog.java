package com.crypticbit.ipa.ui.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class RegDialog extends JDialog {

    private JTextField tfUsername;
    private JTextField tfKey;
    private JLabel lbUsername;
    private JLabel lbPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private boolean okayed = false;

    public RegDialog(Frame parent) {
        super(parent, "Registration details", true);
        //
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;

        lbUsername = new JLabel("Email: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(lbUsername, cs);

        tfUsername = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);

        lbPassword = new JLabel("Key: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        tfKey = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(tfKey, cs);
        panel.setBorder(new LineBorder(Color.GRAY));


        btnLogin = new JButton("Register");

        btnLogin.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            		okayed = true;
                    dispose();
            }
        });
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JPanel bp = new JPanel();
        bp.add(btnLogin);
        bp.add(btnCancel);



        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
        
//        tfUsername.setText("mat@hatless.co.uk");
//        tfKey.setText("a326ac9e7c5a8b94dc36446f625188b050c8236f");
    }

    public String getUsername() {
        return tfUsername.getText().trim();
    }

    public String getKey() {
        return new String(tfKey.getText().trim());
    }

    public boolean isOkayed() {
        return okayed;
    }
}