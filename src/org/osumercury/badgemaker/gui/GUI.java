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
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.SwingUtilities;
import org.osumercury.badgemaker.Badge;
import org.osumercury.badgemaker.IO;
import org.osumercury.badgemaker.Progress;

/**
 *
 * @author wira
 */
public class GUI {
    public static Progress createProgressDialog(String title) {
        ProgressDialog pD = new ProgressDialog();
        Progress p = new Progress((callback) -> {
            pD.update();
        });
        pD.init(p, title);
        pD.display();
        return p;
    }
    
    public static void createMainWindow(String file) {
        MainWindow mainWindow = new MainWindow();
        SwingUtilities.invokeLater(() -> {
            mainWindow.init(file);
            if(file != null) {
                Progress p = createProgressDialog("Importing " + file);
                List<Badge> badges = new ArrayList<>();
                (new Thread(() -> {
                    System.out.println("Importing...");
                    badges.addAll(IO.readFromCSV(p, file,
                                  Badge.DEFAULT_WIDTH,
                                  Badge.DEFAULT_WIDTH*Badge.DEFAULT_PROPORTION,
                                  Badge.DEFAULT_RESOLUTION));
                })).start();
                mainWindow.getBadgeList().addAll(badges);
            }
            mainWindow.setVisible(true);
        });
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
    
    public static boolean confirmYesNo(JFrame parent,
                                       String message, String title) {
        return JOptionPane.showConfirmDialog(parent, message, title,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    
    public static boolean confirmOKCancel(JFrame parent,
                                          String message, String title) {
        return JOptionPane.showConfirmDialog(parent, message, title,
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
    }
}
