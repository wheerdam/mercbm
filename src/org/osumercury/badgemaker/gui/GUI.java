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
import java.io.File;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import org.osumercury.badgemaker.Progress;

/**
 *
 * @author wira
 */
public class GUI {
    public static Progress createProgressDialog(boolean async) {
        JDialog progressFrame = new JDialog();
        JProgressBar progressBar = new JProgressBar();
        JButton cancelButton = new JButton("Cancel");
        
        progressFrame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        progressBar.setMinimum(0);
        progressBar.setMaximum(1000);
        Container pane = progressFrame.getContentPane();
        pane.add(progressBar, BorderLayout.CENTER);
        pane.add(cancelButton, BorderLayout.PAGE_END);
        
        Progress p = new Progress((handle) -> {
            if(!async) {
                progressFrame.setTitle(handle.text);
                progressBar.setValue((int)(handle.percent * 1000));
                
                if(handle.done || handle.cancel) {
                    progressFrame.dispose();
                }
            }
        });
        
        cancelButton.addActionListener((ActionEvent ev) -> {
            p.cancel = true;
            progressFrame.dispose();
        });
        
        progressFrame.pack();
        progressFrame.setSize(300, 100);
        progressFrame.setLocationRelativeTo(null);
        progressFrame.setVisible(true);
        
        if(async) {
            (new Thread(() -> {
                while(!p.done && !p.cancel) {
                    progressFrame.setTitle(p.text);
                    progressBar.setValue((int)(p.percent * 1000));
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {

                    }
                }
                progressFrame.dispose();
            })).start();
        }
        return p;
    }
    
    public static void createMainWindow() {
        
    }
    
    public static String browseForInputFile() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("."));
        fc.setDialogTitle("Open input file");
        if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
}
