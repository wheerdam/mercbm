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
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
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
    private JPanel paneOutput;
    private JButton btnExit;
    
    private JPanel paneInputControls;
    private JScrollPane scrollerTblInput;
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
    
    private JPanel paneOutputPDF;
    private TextInputPane paneOutputPNG;
    private TextInputPane paneOutputJPG;
    private JPanel paneOutputHalf;
    
    private JComboBox cmbPDFPageSize;
    private JComboBox cmbPDFPageOrientation;
    private JTextField txtPDFPageMargin;
    private JTextField txtPDFBadgeSpacing;
    private JTextField txtPDFOutputFile;
    private JLabel lblPDFOutputFile;
    private JLabel lblPDFPageSize;
    private JLabel lblPDFPageMargin;
    private JLabel lblPDFBadgeSpacing;
    private JButton btnPDFBrowse;
    private JButton btnPDFSave;
    
    public void init() {
        // use default
        r0 = new ClassicMercuryBadgeRenderer();
        r1 = new MercuryCertificateRenderer();
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
        paneGlobal.add(btnExit);
        pane.add(paneGlobal, BorderLayout.PAGE_END);
        
        paneInput.setLayout(new BorderLayout());
        tblInput = new JTable();
        scrollerTblInput = new JScrollPane(tblInput);
        paneInput.add(scrollerTblInput, BorderLayout.CENTER);
        btnImport = new JButton("Import CSV");
        btnExport = new JButton("Export");
        btnAddEntry = new JButton("Add");
        btnAddEntry.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        btnAddEntry.addActionListener(e -> { addBadge(null); });
        btnEditEntry = new JButton("Edit");
        btnEditEntry.addActionListener(e -> { editBadge(); });
        btnDeleteEntry = new JButton("Delete");
        btnDeleteEntry.addActionListener(e -> { deleteInputEntry(); });
        btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> { 
            clearInputData(); 
        });
        btnImport.addActionListener(e -> { importCSV(); });
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
        cmbRenderers.addActionListener(e -> { rendererListSelectionChanged(); });
        
        paneRendererControls.add(lblRenderer);
        paneRendererControls.add(cmbRenderers);
        paneRendererControls.add(btnPreviewRender);
        
        lblImageSize = new JLabel();
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
        paneRenderer.add(paneRendererControls, BorderLayout.PAGE_END);
        paneRenderer.add(paneImageSizeControls, BorderLayout.PAGE_START);
        
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
        min = new Dimension(5, 5);
        pref = new Dimension(5, 5);
        max = new Dimension(5, Short.MAX_VALUE);
        paneOutputHalf.add(new Box.Filler(min, pref, max));
        
        paneOutputPDF = new JPanel();
        paneOutputPDF.setLayout(new BoxLayout(paneOutputPDF, BoxLayout.Y_AXIS));
        paneOutputPDF.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        
        paneOutput.add(paneOutputPDF);
        paneOutput.add(Box.createRigidArea(new Dimension(5, 5)));
        paneOutput.add(paneOutputHalf);
        
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
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel m = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
            String[] colNames = { "#", "Name", "Institution", "BG Color",
                                  "Text BG Color", "Text Color", "BG File",
                                  "BG Fit"};
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
            default:
            case 2: return Main.getRenderer();
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
        String file = GUI.browseForInputFile("Select File to Import");       
        if(file != null) {
            Progress p = ProgressDialog.create("Importing " + file);
            (new Thread(() -> {
                badges.addAll(IO.readFromCSV(p, file, 
                                            width, height, (int) dpi));
                populateInputTable();
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
    
    private void editBadge() {
        int index = tblInput.getSelectedRow();
        if(index < 0) {
            return;
        }
        addBadge(badges.get(index));
    }
    
    public void exit(int code) {
        if(GUI.confirmYesNo(this, "Exit the program?", "Exit")) {
            System.exit(code);
        }
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
