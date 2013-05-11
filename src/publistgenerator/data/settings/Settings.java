/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.data.settings;

import java.io.File;

/**
 *
 * @author Sander
 */
public class Settings {

    private File publications;
    private boolean generateHTML;
    private HTMLSettings htmlSettings;
    private boolean generateText;
    private FormatSettings plainSettings;

    /**
     * Creates a new Settings object with default html and plain settings.
     *
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

    public boolean generateHTML() {
        return generateHTML;
    }

    public HTMLSettings getHtmlSettings() {
        return htmlSettings;
    }

    public boolean generateText() {
        return generateText;
    }

    public FormatSettings getPlainSettings() {
        return plainSettings;
    }

    public void setPublications(File publications) {
        this.publications = publications;
    }

    public void setGenerateHTML(boolean generateHTML) {
        this.generateHTML = generateHTML;
    }

    public void setGenerateText(boolean generateText) {
        this.generateText = generateText;
    }

    /**
     * Populates this object with the default values. The publications file
     * remains the same.
     */
    public void resetToDefault() {
        htmlSettings = new HTMLSettings();
        plainSettings = new FormatSettings();
    }
}
