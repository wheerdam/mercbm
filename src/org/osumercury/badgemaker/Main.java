/*
    Copyright 2016-2017 Wira Mulia

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.osumercury.badgemaker.gui.GUI;
import org.osumercury.badgemaker.renderers.*;

/**
 *
 * @author wira
 */
public class Main {
    private static String fontName;
    private static String file;
    private static String pngOutputDir;
    private static String jpgOutputDir;
    private static String pdf;
    private static String pdfPageSize;
    private static int pdfUnits;
    private static boolean gui = false;
    private static float pdfPageHMargin;
    private static float pdfPageVMargin;
    private static float pdfBadgeHSpacing;
    private static float pdfBadgeVSpacing;
    private static boolean pdfLandscape;
    private static boolean preferLosslessOutput = false;
    private static Renderer r = new ClassicMercuryBadgeRenderer();
    private static final CustomClassLoader loader = new CustomClassLoader();
    private static float width = Badge.DEFAULT_WIDTH;
    private static float height = Badge.DEFAULT_PROPORTION *
                                        Badge.DEFAULT_WIDTH;
    private static int resolution = Badge.DEFAULT_RESOLUTION;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int i = 0;
        while(i < args.length) {
            if(args[i].startsWith("-")) {
                try {
                    switch(args[i]) {
                        case "--":
                            gui = true;
                        case "-i":
                            check(args, i, 1);
                            if(file == null) {
                                file = args[++i];
                            } else {
                                Log.err("Can only process one " +
                                                   "input file");
                                System.exit(1);
                            }
                            break;
                        case "--help":
                            Log.d(0, usage());
                            System.exit(0);
                        case "--debug":
                            check(args, i , 1);
                            Log.debugLevel = Integer.parseInt(args[++i]);
                            break;
                        case "--certificate-renderer":
                            r = new MercuryCertificateRenderer();
                            break;
                        case "--scriptable":
                            check(args, i, 1);
                            r = new ScriptableRenderer();
                            r.setProperty("script-file", args[++i]);
                            break;
                        case "--custom-renderer":
                            check(args, i, 2);
                            loadCustomRenderer(args[++i], args[++i]);
                            break;
                        case "-f":
                            check(args, i, 1);
                            fontName = args[++i]; 
                            break;
                        case "-w":
                            check(args, i, 1);
                            width = Float.parseFloat(args[++i]);
                            break;
                        case "-h":
                            check(args, i, 1);
                            height = Float.parseFloat(args[++i]);
                            break;
                        case "-r":
                            check(args, i, 1);
                            resolution = Integer.parseInt(args[++i]);
                            break;
                        case "-p":
                            preferLosslessOutput = true;
                            break;
                        case "--png":
                            check(args, i, 1);
                            pngOutputDir = args[++i];
                            break;
                        case "--jpg":
                            check(args, i, 1);
                            jpgOutputDir = args[++i];
                            break;
                        case "--pdf":
                            check(args, i, 8);
                            pdf = args[++i];
                            pdfPageSize = args[++i].toUpperCase();
                            pdfLandscape = args[++i].toUpperCase().equals("LANDSCAPE");
                            switch(args[++i].toUpperCase()) {
                                case "MM":
                                    pdfUnits = IO.UNIT_MM;
                                    break;
                                default:
                                    pdfUnits = IO.UNIT_INCHES;
                            }
                            pdfPageHMargin = Float.parseFloat(args[++i]);
                            pdfPageVMargin = Float.parseFloat(args[++i]);
                            pdfBadgeHSpacing = Float.parseFloat(args[++i]);
                            pdfBadgeVSpacing = Float.parseFloat(args[++i]);
                            break;
                        case "-s":
                            check(args, i, 2);
                            r.setProperty(args[++i], args[++i]);
                            break;
                        case "-l":
                            printRendererProperties(r);
                            System.exit(0);
                        case "-c":
                            check(args, i, 2);
                            loader.loadClass(args[++i], args[++i]);
                            break;
                        default:
                            Log.err("Unknown option: " + args[i]);
                            Log.err("Run with '--help' for command line options");
                            System.err.println();
                            System.exit(1);
                    }
                } catch(Exception e) {
                    Log.err("Failed to parse option: " + args[i]);
                    System.exit(1);
                }
                i++;
            } else if(file == null) {
                file = args[i++];
                gui = true;
            } else {
                Log.err("Can only process one input file");
                System.exit(1);
            }
        }
               
