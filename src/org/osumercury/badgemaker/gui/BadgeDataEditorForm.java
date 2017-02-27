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

import com.sun.glass.events.KeyEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.osumercury.badgemaker.*;

/**
 *
 * @author wira
 */
public class BadgeDataEditorForm extends JDialog {
    private Badge finalBadge;
    private BufferedImage background;
    private Color bg, textBg, text;
    
    private JPanel paneMain;
    private JPanel paneGlobalControls;
    
    private JButton btnOK;
    private JButton btnCancel;
    
    private JPanel paneText;
    private TextInputPane paneNumber;
    private TextInputPane paneName;
    private TextInputPane paneInstitution;
    private JPanel paneColors;
    private JPanel paneBackground;
    private ComboBoxPane paneBackgroundFit;
    
    private JLabel lblColors;
    private JLabel lblBackgroundColor;
    private JLabel lblTextBackgroundColor;
    private JLabel lblTextColor;
    private JLabel lblBackground;
    
    public static Badge create(JFrame parent, Badge defaultValues) {
        BadgeDataEditorForm form = new BadgeDataEditorForm();
        form.init(parent, defaultValues);
        return form.getBadge();
    }
    
    public void init(JFrame parent, Badge badge) {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        paneMain = new JPanel();
        paneMain.setLayout(new BoxLayout(paneMain, BoxLayout.Y_AXIS));
        paneText = new JPanel();
        paneText.setLayout(new BoxLayout(paneText, BoxLayout.Y_AXIS));
        paneNumber = new TextInputPane("Number: ", 120);
        paneName = new TextInputPane("Name: ", 120);
        paneInstitution = new TextInputPane("Institution: ", 120);
        paneNumber.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        paneName.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        paneInstitution.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        if(badge != null) {
            paneNumber.setText(String.valueOf(badge.number));
            paneName.setText(badge.primaryText);
            paneInstitution.setText(badge.secondaryText);
        }
        paneText.add(paneNumber);
        paneText.add(Box.createRigidArea(new Dimension(5 ,5)));
        paneText.add(paneName);
        paneText.add(Box.createRigidArea(new Dimension(5 ,5)));
        paneText.add(paneInstitution);        
        paneMain.add(paneText);
        paneColors = new JPanel();
        paneColors.setLayout(new GridLayout(1, 3));
        paneColors.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        lblBackgroundColor = new JLabel("Background");
        lblTextBackgroundColor = new JLabel("Text Background");
        lblTextColor = new JLabel("Text Color");
        lblBackgroundColor.setHorizontalAlignment(SwingConstants.CENTER);
        lblTextBackgroundColor.setHorizontalAlignment(SwingConstants.CENTER);
        lblTextColor.setHorizontalAlignment(SwingConstants.CENTER);
        lblBackgroundColor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        lblTextBackgroundColor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        lblTextColor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));    
        lblBackgroundColor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color c = JColorChooser.showDialog(null, 
                                                   "Choose Background Color",
                                                   bg);
                if(c != null) {
                    bg = c;
                    updateColors();
                }
            }
        });
        lblTextBackgroundColor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color c = JColorChooser.showDialog(null, 
                                                   "Choose Text Background Color",
                                                   textBg);
                if(c != null) {
                    textBg = c;
                    updateColors();
                }
            }
        });
        lblTextColor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color c = JColorChooser.showDialog(null, 
                                                   "Choose Text Color",
                                                   text);
                if(c != null) {
                    text = c;
                    updateColors();
                }
            }
        });
        paneColors.add(lblBackgroundColor);
        paneColors.add(lblTextBackgroundColor);
        paneColors.add(lblTextColor);
        paneColors.setMaximumSize(new Dimension(Short.MAX_VALUE, 150));
        paneMain.add(Box.createRigidArea(new Dimension(5 ,5)));
        JPanel paneLblColors = new JPanel();
        lblColors = new JLabel("Click to change a color:");
        lblColors.setHorizontalAlignment(SwingConstants.LEFT);
        paneLblColors.add(lblColors);
        paneLblColors.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        paneMain.add(paneLblColors);
        paneMain.add(Box.createRigidArea(new Dimension(5 ,5)));
        paneMain.add(paneColors);
        paneMain.add(Box.createRigidArea(new Dimension(5 ,5)));
        paneBackground = new JPanel();
        lblBackground = new JLabel();
        lblBackground.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) {
                    importBackgroundFile();
                }
            }
        });
        paneBackground.add(lblBackground);
        paneBackground.setMaximumSize(new Dimension(Short.MAX_VALUE, 300));
        paneMain.add(paneBackground);
        paneMain.add(Box.createRigidArea(new Dimension(5 ,5)));
        String[] fit = {"fit width", "fit height"};
        paneBackgroundFit = new ComboBoxPane("Picture: ", fit, 100,
                                             "Browse", "Paste", "Remove");
        paneBackgroundFit.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        paneBackgroundFit.addAction(0, e -> {
            importBackgroundFile();
        });
        paneBackgroundFit.addAction(1, e -> {
            pasteImage();
        });
        paneBackgroundFit.addAction(2, e -> {
            updateBackgroundImage(null);
        });
        paneMain.add(paneBackgroundFit);
        paneMain.add(Box.createRigidArea(new Dimension(5, 5)));
        paneMain.add(Box.createVerticalGlue());       
        paneGlobalControls = new JPanel(new FlowLayout());
        btnOK = new JButton("OK");
        btnCancel = new JButton("Cancel");
        paneGlobalControls.add(btnOK);
        paneGlobalControls.add(btnCancel);
        btnOK.addActionListener(e -> {
            if(apply()) {
                dispose();
            }
        });
        btnCancel.addActionListener(e -> {
            dispose();
        });
        contentPane.add(paneMain, BorderLayout.CENTER);
        contentPane.add(paneGlobalControls, BorderLayout.PAGE_END);
        
        if(badge != null) {
            bg = badge.backgroundColor;
            textBg = badge.textBackgroundColor;
            text = badge.textColor;
            updateColors();
            paneBackgroundFit.getComboBox().setSelectedIndex(
                    badge.getBackgroundScaling());
            updateBackgroundImage(badge.background);
        } else {
            bg = Color.WHITE;
            textBg = Color.LIGHT_GRAY;
            text = Color.BLACK;
            updateColors();
            paneBackgroundFit.getComboBox().setSelectedIndex(0);
            updateBackgroundImage(null);
        }
        
        GUI.attachKeyShortcut(this, KeyEvent.VK_ESCAPE, 0,
                new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        pack();
        setTitle((badge == null ? "Add" : "Edit") + " Badge");
        setLocationRelativeTo(parent);
        setResizable(false);
        setModal(true);
        setVisible(true);
    }
    
    private void updateColors() {
        lblBackgroundColor.setOpaque(true);
        lblTextBackgroundColor.setOpaque(true);
        lblTextColor.setOpaque(true);
        
        if(bg.getRed() + bg.getGreen() + bg.getBlue() < 250) {
            lblBackgroundColor.setForeground(Color.WHITE);
        } else {
            lblBackgroundColor.setForeground(Color.BLACK);
        }
        
        if(textBg.getRed() + textBg.getGreen() + textBg.getBlue() < 250) {
            lblTextBackgroundColor.setForeground(Color.WHITE);
        } else {
            lblTextBackgroundColor.setForeground(Color.BLACK);
        }
        
        lblBackgroundColor.setBackground(bg);
        lblTextBackgroundColor.setBackground(textBg);
        lblTextColor.setBackground(textBg);
        lblTextColor.setForeground(text);
    }
    
    private void updateBackgroundImage(BufferedImage img) {
        background = img;
        BufferedImage scaledImg;
        if(img == null) {
            scaledImg = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = scaledImg.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
            String str = "No Badge Picture";
            int strWidth = g.getFontMetrics().stringWidth(str);
            int strHeight = g.getFontMetrics().getHeight();
            int strDescent = g.getFontMetrics().getDescent();
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, 299, 199);
            g.drawString(str, (int)(300 / 2.0f - strWidth / 2.0f),
                              (int)(200 / 2.0f + strHeight / 2.0f - strDescent));
            g.dispose();
        } else {
            scaledImg = ImageTools.scale(img, 200, 200);
        }
        lblBackground.setIcon(new ImageIcon(scaledImg));
    }
    
    private void importBackgroundFile() {
        String file = GUI.browseForFile("Select Image File",
                new FileNameExtensionFilter("Supported image formats",
                                            "png", "jpg", "jpeg"));
        if(file == null) {
            return;
        }
        try {
            Log.d(0, "Reading " + file);
            BufferedImage img = ImageIO.read(new File(file));
            if(img == null) {
                throw new IOException("Unrecognized Image Format");
            }
            updateBackgroundImage(img);
        } catch(IOException ioe) {
            Log.err("Failed to read file: " + ioe);
        }
    }
    
    private void pasteImage() {
        // http://alvinalexander.com/java/java-clipboard-image-copy-paste
        Transferable transferable = Toolkit.getDefaultToolkit()
                                    .getSystemClipboard().getContents(null);
        if (transferable != null && 
            transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                updateBackgroundImage((BufferedImage) transferable
                                      .getTransferData(DataFlavor.imageFlavor));
            } catch (UnsupportedFlavorException e) {
                Log.err("Unable to paste image from the clipboard:\n" + e);
            }
            catch (IOException e) {
                Log.err("Unable to paste image from the clipboard:\n" + e);
            }
        } else {
            Log.err("Failed to paste from the clipboard:\nNot an image");
        }
    }

    private boolean apply() {
        // validate inputs
        if(paneNumber.getText().trim().equals("")) {
            Log.err("A number ID is required\nEnter '-1' if you don't want" +
                    " a number to be rendered for this badge");
            return false;
        }
        int numID;
        try {
            numID = Integer.parseInt(paneNumber.getText());
        } catch(NumberFormatException nfe) {
            Log.err("Failed to parse number ID: " + paneNumber.getText());
            return false;
        }
        String name = paneName.getText().trim();
        if(name.equals("")) {
            // Log.err("Name field can not be empty");
            // return false;
            name = " ";
        }
        String institution = paneInstitution.getText().trim();
        
        finalBadge = new Badge(numID, name, institution, background,
                               String.format("%06x", bg.getRGB()),
                               String.format("%06x", textBg.getRGB()),
                               String.format("%06x", text.getRGB()));
        finalBadge.setBackgroundFit(paneBackgroundFit.getSelectedIndex());
        return true;
    }
    
    public Badge getBadge() {
        return finalBadge;
    }
}
