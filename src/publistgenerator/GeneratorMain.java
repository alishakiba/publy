/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
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
        Settings settings = null;
        Throwable exception = null;

        try {
            settings = SettingsReader.parseSettings();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            exception = ex;
        }

        if (settings == null) {
            // Notify the user
            JOptionPane.showMessageDialog(null, "No configuration information was found. Please set up your preferences.", "Publication List Generator - Launching Settings Window", JOptionPane.INFORMATION_MESSAGE);

            // Launch the GUI
            MainFrame mf = new MainFrame(new Settings());

            // Report an Exception, if one occurred
            if (exception != null) {
                Console.except(exception, "Exception occurred while parsing the configuration:");
            } else {
                Console.log("Configuration parsed successfully.");
            }

            mf.setVisible(true);
        } else {
            Console.log("Configuration parsed successfully.");
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
                Console.log("Publications list \"%s\" parsed successfully.", settings.getPublications().getName());
            } catch (Exception | AssertionError ex) {
                Console.except(ex, "Exception while parsing publications list:");
            }

            if (items != null && settings.generateHTML()) {
                try {
                    HTMLPublicationListWriter writer = new HTMLPublicationListWriter(settings.getHtmlSettings());
                    writer.writePublicationList(items);
                    Console.log("HTML publication list written successfully.");
                } catch (Exception | AssertionError ex) {
                    Console.except(ex, "Exception while writing HTML publication list:");
                }
            }

            if (items != null && settings.generateText()) {
                try {
                    PlainPublicationListWriter plainWriter = new PlainPublicationListWriter(settings.getPlainSettings());
                    plainWriter.writePublicationList(items);
                    Console.log("Plain text publication list written successfully.");
                } catch (Exception | AssertionError ex) {
                    Console.except(ex, "Exception while writing plain text publication list:");
                }
            }
            
            Console.log("Done.");
        }
    }
}
