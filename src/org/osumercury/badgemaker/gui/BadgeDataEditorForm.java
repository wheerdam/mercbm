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

import javax.swing.*;
import java.awt.*;
import org.osumercury.badgemaker.*;

/**
 *
 * @author wira
 */
public class BadgeDataEditorForm extends JDialog {
    private Badge badge;
    
    private JPanel paneMain;
    private JPanel paneGlobalControls;
    
    private JButton btnOK;
    private JButton btnCancel;
    
    private JPanel paneText;
    private TextInputPane paneNumber;
    private TextInputPane paneName;
    private TextInputPane paneInstitution;
    private JPanel paneColors;
    private JPanel paneBackground;
    
    private JLabel lblNumber;
    private JLabel lblName;
    private JLabel lblInstitution;
    private JLabel lblBackgroundColor;
    private JLabel lblTextBackgroundColor;
    private JLabel lblTextColor;
    private JLabel lblBackground;
    
    private JTextField txtNumber;
    private JTextField txtName;
    private JTextField txtInstitution;
    
    private JComboBox cmbBackgroundFit;
    
    public static Badge create(Badge defaultValues) {
        BadgeDataEditorForm form = new BadgeDataEditorForm();
        form.init(defaultValues);
        return form.getBadge();
    }
    
    public void init(Badge defaultValues) {
        this.badge = defaultValues;
        Dimension min, pref, max;
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        paneMain = new JPanel();
        paneMain.setLayout(new BoxLayout(paneMain, BoxLayout.Y_AXIS));
        paneNumber = new TextInputPane("Number ID: ");
        paneName = new TextInputPane("Name: ");
        paneInstitution = new TextInputPane("Institution: ");
        if(defaultValues != null) {
            paneNumber.setText(String.valueOf(defaultValues.number));
            paneName.setText(defaultValues.primaryText);
            paneInstitution.setText(defaultValues.secondaryText);
        }
        paneMain.add(paneNumber);
        paneMain.add(paneName);
        paneMain.add(paneInstitution);
        min = new Dimension(5, 5);
        pref = new Dimension(5, 5);
        max = new Dimension(5, Short.MAX_VALUE);
        paneMain.add(new Box.Filler(min, pref, max));
        paneGlobalControls = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        btnOK = new JButton("OK");
        btnCancel = new JButton("Cancel");
        paneGlobalControls.add(btnOK);
        paneGlobalControls.add(btnCancel);
        btnOK.addActionListener(e -> {
            if(apply()) {
                dispose();
            }
        });
        btnCancel.addActionListener(e -> {
            dispose();
        });
        contentPane.add(paneMain, BorderLayout.CENTER);
        contentPane.add(paneGlobalControls, BorderLayout.PAGE_END);
        pack();
        setTitle("Add / Edit Badge");
        setLocationRelativeTo(null);
        setSize(400, 300);
        setModal(true);
        setVisible(true);
    }
    
    private boolean apply() {
        
        return false;
    }
    
    public Badge getBadge() {
        return badge;
    }
}
