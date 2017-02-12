/*
    Copyright 2016 Wira Mulia

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package org.osumercury.badgemaker.gui;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import javax.swing.*;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.event.ListSelectionEvent;

/**
 *
 * @author wira
 */
public class FontSelectDialog extends JDialog {
    private JList<String> listFont;
    private JTextField txtPreview;
    private JButton btnOK;
    private JButton btnCancel;
    private boolean OK;
    private String fontName;
    
    private static int lastSelection = -1;
    
    public FontSelectDialog(String title) {
        setTitle(title);
        
        btnOK = new JButton("OK");
        btnCancel = new JButton("Cancel");
        txtPreview = new JTextField("No Preview");
        txtPreview.setFont(new Font("Monospaced", Font.PLAIN, 20));
        txtPreview.setMinimumSize(new Dimension(500, 100));
        txtPreview.setBackground(Color.BLACK);
        txtPreview.setForeground(new Color(32, 187, 255));
        txtPreview.setMargin(new Insets(5, 5, 5, 5));
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        listFont = new JList();
        JScrollPane listScroller = new JScrollPane(listFont);
        listScroller.setPreferredSize(new Dimension(500,250));
        DefaultListModel<String> listModel = new DefaultListModel();
        for(Font f : ge.getAllFonts()) {
            listModel.addElement(f.getFontName());
        }
        listFont.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listFont.setModel(listModel);
        setSize(500,500);
        Container pane = this.getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
        JPanel paneButtons = new JPanel();
        paneButtons.add(btnOK);
        paneButtons.add(btnCancel);
        OK = false;
        
        btnOK.addActionListener((ActionEvent e) -> {
            fontName = listFont.getSelectedValue();
            OK = true;
            lastSelection = listFont.getSelectedIndex();
            dispose();
        });
               
        btnCancel.addActionListener((ActionEvent e) -> {
            dispose();
        });
        
        listFont.addListSelectionListener((ListSelectionEvent e) -> {
            String name = listFont.getSelectedValue();
            txtPreview.setText(name);
            txtPreview.setFont(new Font(name, Font.PLAIN, 20));
        });
        
        listFont.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if(evt.getClickCount() == 2) {
                    fontName = listFont.getSelectedValue();
                    OK = true;
                    lastSelection = listFont.getSelectedIndex();
                    dispose();
                }
            }
        });
        if(lastSelection != -1 && lastSelection < listModel.size()) {
            listFont.setSelectedIndex(lastSelection);
            listFont.ensureIndexIsVisible(lastSelection);
        } else {
            listFont.setSelectedIndex(0);
        }
        
        pane.add(txtPreview);        
        pane.add(listScroller);
        pane.add(paneButtons);
        
        GUI.attachKeyShortcut(this, KeyEvent.VK_ESCAPE, 0,
                new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        pack();
    }
    
    public String getFontName() {
        return fontName;
    }

    public boolean isApproved() {
        return OK;
    }
    
    public void showDialog() {
        this.setVisible(true);
    }
}
