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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.osumercury.badgemaker.Badge;
import org.osumercury.badgemaker.ImageTools;
import org.osumercury.badgemaker.Log;
import org.osumercury.badgemaker.Main;
import org.osumercury.badgemaker.Renderer;

/**
 *
 * @author wira
 */
public class ScriptableRenderer extends Renderer {
    private String scriptFile;
    private File scriptPath;
    private List<String> script;
    private int initialFontSize = 200;

    public ScriptableRenderer() {
        addProperty("script-file", Renderer.Property.STRING, 
                    "script file to execute");
    }
    
    @Override
    public void setProperty(String key, String value) {
        Log.d(0, Main.pad(25, key) + " " + value);
        try {
            switch(key) {
                case "initial-font-size":
                    initialFontSize = Integer.parseInt(value);
                    break;
                case "script-file":
                    scriptFile = value;
                    try {
                        script = Files.readAllLines(Paths.get(scriptFile));
                        scriptPath = (new File(scriptFile)).getParentFile();
                    } catch(IOException ioe) {
                        Log.err("Failed to read script file " + scriptFile + "\n" +
                                ioe.getMessage());
                        scriptPath = null;
                        scriptFile = null;
                        script = null;
                    }
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
        
        if(script == null || script.isEmpty()) {
            Log.err("Script is undefined");
            return out;
        }
        
        int x, y, w, h;
        Color color;
        int lineNumber = 1;
        String[] coords;
        Polygon p;
        String str;
        boolean customText;
        Font f;
        
        for(String l : script) {
            try {
                customText = true;
                // http://stackoverflow.com/questions/7804335/split-string-on-spaces-in-java-except-if-between-quotes-i-e-treat-hello-wor
                List<String> tokens = new ArrayList<>();
                Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(l);
                while (m.find())
                    tokens.add(m.group(1).replaceAll("\"", ""));
                switch(tokens.get(0)) {
                    case "polyfill":
                    case "polyedge":
                        color = parseColor(badge, tokens.get(1));
                        p = new Polygon();
                        for(String pStr : tokens.subList(2, tokens.size())) {
                            coords = pStr.split(",");
                            x = (int)(Float.parseFloat(coords[0])/100 * (d.width-1));
                            y = (int)(Float.parseFloat(coords[1])/100 * (d.height-1));
                            p.addPoint(x, y);
                        }
                        g.setColor(color);
                        if(tokens.get(0).equals("polyfill")) {
                            g.fillPolygon(p);
                        } else {
                            g.drawPolygon(p);
                        }
                        break;
                    case "primarytext":
                        str = badge.primaryText;
                        drawText(g, badge, d, str, tokens.subList(1, tokens.size()));
                        break;
                    case "secondarytext":
                        str = badge.secondaryText;
                        drawText(g, badge, d, str, tokens.subList(1, tokens.size()));                       
                        break;
                    case "number":
                        str = String.valueOf(badge.number);
                        drawText(g, badge, d, str, tokens.subList(1, tokens.size()));
                        break;
                    case "text":
                        drawText(g, badge, d, tokens.get(1), 
                                 tokens.subList(2, tokens.size()));                        
                        break;
                    case "blit":
                        File path = new File(scriptPath.getAbsolutePath() +
                                         File.separator + tokens.get(1));
                        Log.d(1, this + ".render: blit " + path.getAbsolutePath());
                        BufferedImage img = ImageTools.get(path);
                        switch(tokens.get(2)) {
                            case "width":
                                w = (int)(Float.parseFloat(tokens.get(3))/100 * d.width);
                                h = (int)((float)w/img.getWidth()*img.getHeight());
                                break;
                            default:
                                Log.d(0, this + ".render: unknown blit scaling " + 
                                         "method \'" + tokens.get(2) + "\', " +
                                         "using height scaling");
                            case "height":
                                h = (int)(Float.parseFloat(tokens.get(3))/100 * d.height);
                                w = (int)((float)h/img.getHeight()*img.getWidth());
                        }
                        x = parsePosition(d.width, w, tokens.get(4));                        
                        y = parsePosition(d.height, h, tokens.get(5));
                        BufferedImage scaled = ImageTools.scale(img, w, h);
                        if(tokens.size() == 7) {
                            // alpha command
                            ImageTools.setAlpha(scaled, 
                                                Integer.parseInt(tokens.get(6)));
                        }
                        g.drawImage(scaled, x, y, null);
                        break;
                }
            } catch(Exception e) {
                Log.d(0, this + ".render(" + scriptFile + ":" +
                         lineNumber + "): failed to parse \'" + l + "\'\n\t" +
                        "reason: " + e.getMessage());
                if(Log.debugLevel > 0) {
                    e.printStackTrace();
                }
            }
            lineNumber++;
        }
        
        return out;
    }
        
    private void drawText(Graphics2D g, Badge badge, Dimension d,
                          String str, List<String> tokens) {        
        g.setColor(parseColor(badge, tokens.get(0)));
        Font f = new Font(tokens.get(1), 
                    (tokens.get(2).contains("bold") ? Font.BOLD : 0) |
                    (tokens.get(2).contains("italic") ? Font.ITALIC : 0),
                    initialFontSize);
        g.setFont(f);
        
        int w = (int)(Float.parseFloat(tokens.get(5))/100 * d.width);
        int h = (int)(Float.parseFloat(tokens.get(6))/100 * d.height);
        
        BufferedImage text = ImageTools.drawString(g.getFont(), str, 
                g.getColor(), h, w);
        
        int x = parsePosition(d.width, w, tokens.get(3));
        int y = parsePosition(d.height, h, tokens.get(4));
        
        g.drawImage(text,
                    x+(w-text.getWidth())/2, 
                    y+(h-text.getHeight())/2, 
                    null);
    }
    
    public static int parsePosition(int bounds, int dim, String value) {
        switch(value) {
            case "left":
            case "top":
                return 0;
            case "right":
            case "bottom":
                return bounds - dim;
            case "centered":
                return (bounds - dim) / 2;
            default:
                return (int)(Float.parseFloat(value)/100 * bounds);
        }
    }
    
    public static Color parseColor(Badge badge, String value) {
        switch(value) {
            case "bg":
                return badge.backgroundColor;
            case "textbg":
                return badge.textBackgroundColor;
            case "text":
                return badge.textColor;
            default:
                return ImageTools.parseHexColor(value);
        }
    }

    @Override
    public String getDescription() {
        return "Scriptable Renderer";
    }    
    
    @Override
    public String toString() {
        return "ScriptableRenderer";
    }
}