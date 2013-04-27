/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator;

import java.io.File;
import java.util.List;
import publistgenerator.bibitem.BibItem;
import publistgenerator.io.BibTeXParser;
import publistgenerator.io.html.HTMLPublicationListWriter;
import publistgenerator.io.plain.PlainPublicationListWriter;
import publistgenerator.settings.HTMLSettings;
import publistgenerator.settings.Settings;
import publistgenerator.settings.SettingsReader;

/**
 *
 * @author Sander
 */
public class GeneratorMain {

    private static final String DEFAULT_SETTINGS_LOCATION = "./PubListGenerator.config";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        generatePublications();
    }

    private static File webDir = new File("../../../My Dropbox/Website/");
    
    private static void generatePublications() {
        // Read settings
        Settings settings = SettingsReader.parseSettings(DEFAULT_SETTINGS_LOCATION);
        
        // Parse all publications
        List<BibItem> items = BibTeXParser.parseFile(settings.getPublications());

        HTMLPublicationListWriter writer = new HTMLPublicationListWriter(new File(webDir, "publications/PublicationsHeader.html"), new File(webDir, "publications/PublicationsFooter.html"));
        writer.writePublicationList(items, (HTMLSettings) settings.getSettings("html"));

        PlainPublicationListWriter plainWriter = new PlainPublicationListWriter();
        plainWriter.writePublicationList(items, settings.getSettings("plain"));
    }
}
