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
package org.osumercury.badgemaker;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;

/**
 *
 * @author wira
 */
public class ImageTools {
    private static Map<String, BufferedImage> cache;
    
    public static BufferedImage get(File f) throws IOException {
        return get(f.getAbsolutePath());
    }
    
    public static BufferedImage get(String path) throws IOException {
        if(cache == null) {
            cache = new HashMap<>();
        }
        if(!cache.containsKey(path)) {
            Log.d(0, "      caching " + path);
            BufferedImage img = ImageIO.read(new File(path));
            cache.put(path, img);
            return img;
        } else {
            Log.d(0, "      hit " + path);
            return cache.get(path);
        }
    }
    
    public static void invalidate(String path) {
        cache.remove(path);
    }
    
    public static BufferedImage scale(BufferedImage src, int width, int height) {
        // we're for sure don't want an image with 0 dimension
        width = width == 0 ? 1 : width;
        height = height == 0 ? 1 : height;
        BufferedImage result = Scalr.resize(src, Scalr.Method.ULTRA_QUALITY, 
                                            width, height,
                                            Scalr.OP_ANTIALIAS);
        return result;
    }
    
    public static BufferedImage fastScale(BufferedImage src,
                                          int width, int height) {
        width = width == 0 ? 1 : width;
        height = height == 0 ? 1 : height;
        BufferedImage result = Scalr.resize(src, Scalr.Method.SPEED, 
                                            width, height);
        return result;
    }
    
    public static Color parseHexColor(String hex) {
        try {
            if(hex.startsWith("#")) {
                hex = hex.substring(1, hex.length());
            }
            Color c;
            switch (hex.length()) {
                case 6:
                    c = new Color(Integer.parseInt(hex, 16));
                    break;
                case 8:
                    c = new Color((int)Long.parseLong(hex, 16), true);
                    break;
                default:
                    Log.err("Unable to parse color code: \'" + hex + "\'\n" +
                            "Defaulting to black");
                    c = Color.BLACK;
                    break;
            }
            return c;
        } catch(Exception e) {
            // just return black if we failed to parse
            Log.err("Unable to parse color code: \'" + hex + "\'\n" +
                    "Defaulting to black");
            return Color.BLACK;
        }
    }
    
    public static BufferedImage discardAlphaChannel(BufferedImage img) {
        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(),
                                                 BufferedImage.TYPE_INT_RGB);
        Graphics g = newImg.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return newImg;
    }
    
    public static BufferedImage drawString(Font f, String str, Color color,
                                           int height, int widthLimit) {
        BufferedImage dummy = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dummy.createGraphics();
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        BufferedImage unscaled = new BufferedImage(fm.stringWidth(str), 
                fm.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D gg = unscaled.createGraphics();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setColor(color);
        gg.setFont(f);
        gg.drawString(str, 0, fm.getHeight()-fm.getDescent());
        gg.dispose();
        g.dispose();
        int width = (int)((float)height/unscaled.getHeight() * unscaled.getWidth());
        if(width > widthLimit) {
            width = widthLimit;
            height = (int)((float)width/unscaled.getWidth() * unscaled.getHeight());
        }
        return ImageTools.scale(unscaled, width, height);
    }
    
    public static void setAlpha(BufferedImage img, int alphaMultiplier) {
        if(img.getType() != BufferedImage.TYPE_INT_ARGB) {
            Log.d(0, "ImageTools.setAlpha only supports TYPE_INT_ARGB");
            return;
        }
        
        int x, y, rgb, alpha;
        for(x = 0; x < img.getWidth(); x++) {
            for(y = 0; y < img.getHeight(); y++) {
                rgb = img.getRGB(x, y);
                alpha = rgb >> 24 & 0xff;
                alpha = (int)(alphaMultiplier/100.0 * alpha);
                alpha = alpha > 255 ? 255 : alpha;
                rgb = (int)((rgb & 0x00ffffffL) | (alpha << 24));
                img.setRGB(x, y, rgb);
            }
        }
    }
}
