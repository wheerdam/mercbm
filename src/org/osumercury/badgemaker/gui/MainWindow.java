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
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.osumercury.badgemaker.*;
import org.osumercury.badgemaker.renderers.*;

/**
 *
 * @author wira
 */
public class MainWindow extends JFrame {
    org.osumercury.badgemaker.Renderer r0, r1, r2;
    org.osumercury.badgemaker.Renderer currentRenderer;
    
    private static final FontSelectDialog fontSelectDialog = 
            new FontSelectDialog("Choose Font");
    
    private List<Badge> badges;
    private float width, height;
    private float dpi;
    private int units;
    
    private HelpWindow helpWindow;
    private JTabbedPane tabs;
    private JPanel paneGlobal;
    private JPanel paneInput;
    private JPanel paneRenderer;
    private JPanel paneOutput;
    private JButton btnExit;
    private JButton btnHelp;    
    
    private JPanel paneInputControls;
    private JScrollPane scrollerTblInput;
    private JTable tblInput;
    private JButton btnImport;
    private JButton btnExport;
    private JButton btnNew;
    private JButton btnEditEntry;
    private JButton btnDuplicateEntry;
    private JButton btnDeleteEntry;
    private JButton btnClear;
    
    private JPanel paneFormatHeader;
    private JPanel paneRendererControls;
    private JPanel paneImageSizeControls;
    private JPanel paneRenderPreview;
    private JLabel lblRenderer;
    private JLabel lblImageSize;
    private JLabel lblRenderPreview;
    private JButton btnChangeImageSize;
    private JButton btnPreviewRender;
    private JButton btnSaveRendererSettings;
    private JButton btnLoadRendererSettings;
    private JComboBox cmbUnits;
    private JComboBox cmbRenderers;
    private JPanel paneCurrentRendererGUIControls;
    private JScrollPane scrollCurrentRendererGUIControls;
    
    private JPanel paneOutputPDF;
    private TextInputPane paneOutputPNG;
    private TextInputPane paneOutputJPG;
    private JPanel paneOutputHalf;
    
    private ComboBoxPane panePDFPageSize;
    private ComboBoxPane panePDFPageOrientation;
    private TextInputPane panePDFPageMargin;
    private TextInputPane panePDFBadgeSpacing;
    private TextInputPane panePDFOutputFile;
    
