/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import publy.data.bibitem.BibItem;
import publy.data.settings.Settings;
import publy.gui.MainFrame;
import publy.io.BibTeXParser;
import publy.io.PublicationListWriter;
import publy.io.bibtex.BibtexPublicationListWriter;
import publy.io.html.HTMLPublicationListWriter;
import publy.io.plain.PlainPublicationListWriter;
import publy.io.settings.SettingsReader;

/**
 *
 * @author Sander
 */
public class GeneratorMain {

    public static final int MAJOR_VERSION = 0;
    public static final int MINOR_VERSION = 2;
    
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
            JOptionPane.showMessageDialog(null, "No configuration information was found. Please set up your preferences.", "Publy - Launching Settings Window", JOptionPane.INFORMATION_MESSAGE);

            // Launch the GUI
            MainFrame mf = new MainFrame(Settings.defaultSettings());

            // Report an Exception, if one occurred
            if (exception != null) {
                Console.except(exception, "Exception occurred while parsing the configuration:");
            }

            mf.setVisible(true);
        } else {
            Console.log("Configuration parsed successfully.");
            generatePublicationList(settings);
        }
    }
    
    public static void generatePublicationList(Settings settings) {
        // Check if the publication list is set and exists
        Path pubList = settings.getPublications();

        if (pubList == null) {
            Console.error("No publication list was set.");
        } else if (Files.notExists(pubList)) {
            Console.error("No publication list was found at: %s", pubList);
        } else {
            // Parse all publications
            List<BibItem> items = null;
            
            try {
                items = BibTeXParser.parseFile(settings.getPublications());
                Console.log("Publications list \"%s\" parsed successfully.", settings.getPublications().getFileName());
            } catch (Exception | AssertionError ex) {
                Console.except(ex, "Exception while parsing publications list:");
            }

            if (items != null) {
                try {
                    PublicationListWriter writer = new HTMLPublicationListWriter(settings.getGeneralSettings(), settings.getHtmlSettings());
                    writer.writePublicationList(items, settings.getGeneralSettings().getTarget());
                    Console.log("HTML publication list written successfully.");
                } catch (Exception | AssertionError ex) {
                    Console.except(ex, "Exception while writing HTML publication list:");
                }
            }

            if (items != null && settings.getHtmlSettings().linkToTextVersion()) {
                try {
                    PublicationListWriter writer = new PlainPublicationListWriter(settings.getGeneralSettings());
                    writer.writePublicationList(items, settings.getGeneralSettings().getPlainTextTarget());
                    Console.log("Plain text publication list written successfully.");
                } catch (Exception | AssertionError ex) {
                    Console.except(ex, "Exception while writing plain text publication list:");
                }
            }
            
            if (items != null && settings.getHtmlSettings().linkToBibtexVersion()) {
                try {
                    PublicationListWriter writer = new BibtexPublicationListWriter(settings.getGeneralSettings());
                    writer.writePublicationList(items, settings.getGeneralSettings().getBibtexTarget());
                    Console.log("BibTeX publication list written successfully.");
                } catch (Exception | AssertionError ex) {
                    Console.except(ex, "Exception while writing BibTeX publication list:");
                }
            }
            
            Console.log("Done.");
        }
    }
}
