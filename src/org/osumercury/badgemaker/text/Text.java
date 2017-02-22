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
package org.osumercury.badgemaker.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.*;
import org.osumercury.badgemaker.Log;
import org.osumercury.badgemaker.Main;

/**
 *
 * @author wira
 */
public class Text {
    public static String parseHTMLResource(String filename) {
        String data = retrieveText(filename);
        if(data == null) {
            return null;
        }
        Pattern p = Pattern.compile("src=\"(.*)\"");
        Matcher m = p.matcher(data);
        StringBuffer sb = new StringBuffer();
        String key, f;
        URL url;
        while(m.find()) {
            key = m.group(1);
            url = resolve(key);
            if(url != null) {
                f = resolve(key).toString();
            } else {
                f = "";
            }
            m.appendReplacement(sb, "src=\"" + f + "\"");
        }
        m.appendTail(sb);
        return sb.toString();
    }
    
    public static String resolveBuiltInText(String data) {
        if(data == null) {
            return null;
        }
        Pattern p = Pattern.compile("&&(.*)&&");
        Matcher m = p.matcher(data);
        StringBuffer sb = new StringBuffer();
        while(m.find()) {
            switch(m.group(1)) {
                case "helpMessage":
                    m.appendReplacement(sb, Main.usage());
                    break;
                case "version":
                    m.appendReplacement(sb, Main.version());
                    break;
                case "copyright":
                    m.appendReplacement(sb, Main.copyright());
                    break;
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }
    
    public static String retrieveText(String filename) {
        String line;
        StringBuilder str = new StringBuilder();
        String path = "/org/osumercury/badgemaker/text/" + filename;
        Log.d(1, "Retrieving resource: " + resolve(filename));
        InputStream in = Text.class.getResourceAsStream(path);
        if(in == null) {
            Log.err("Unknown text resource: " + path);
            return null;
        }
        BufferedReader r =  new BufferedReader(new InputStreamReader(in));
        try {
            while((line = r.readLine()) != null) {
                str.append(line);
                str.append("\n");
            }
        } catch(IOException ioe) {
            Log.err("Failed to retrieve text resource:\n" + filename);
            return null;
        }
        
        return str.toString();
    }
    
    public static URL resolve(String filename) {
        return Text.class.getResource("/org/osumercury/badgemaker/text/" + filename);
    }
}