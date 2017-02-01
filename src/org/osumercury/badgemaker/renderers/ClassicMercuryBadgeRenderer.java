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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.osumercury.badgemaker.*;

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
                    "Line height to text ratio (smaller for bigger text, 1.0 " +
                    "for equal heights)");
        addProperty("font-bold", Property.STRING, "no",
                    "Make text bold {\"yes\", \"no\"}");
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
                default:
                    System.err.println("Unknown property key: " + key);
            }
        } catch(Exception e) {
            Log.err("Failed to set property: " + key + ":" + value);
        }
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
                    backgroundY = (int)(d.height/2.0 - background.getHeight()/2.0);
            }
            g.drawImage(background,
                        (int)(d.width/2.0 - background.getWidth()/2.0),
                        backgroundY, null);
        }
        g.setColor(badge.textBackgroundColor);
        Polygon p = new Polygon();
        p.addPoint(0,                       0);
        p.addPoint(0,                       (int) (primaryHeight * d.height));
        p.addPoint((int)(0.85 * d.width),   (int) (primaryHeight * d.height));
        p.addPoint((int)(1.00 * d.width),   0);
        g.fillPolygon(p);
        p = new Polygon();
        p.addPoint(0,                       (int) ((1-secondaryHeight) * d.height));
        p.addPoint(0,                       (int) (1.00 * d.height));
        p.addPoint((int)(1.00 * d.width),   (int) (1.00 * d.height));
        p.addPoint((int)(1.00 * d.width),   (int) ((1-secondaryHeight) * d.height));
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
            textH = (int)(primaryHeight * d.height);
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
        textH = (int)(primaryHeight * d.height);
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
        textH = (int)(secondaryHeight * d.height);
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
        /*
        if(badge.number > -1) {
            int numberFontSize = 0;
            
            limitH = (int)(primaryHeight * d.height);
            limitW = (int)(0.25 * d.width);
            do {
                numberFontSize++;
                numberFont = new Font(font,
                                      fontBold ? Font.BOLD : Font.PLAIN,
                                      numberFontSize);
                fm = g.getFontMetrics(numberFont);
                textW = fm.stringWidth(fNumber);
                textH = (int)(textHeightFactor * fm.getHeight());
            } while(textH < limitH &&
                    textW < limitW);

            textX = (int)(0.03 * d.width);
            textY = (limitH - textH)/2
                    + textH - (int)(textHeightFactor * fm.getDescent());

            g.setFont(numberFont);
            g.setColor(badge.textColor);
            g.drawString(fNumber, textX, textY);
            secondaryTextOffset += textX + fm.stringWidth(fNumber)
                                + (int)(0.02 * d.width);
        }
        
        int primaryFontSize = 0;
        limitH = (int)(primaryHeight * d.height);
        limitW = (int)(0.94 * d.width);
        do {
            primaryFontSize++;
            primaryFont = new Font(font,
                                   fontBold ? Font.BOLD : Font.PLAIN,
                                   primaryFontSize);
            fm = g.getFontMetrics(primaryFont);
            textW = fm.stringWidth(badge.primaryText);
            textH = (int)(textHeightFactor * fm.getHeight());
        } while(textH < limitH && 
                textW < limitW);
        
        g.setColor(badge.textColor);
        textX = (int)(0.03 * d.width);
        textY = (int)((1.00 - primaryHeight) * d.height)
                + (limitH - textH)/2
                + textH - (int)(textHeightFactor * fm.getDescent());
        g.setFont(primaryFont);
        g.drawString(badge.primaryText, textX, textY);
        
        int secondaryFontSize = 0;
        limitH = (int)(secondaryHeight * d.height);
        limitW = (int)(0.80 * d.width) - secondaryTextOffset;
        do {
            secondaryFontSize++;
            secondaryFont = new Font(font,
                                     fontBold ? Font.BOLD : Font.PLAIN,
                                     secondaryFontSize);
            fm = g.getFontMetrics(secondaryFont);
            textW = fm.stringWidth(badge.secondaryText);
            textH = (int)(textHeightFactor * fm.getHeight());
        } while(textH < limitH &&
                textW < limitW);        
        textX = secondaryTextOffset + (int)(0.02 * d.width);
        textY = (limitH - textH)/2
                + textH - (int)(textHeightFactor * fm.getDescent());
        g.setFont(secondaryFont);
        g.drawString(badge.secondaryText, textX, textY);         
        */
                
        g.dispose();
        return out;
    }
    
    @Override
    public JPanel getRendererGUIControls() {
        JPanel pane = new JPanel();
        JLabel lblNoGUI = new JLabel("Mercury Badge");
        lblNoGUI.setHorizontalAlignment(SwingConstants.CENTER);
        pane.setLayout(new BorderLayout());
        pane.add(lblNoGUI, BorderLayout.CENTER);
        return pane;
    }
}
