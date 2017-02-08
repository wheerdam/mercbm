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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
    private JPanel paneControls;
    private boolean initialized = false;
    
    public static Progress create(String title) {
        ProgressDialog pD = new ProgressDialog();
        Progress p = new Progress((callback) -> {
            pD.update();
        });
        p.setCompletedCallback((callback) -> {
            if(pD.isVisible()) {
                SwingUtilities.invokeLater(() -> {
                    pD.dispose();
                });
            }
        });
        pD.init(p, title);
        pD.display();
        return p;
    }

    public void init(Progress p, String title) {
        this.p = p;
        progressBar = new JProgressBar();
        btnCancel = new JButton("Cancel");
        paneControls = new JPanel();
        text = new JLabel(" ");
        text.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        text.setMinimumSize(new Dimension(300, 30));
        text.setPreferredSize(new Dimension(300, 30));
        setModal(true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        progressBar.setMinimum(0);
        progressBar.setMaximum(1000);
        progressBar.setMinimumSize(new Dimension(300, 50));
        progressBar.setPreferredSize(new Dimension(300, 50));
        Container pane = this.getContentPane();
        pane.add(text, BorderLayout.PAGE_START);
        pane.add(Box.createRigidArea(new Dimension(5, 5)), 
                 BorderLayout.LINE_START);
        pane.add(Box.createRigidArea(new Dimension(5, 5)), 
                 BorderLayout.LINE_END);
        pane.add(progressBar, BorderLayout.CENTER);
        paneControls.add(btnCancel);
        pane.add(paneControls, BorderLayout.PAGE_END);        

        btnCancel.addActionListener((ActionEvent ev) -> {
            text.setText("Cancelling...");
            p.cancel = true;
        });

        pack();
        setTitle(title);
        // setSize(300, 200);
        setLocationRelativeTo(null);
        initialized = true;
    }
    
    public Progress getProgress() {
        return p;
    }
    
    public void display() {
        SwingUtilities.invokeLater(() -> {
            if(!p.done && !p.cancel) {
                setVisible(true); 
            }
        });
    }
    
    public void update() {
        if(!initialized) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            if(!p.cancel) {
                text.setText(p.text);
                progressBar.setValue((int)(p.percent * 1000));
            }
            if(p.done) {
                dispose();
            }
        });
    }
}
