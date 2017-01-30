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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.osumercury.badgemaker.*;

/**
 *
 * @author wira
 */
public class MercuryCertificateRenderer extends Renderer {
    private String font = Font.SANS_SERIF;
    private Color textBackgroundColor = new Color(0xff, 0x73, 0x00);
    private int originalFontSize = 200;
    private float topPadding = 0.07f;
    private float signaturesPosition = 0.84f;
    private float backgroundHeight = 0.75f;
    private float logoHeight = 0.25f;
    private float nameHeight = 0.10f;
    private float institutionHeight = 0.06f;
    private float mainTextHeight = 0.04f;
    private float competitionTitleHeight = 0.06f;
    private float dateLocationHeight = 0.035f;
    private float staffNamesHeight = 0.035f;
    private float staffTitleHeight = 0.025f;
    private float majorSpacing = 0.039f;
    private float minorSpacing = 0.010f;
    
    private String textCertification = "This is to certify that the robot";
    private String textParticipation = "participated in the";
    private String textAdvisorName = "Dr. Carl D. Latino";
    private String textAdvisorTitle = "Creator / Director";
    private String textPresidentName = "Mr. Club President";
    private String textPresidentTitle = "Mercury Robotics President";
    private String textCompetitionTitle = "8th Annual Mercury Remote Robot Challenge";
    private String textHostTitle = "Hosted by the Electrical and Computer " +
                                   "Engineering Department";
    private String textHostInstitution = "Oklahoma State University";
    private String textDateLocation = "Month ##, 20## - Stillwater, Oklahoma";
    private String pathToLogo = null;
    private String pathToBackground = null;
    
    public MercuryCertificateRenderer() {
        addProperty("font", Property.STRING, font,
                    "name of font to use for text");
        addProperty("certification", Property.STRING, textCertification,
                    "certification preamble");
        addProperty("participation", Property.STRING, textParticipation,
                    "participation preamble");
        addProperty("advisor-name", Property.STRING, textAdvisorName,
                    "name of competition advisor");
        addProperty("advisor-title", Property.STRING, textAdvisorTitle,
                    "competition advisor's title");
        addProperty("president-name", Property.STRING, textPresidentName,
                    "name of club president");
        addProperty("president-title", Property.STRING, textPresidentTitle,
                    "title of club president");
        addProperty("competition-title", Property.STRING, textCompetitionTitle,
                    "competition title");
        addProperty("host-title", Property.STRING, textHostTitle,
                    "certification sub-heading");
        addProperty("host-institution", Property.STRING, textHostInstitution,
                    "host institution");
        addProperty("date-and-location", Property.STRING, textDateLocation,
                    "competition date and location");
        addProperty("path-to-logo", Property.STRING, "path to logo image file");
        addProperty("path-to-background", Property.STRING, "path to background image file");
        addProperty("background-height", Property.FLOAT, String.format("%.3f", backgroundHeight),
                    "page background height in proportion to page height");
        addProperty("text-background", Property.STRING, "ff7300",
                    "name text background (HTML hex)");
        addProperty("padding-top", Property.FLOAT, String.format("%.3f", topPadding),
                    "top padding in proportion to page height");
        addProperty("main-text-height", Property.FLOAT, String.format("%.3f", mainTextHeight),
                    "main text height in proportion to page height");
        addProperty("name-text-height", Property.FLOAT, String.format("%.3f", nameHeight),
                    "team name text height in proportion to page height");
        addProperty("institution-height", Property.FLOAT, String.format("%.3f", institutionHeight),
                    "team institution text height in proportion to page height");
        addProperty("title-height", Property.FLOAT, String.format("%.3f", competitionTitleHeight),
                    "competition title height in proportion to page height");
        addProperty("date-text-height", Property.FLOAT, String.format("%.3f", dateLocationHeight),
                    "date text height in proportion to page height");
        addProperty("staff-name-height", Property.FLOAT, String.format("%.3f", staffNamesHeight),
                    "staff name text height in proportion to page height");
        addProperty("staff-title-height", Property.FLOAT, String.format("%.3f", staffTitleHeight),
                    "staff title text height in proportion to page height");
        addProperty("signatures-position", Property.FLOAT, String.format("%.3f", signaturesPosition),
                    "positiion of staff signatures in proportion to page height from the top");
        addProperty("logo-height", Property.FLOAT, String.format("%.3f", logoHeight),
                    "competition logo height in proportion to page height");
        addProperty("major-spacing", Property.FLOAT, String.format("%.3f", majorSpacing),
                    "vertical spacing between text in proportion to the page height");
        addProperty("minor-spacing", Property.FLOAT, String.format("%.3f", minorSpacing),
                    "minor vertical spacing between text in proportion to the page height");

    }
    
