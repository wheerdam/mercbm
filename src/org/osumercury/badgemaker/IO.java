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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.image.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.commons.csv.*;

/**
 *
 * @author wira
 */
public class IO {
    public static final int UNIT_INCHES = 0;
    public static final int UNIT_MM = 1;
    
    public static void generatePDF(Renderer r, Progress p,
                                   PDRectangle pdPageSize,
                                   float pageMargin, float badgeSpacing,
                                   int units,
                                   boolean landscape,
                                   boolean PNG,
                                   List<Badge> badges,
                                   File output) {
        boolean mm = units == UNIT_MM;
        
        // set up our coordinate system
        // pdf uses 1/72" coodinate units, we use this to convert our internal
        // dimensions to pdf coordinates
        float margin = (mm ? 1/25.4f : 1.0f) * pageMargin * 72.0f;
        float spacing = (mm ? 1/25.4f : 1.0f) * badgeSpacing * 72.0f;
        
        if(badges.isEmpty()) {
            Log.err("No badges to output");
            if(p != null) {
                p.complete();
            }
            return;
        }
        
        PDDocument doc = new PDDocument();
        String badgeTitle;
        float offX = margin;
        float offY = margin;
        float pageW = landscape ? pdPageSize.getHeight() : pdPageSize.getWidth();
        float pageH = landscape ? pdPageSize.getWidth() : pdPageSize.getHeight();
        float limitW = pageW - 2*margin;
        float limitH = pageH - 2*margin;
        float badgeW;
        float badgeH;
        float largestH = -1;
        int badgeNum = 1;
        PDPage page = null;
        PDPageContentStream pS = null;
        Log.d(0, "    Generating PDF model (" + 
                           String.format("%.2f", pageW) + " x " +
                           String.format("%.2f", pageH) + ") " +
                           "- " + (PNG ? "lossless images" : "JPEG images"));
        for(Badge badge : badges) {
            badgeTitle = badge.number + "-" + badge.primaryText;
            badgeW = (mm ? 1/25.4f : 1.0f) * badge.getWidth() * 72.0f;
            badgeH = (mm ? 1/25.4f : 1.0f) * badge.getHeight() * 72.0f;
            Log.d(0, "      Placing " +
                               Main.pad(25, badgeTitle) + " (" +
                               String.format("%.2f", badgeW) + " x " +
                               String.format("%.2f", badgeH) + ")");
            if(p != null) {
                if(p.cancel) {
                    try {
                        if(pS != null) {
                            pS.close();
                        }
                        doc.close();
                    } catch(IOException ioe) {
                        Log.err("Failed to close document "
                                           + ioe);
                    }
                    p.complete();
                    Log.d(0, "CANCELLED");
                    return;
                }
                p.text = "PDF: processing " + badgeTitle;
                p.percent = (float)badgeNum / badges.size();
                p.update();
            }
            badgeNum++;
            if(page == null || 
                ( // check if we need to be on a new page
                    (offX + spacing + badgeW) > limitW &&
                    (offY + largestH + spacing + badgeH) > limitH
                ))
            {
                // add previous page to doc if we're not on first page
                if(page != null) {
                    doc.addPage(page);
                    try {
                        if(pS != null) {
                            pS.close();
                        }
                    } catch(IOException ioe) {
                        Log.err("Failed to close page content " + 
                                "stream: " + ioe);
                    }
                }
                page = !landscape ? new PDPage(pdPageSize) :
                       new PDPage(new PDRectangle(pdPageSize.getHeight(),
                                                  pdPageSize.getWidth()));
                try {
                    pS = new PDPageContentStream(doc, page);
                    if(pS == null) {
                        throw new IOException();
                    }
                } catch(IOException ioe) {
                    Log.err("Failed to get page content stream: "
                                       + ioe);
                    if(p != null) {
                        p.complete();
                    }
                    return;
                }
                // reset our position
                offX = margin;
                offY = margin;
                largestH = -1;
            }
            
            try {
                PDImageXObject img = PNG ? 
                                     LosslessFactory.
                                     createFromImage(doc, r.render(badge))
                                     : JPEGFactory.
                                     createFromImage(doc, r.render(badge));
                largestH = badgeH > largestH ? badgeH : largestH;
                if(offX + spacing + badgeW > limitW) {
                    // Log.d(0, "    New row");
                    offX = margin;
                    offY += largestH + spacing;
                }
                offX += spacing;
                if(pS != null) {
                    pS.drawImage(img, offX, offY, badgeW, badgeH);
                }
                offX += badgeW;
            } catch(IOException ioe) {
                Log.err("Failed to read image for " + badgeTitle);
            }
        }
        
        doc.addPage(page);
        try {
            if(pS != null) {
                pS.close();
            }
            Log.d(0, "    Saving PDF to " + output.getAbsolutePath());
            if(p != null) {
                p.text = "Saving to " + output.getName();
                p.update();
            }
            doc.save(output);
        } catch(IOException ioe) {
            Log.err("Failed to save to " +
                               output.getAbsolutePath() + " reason: " + ioe);
        }
        
        try {
            doc.close();
        } catch(IOException ioe) {
            Log.err("Failed to close " +
                               output.getAbsolutePath() + " reason: " + ioe);
        }
        
        if(p != null) {
            p.complete();
        }
    }
    
