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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author wira
 */
public class ComboBoxPane extends JPanel {
    private final JComboBox<String> combo;
    private final JLabel label;
    private final JButton[] buttons;
    
    public ComboBoxPane(String labelText, String[] items, int labelWidth,
                        String...buttonTitles) {
        this.setLayout(new GridBagLayout());
        buttons = new JButton[buttonTitles.length];
        label = new JLabel(labelText);
        combo = new JComboBox();
        for(String item : items) {
            combo.addItem(item);
        }
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
        c.gridx = 1;
        c.weightx = 1.0;
        add(combo, c);
        c.weighty = 1.0;
        c.weightx = 0.0;
        for(i = 0; i < buttons.length; i++) {
            c.gridx = i + 2;
            buttons[i] = new JButton(buttonTitles[i]);
            add(buttons[i], c);
        }
    }
    
    public String getText() {
        return (String) combo.getSelectedItem();
    }
    
    public int getSelectedIndex() {
        return combo.getSelectedIndex();
    }
    
    public JComboBox getComboBox() {
        return combo;
    }
    
    public void addAction(int buttonIndex, ActionListener a) {
        buttons[buttonIndex].addActionListener(a);
    }    
}
