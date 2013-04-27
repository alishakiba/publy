/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.settings;

import java.io.File;

/**
 *
 * @author Sander
 */
public class Settings {

    private File publications;
    private HTMLSettings htmlSettings;
    private FormatSettings plainSettings;

    /**
     * Creates a new Settings object with default html and plain settings.
     * @param publications 
     */
    public Settings() {
        publications = null;
        htmlSettings = new HTMLSettings();
        plainSettings = new FormatSettings();
    }

    public File getPublications() {
        return publications;
    }

    public void setPublications(File publications) {
        this.publications = publications;
    }

    public HTMLSettings getHtmlSettings() {
        return htmlSettings;
    }

    public FormatSettings getPlainSettings() {
        return plainSettings;
    }

    /**
     * Populates this object with the default values. The publications file remains the same.
     */
    public void resetToDefault() {
        htmlSettings = new HTMLSettings();
        plainSettings = new FormatSettings();
    }
}
