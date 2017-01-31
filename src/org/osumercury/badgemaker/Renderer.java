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

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author wira
 */
public abstract class Renderer {
    private List<Property> properties = new ArrayList<>();
    
    public abstract BufferedImage render(Badge badge);
    
    public abstract String getDescription();
    
    protected final void addProperty(String key, int type, String description) {
        properties.add(new Property(key, type, "", description));
    }
    
    protected final void addProperty(String key, int type, String defaultvalue, 
                                     String description) {
        properties.add(new Property(key, type, defaultvalue, description));
    }
    
    public JPanel getRendererGUIControls() {
        JPanel pane = new JPanel();
        JLabel lblNoGUI = new JLabel("This renderer does not have GUI controls");
        lblNoGUI.setHorizontalAlignment(SwingConstants.CENTER);
        pane.setLayout(new BorderLayout());
        pane.add(lblNoGUI, BorderLayout.CENTER);
        return pane;
    }
    
    public void setProperty(String key, String value) { }
    
    public List<Property> getValidProperties() {
        return properties;
    }
    
    public class Property {
        public static final int INTEGER = 0;
        public static final int FLOAT = 1;
        public static final int STRING = 2;
        
        String key;
        int type;
        String description;
        String defaultValue;
               
        public Property(String key, int type, String defaultValue, 
                        String description) {
            this.key = key;
            this.type = type;
            this.description = description;
            this.defaultValue = defaultValue;
        }
        
        public String getKey() {
            return key;
        }
        
        public int getType() {
            return type;
        }
        
        public String getDefaultValue() {
            return defaultValue;
        }
        
        public String getDescription() {
            return description;
        }
    }
}