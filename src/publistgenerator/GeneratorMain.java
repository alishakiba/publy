/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import publistgenerator.data.bibitem.BibItem;
import publistgenerator.data.settings.Settings;
import publistgenerator.gui.MainFrame;
import publistgenerator.io.BibTeXParser;
import publistgenerator.io.html.HTMLPublicationListWriter;
import publistgenerator.io.plain.PlainPublicationListWriter;
import publistgenerator.io.settings.SettingsReader;

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
            MainFrame mf = new MainFrame(new Settings());
            mf.setVisible(true);
        } else {
            generatePublicationList(settings);
        }
    }

    public static void generatePublicationList(Settings settings) {
        // Check if the publication list is set and exists
        File pubList = settings.getPublications();

        if (pubList == null) {
            Console.error("No publication list was set.");
        } else if (!pubList.exists()) {
            Console.error("No publication list was found at: %s", pubList.getPath());
        } else {
            // Parse all publications
            List<BibItem> items = null;

            try {
                items = BibTeXParser.parseFile(settings.getPublications());
            } catch (IOException ex) {
                Logger.getLogger(GeneratorMain.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (items != null && settings.generateHTML()) {
                HTMLPublicationListWriter writer = new HTMLPublicationListWriter(settings.getHtmlSettings());
                writer.writePublicationList(items);
            }

            if (items != null && settings.generateText()) {
                PlainPublicationListWriter plainWriter = new PlainPublicationListWriter(settings.getPlainSettings());
                plainWriter.writePublicationList(items);
            }
        }
    }
}
