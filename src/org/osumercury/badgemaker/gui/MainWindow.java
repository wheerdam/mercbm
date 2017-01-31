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

import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.osumercury.badgemaker.*;
import org.osumercury.badgemaker.renderers.ClassicMercuryBadgeRenderer;
import org.osumercury.badgemaker.renderers.MercuryCertificateRenderer;

/**
 *
 * @author wira
 */
public class MainWindow extends JFrame {
    org.osumercury.badgemaker.Renderer r0;
    org.osumercury.badgemaker.Renderer r1;
    private List<Badge> badges;
    private float width, height;
    private float dpi;
    private int units;
    
    private JTabbedPane tabs;
    private JPanel paneGlobal;
    private JPanel paneInput;
    private JPanel paneRenderer;
    private JPanel panePDFOutput;
    private JButton btnExit;
    
    private JPanel paneInputControls;
    private JTable tblInput;
    private JButton btnImport;
    private JButton btnExport;
    private JButton btnAddEntry;
    private JButton btnEditEntry;
    private JButton btnDeleteEntry;
    private JButton btnClear;
    
    private JButton btnPDFGenerate;
    
    private JPanel paneRendererControls;
    private JPanel paneImageSizeControls;
    private JPanel paneRenderPreview;
    private JLabel lblRenderer;
    private JLabel lblImageSize;
    private JLabel lblRenderPreview;
    private JButton btnChangeImageSize;
    private JButton btnPreviewRender;
    private JComboBox cmbUnits;
    private JComboBox cmbRenderers;
    private JPanel paneCurrentRendererGUIControls;
    
    private JComboBox cmbPDFPageSize;
    private JTextField txtPDFPageMargin;
    private JTextField txtPDFBadgeSpacing;
    private JLabel lblPDFPageSize;
    private JLabel lblPDFPageMargin;
    private JLabel lblPDFBadgeSpacing;
    private JButton btnPDFBrowse;
    
