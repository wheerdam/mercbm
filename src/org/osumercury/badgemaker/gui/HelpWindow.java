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
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Insets;
import javax.swing.*;
import java.awt.event.*;
import java.util.Stack;
import javax.swing.event.HyperlinkEvent;
import org.osumercury.badgemaker.Log;
import org.osumercury.badgemaker.text.Text;

/**
 *
 * @author wira
 */
public class HelpWindow extends JFrame {
    private JScrollPane scrollAbout;
    private JEditorPane text;
    private JButton btnClose;
    private JButton btnHome;
    private JButton btnBack;
    private JPanel paneGlobalControls;
    private JPanel paneNavigation;
    private Stack<String> history;
    private String currentURL;
    
    public void init() {
        history = new Stack<>();
        paneNavigation = new JPanel();
        paneNavigation.setLayout(new FlowLayout(FlowLayout.LEADING));
        btnHome = new JButton("Home");
        btnBack = new JButton("Back");
        btnHome.addActionListener((e) -> { openHelpDocument("Help.html"); });
        btnBack.addActionListener((e) -> {
            if(!history.isEmpty()) {
                currentURL = history.pop();
                text.setText(Text.parseHTMLResource(currentURL));
                if(history.isEmpty()) {
                    btnBack.setEnabled(false);
                }
            }
        });
        btnBack.setEnabled(false);
        paneNavigation.add(btnHome);
        paneNavigation.add(btnBack);
        getContentPane().add(paneNavigation, BorderLayout.PAGE_START);
        
        text = new JEditorPane();
        // text.setLineWrap(true);
        // text.setWrapStyleWord(true);
        text.setEditable(false);
        text.setMargin(new Insets(15, 15, 15, 15));
        text.setOpaque(true);
        text.setContentType("text/html");
        currentURL = "Help.html";
        text.setText(Text.parseHTMLResource(currentURL));
        text.addHyperlinkListener((ev) -> {
            if(ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                String url = ev.getDescription();
                if(url == null) {
                    return;
                }
                if(url.startsWith("http:") || url.startsWith("https:")) {
                    // open system browser
                    if(Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(ev.getURL().toURI());
                        } catch(Exception e) {
                            Log.err("Failed to open link:\n" + e);
                        }
                    }   
                } else if(url.startsWith("file:")) {
                    openHelpDocument(url.substring(5, url.length()));
                } else {
                    openHelpDocument(url);
                }
            }
        });
        scrollAbout = new JScrollPane(text);
        scrollAbout.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollAbout.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        paneGlobalControls = new JPanel();
        btnClose = new JButton("Close");
        btnClose.addActionListener((e) -> { dispose(); });
        paneGlobalControls.add(btnClose);
        getContentPane().add(scrollAbout, BorderLayout.CENTER);
        getContentPane().add(paneGlobalControls, BorderLayout.PAGE_END);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);        
        setTitle("Help");
        // aboutWindow.pack();
        GUI.attachKeyShortcut(this, KeyEvent.VK_ESCAPE, 0,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });
        setSize(900, 600);
    }
    
    private void openHelpDocument(String key) {
        String document = Text.parseHTMLResource(key);
        if(document == null) {
            text.setText("<h2>" + key + " - Document Not Found</h2>");
            return;
        }
        history.push(currentURL);
        btnBack.setEnabled(true);
        currentURL = key;
        text.setText(document);
        text.setCaretPosition(0);
    }
    
    public void display(JFrame spawningWindow) {
        SwingUtilities.invokeLater(() -> {
            setLocationRelativeTo(spawningWindow);
            setVisible(true);
        });
    }
}