    @Override
    public void setProperty(String key, String value) {
        try {
            System.out.println(Main.pad(25, key) + " " + value);
            switch(key) {
                case "font":                font = value; break;
                case "certification":       textCertification = value; break;
                case "participation":       textParticipation = value; break;
                case "advisor-name":        textAdvisorName = value; break;
                case "advisor-title":       textAdvisorTitle = value; break;
                case "president-name":      textPresidentName = value; break;
                case "president-title":     textPresidentTitle = value; break;
                case "competition-title":   textCompetitionTitle = value; break;
                case "host-title":          textHostTitle = value; break;
                case "host-institution":    textHostInstitution = value; break;
                case "date-and-location":   textDateLocation = value; break;
                case "path-to-logo":        pathToLogo = value; break;
                case "path-to-background":  pathToBackground = value; break;
                case "background-height":   backgroundHeight = Float.parseFloat(value); break;
                case "text-background":     textBackgroundColor = ImageTools.parseHexColor(value); break;
                case "padding-top":         topPadding = Float.parseFloat(value); break;
                case "main-text-height":    mainTextHeight = Float.parseFloat(value); break;
                case "name-text-height":    nameHeight = Float.parseFloat(value); break;
                case "institution-height":  institutionHeight = Float.parseFloat(value); break;
                case "title-height":        competitionTitleHeight = Float.parseFloat(value); break;
                case "date-text-height":    dateLocationHeight = Float.parseFloat(value); break;
                case "staff-name-height":   staffNamesHeight = Float.parseFloat(value); break;
                case "staff-title-height":  staffTitleHeight = Float.parseFloat(value); break;
                case "signatures-position": signaturesPosition = Float.parseFloat(value); break;
                case "logo-height":         logoHeight = Float.parseFloat(value); break;
                case "major-spacing":       majorSpacing = Float.parseFloat(value); break;
                case "minor-spacing":       minorSpacing = Float.parseFloat(value); break;
                default:
                    System.err.println("Unknown property key: " + key);
            }
        } catch(Exception e) {
            System.err.println("Failed to set property: " + key + ":" + value);
        }
    }