    public void init(String initialFile) {
        // use default
        r0 = new ClassicMercuryBadgeRenderer();
        r1 = new MercuryCertificateRenderer();
        width = Badge.DEFAULT_WIDTH;
        height = Badge.DEFAULT_PROPORTION * Badge.DEFAULT_WIDTH;
        dpi = Badge.DEFAULT_RESOLUTION;
        units = IO.UNIT_INCHES;
        JFrame f = this;
        badges = new ArrayList<>();
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        tabs = new JTabbedPane();
        paneGlobal = new JPanel();
        paneInput = new JPanel();
        paneInput.setName("Input");
        paneRenderer = new JPanel();
        paneRenderer.setName("Format");
        panePDFOutput = new JPanel();
        panePDFOutput.setName("PDF Output");
        tabs.add(paneInput);
        tabs.add(paneRenderer);
        tabs.add(panePDFOutput);
        Container pane = this.getContentPane();
        pane.add(tabs, BorderLayout.CENTER);
        btnExit = new JButton("Exit");
        paneGlobal.add(btnExit);
        pane.add(paneGlobal, BorderLayout.PAGE_END);
        
        paneInput.setLayout(new BorderLayout());
        tblInput = new JTable();
        paneInput.add(tblInput, BorderLayout.CENTER);
        btnImport = new JButton("Import CSV");
        btnExport = new JButton("Export CSV");
        btnAddEntry = new JButton("Add");
        btnAddEntry.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        btnEditEntry = new JButton("Edit");
        btnDeleteEntry = new JButton("Delete");
        btnClear = new JButton("Clear");
        btnImport.addActionListener((ActionEvent e) -> {
            String file = GUI.browseForInputFile();       
            if(file != null) {
                Progress p = GUI.createProgressDialog("Importing " + file);
                (new Thread(() -> {
                    badges.addAll(IO.readFromCSV(p, file, 
                                                width, height, (int) dpi));
                })).start();
            }
        });
        paneInputControls = new JPanel(new FlowLayout(FlowLayout.LEADING));
        paneInputControls.add(btnAddEntry);
        paneInputControls.add(btnEditEntry);
        paneInputControls.add(Box.createRigidArea(new Dimension(5, 0)));
        paneInputControls.add(btnImport);
        paneInputControls.add(btnExport);
        paneInputControls.add(Box.createRigidArea(new Dimension(5, 0)));
        paneInputControls.add(btnDeleteEntry);
        paneInputControls.add(btnClear);
        paneInput.add(paneInputControls, BorderLayout.PAGE_START);
        
        paneRenderer.setLayout(new BorderLayout());
        paneCurrentRendererGUIControls = r0.getRendererGUIControls();
        paneRendererControls = new JPanel(new FlowLayout(FlowLayout.LEADING));
        lblRenderer = new JLabel("Renderer: ");
        cmbRenderers = new JComboBox();
        btnPreviewRender = new JButton("Preview");
        lblRenderPreview = new JLabel("Preview");
        lblRenderPreview.setHorizontalAlignment(SwingConstants.CENTER);
        paneRenderPreview = new JPanel(new GridLayout(1, 2));
        paneRenderPreview.add(paneCurrentRendererGUIControls);
        paneRenderPreview.add(lblRenderPreview);
        paneRenderer.add(paneRenderPreview, BorderLayout.CENTER);
        cmbRenderers.addItem(r0.getDescription());
        cmbRenderers.addItem(r1.getDescription());
        if(!(Main.getRenderer() instanceof ClassicMercuryBadgeRenderer ||
             Main.getRenderer() instanceof MercuryCertificateRenderer)) {
            cmbRenderers.addItem(Main.getRenderer().getDescription());
        }
        cmbRenderers.addActionListener((ActionEvent e) -> {
            paneRenderPreview.removeAll();
            switch(cmbRenderers.getSelectedIndex()) {
                case 0:
                    paneCurrentRendererGUIControls = r0.getRendererGUIControls();
                    break;
                case 1:
                    paneCurrentRendererGUIControls = r1.getRendererGUIControls();
                    break;
                case 2:
                    paneCurrentRendererGUIControls = Main.getRenderer().getRendererGUIControls();
                    break;
            }
            paneRenderPreview.add(paneCurrentRendererGUIControls);
            paneRenderPreview.add(lblRenderPreview);
            paneRenderPreview.validate();
        });
        
        paneRendererControls.add(lblRenderer);
        paneRendererControls.add(cmbRenderers);
        paneRendererControls.add(btnPreviewRender);
        
        lblImageSize = new JLabel();
        updateImageSizeLabel();
        btnChangeImageSize = new JButton("Change Size");
        cmbUnits = new JComboBox();
        cmbUnits.addItem("Inches");
        cmbUnits.addItem("Millimeters");
        cmbUnits.addActionListener((ActionEvent e) -> {
            int oldUnits = units;
            units = cmbUnits.getSelectedIndex() == 0 ? IO.UNIT_INCHES :
                                                       IO.UNIT_MM;
            if(oldUnits == IO.UNIT_INCHES && units == IO.UNIT_MM) {
                width *= 25.4f;
                height *= 25.4f;
                dpi /= 25.4f;
            } else if(oldUnits == IO.UNIT_MM && units == IO.UNIT_INCHES) {
                width /= 25.4f;
                height /= 25.4f;
                dpi *= 25.4f;
            }
            updateImageSizeLabel();
        });
        paneImageSizeControls = new JPanel(new FlowLayout(FlowLayout.LEADING));
        paneImageSizeControls.add(cmbUnits);
        paneImageSizeControls.add(Box.createRigidArea(new Dimension(5, 0)));
        paneImageSizeControls.add(btnChangeImageSize);
         paneImageSizeControls.add(Box.createRigidArea(new Dimension(5, 0)));
        paneImageSizeControls.add(lblImageSize);
        paneRenderer.add(paneRendererControls, BorderLayout.PAGE_END);
        paneRenderer.add(paneImageSizeControls, BorderLayout.PAGE_START);
        
        
        btnExit.addActionListener((ActionEvent e) -> {
            exit(0);
        });
        
        pack();
        setSize(800, 600);
        setTitle("Mercury Badge Maker");
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { 
                exit(0);
            }
        });
        
        
    }
    
    public void populateInputTable() {
        
    }
    
    public List<Badge> getBadgeList() {
        return badges;
    }
    
    private void updateImageSizeLabel() {
        lblImageSize.setText("Size: " + 
                              String.format("%.2f", width) + " x " +
                              String.format("%.2f", height) + " at " + 
                              String.format("%.1f", dpi) + " " +
                              (units == IO.UNIT_INCHES ? "dpi" : "d/mm"));
    }   
    
    public void exit(int code) {
        if(GUI.confirmYesNo(this, "Exit the program?", "Exit")) {
            System.exit(code);
        }
    }
}
