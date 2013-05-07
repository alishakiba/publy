/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator;

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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Read settings
        Settings settings = SettingsReader.parseSettings();

        if (settings == null) {
            // Notify the user
            JOptionPane.showMessageDialog(null, "No configuration information was found. Please set up your preferences.", "Publication List Generator - Launching Settings Window", JOptionPane.INFORMATION_MESSAGE);

            // Launch the GUI
            plgsettings.PLGSettings.launchGUI(settings);
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