    public void init() {
        // use default
        r0 = new ClassicMercuryBadgeRenderer();
        r1 = new MercuryCertificateRenderer();
        r2 = new ScriptableRenderer();
        currentRenderer = r0;
        width = Badge.DEFAULT_WIDTH;
        height = Badge.DEFAULT_PROPORTION * Badge.DEFAULT_WIDTH;
        dpi = Badge.DEFAULT_RESOLUTION;
        units = IO.UNIT_INCHES;
        Dimension min, pref, max;
        badges = new ArrayList<>();
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        tabs = new JTabbedPane();
        paneGlobal = new JPanel();
        paneInput = new JPanel();
        paneInput.setName("Input");
        paneRenderer = new JPanel();
        paneRenderer.setName("Format");
        paneOutput = new JPanel();
        paneOutput.setName("Output");
        tabs.add(paneInput);
        tabs.add(paneRenderer);
        tabs.add(paneOutput);
        Container pane = this.getContentPane();
        pane.add(tabs, BorderLayout.CENTER);
        btnExit = new JButton("Exit");
        btnHelp = new JButton("Help");
        helpWindow = new HelpWindow();
        helpWindow.init();
        btnHelp.addActionListener((e) -> { showAboutWindow(); });
        btnExit.addActionListener((e) -> { exit(0); });
        paneGlobal.add(btnHelp);
        paneGlobal.add(btnExit);
        pane.add(paneGlobal, BorderLayout.PAGE_END);
        
        paneInput.setLayout(new BorderLayout());
        tblInput = new JTable();
        scrollerTblInput = new JScrollPane(tblInput);
        paneInput.add(scrollerTblInput, BorderLayout.CENTER);
        btnImport = new JButton("Load Data");
        btnExport = new JButton("Save Data");
        btnNew = new JButton("New");
        btnNew.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        btnNew.addActionListener(e -> { addBadge(null); });
        btnNew.setToolTipText("CTRL+N");
        btnEditEntry = new JButton("Edit");
        btnEditEntry.addActionListener(e -> { editBadge(); });
        btnEditEntry.setToolTipText("CTRL+E");
        btnDuplicateEntry = new JButton("Duplicate");
        btnDuplicateEntry.addActionListener(e -> { duplicateBadge(); });
        btnDuplicateEntry.setToolTipText("CTRL+D");
        btnDeleteEntry = new JButton("Delete");
        btnDeleteEntry.addActionListener(e -> { deleteInputEntry(); });
        btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> { 
            clearInputData(); 
        });
        btnImport.addActionListener(e -> { importCSV(); });
        btnExport.addActionListener(e -> { exportCSV(); });
        paneInputControls = new JPanel(new FlowLayout(FlowLayout.LEADING));
        paneInputControls.add(btnNew);
        paneInputControls.add(btnEditEntry);
        paneInputControls.add(btnDuplicateEntry);
        paneInputControls.add(Box.createRigidArea(new Dimension(5, 0)));
        paneInputControls.add(btnExport);
        paneInputControls.add(btnImport);
        paneInputControls.add(Box.createRigidArea(new Dimension(5, 0)));
        paneInputControls.add(btnDeleteEntry);
        paneInputControls.add(btnClear);
        paneInput.add(paneInputControls, BorderLayout.PAGE_START);
        
        paneRenderer.setLayout(new BorderLayout());
        paneCurrentRendererGUIControls = r0.getRendererGUIControls();
        scrollCurrentRendererGUIControls = new JScrollPane(paneCurrentRendererGUIControls);
        paneRendererControls = new JPanel(new FlowLayout(FlowLayout.LEADING));
        lblRenderer = new JLabel("Renderer: ");
        cmbRenderers = new JComboBox();
        btnPreviewRender = new JButton("Preview");
        btnSaveRendererSettings = new JButton("Save Settings");
        btnLoadRendererSettings = new JButton("Load Settings");
        btnPreviewRender.addActionListener(e -> { previewRender(); });
        btnPreviewRender.setMaximumSize(new Dimension(100, 60));
        lblRenderPreview = new JLabel("Click to see a preview");
        lblRenderPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblRenderPreview.setVerticalAlignment(SwingConstants.TOP);
        lblRenderPreview.setMinimumSize(new Dimension(380, 5));
        lblRenderPreview.setPreferredSize(new Dimension(380, 5));
        lblRenderPreview.setMaximumSize(new Dimension(380, Short.MAX_VALUE));
        paneRenderPreview = new JPanel();
        paneRenderPreview.setLayout(new BoxLayout(paneRenderPreview, 
                                                  BoxLayout.Y_AXIS));
        btnPreviewRender.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblRenderPreview.setAlignmentX(Component.CENTER_ALIGNMENT);
        paneRenderPreview.add(btnPreviewRender);
        paneRenderPreview.add(Box.createRigidArea(new Dimension(5, 5)));
        paneRenderPreview.add(lblRenderPreview);
        paneRenderer.add(scrollCurrentRendererGUIControls, BorderLayout.CENTER);
        paneRenderer.add(paneRenderPreview, BorderLayout.LINE_END);
        cmbRenderers.addItem(r0.getDescription());
        cmbRenderers.addItem(r1.getDescription());
        cmbRenderers.addItem(r2.getDescription());
        if(!(Main.getRenderer() instanceof ClassicMercuryBadgeRenderer ||
             Main.getRenderer() instanceof MercuryCertificateRenderer ||
             Main.getRenderer() instanceof ScriptableRenderer)) {
            cmbRenderers.addItem(Main.getRenderer().getDescription());
        }
        cmbRenderers.addActionListener(e -> { rendererListSelectionChanged(); });
        btnSaveRendererSettings.addActionListener(e -> {
            saveRendererSettings();
        });
        btnLoadRendererSettings.addActionListener(e -> {
            loadRendererSettings();
        });
        paneRendererControls.add(lblRenderer);
        paneRendererControls.add(cmbRenderers);
        paneRendererControls.add(btnSaveRendererSettings);
        paneRendererControls.add(btnLoadRendererSettings);
        // paneRendererControls.add(btnPreviewRender);
        
