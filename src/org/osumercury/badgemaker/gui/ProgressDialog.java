/*
    Copyright 2017 Wira Mulia

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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import org.osumercury.badgemaker.Progress;

/**
 *
 * @author wira
 */
public class ProgressDialog extends JDialog {
    private Progress p;
    private JLabel text;
    private JProgressBar progressBar;
    private JButton btnCancel;
    private boolean initialized = false;

    public void init(Progress p, String title) {
        this.p = p;
        progressBar = new JProgressBar();
        btnCancel = new JButton("Cancel");
        text = new JLabel(" ");
        setModal(true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        progressBar.setMinimum(0);
        progressBar.setMaximum(1000);
        Container pane = this.getContentPane();
        pane.add(text, BorderLayout.PAGE_START);
        pane.add(progressBar, BorderLayout.CENTER);
        pane.add(btnCancel, BorderLayout.PAGE_END);        

        btnCancel.addActionListener((ActionEvent ev) -> {
            p.cancel = true;
            this.dispose();
        });

        pack();
        setSize(300, 100);
        setLocationRelativeTo(null);
        initialized = true;
    }
    
    public Progress getProgress() {
        return p;
    }
    
    public void display() {
        SwingUtilities.invokeLater(() -> {
           setVisible(true); 
        });
    }
    
    public void update() {
        if(!initialized) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            text.setText(p.text);
            progressBar.setValue((int)(p.percent * 1000));
            if(p.done || p.cancel) {
                dispose();
            }
        });
    }
}