    public static void savePNG(Renderer r, Progress p, List<Badge> badges,
                               String pngOutputDir) {
        int badgeNum = 1;
        for(Badge badge : badges) {
            String fileName = null;
            try {
                fileName = badge.number + "-" + 
                           badge.primaryText + ".png";
                File outFile = new File(pngOutputDir + "/" + fileName);
                if(p != null) {
                    if(p.cancel) {
                        p.complete();
                        Log.d(0, "CANCELLED");
                        return;
                    }
                    p.text = "Saving " + outFile.getName();
                    p.percent = (float)badgeNum / badges.size();
                    p.update();
                }
                badgeNum++;
                Log.d(0, "    Saving to " + outFile.getAbsolutePath());
                if(outFile.exists() && !outFile.canWrite()) {
                    Log.err("Unable to write to: " +
                                       outFile.getAbsolutePath());
                } else {
                    outFile.createNewFile();
                    ImageIO.write(r.render(badge), "png", outFile);
                }
            } catch(Exception e) {
                Log.err("Failed to write " + fileName +
                                   ", reason: " + e);
            }
        }
        
        if(p != null) {
            p.complete();
        }
    }
        
    public static void saveJPG(Renderer r, Progress p, List<Badge> badges,
                               String jpgOutputDir) {
        int badgeNum = 1;
        for(Badge badge : badges) {
            String fileName = null;
            try {
                fileName = badge.number + "-" + 
                           badge.primaryText + ".jpg";
                File outFile = new File(jpgOutputDir + "/" + fileName);
                if(p != null) {
                    if(p.cancel) {
                        p.complete();
                        Log.d(0, "CANCELLED");
                        return;
                    }
                    p.text = "Saving " + outFile.getName();
                    p.percent = (float)badgeNum / badges.size();
                    p.update();
                }
                badgeNum++;
                Log.d(0, "    Saving to " + outFile.getAbsolutePath());
                if(outFile.exists() && !outFile.canWrite()) {
                    Log.err("Unable to write to: " +
                                       outFile.getAbsolutePath());
                } else {
                    outFile.createNewFile();
                    // need to convert to TYPE_INT_RGB to remove alpha channel
                    ImageIO.write(ImageTools.discardAlphaChannel(r.render(badge)),
                                  "jpg", outFile);
                }
            } catch(Exception e) {
                Log.err("Failed to write " + fileName + ", reason: " + e);
            }
        }
        
        if(p != null) {
            p.complete();
        }
    }
    