        lblImageSize = new JLabel();
        lblImageSize.setToolTipText("This is the size of the individual " +
                "badge at the specified units");
        lblImageSize.setMinimumSize(new Dimension(120, 5));
        lblImageSize.setMaximumSize(new Dimension(120, Short.MAX_VALUE));
        updateImageSizeLabel();
        btnChangeImageSize = new JButton("Change Size");
        btnChangeImageSize.addActionListener(e -> { changeSize(); });
        cmbUnits = new JComboBox();
        cmbUnits.addItem("Inches");
        cmbUnits.addItem("Millimeters");
        cmbUnits.addActionListener((ActionEvent e) -> { unitsChanged(); });
        paneImageSizeControls = new JPanel(new FlowLayout(FlowLayout.LEADING));
        paneImageSizeControls.add(cmbUnits);
        paneImageSizeControls.add(Box.createRigidArea(new Dimension(5, 0)));
        paneImageSizeControls.add(btnChangeImageSize);
        paneImageSizeControls.add(Box.createRigidArea(new Dimension(5, 0)));
        paneImageSizeControls.add(lblImageSize);
        paneFormatHeader = new JPanel();
        paneFormatHeader.setLayout(new BoxLayout(paneFormatHeader, 
                                   BoxLayout.Y_AXIS));
        paneFormatHeader.add(paneRendererControls);
        paneFormatHeader.add(paneImageSizeControls);
        paneFormatHeader.add(Box.createRigidArea(new Dimension(5, 0)));
        paneRenderer.add(paneFormatHeader, BorderLayout.PAGE_START);
        
        paneOutput.setLayout(new BoxLayout(paneOutput, BoxLayout.Y_AXIS));
        paneOutputHalf = new JPanel();
        paneOutputHalf.setLayout(new BoxLayout(paneOutputHalf, 
                                 BoxLayout.Y_AXIS));
        paneOutputPNG = new TextInputPane("PNG Output Directory: ", 200,
                                          "Browse", "Save");
        paneOutputJPG = new TextInputPane("JPG Output Directory: ", 200,
                                          "Browse", "Save");
        paneOutputPNG.addAction(0, e -> {
            String path = GUI.browseForDirectory();
            if(path != null) {
                paneOutputPNG.setText(path);
            }
        });
        paneOutputPNG.addAction(1, e -> {
            savePNG(paneOutputPNG.getText());
        });
        paneOutputPNG.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        paneOutputJPG.addAction(0, e -> {
            String path = GUI.browseForDirectory();
            if(path != null) {
                paneOutputJPG.setText(path);
            }
        });
        paneOutputJPG.addAction(1, (ActionEvent e) -> {
            saveJPG(paneOutputJPG.getText());
        });
        paneOutputJPG.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        
        paneOutputHalf.add(paneOutputPNG);
        paneOutputHalf.add(Box.createRigidArea(new Dimension(5, 5)));
        paneOutputHalf.add(paneOutputJPG);
        paneOutputHalf.setMaximumSize(new Dimension(Short.MAX_VALUE, 250));
        
