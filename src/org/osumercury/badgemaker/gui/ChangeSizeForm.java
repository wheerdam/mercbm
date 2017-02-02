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
import java.awt.event.*;
import javax.swing.*;
import org.osumercury.badgemaker.Log;

/**
 *
 * @author wira
 */
public class ChangeSizeForm extends JDialog {
    private float[] output;
    private TextInputPane paneW, paneH, paneRes;
    
    public static float[] create(JFrame parent, float w, float h, float dpi) {
        ChangeSizeForm form = new ChangeSizeForm();
        form.init(parent, w, h, dpi);
        return form.getOutput();
    }
    
    public void init(JFrame parent, float w, float h, float dpi) {
        Container pane = this.getContentPane();
        output = new float[3];
        output[0] = w;
        output[1] = h;
        output[2] = dpi;
        paneW = new TextInputPane("Width: ", 100);
        paneH = new TextInputPane("Height: ", 100);
        paneRes = new TextInputPane("Resolution: ", 100);
        paneW.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        paneH.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        paneRes.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        paneW.setText(String.valueOf(w));
        paneH.setText(String.valueOf(h));
        paneRes.setText(String.valueOf(dpi));
        JPanel paneInputs = new JPanel();
        paneInputs.setLayout(new BoxLayout(paneInputs, BoxLayout.Y_AXIS));
        paneInputs.add(paneW);
        paneInputs.add(paneH);
        paneInputs.add(paneRes);
        paneInputs.add(Box.createRigidArea(new Dimension(300, 5)));
        pane.add(paneInputs, BorderLayout.CENTER);
        JButton btnOK = new JButton("OK");
        JButton btnCancel = new JButton("Cancel");
        btnOK.addActionListener(e -> {
            if(apply()) {
                dispose();
            }
        });
        btnCancel.addActionListener(e -> {
            dispose();
        });
        JPanel paneGlobalControls = new JPanel();
        paneGlobalControls.add(btnOK);
        paneGlobalControls.add(btnCancel);
        pane.add(paneGlobalControls, BorderLayout.PAGE_END);
        
        pack();
        setTitle("Change Size");
        setLocationRelativeTo(parent);
        setResizable(false);
        setModal(true);
        setVisible(true);
    }
    
    private boolean apply() {
        float newW, newH, newRes;
        try {
            newW = Float.parseFloat(paneW.getText());
            newH = Float.parseFloat(paneH.getText());
            newRes = Float.parseFloat(paneRes.getText());
            output[0] = newW;
            output[1] = newH;
            output[2] = newRes;
        } catch(NumberFormatException nfe) {
            Log.err("Failed to parse value: " + nfe);
            return false;
        }
        return true;
    }
    
    public float[] getOutput() {
        return output;
    }
}
