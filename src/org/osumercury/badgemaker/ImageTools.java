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
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import org.imgscalr.Scalr;

/**
 *
 * @author wira
 */
public class ImageTools {
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
            Color c = null;
            if(hex.length() == 6) {
                c = new Color(Integer.parseInt(hex, 16));     
            } else if(hex.length() == 8) {
                c = new Color((int)Long.parseLong(hex, 16), true);
            } else {
                c = Color.BLACK;
            }
            return c;
        } catch(Exception e) {
            // just return black if we failed to parse
            System.err.println(e);
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
}
