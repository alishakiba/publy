/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.settings;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Sander
 */
public class Settings {

    private Map<String, FormatSettings> formatSettings;

    public Settings() {
        formatSettings = new HashMap<>();
    }

    public FormatSettings getSettings(String format) {
        return formatSettings.get(format);
    }

    public void addFormat(String format, FormatSettings settings) {
        formatSettings.put(format, settings);
    }

    /**
     * Populates this object with the default values.
     */
    public void resetToDefault() {
        formatSettings.clear();
        
        // Default HTML settings
        HTMLSettings html = new HTMLSettings();
        formatSettings.put(html.getFormat(), html);
    }
}
