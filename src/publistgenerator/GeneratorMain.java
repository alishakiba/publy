/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator;

import java.io.File;
import java.util.List;
import javax.swing.JOptionPane;
import publistgenerator.bibitem.BibItem;
import publistgenerator.io.BibTeXParser;
import publistgenerator.io.html.HTMLPublicationListWriter;
import publistgenerator.io.plain.PlainPublicationListWriter;
import plgsettings.settings.Settings;
import plgsettings.settings.SettingsReader;

/**
 *
 * @author Sander
 */
public class GeneratorMain {

    private static final String DEFAULT_SETTINGS_LOCATION = "./PubListGenerator.config";
    private static File webDir = new File("../../../My Dropbox/Website/"); // TODO: remove. Should be part of settings

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Read settings
        Settings settings = SettingsReader.parseSettings(DEFAULT_SETTINGS_LOCATION);

        if (settings == null) {
            // Notify the user
            JOptionPane.showMessageDialog(null, "No configuration information was found. Please create a configuration in the Settings window.", "Publication List Generator - Launching Settings Window", JOptionPane.INFORMATION_MESSAGE);
            
            // Launch the GUI
            // TODO
        } else {
            // Parse all publications
            List<BibItem> items = BibTeXParser.parseFile(settings.getPublications());

            HTMLPublicationListWriter writer = new HTMLPublicationListWriter();
            writer.writePublicationList(items, settings.getHtmlSettings());

            PlainPublicationListWriter plainWriter = new PlainPublicationListWriter();
            plainWriter.writePublicationList(items, settings.getPlainSettings());
        }
    }
}
