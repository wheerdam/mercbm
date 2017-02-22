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
package org.osumercury.badgemaker.renderers;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.osumercury.badgemaker.*;
import org.osumercury.badgemaker.gui.MainWindow;
import org.osumercury.badgemaker.gui.OptionsPane;
import org.osumercury.badgemaker.gui.TextInputPane;

/**
 *
 * @author wira
 */
public class ClassicMercuryBadgeRenderer extends Renderer {
    private String font = Font.SANS_SERIF;
    private int originalFontSize = 200;
    private boolean fontBold = false;
    private float primaryHeight = 0.15f;
    private float secondaryHeight = 0.15f;
    private float textHeightFactor = 1.00f;
    
    @Override
    public String getDescription() {
        return "Classic Mercury Competition Badge Renderer";
    }
    
    public ClassicMercuryBadgeRenderer() {
        addProperty("font", Property.STRING, Font.SANS_SERIF,
                    "Name of font to use");
        addProperty("primary-height", Property.FLOAT, String.format("%.3f", primaryHeight),
                    "Primary text line height in proportion to badge height");
        addProperty("secondary-height", Property.FLOAT, String.format("%.3f", secondaryHeight),
                    "Secondary text line height in proportion to badge height");
        addProperty("text-height-factor", Property.FLOAT, String.format("%.3f", textHeightFactor),
                    "Text height to area ratio (larger for bigger text, 1.0 " +
                    "for fit)");
        addProperty("font-bold", Property.STRING, "no",
                    "Make text bold {\"yes\", \"no\"}");
        addProperty("font-size-initial", Property.INTEGER, "" + originalFontSize,
                    "initial full resolution font size");
    }
    
    @Override
    public void setProperty(String key, String value) {
        Log.d(0, Main.pad(25, key) + " " + value);
        try {
            switch(key) {
                case "font":
                    font = value;
                    break;
                case "primary-height":
                    primaryHeight = Float.parseFloat(value);
                    break;
                case "secondary-height":
                    secondaryHeight = Float.parseFloat(value);
                    break;
                case "text-height-factor":
                    textHeightFactor = Float.parseFloat(value);
                    break;
                case "font-bold":
                    fontBold = value.equals("yes");
                    break;
                case "font-size-initial":   
                    originalFontSize = Integer.parseInt(value); 
                    break;
                default:
                    System.err.println("Unknown property key: " + key);
            }
        } catch(Exception e) {
            Log.err("Failed to set property: " + key + ":" + value);
        }
    }
    
    @Override
    public Object getProperty(String key) {
        switch(key) {
            case "font":                return font;
            case "primary-height":      return primaryHeight;
            case "secondary-height":    return secondaryHeight;
            case "text-height-factor":  return textHeightFactor;
            case "font-bold":           return fontBold ? "yes" : "no";
            default:
                System.err.println("Unknown property key: " + key);
        }
        return null;
    }
    
