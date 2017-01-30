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

/**
 *
 * @author wira
 */
public class MainWindow extends JFrame {
    private JCheckBox chkPDFOutput;
    private JCheckBox chkPNGOutput;
    private JCheckBox chkJPGOutput;
    private JButton btnGenerate;
    private JButton btnExit;
    
    private JComboBox cmbPDFPageSize;
    private JTextField txtPDFPageMargin;
    private JTextField txtPDFBadgeSpacing;
    private JLabel lblPDFPageSize;
    private JLabel lblPDFPageMargin;
    private JLabel lblPDFBadgeSpacing;
    private JButton btnPDFBrowse;
    
}
