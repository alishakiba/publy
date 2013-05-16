/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator;

import java.util.List;
import javax.swing.JOptionPane;
import publistgenerator.io.settings.SettingsReader;
import publistgenerator.data.settings.Settings;
import publistgenerator.data.bibitem.BibItem;
import publistgenerator.gui.MainFrame;
import publistgenerator.io.BibTeXParser;
import publistgenerator.io.html.HTMLPublicationListWriter;
import publistgenerator.io.plain.PlainPublicationListWriter;

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
        // Parse all publications
        List<BibItem> items = BibTeXParser.parseFile(settings.getPublications());

        if (settings.generateHTML()) {
            HTMLPublicationListWriter writer = new HTMLPublicationListWriter();
            writer.writePublicationList(items, settings.getHtmlSettings());
        }

        if (settings.generateText()) {
            PlainPublicationListWriter plainWriter = new PlainPublicationListWriter();
            plainWriter.writePublicationList(items, settings.getPlainSettings());
        }
    }
}
