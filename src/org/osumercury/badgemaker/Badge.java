/*
    Copyright 2016-2017 Wira Mulia

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
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wira
 */
public class Badge {
    private BufferedImage unscaledImage;
    private final List<String> extraData;
    public float width;
    public float proportion;
    public float resolution;
    public final String primaryText;
    public final String secondaryText;
    public final int number;
    public final BufferedImage background;
    public final Color backgroundColor;
    public final Color textBackgroundColor;
    public final Color textColor;
    private int backgroundScaling;
    private int backgroundVerticalPosition;
    
    public static final int BACKGROUND_FIT_WIDTH = 0;
    public static final int BACKGROUND_FIT_HEIGHT = 1;
    public static final int BACKGROUND_FILL = 2;
    
    public static final int BACKGROUND_MIDDLE = 0;
    public static final int BACKGROUND_BOTTOM = 1;
    public static final int BACKGROUND_TOP = 2;
    
    public static final float DEFAULT_WIDTH = 2.5f;
    public static final float DEFAULT_PROPORTION = 1.25f;
    public static final int DEFAULT_RESOLUTION = 300;
    
    public Badge(int number, String name, String secondary,
                 BufferedImage background, String backgroundColor,
                 String textBackgroundColor, String textColor) {
        this.number = number;
        this.primaryText = name;
        this.secondaryText = secondary;
        this.background = background;
        this.backgroundColor = ImageTools.parseHexColor(backgroundColor);
        this.textBackgroundColor = ImageTools.parseHexColor(textBackgroundColor);
        this.textColor = ImageTools.parseHexColor(textColor);
        backgroundScaling = BACKGROUND_FIT_WIDTH;
        width = DEFAULT_WIDTH;
        proportion = DEFAULT_PROPORTION;
        resolution = DEFAULT_RESOLUTION;
        extraData = new ArrayList<>();
    }
    
    public void setProportion(float f) {
        this.proportion = f;
        // reset image
        unscaledImage = null;
    }
    
    public void setResolution(int v) {
        this.resolution = v;
        // reset image
        unscaledImage = null;
    }
    
    public void setWidth(float f) {
        this.width = f;
        // reset image
        unscaledImage = null;
    }    
    
    public Dimension getPixelDimension() {
        return new Dimension((int)(width*resolution),
                (int)(proportion*width*resolution));
    }
    
    public void setBackgroundFit(int v) {
        backgroundScaling = v;
    }    
    
    public void setBackgroundVerticalPosition(int v) {
        backgroundVerticalPosition = v;
    }
    
    public void addExtraData(String data) {
        extraData.add(data);
    }
    
    public List<String> getExtraData() {
        return extraData;
    }
    
    public int getBackgroundScaling() {
        return backgroundScaling;
    }
    
    public int getBackgroundVerticalPosition() {
        return backgroundVerticalPosition;
    }
    
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return proportion * width;
    }
    
    public float getResolution() {
        return resolution;
    }
    
    public void render(Renderer r) {
        unscaledImage = r.render(this);
    }
    
    public BufferedImage getImage(Renderer r) {
        if(unscaledImage == null) {
            render(r);
        }
        return unscaledImage;
    }
}