    @Override
    public BufferedImage render(Badge badge) {
        Dimension d = badge.getPixelDimension();
        BufferedImage img = new BufferedImage(d.width, d.height,
                                              BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
        int w, h, y, x;
        BufferedImage sprite, logo = null, background = null;
        g.setColor(badge.backgroundColor);
        g.fillRect(0, 0, d.width, d.height);
        
        if(pathToBackground != null) {
            try{
                background = ImageIO.read(new File(pathToBackground));
            } catch(IOException ioe) {
                System.err.println("[E] Failed to load " + pathToLogo);
            }
        }
        
        if(background != null) {
            h = (int)(backgroundHeight * d.height);
            w = (int)((float)h / background.getHeight() * background.getWidth());
            g.drawImage(ImageTools.scale(background, w, h),
                        (int)(d.width / 2.0f - w / 2.0f),
                        (int)(d.height / 2.0f - h / 2.0f), null);
        }
        
        if(pathToLogo != null) {
            try{
                logo = ImageIO.read(new File(pathToLogo));
            } catch(IOException ioe) {
                System.err.println("[E] Failed to load " + pathToLogo);
            }
        }
        
        if(logo != null) {
            h = (int)(logoHeight * d.height);
            w = (int)((float)h / logo.getHeight() * logo.getWidth());
            g.drawImage(ImageTools.scale(logo, w, h),
                        (int)(d.width / 2.0f - w / 2.0f),
                        (int)((1.0f - 0.05f - logoHeight) * d.height), null);
        }
        
        // generate our texts (we scall them down after they're rasterized)
        BufferedImage imgPrimary;
        BufferedImage imgSecondary;
        BufferedImage imgCertification;
        BufferedImage imgParticipation;
        BufferedImage imgAdvisorName;
        BufferedImage imgAdvisorTitle;
        BufferedImage imgPresidentName;
        BufferedImage imgPresidentTitle;
        BufferedImage imgCompetitionTitle;
        BufferedImage imgHostTitle;
        BufferedImage imgHostInstitution;
        BufferedImage imgDateLocation;
        
        // let's do the non-bold ones first
        Font f = new Font(font, Font.PLAIN, originalFontSize);
        Graphics2D gg;
        FontMetrics fm = g.getFontMetrics(f);
        
        imgCertification = new BufferedImage(fm.stringWidth(textCertification),
                                             fm.getHeight(),
                                             BufferedImage.TYPE_INT_ARGB);
        gg = imgCertification.createGraphics();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setColor(Color.BLACK);
        gg.setFont(f);
        gg.drawString(textCertification, 0, fm.getHeight()-fm.getDescent());
        gg.dispose();
        
        String textParticipationFinal = textParticipation;
        for(String line : badge.getExtraData()) {
            if(line.trim().startsWith("text-participation::")) {
                String tokens[] = line.trim().split("::", 2);
                if(tokens.length == 2) {
                    textParticipationFinal = tokens[1];
                }
            }
        }
        imgParticipation = new BufferedImage(fm.stringWidth(textParticipationFinal),
                                             fm.getHeight(),
                                             BufferedImage.TYPE_INT_ARGB);
        gg = imgParticipation.createGraphics();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setColor(Color.BLACK);
        gg.setFont(f);
        gg.drawString(textParticipationFinal, 0, fm.getHeight()-fm.getDescent());
        gg.dispose();                
        
        imgAdvisorTitle = new BufferedImage(fm.stringWidth(textAdvisorTitle),
                                            fm.getHeight(),
                                            BufferedImage.TYPE_INT_ARGB);
        gg = imgAdvisorTitle.createGraphics();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setColor(Color.BLACK);
        gg.setFont(f);
        gg.drawString(textAdvisorTitle, 0, fm.getHeight()-fm.getDescent());
        gg.dispose();                
        
        imgPresidentTitle = new BufferedImage(fm.stringWidth(textPresidentTitle),
                                              fm.getHeight(),
                                              BufferedImage.TYPE_INT_ARGB);
        gg = imgPresidentTitle.createGraphics();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setColor(Color.BLACK);
        gg.setFont(f);
        gg.drawString(textPresidentTitle, 0, fm.getHeight()-fm.getDescent());
        gg.dispose();                
        
        imgHostTitle = new BufferedImage(fm.stringWidth(textHostTitle),
                                         fm.getHeight(),
                                         BufferedImage.TYPE_INT_ARGB);
        gg = imgHostTitle.createGraphics();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setColor(Color.BLACK);
        gg.setFont(f);
        gg.drawString(textHostTitle, 0, fm.getHeight()-fm.getDescent());
        gg.dispose();
        
        imgHostInstitution = new BufferedImage(fm.stringWidth(textHostInstitution),
                                               fm.getHeight(),
                                               BufferedImage.TYPE_INT_ARGB);
        gg = imgHostInstitution.createGraphics();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setColor(Color.BLACK);
        gg.setFont(f);
        gg.drawString(textHostInstitution, 0, fm.getHeight()-fm.getDescent());
        gg.dispose();
        
        imgDateLocation = new BufferedImage(fm.stringWidth(textDateLocation),
                                            fm.getHeight(),
                                            BufferedImage.TYPE_INT_ARGB);
        gg = imgDateLocation.createGraphics();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setColor(Color.BLACK);
        gg.setFont(f);
        gg.drawString(textDateLocation, 0, fm.getHeight()-fm.getDescent());
        gg.dispose();
        
        // bold texts
        f = new Font(font, Font.BOLD, originalFontSize);
        fm = g.getFontMetrics(f);
        
        imgCompetitionTitle = new BufferedImage(fm.stringWidth(textCompetitionTitle),
                                                fm.getHeight(),
                                                BufferedImage.TYPE_INT_ARGB);
        gg = imgCompetitionTitle.createGraphics();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setColor(Color.BLACK);
        gg.setFont(f);
        gg.drawString(textCompetitionTitle, 0, fm.getHeight()-fm.getDescent());
        gg.dispose();
        
        imgAdvisorName = new BufferedImage(fm.stringWidth(textAdvisorName),
                                           fm.getHeight(),
                                           BufferedImage.TYPE_INT_ARGB);
        gg = imgAdvisorName.createGraphics();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setColor(Color.BLACK);
        gg.setFont(f);
        gg.drawString(textAdvisorName, 0, fm.getHeight()-fm.getDescent());
        gg.dispose();
        
        imgPresidentName = new BufferedImage(fm.stringWidth(textPresidentName),
                                             fm.getHeight(),
                                             BufferedImage.TYPE_INT_ARGB);
        gg = imgPresidentName.createGraphics();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setColor(Color.BLACK);
        gg.setFont(f);
        gg.drawString(textPresidentName, 0, fm.getHeight()-fm.getDescent());
        gg.dispose();
        
        imgPrimary = new BufferedImage(fm.stringWidth(badge.primaryText),
                                       fm.getHeight(),
                                       BufferedImage.TYPE_INT_ARGB);
        gg = imgPrimary.createGraphics();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setColor(Color.WHITE);
        gg.setFont(f);
        gg.drawString(badge.primaryText, 0, fm.getHeight()-fm.getDescent());
        gg.dispose();
        
        imgSecondary = new BufferedImage(fm.stringWidth(badge.secondaryText),
                                         fm.getHeight(),
                                         BufferedImage.TYPE_INT_ARGB);
        gg = imgSecondary.createGraphics();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setColor(Color.BLACK);
        gg.setFont(f);
        gg.drawString(badge.secondaryText, 0, fm.getHeight()-fm.getDescent());
        gg.dispose();
        
        y = (int)(topPadding * d.height);
        h = (int)(mainTextHeight * d.height);
        w = (int)((float)h / imgCertification.getHeight() *
                  imgCertification.getWidth());
        x = (int)(d.width / 2.0f - w / 2.0f);
        sprite = ImageTools.scale(imgCertification, w, h);
        g.drawImage(sprite, x, y, null);
        
        y += (int)(h + majorSpacing * d.height);
        h = (int)(nameHeight * d.height);
        w = (int)((float)h / imgPrimary.getHeight() *
                  imgPrimary.getWidth());
        x = (int)(d.width / 2.0f - w / 2.0f);
        sprite = ImageTools.scale(imgPrimary, w, h);
        g.setColor(textBackgroundColor);
        g.fillRoundRect(x - (int)(0.03f*d.width),
                        y,
                        w + (int)(0.06f*d.width),
                        h, 
                        (int)(0.05f*d.height), (int)(0.05f*d.height));
        g.drawImage(sprite, x, y, null);
        
        y += (int)(h + minorSpacing * d.height);
        h = (int)(institutionHeight * d.height);
        w = (int)((float)h / imgSecondary.getHeight() *
                  imgSecondary.getWidth());
        x = (int)(d.width / 2.0f - w / 2.0f);
        sprite = ImageTools.scale(imgSecondary, w, h);
        g.drawImage(sprite, x, y, null);
        
        y += (int)(h + majorSpacing * d.height);
        h = (int)(mainTextHeight * d.height);
        w = (int)((float)h / imgParticipation.getHeight() *
                  imgParticipation.getWidth());
        x = (int)(d.width / 2.0f - w / 2.0f);
        sprite = ImageTools.scale(imgParticipation, w, h);
        g.drawImage(sprite, x, y, null);
        
        y += (int)(h + majorSpacing * d.height);
        h = (int)(competitionTitleHeight * d.height);
        w = (int)((float)h / imgCompetitionTitle.getHeight() *
                  imgCompetitionTitle.getWidth());
        x = (int)(d.width / 2.0f - w / 2.0f);
        sprite = ImageTools.scale(imgCompetitionTitle, w, h);
        g.drawImage(sprite, x, y, null);
        
        y += (int)(h + majorSpacing * d.height);
        h = (int)(mainTextHeight * d.height);
        w = (int)((float)h / imgHostTitle.getHeight() *
                  imgHostTitle.getWidth());
        x = (int)(d.width / 2.0f - w / 2.0f);
        sprite = ImageTools.scale(imgHostTitle, w, h);
        g.drawImage(sprite, x, y, null);
        
        y += (int)(h + minorSpacing * d.height);
        h = (int)(mainTextHeight * d.height);
        w = (int)((float)h / imgHostInstitution.getHeight() *
                  imgHostInstitution.getWidth());
        x = (int)(d.width / 2.0f - w / 2.0f);
        sprite = ImageTools.scale(imgHostInstitution, w, h);
        g.drawImage(sprite, x, y, null);
        
        y += (int)(h + minorSpacing * d.height);
        h = (int)(dateLocationHeight * d.height);
        w = (int)((float)h / imgDateLocation.getHeight() *
                  imgDateLocation.getWidth());
        x = (int)(d.width / 2.0f - w / 2.0f);
        sprite = ImageTools.scale(imgDateLocation, w, h);
        g.drawImage(sprite, x, y, null);
        
        y = (int)(signaturesPosition * d.height);
        w = (int)(0.15f * d.width);
        x = (int)(0.5f * 0.35f * d.width) - w / 2;
        h = (int)(0.003f * d.height);
        g.setColor(Color.BLACK);
        g.fillRect(x, y, w, h);
        x = (int)((1 - 0.5f * 0.35f) * d.width) - w / 2;
        g.fillRect(x, y, w, h);
        
        y += (int)(h + 2 * minorSpacing * d.height);
        h = (int)(staffNamesHeight * d.height);
        w = (int)((float)h / imgAdvisorName.getHeight() *
                  imgAdvisorName.getWidth());
        x = (int)(0.5f * 0.35f * d.width) - w / 2;
        sprite = ImageTools.scale(imgAdvisorName, w, h);
        g.drawImage(sprite, x, y, null);
        w = (int)((float)h / imgPresidentName.getHeight() *
                  imgPresidentName.getWidth());
        x = (int)((1 - 0.5f * 0.35f) * d.width) - w / 2;
        sprite = ImageTools.scale(imgPresidentName, w, h);
        g.drawImage(sprite, x, y, null);
        
        y += (int)(h + minorSpacing * d.height);
        h = (int)(staffTitleHeight * d.height);
        w = (int)((float)h / imgAdvisorTitle.getHeight() *
                  imgAdvisorTitle.getWidth());
        x = (int)(0.5f * 0.35f * d.width) - w / 2;
        sprite = ImageTools.scale(imgAdvisorTitle, w, h);
        g.drawImage(sprite, x, y, null);
        w = (int)((float)h / imgPresidentTitle.getHeight() *
                  imgPresidentTitle.getWidth());
        x = (int)((1 - 0.5f * 0.35f) * d.width) - w / 2;
        sprite = ImageTools.scale(imgPresidentTitle, w, h);
        g.drawImage(sprite, x, y, null);
        
        g.dispose();
        return img;
    }

    @Override
    public String getDescription() {
        return "Mercury Certificate of Participation Renderer";
    }
}