    public static void saveCSV(Progress p, List<Badge> badges, String csvFile) {
        File parentPath = (new File(csvFile)).getParentFile();
        String imageParentPath = parentPath == null ? "." : 
                                 parentPath.getAbsolutePath();
        FileWriter w = null;
        CSVPrinter printer = null;
        try {
            w = new FileWriter(new File(csvFile));
            CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator("\n");
            printer = new CSVPrinter(w, format);
            int badgeNum = 1;
            for(Badge badge : badges) {
                String name = badge.number + "-" + badge.primaryText;
                if(p != null) {
                    if(p.cancel) {
                        p.complete();
                        w.close();
                        return;
                    }
                    p.text = "Exporting " + name;
                    p.percent = (float) badgeNum / badges.size();
                    badgeNum++;
                    p.update();
                }
                List<String> fields = new ArrayList<>();
                fields.add(String.valueOf(badge.number));
                fields.add(badge.primaryText);
                fields.add(badge.secondaryText);
                if(badge.background != null) {
                    fields.add(name + ".png");
                } else {
                    fields.add("");
                }
                fields.add(String.format("%06x", 
                           badge.backgroundColor.getRGB() & 0xffffffL));
                fields.add(String.format("%06x", 
                           badge.textBackgroundColor.getRGB() & 0xffffffL));
                fields.add(String.format("%06x", 
                           badge.textColor.getRGB() & 0xffffffL));
                switch(badge.getBackgroundScaling()) {
                    case 0:
                        fields.add("fit_width");
                        break;
                    case 1:
                        fields.add("fit_height");
                        break;
                }
                for(String d : badge.getExtraData()) {
                    fields.add(d);
                }
                printer.printRecord(fields);
                if(badge.background != null) {
                    ImageIO.write(badge.background, "png",
                                  new File(imageParentPath + "/" + name + ".png"));
                }
            }
        } catch(Exception e) {
            Log.err("Failed to save CSV:\n" + e);
        } finally {
            try {
                w.flush();
                w.close();
                printer.close();
            } catch(IOException ioe) {
                Log.err("Failed to close output");
            }
        }
        
        if(p != null) {
            p.complete();
        }
    }
    
    public static List<Badge> readFromCSV(Progress p,
                                          String csvFile,
                                          float badgeWidth,
                                          float badgeHeight,
                                          int resolution)
    {
        File parentPath = (new File(csvFile)).getParentFile();
        String imageParentPath = parentPath == null ? "." :
                                 parentPath.getAbsolutePath();
        try {
            Log.d(0, "    Parsing " + csvFile);
            return parseCSV(p, 
                            new String(Files.readAllBytes(Paths.get(csvFile)),
                                       StandardCharsets.UTF_8),
                                       imageParentPath, badgeWidth, badgeHeight,
                                       resolution);
        } catch(IOException ioe) {
            Log.err("Failed to parse " + csvFile);
        }
        if(p != null) {
            p.complete();
        }
        return null;
    }
    
