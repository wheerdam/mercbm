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

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author wira
 */
public class OptionsPane extends JPanel {
    private final JLabel label;
    private final JToggleButton[] checkboxes;
    
    public OptionsPane(String labelText, int labelWidth,
                       String...checkboxTitles) {
   
        this.setLayout(new GridBagLayout());
        checkboxes = new JToggleButton[checkboxTitles.length];
        label = new JLabel(labelText);
        GridBagConstraints c = new GridBagConstraints();
        Dimension max;
        int i;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 5, 0, 5);
        max = new Dimension(labelWidth, 25);
        label.setPreferredSize(max);
        label.setMaximumSize(max);
        add(label, c);
        c.weighty = 1.0;
        c.weightx = 0.0;
        for(i = 0; i < checkboxes.length; i++) {
            c.gridx = i + 1;
            checkboxes[i] = new JToggleButton(checkboxTitles[i]);
            checkboxes[i].setPreferredSize(max);
            checkboxes[i].setMaximumSize(max);
            add(checkboxes[i], c);
        }
        c.weightx = 1.0;
        c.gridx++;
        add(Box.createHorizontalBox(), c);
    }
    
    public boolean getValue(int i) {
        return checkboxes[i].isSelected();
    }
    
    public void setValue(int i, boolean b) {
        checkboxes[i].setSelected(b);
    }
    
    public void addAction(int i, ActionListener a) {
        checkboxes[i].addActionListener(a);
    }
}

