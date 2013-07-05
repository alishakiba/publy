/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publy.data.settings;

import java.nio.file.Path;

/**
 *
 * @author Sander
 */
public class Settings {

    private Path publications;
    private HTMLSettings htmlSettings;
    private FormatSettings generalSettings;

    /**
     * Creates a new empty Settings object.
     *
     * @param publications
     */
    public Settings() {
        publications = null;
        htmlSettings = new HTMLSettings();
        generalSettings = new FormatSettings();
    }
    
    /**
     * Returns a new Settings object with default settings.
     * @return 
     */
    public static Settings defaultSettings() {
        Settings result = new Settings();
        
        result.generalSettings = FormatSettings.defaultSettings();
        
        return result;
    }

    public Path getPublications() {
        return publications;
    }

    public HTMLSettings getHtmlSettings() {
        return htmlSettings;
    }

    public FormatSettings getGeneralSettings() {
        return generalSettings;
    }

    public void setPublications(Path publications) {
        this.publications = publications;
    }
}