    public static List<Badge> parseCSV(Progress p,
                                       String csvString,
                                       String imageParentPath,
                                       float badgeWidth,
                                       float badgeHeight,
                                       int resolution)
    {
        List<Badge> badges = new ArrayList<>();
        try {
            File imageFilePath = null;
            CSVParser parser = CSVParser.parse(csvString, CSVFormat.DEFAULT);
            List<CSVRecord> records = parser.getRecords();
            BufferedImage background;
            int recordNum = 1;
            for(CSVRecord record : records) {
                if(p != null) {
                    if(p.cancel) {
                        p.complete();
                        Log.d(0, "CANCELLED");
                        return badges;
                    }
                    p.text = "Parsing record #" + recordNum + " of " +
                              records.size();
                    p.percent = (float)recordNum / records.size();
                    p.update();
                }
                try {
                    Log.d(1, record.toString());
                    background = null;
                    if(record.size() < 7) {
                        throw new IOException("invalid number of columns (" +
                                              record.size() + ")");
                    }
                    if(!record.get(3).trim().equals("")) {
                        try {
                            imageFilePath = new File(imageParentPath
                                    + "/" + record.get(3).trim());
                            
                            background = ImageTools.get(imageFilePath);
                        } catch(IOException ioe) {
                            Log.err("Failed to load image " + 
                                    "(line " + recordNum + "): " +
                                    imageFilePath.getAbsolutePath());
                        }
                    }
                    Badge img = new Badge(
                            Integer.parseInt(record.get(0)), // number
                            record.get(1),                   // primary text
                            record.get(2),                   // secondary text
                            background,
                            record.get(4),                   // background color
                            record.get(5),                   // text background color
                            record.get(6)                    // text color
                    );
                    if(record.size() > 7) {
                        switch(record.get(7).toLowerCase()) {
                            case "fit_height":
                                img.setBackgroundFit(Badge.BACKGROUND_FIT_HEIGHT);
                                break;
                            default:
                            case "fit_width":
                                img.setBackgroundFit(Badge.BACKGROUND_FIT_WIDTH);
                        }
                        for(int i = 8; i < record.size(); i++) {
                            img.addExtraData(record.get(i));
                        }
                    }
                    img.setWidth(badgeWidth);
                    img.setProportion(badgeHeight / badgeWidth);
                    img.setResolution(resolution);
                    badges.add(img);
                } catch(Exception e) {
                    Log.err("Failed to parse record " + recordNum + ": " + e);
                    if(p != null) {
                        p.complete();
                    }
                    return badges;
                }
                recordNum++;
            }
        } catch(Exception e) {
            Log.err("Error reading input file: " + e);
        }
        if(p != null) {
            p.complete();
        }
        Log.d(1, "Entries read: "  + badges.size());
        return badges;
    }
    
    public static void saveRendererSettings(Renderer r, float[] sizes, 
                                            String csvFile) {
        FileWriter w = null;
        CSVPrinter printer = null;
        try {
            w = new FileWriter(new File(csvFile));
            CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator("\n");
            printer = new CSVPrinter(w, format);
            String key;
            int type;
            Object value;
            List<String> fields = new ArrayList<>();
            fields.add(String.valueOf(sizes[0]));
            fields.add(String.valueOf(sizes[1]));
            fields.add(String.valueOf(sizes[2]));
            fields.add(String.valueOf(sizes[3]));
            fields.add(String.valueOf(sizes[4]));
            printer.printRecord(fields);
            for(Renderer.Property p : r.getValidProperties()) {
                fields = new ArrayList<>();
                key = p.getKey();
                type = p.getType();
                value = r.getProperty(key);
                if(value == null) {
                    continue;
                }
                fields.add(key);
                fields.add(String.valueOf(type));
                switch(type) {
                    case Renderer.Property.FLOAT:   
                        fields.add(String.valueOf((Float) value));
                        break;
                    case Renderer.Property.INTEGER:   
                        fields.add(String.valueOf((Integer) value));
                        break;
                    case Renderer.Property.STRING:   
                        fields.add((String) value);
                        break;
                }
                printer.printRecord(fields);
            }
            w.flush();
            w.close();
            printer.close();
        } catch(Exception e) {
            Log.err("Failed to save renderer settings:\n" + e);
        }
    }
    
    public static float[] loadRendererSettings(String csvFile, Renderer...r) {
        try {
            float[] sizes = new float[5];
            String csvString = new String(Files.readAllBytes(Paths.get(csvFile)));
            CSVParser parser = CSVParser.parse(csvString, CSVFormat.DEFAULT);
            List<CSVRecord> records = parser.getRecords();
            int numRecord = 0;
            int rendererIndex = 0;
            for(CSVRecord record : records) {
                if(numRecord == 0) {
                    sizes[0] = Float.parseFloat(record.get(0));
                    sizes[1] = Float.parseFloat(record.get(1));
                    sizes[2] = Float.parseFloat(record.get(2));
                    sizes[3] = Float.parseFloat(record.get(3));
                    sizes[4] = Float.parseFloat(record.get(4));
                    rendererIndex = (int) sizes[3];
                } else {
                    r[rendererIndex].setProperty(record.get(0), record.get(2));
                }
                numRecord++;
            }
            return sizes;
        } catch(Exception e) {
            Log.err("Failed to load renderer settings:\n" + e);
        }
        return null;
    }
}