        if(!gui && file != null) {
            if(pdf == null && pngOutputDir == null) {
                Log.err("No output was specified");
                System.exit(1);
            }
            
            Log.d(0, "Renderer: " + r.getDescription());
            
            if(fontName != null) {
                r.setProperty("font", fontName);
            }
            
            List<Badge> badges = IO.readFromCSV(null, file,
                                                width, height,
                                                resolution);
            if(badges == null) {
                Log.err("No valid entries found");
                System.exit(1);
            }
            
            if(pdf != null) {
                PDRectangle pageSize;
                switch(pdfPageSize) {
                    case "A0": pageSize = PDRectangle.A0; break;
                    case "A1": pageSize = PDRectangle.A1; break;
                    case "A2": pageSize = PDRectangle.A2; break;
                    case "A3": pageSize = PDRectangle.A3; break;
                    case "A4": pageSize = PDRectangle.A4; break;
                    case "A5": pageSize = PDRectangle.A5; break;
                    case "A6": pageSize = PDRectangle.A6; break;
                    case "LEGAL": pageSize = PDRectangle.LEGAL; break;
                    default:
                        Log.d(0, "    Unknown paper size '" + pdfPageSize
                                            + "', setting PDF page size to the "
                                            + "default LETTER");
                    case "LETTER": pageSize = PDRectangle.LETTER;
                }
                
                IO.generatePDF(r, null, pageSize,
                               pdfPageHMargin, pdfPageVMargin,
                               pdfBadgeHSpacing, pdfBadgeVSpacing,
                               pdfUnits, pdfLandscape,
                               preferLosslessOutput,
                               badges, 
                               new File(pdf));
            }
            if(pngOutputDir != null) {
                IO.savePNG(r, null, badges, pngOutputDir);
            }
            if(jpgOutputDir != null) {
                IO.saveJPG(r, null, badges, jpgOutputDir);
            }
        } else {
            // gui init
            Log.errorDialogBox = true;
            GUI.createMainWindow(file);
        }
    }
    
    private static String[] check(String[] args, int cur, int need) {
        if(cur + need + 1 > args.length) {
            Log.err("Invalid number of arguments for '" +
                               args[cur] + "'");
            System.exit(1);
        }
        
        String[] optArgs = new String[need];
        System.arraycopy(args, cur, optArgs, 0, need);
        return optArgs;
    }
    
    public static String pad(int width, String str) {
        int spaces = width - str.length();
        if(spaces > 0) {
            return str + String.format("%" + spaces + "s", "");
        } else if(spaces == 0) {
            return str;
        } else if(width > 3) {
            return str.substring(0, width-3) + "...";
        } else if(width > 0) {
            return str.substring(0, width-1) + "-";
        } else {
            return "";
        }
    }
    
    public static Renderer getRenderer() {
        return r;
    }
    
    public static void loadCustomRenderer(String className, String classFile) {
        // let's load the file and box it        
        try {
            Class c = loader.loadClass(className, classFile);
            if(c == null) {
                System.exit(1);
            }
            r = (Renderer) c.newInstance();
        } catch(InstantiationException | IllegalAccessException e) {
            Log.err("Failed to instantiate custom renderer: " +
                               e);
            System.exit(1);
        }
    }        
    
    public static void printRendererProperties(Renderer r) {
        Log.d(0, "Renderer: " + r.getDescription());
        Log.d(0, "Valid properties:");
        Log.d(0, pad(10, "Type") +
                           pad(25, "Key") +
                           pad(14, "Default") +
                           " Description");
        for(Renderer.Property p : r.getValidProperties()) {
            switch(p.getType()) {
                case Renderer.Property.FLOAT:
                    Log.di(0, pad(12, "  FLOAT"));
                    break;
                case Renderer.Property.INTEGER:
                    Log.di(0, pad(12, "  INTEGER"));
                    break;
                case Renderer.Property.STRING:
                    Log.di(0, pad(12, "  STRING"));
                    break;
                default:
                    Log.di(0, pad(12, "  INVALID"));
            }
            Log.d(0, pad(25, p.getKey()) + " " +
                               pad(11, p.getDefaultValue()) + "   " +
                               p.getDescription());
        }
    }

    static class CustomClassLoader extends ClassLoader {
        public Class loadClass(String name, String path) {
            Class c = null;
            try {
                byte[] data = Files.readAllBytes(Paths.get(path));
                c = defineClass(name, data, 0, data.length);
            } catch(IOException ioe) {
                Log.err("Failed to read class file " + ioe);
            } catch(NoClassDefFoundError e) {
                Log.err("Failed to load custom renderer class: " + e);
            }
            return c;
        }
    }
    
    public static String usage() {
        String help;
        help  =   "Usage:\n"
                + "  java -jar <jar-file> [[--] or [-i] INPUT] [RENDERER] [OPTIONS] [OUTPUTS]\n"
                + "\n"
                + "INPUT: use '--' prior to INPUT if file name begins with a '-', use the '-i'\n"
                + "option for non-interactive command line interface. If either INPUT is\n"
                + "omitted *or* '-i' is omitted while INPUT is given, the GUI will start\n"
                + "\n"
                + "   Column 1              number (-1 if not desired)\n"
                + "   Column 2              primary text / name\n"
                + "   Column 3              secondary text / institution or group\n"
                + "   Column 4              path to background image relative to input file\n"
                + "                           leave this column empty for no background image\n"
                + "   Column 5              background color in hex format (e.g. ffeeff)\n"
                + "   Column 6              text background color in hex format\n"
                + "   Column 7              text color in hex format\n"
                + "   Column 8              background scaling: {fit_width, fit_height}\n"
                + "   Column 9...           extra data (renderer specific)\n"
                + "\n"
                + "RENDERER: by default, the program will use the classic Mercury badge\n"
                + "renderer. The following is a list of alternative renderers:\n"
                + "\n"
                + " --certificate-renderer  Mercury certificate of participation\n"
                + " --scriptable SCRIPT     the scriptable renderer\n"
                + " --custom-renderer CLASSNAME CLASSFILE\n"
                + "                         dynamically load an external renderer\n"
                + "\n"
                + "OPTIONS:\n"
                + "  -l                     list active renderer properties and quit\n"
                + "  -s KEY VALUE           set active renderer property\n"
                + "  -f FONTNAME            set font to use for text\n"
                + "  -w VALUE               badge width (default " +
                                            String.format("%.2f", Badge.DEFAULT_WIDTH) + ")\n"
                + "  -h VALUE               badge height (default " +
                                            String.format("%.2f", Badge.DEFAULT_PROPORTION *
                                                                  Badge.DEFAULT_WIDTH) + ")\n"
                + "  -r RESOLUTION          output image resolution (dots per units, default " +
                                            Badge.DEFAULT_RESOLUTION + ")\n"
                + "  -p                     use lossless image format when applicable\n"
                + "\n"
                + "OUTPUT formats (must specify at least one if '-i' is used):\n" 
                + "  --png DIRECTORY        output badges as PNG files\n"
                + "  --jpg DIRECTORY        output badges as JPG files\n"
                + "  --pdf FILENAME SIZE ORIENTATION UNITS H-MARGIN V-MARGIN H-SPACING V-SPACING\n"
                + "                         generate a PDF document with the specified format\n"
                + "                           valid sizes: A0, ... A6, LETTER, LEGAL\n"
                + "                           orientations: PORTRAIT, LANDSCAPE\n"
                + "                           units: MM, INCHES\n";
        return help;
    }
    
    public static String version() { return "0.4"; }        
    public static String copyright() { 
        return "Copyright (c)2017 Carl D. Latino and Wira D. Mulia"; 
    }    
}