    @Override
    public BufferedImage render(Badge badge) {
        // the renderer always works with pixel dimensions
        Dimension d = badge.getPixelDimension();
        BufferedImage out = new BufferedImage(d.width, d.height, 
                                              BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(badge.backgroundColor);
        g.fillRect(0, 0, d.width, d.height);
        if(badge.background != null) {
            BufferedImage background;
            int bgWidth, bgHeight;
            switch(badge.getBackgroundScaling()) {
                case Badge.BACKGROUND_FILL:
                    bgWidth = d.width;
                    bgHeight = d.height;
                    break;
                case Badge.BACKGROUND_FIT_HEIGHT:
                    bgHeight = (int)((1-primaryHeight-secondaryHeight)*d.height);
                    bgWidth = (int)((float)bgHeight /
                              badge.background.getHeight()
                              * badge.background.getWidth());
                    
                    break;
                default:
                case Badge.BACKGROUND_FIT_WIDTH:
                    bgWidth = d.width;
                    bgHeight = (int)((float)d.width / 
                               badge.background.getWidth()
                               * badge.background.getHeight());
                    break;
            }
            Log.d(0, "        Scaling background image");
            background = ImageTools.scale(badge.background, bgWidth, bgHeight);
            int backgroundY;
            switch(badge.getBackgroundVerticalPosition()) {
                case Badge.BACKGROUND_TOP:
                    backgroundY = 0;
                    break;
                case Badge.BACKGROUND_BOTTOM:
                    backgroundY = d.height - background.getHeight();
                    break;
                default:
                case Badge.BACKGROUND_MIDDLE:         
                    if(badge.getBackgroundScaling() == Badge.BACKGROUND_FIT_HEIGHT) {
                        backgroundY = (int)(secondaryHeight*d.height + 
                                      (1-secondaryHeight-primaryHeight)*d.height/2.0f -
                                      background.getHeight()/2.0);
                    } else {
                        backgroundY = (int)(d.height/2.0f -
                                      background.getHeight()/2.0);
                    }
            }
            g.drawImage(background,
                        (int)(d.width/2.0 - background.getWidth()/2.0),
                        backgroundY, null);
        }
        g.setColor(badge.textBackgroundColor);
        Polygon p = new Polygon();
        p.addPoint(0,                       0);
        p.addPoint(0,                       (int) (secondaryHeight * d.height));
        p.addPoint((int)(0.85 * d.width),   (int) (secondaryHeight * d.height));
        p.addPoint((int)(1.00 * d.width),   0);
        g.fillPolygon(p);
        p = new Polygon();
        p.addPoint(0,                       (int) ((1-primaryHeight) * d.height));
        p.addPoint(0,                       (int) (1.00 * d.height));
        p.addPoint((int)(1.00 * d.width),   (int) (1.00 * d.height));
        p.addPoint((int)(1.00 * d.width),   (int) ((1-primaryHeight) * d.height));
        g.fillPolygon(p);
        FontMetrics fm;
        int textX, textY, textW, textH, limitW, numberW;
        
        BufferedImage imgNumber;
        BufferedImage imgPrimaryText;
        BufferedImage imgSecondaryText;
        
        String fNumber = String.format("%02d", badge.number);
        Font f = new Font(font, fontBold ? Font.BOLD : Font.PLAIN,
                          originalFontSize);
        Graphics2D gg;
        fm = g.getFontMetrics(f);
        
        imgNumber = null;
        numberW = 0;
        if(badge.number > -1) {
            imgNumber = new BufferedImage(fm.stringWidth(fNumber),
                                          fm.getHeight(),
                                          BufferedImage.TYPE_INT_ARGB);
            gg = imgNumber.createGraphics();
            gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            gg.setColor(badge.textColor);
            gg.setFont(f);
            gg.drawString(fNumber, 0, fm.getHeight()-fm.getDescent());
            gg.dispose();
            textH = (int)(secondaryHeight * d.height);
            numberW = (int)((float)textH / imgNumber.getHeight() *
                          imgNumber.getWidth());
            g.drawImage(ImageTools.scale(imgNumber, numberW, textH), 
                        (int)(0.02f * d.width), 0, null);
        }
        
        imgPrimaryText = new BufferedImage(fm.stringWidth(badge.primaryText),
                                           fm.getHeight(),
                                           BufferedImage.TYPE_INT_ARGB);
        gg = imgPrimaryText.createGraphics();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setColor(badge.textColor);
        gg.setFont(f);
        gg.drawString(badge.primaryText, 0, fm.getHeight()-fm.getDescent());
        gg.dispose();
        textH = (int)(textHeightFactor * primaryHeight * d.height);
        textW = (int)((float)textH / imgPrimaryText.getHeight() *
                      imgPrimaryText.getWidth());
        limitW = d.width - (int)(0.08f * d.width);
        if(textW > limitW) {
            textW = limitW;
            textH = (int)((float)textW / imgPrimaryText.getWidth() *
                          imgPrimaryText.getHeight());
        }
        textX = (int)(d.width / 2.0f - textW / 2.0f);        
        textY = d.height - textH - (int)(primaryHeight * d.height - textH) / 2;        
        g.drawImage(ImageTools.scale(imgPrimaryText, textW, textH), 
                    textX, textY, null);
        
        if(!badge.secondaryText.equals("")) {
            imgSecondaryText = new BufferedImage(fm.stringWidth(badge.secondaryText),
                                                 fm.getHeight(),
                                                 BufferedImage.TYPE_INT_ARGB);
            gg = imgSecondaryText.createGraphics();
            gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            gg.setColor(badge.textColor);
            gg.setFont(f);
            gg.drawString(badge.secondaryText, 0, fm.getHeight()-fm.getDescent());
            gg.dispose();
            textH = (int)(textHeightFactor * secondaryHeight * d.height);
            textW = (int)((float)textH / imgSecondaryText.getHeight() *
                          imgSecondaryText.getWidth());
            limitW = d.width - (imgNumber != null ? numberW : 0)
                     - (int)(0.25f * d.width);
            if(textW > limitW) {
                textW = limitW;
                textH = (int)((float)textW / imgSecondaryText.getWidth() *
                              imgSecondaryText.getHeight());
            }
            textX = (imgNumber != null ? numberW : 0)
                    + (int)(0.06f * d.width);
            textY = (int)(secondaryHeight * d.height / 2 - textH / 2.0f);
            if(textY < 0) {
                textY = 0;
            }
            g.drawImage(ImageTools.scale(imgSecondaryText, textW, textH), 
                        textX, textY, null);
        }
                
        g.dispose();
        return out;
    }
    
    @Override
    public JPanel getRendererGUIControls() {
        JPanel pane = new JPanel();
        Dimension min, pref, max;
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.add(Box.createRigidArea(new Dimension(5, 5)));
        
        final TextInputPane paneFont = new TextInputPane("Font: ", 120, false,
                                                         "Change");
        paneFont.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        paneFont.setText(font);
        paneFont.addAction(0, e -> {
            MainWindow.getFontSelectDialog().setModal(true);
            MainWindow.getFontSelectDialog().setLocationRelativeTo(pane);
            MainWindow.getFontSelectDialog().showDialog();
            String fontName = MainWindow.getFontSelectDialog().getFontName();
            if(fontName != null) {
                font = fontName;
                paneFont.setText(font);
            }
        });
        pane.add(paneFont);
        pane.add(Box.createRigidArea(new Dimension(5, 5)));
        
        final OptionsPane paneOptions = new OptionsPane("Font Options: ", 120,
                                                        "Bold");
        paneOptions.addAction(0, e -> {
            fontBold = paneOptions.getValue(0);
        });
        paneOptions.setValue(0, fontBold);
        paneOptions.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        pane.add(paneOptions);
        pane.add(Box.createRigidArea(new Dimension(5, 5)));
        
        panePrimaryHeight = new TextInputPane("Primary Text Height: ", 200);
        panePrimaryHeight.setText(String.format("%.3f", primaryHeight));
        panePrimaryHeight.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        pane.add(panePrimaryHeight);
        pane.add(Box.createRigidArea(new Dimension(5, 5)));
        
        paneSecondaryHeight = new TextInputPane("Secondary Text Height: ", 200);
        paneSecondaryHeight.setText(String.format("%.3f", secondaryHeight));
        paneSecondaryHeight.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        pane.add(paneSecondaryHeight);
        pane.add(Box.createRigidArea(new Dimension(5, 5)));
        
        paneTextHeightFactor = new TextInputPane("Text Height Factor: ", 200);
        paneTextHeightFactor.setText(String.format("%.3f", textHeightFactor));
        paneTextHeightFactor.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        pane.add(paneTextHeightFactor);
        pane.add(Box.createRigidArea(new Dimension(5, 5)));
        
        paneButtonApply = new JPanel();
        btnApply = new JButton("Apply");
        btnApply.addActionListener((e) -> { applySettings(); } );
        paneButtonApply.add(btnApply);
        pane.add(paneButtonApply);
        
        min = new Dimension(5, 5);
        pref = new Dimension(5, 5);
        max = new Dimension(5, Short.MAX_VALUE);
        pane.add(new Box.Filler(min, pref, max));
        
        return pane;
    }
    
    private JPanel paneButtonApply;
    private JButton btnApply;
    private TextInputPane panePrimaryHeight;
    private TextInputPane paneSecondaryHeight;
    private TextInputPane paneTextHeightFactor;
    
    private void applySettings() {
        try {
            float newPrimaryHeight = Float.parseFloat(
                    panePrimaryHeight.getText());
            primaryHeight = newPrimaryHeight;
        } catch(NumberFormatException nfe) {
            panePrimaryHeight.setText(String.format("%.3f", primaryHeight));
        }
        
        try {
            float newSecondaryHeight = Float.parseFloat(
                    paneSecondaryHeight.getText());
            secondaryHeight = newSecondaryHeight;
        } catch(NumberFormatException nfe) {
            paneSecondaryHeight.setText(String.format("%.3f", secondaryHeight));
        }
        
        try {
            float newTextHeightFactor = Float.parseFloat(
                    paneTextHeightFactor.getText());
            textHeightFactor = newTextHeightFactor;
        } catch(NumberFormatException nfe) {
            paneTextHeightFactor.setText(String.format("%.3f", textHeightFactor));
        }
    }
}