        paneOutputPDF = new JPanel();
        paneOutputPDF.setLayout(new BoxLayout(paneOutputPDF, BoxLayout.Y_AXIS));
        paneOutputPDF.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        String[] pageSizes = { "LETTER", "A4", "LEGAL", "A0", "A1", "A2", "A3",
                               "A5", "A6" };
        panePDFPageSize = new ComboBoxPane("PDF Page Size: ", pageSizes, 200);
        String[] orientations = { "Portrait", "Landscape" };
        panePDFPageOrientation = new ComboBoxPane("Page Orientation: ", orientations, 200);
        panePDFPageMargin = new TextInputPane("Margins: ", 200);
        panePDFBadgeSpacing = new TextInputPane("Badge Spacing: ", 200);
        panePDFOutputFile = new TextInputPane("PDF Output File: ", 200,
                                           "Browse", "Save");
        panePDFPageSize.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        panePDFPageOrientation.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        panePDFPageMargin.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        panePDFBadgeSpacing.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        panePDFOutputFile.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        panePDFPageMargin.setText("0.25");
        panePDFBadgeSpacing.setText("0.05");
        panePDFOutputFile.addAction(0, e -> {
            String path = GUI.browseForFile("Specify Output PDF File");
            if(path != null) {
                panePDFOutputFile.setText(path);
            }
        });
        panePDFOutputFile.addAction(1, e -> {
            savePDF(panePDFOutputFile.getText());
        });
        
        paneOutputPDF.setToolTipText("Output a PDF document of the badges. " +
                "Multiple badges will be placed on a page if they fit");
        paneOutputPDF.add(Box.createRigidArea(new Dimension(5, 5)));
        paneOutputPDF.add(panePDFPageSize);
        paneOutputPDF.add(Box.createRigidArea(new Dimension(5, 5)));
        paneOutputPDF.add(panePDFPageOrientation);
        paneOutputPDF.add(Box.createRigidArea(new Dimension(5, 5)));
        paneOutputPDF.add(panePDFPageMargin);
        paneOutputPDF.add(Box.createRigidArea(new Dimension(5, 5)));
        paneOutputPDF.add(panePDFBadgeSpacing);
        paneOutputPDF.add(Box.createRigidArea(new Dimension(5, 5)));
        paneOutputPDF.add(panePDFOutputFile);
        
        paneOutput.add(paneOutputPDF);
        paneOutput.add(Box.createRigidArea(new Dimension(5, 10)));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        // separator.setPreferredSize(new Dimension(Short.MAX_VALUE, 1));
        separator.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        paneOutput.add(separator);
        paneOutput.add(Box.createRigidArea(new Dimension(5, 10)));
        paneOutput.add(paneOutputHalf);
        
        min = new Dimension(5, 5);
        pref = new Dimension(5, 5);
        max = new Dimension(5, Short.MAX_VALUE);
        paneOutput.add(new Box.Filler(min, pref, max));
                
        GUI.attachKeyShortcut(this, KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK,
                new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tabs.getSelectedIndex() == 0) {
                    addBadge(null);
                }
            }
        });

        GUI.attachKeyShortcut(this, KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK,
                new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tabs.getSelectedIndex() == 0) {
                    editBadge();
                }
            }
        });
        
        GUI.attachKeyShortcut(this, KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK,
                new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tabs.getSelectedIndex() == 0) {
                    duplicateBadge();
                }
            }
        });
        
        GUI.attachKeyShortcut(this, KeyEvent.VK_1, InputEvent.CTRL_DOWN_MASK,
                new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabs.setSelectedIndex(0);
            }
        });
        
        GUI.attachKeyShortcut(this, KeyEvent.VK_2, InputEvent.CTRL_DOWN_MASK,
                new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabs.setSelectedIndex(1);
            }
        });
        
        GUI.attachKeyShortcut(this, KeyEvent.VK_3, InputEvent.CTRL_DOWN_MASK,
                new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabs.setSelectedIndex(2);
            }
        });
        
        pack();
        setSize(960, 600);
        setTitle("Mercury Badge Maker");
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { 
                exit(0);
            }
        });
    }
    
    public void populateInputTable() {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel m = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
            String[] colNames = { "#", "Name", "Institution", "BG Color",
                                  "Text BG Color", "Text Color", "Picture",
                                  "Picture Fit"};
            m.setColumnIdentifiers(colNames);
            for(Badge badge : badges) {
                Object[] row = new Object[8];
                row[0] = String.valueOf(badge.number);
                row[1] = badge.primaryText;
                row[2] = badge.secondaryText;
                row[3] = String.format("%06x", badge.backgroundColor.getRGB());
                row[4] = String.format("%06x", badge.textBackgroundColor.getRGB());
                row[5] = String.format("%06x", badge.textColor.getRGB());
                row[6] = badge.background;
                row[7] = badge.getBackgroundScaling();
                m.addRow(row);
            }
            tblInput.setModel(m);
            tblInput.getColumnModel().getColumn(0).setPreferredWidth(35);
            tblInput.getColumnModel().getColumn(1).setPreferredWidth(200);
            tblInput.getColumnModel().getColumn(2).setPreferredWidth(200);
            TableColumn col;
            col = tblInput.getColumnModel().getColumn(3);
            col.setCellRenderer(new ColorColumnCellRenderer());
            col = tblInput.getColumnModel().getColumn(4);
            col.setCellRenderer(new ColorColumnCellRenderer());
            col = tblInput.getColumnModel().getColumn(5);
            col.setCellRenderer(new ColorColumnCellRenderer());
            col = tblInput.getColumnModel().getColumn(6);
            col.setCellRenderer(new BGColumnCellRenderer());
            col = tblInput.getColumnModel().getColumn(7);
            col.setCellRenderer(new FitColumnCellRenderer());
            tblInput.validate();
        });
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
    
    public void applySize() {
        System.out.println("Applying size to " + badges.size() + " badges");
        for(Badge badge : badges) {
            badge.setWidth(width);
            badge.setProportion(height / width);
            badge.setResolution((int) dpi);
        }
    }
    
    public org.osumercury.badgemaker.Renderer getCurrentRenderer() {
        switch(cmbRenderers.getSelectedIndex()) {
            case 0: return r0;
            case 1: return r1;
            case 2: return r2;
            default:
                return Main.getRenderer();
        }
    }
    
    private void savePDF(String path) {
        // validate inputs
        try {
            PDRectangle ps = null;
            switch(panePDFPageSize.getSelectedIndex()) {
                case 0:
                    ps = PDRectangle.LETTER; break;
                case 1:
                    ps = PDRectangle.A4; break;
                case 2:
                    ps = PDRectangle.LEGAL; break;
                case 3:
                    ps = PDRectangle.A0; break;
                case 4:
                    ps = PDRectangle.A1; break;
                case 5:
                    ps = PDRectangle.A2; break;
                case 6:
                    ps = PDRectangle.A3; break;
                case 7:
                    ps = PDRectangle.A5; break;
                case 8:
                    ps = PDRectangle.A6; break;
            }
            final PDRectangle pageSize = ps;
            final boolean landscape = panePDFPageOrientation.getSelectedIndex() == 1;
            final float margin = Float.parseFloat(panePDFPageMargin.getText());
            final float spacing = Float.parseFloat(panePDFBadgeSpacing.getText());

            applySize();
            (new Thread(() -> {
                IO.generatePDF(currentRenderer,
                               ProgressDialog.create("Saving PDF"), 
                               pageSize, margin, spacing, units, 
                               landscape, true, badges,
                               new File(path));
            })).start();
        } catch(Exception e) {
            Log.err("Failed to generate PDF:\n" + e);
        }
    }
    
    private void savePNG(String path) {
        applySize();
        (new Thread(() -> {
            IO.savePNG(getCurrentRenderer(), 
                       ProgressDialog.create("Saving PNG"),
                       badges, path);
        })).start();
    }
    
    private void saveJPG(String path) {
        applySize();
        (new Thread(() -> {
            IO.saveJPG(getCurrentRenderer(), 
                       ProgressDialog.create("Saving JPG"),
                       badges, path);
        })).start();
    }
    
    private void importCSV() {
        String file = GUI.browseForFile("Select File to Import");       
        if(file != null) {
            Progress p = ProgressDialog.create("Importing " + file);
            (new Thread(() -> {
                badges.addAll(IO.readFromCSV(p, file, 
                                            width, height, (int) dpi));
                populateInputTable();
            })).start();
        }
    }
    
    private void exportCSV() {
        String file = GUI.browseForFile("Select File to Export Data");       
        if(file != null) {
            Progress p = ProgressDialog.create("Exporting " + file);
            (new Thread(() -> {
                IO.saveCSV(p, badges, file);
            })).start();
        }
    }
    
    private void deleteInputEntry() {
        int[] rows = tblInput.getSelectedRows();
        if(rows.length == 0) {
            return;
        }
        
        if(!GUI.confirmYesNo(this, "Are you sure you want to delete the " +
                             "selected entries?", "Delete Confirmation")) {
            return;
        }
        
        for(int i = rows.length - 1; i >= 0; i--) {
            badges.remove(rows[i]);
        }
        populateInputTable();
    }
    
    private void clearInputData() {
        if(!GUI.confirmYesNo(this, "Are you sure you want to clear ALL the " +
                             "entries?", "Clear Confirmation")) {
            return;
        }
        
        badges.clear();
        populateInputTable();
    }
    
    private void rendererListSelectionChanged() {
        paneRenderer.removeAll();
        paneFormatHeader.removeAll();
        switch(cmbRenderers.getSelectedIndex()) {
            case 0:
                currentRenderer = r0;
                break;
            case 1:
                currentRenderer = r1;
                break;
            case 2:
                currentRenderer = r2;
                break;
            case 3:
                currentRenderer = Main.getRenderer();
                break;
        }
        paneCurrentRendererGUIControls = currentRenderer.getRendererGUIControls();
        scrollCurrentRendererGUIControls = new JScrollPane(paneCurrentRendererGUIControls);
        paneRenderer.add(scrollCurrentRendererGUIControls, BorderLayout.CENTER);
        paneRenderer.add(paneRenderPreview, BorderLayout.LINE_END);
        paneFormatHeader.add(paneRendererControls);
        paneFormatHeader.add(paneImageSizeControls);
        paneFormatHeader.add(Box.createRigidArea(new Dimension(5, 0)));
        paneRenderer.add(paneFormatHeader, BorderLayout.PAGE_START);
        paneRenderer.validate();
    }
    
    private void changeSize() {
        float[] ret = ChangeSizeForm.create(this, width, height, dpi);
        if(ret != null) {
            width = ret[0];
            height = ret[1];
            dpi = ret[2];
            updateImageSizeLabel();
        }
    }
    
    private void unitsChanged() {
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
    }
    
    private void addBadge(Badge oldValues) {
        int index = tblInput.getSelectedRow();
        Badge badge = BadgeDataEditorForm.create(this, oldValues);
        if(badge != null) {
            if(oldValues != null && index >= 0) {
                badges.remove(index);
                badges.add(index, badge);
            } else {
                badges.add(badge);
            }
            populateInputTable();
        }
    }
    
    public static FontSelectDialog getFontSelectDialog() {
        return fontSelectDialog;
    }
    
    private void editBadge() {
        int index = tblInput.getSelectedRow();
        if(index < 0) {
            return;
        }
        addBadge(badges.get(index));
    }
    
    private void duplicateBadge() {
        int[] rows = tblInput.getSelectedRows();
        if(rows.length == 0) {
            return;
        }
        
        for(int i = rows.length - 1; i >= 0; i--) {
            badges.add(rows[i], badges.get(rows[i]));
        }
        populateInputTable();
    }
    
    public void exit(int code) {
        if(GUI.confirmYesNo(this, "Exit the program?", "Exit")) {
            System.exit(code);
        }
    }

    private void previewRender() {
        Badge preview = new Badge(47, "Full Name", "Institution", null,
                                  "ffffff", "ff7300", "ffffff");
        preview.setWidth(width);
        preview.setProportion(height / width);
        preview.setResolution((int) dpi);
        lblRenderPreview.setIcon(null);
        lblRenderPreview.setText("Rendering Preview...");
        (new Thread(() -> {
            BufferedImage scaledPreview = ImageTools.scale(
                                          preview.getImage(currentRenderer), 
                                          360, 360);
            int w = scaledPreview.getWidth();
            int h = scaledPreview.getHeight();
            BufferedImage previewImage = new BufferedImage(w+4, h+4,
                                         BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = previewImage.createGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, w+4, h+4);
            g.setColor(Color.WHITE);
            g.fillRect(1, 1, w+2, h+2);
            g.drawImage(scaledPreview, 2, 2, null);
            g.dispose();
            Icon icon = new ImageIcon(previewImage);
            SwingUtilities.invokeLater(() -> {
                lblRenderPreview.setText("");
                lblRenderPreview.setIcon(icon);        
            });
        })).start();
    }
    
    private void saveRendererSettings() {
        String file = GUI.browseForFile("Save Settings");
        if(file != null) {
            float[] sizes = new float[5];
            sizes[0] = width;
            sizes[1] = height;
            sizes[2] = dpi;
            sizes[3] = cmbRenderers.getSelectedIndex();
            sizes[4] = cmbUnits.getSelectedIndex();
            IO.saveRendererSettings(currentRenderer, sizes, file);
        }
    }
    
    private void loadRendererSettings() {
        String file = GUI.browseForFile("Load Settings");
        if(file != null) {
            float[] sizes = IO.loadRendererSettings(file, r0, r1, Main.getRenderer());
            if(sizes != null) {
                width = sizes[0];
                height = sizes[1];
                dpi = sizes[2];
                cmbRenderers.setSelectedIndex((int) sizes[3]);
                units = (int) sizes[4];
                cmbUnits.setSelectedIndex((int) sizes[4]);
                updateImageSizeLabel();
            }
            rendererListSelectionChanged();
        }
    }
    
    public org.osumercury.badgemaker.Renderer getRenderer(int index) {
        switch(index) {
            default:
            case 0:
                return r0;
            case 1:
                return r1;
            case 2:
                return Main.getRenderer();
        }
    }
    
    private void showAboutWindow() {
        helpWindow.display(this);
    }
    
    class ColorColumnCellRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, 
                                                       Object value, 
                                                       boolean isSelected, 
                                                       boolean hasFocus, 
                                                       int row, int column) {
            Color c = ImageTools.parseHexColor((String) value);
            
            this.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            this.setHorizontalAlignment(SwingConstants.CENTER);
            setText(String.format("%06x", c.getRGB() & 0xffffffL).toUpperCase());
            setOpaque(true);
            if(c.getRed() + c.getGreen() + c.getBlue() < 200) {
                setForeground(Color.WHITE);
            } else {
                setForeground(Color.BLACK);
            }
            setBackground(c);
            return this;
        }        
    }
    
    class BGColumnCellRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, 
                                                       Object value, 
                                                       boolean isSelected, 
                                                       boolean hasFocus, 
                                                       int row, int column) {
            this.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));            
            this.setHorizontalAlignment(SwingConstants.CENTER);
            if(value != null) {
                setText("YES");
            } else {
                setText("");
            }
            return this;
        }        
    }
    
    class FitColumnCellRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, 
                                                       Object value, 
                                                       boolean isSelected, 
                                                       boolean hasFocus, 
                                                       int row, int column) {
            this.setHorizontalAlignment(SwingConstants.CENTER);
            this.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
            int scaling = (Integer) value;
            switch(scaling) {
                case Badge.BACKGROUND_FIT_WIDTH:
                    setText("width");
                    break;
                case Badge.BACKGROUND_FIT_HEIGHT:
                    setText("height");
                    break;
                case Badge.BACKGROUND_FILL:
                    setText("fill");
                    break;
            }
            return this;
        }        
    }
}
