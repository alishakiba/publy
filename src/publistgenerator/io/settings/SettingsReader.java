/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.settings;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import publistgenerator.data.category.CategoryIdentifier;
import publistgenerator.data.settings.FormatSettings;
import publistgenerator.data.settings.HTMLSettings;
import publistgenerator.data.settings.Settings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class SettingsReader extends DefaultHandler {

    static final String DEFAULT_SETTINGS_LOCATION = "./PLGSettings.xml";
    private StringBuilder textBuffer; // Contains the characters that are read between start and end elements (e.g. <item>Text</item>)
    private Settings settings; // Contains the read settings after parsing.
    private FormatSettings format = null;
    private CategoryIdentifier noteFor = null; // The category the current note is for. (null if there is none)

    private SettingsReader(Settings settings) {
        this.settings = settings;
    }

    public static Settings parseSettings() {
        File settingsFile = new File(DEFAULT_SETTINGS_LOCATION);
        Settings settings = null;

        if (settingsFile.exists()) {
            settings = new Settings();
            parseSettings(settings, settingsFile);
        }

        return settings;
    }

    private static void parseSettings(Settings settings, File inputFile) {
        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // Create a new instance of this class as handler
        SettingsReader handler = new SettingsReader(settings);

        try {
            // Parse the input
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(inputFile, handler);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(SettingsReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        switch (qName) {
            case "plaintextsettings":
                format = settings.getPlainSettings();
                break;
            case "htmlsettings":
                format = settings.getHtmlSettings();
                break;
            case "note":
                noteFor = CategoryIdentifier.valueOf(attrs.getValue("category"));
                break;
            default:
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // Handle the characters that were recorded between the tags
        String text = null;

        if (textBuffer != null) {
            text = textBuffer.toString().trim();
            textBuffer = null;
        }

        if (text != null && !text.isEmpty()) {
            switch (qName) {
                // General settings
                case "publications":
                    settings.setPublications(new File(text));
                    break;
                case "generateplaintext":
                    settings.setGenerateText(Boolean.parseBoolean(text));
                    break;
                case "generatehtml":
                    settings.setGenerateHTML(Boolean.parseBoolean(text));
                    break;
                // Format settings
                case "target":
                    format.setTarget(new File(text));
                    break;
                case "listallauthors":
                    format.setListAllAuthors(Boolean.parseBoolean(text));
                    break;
                case "presentedtext":
                    format.setPresentedText(text);
                    break;
                case "numbering":
                    format.setNumbering(FormatSettings.Numbering.valueOf(text));
                    break;
                case "category":
                    format.addCategory(CategoryIdentifier.valueOf(text));
                    break;
                case "note":
                    format.setNote(noteFor, text);
                    break;
                // HTML-specific settings
                case "linktotextversion":
                    ((HTMLSettings) format).setLinkToTextVersion(Boolean.parseBoolean(text));
                    break;
                case "includeabstract":
                    ((HTMLSettings) format).setIncludeAbstract(HTMLSettings.PublicationType.valueOf(text));
                    break;
                case "includebibtex":
                    ((HTMLSettings) format).setIncludeBibtex(HTMLSettings.PublicationType.valueOf(text));
                    break;
                case "includepdf":
                    ((HTMLSettings) format).setIncludePDF(HTMLSettings.PublicationType.valueOf(text));
                    break;
                case "header":
                    ((HTMLSettings) format).setHeader(new File(text));
                    break;
                case "footer":
                    ((HTMLSettings) format).setFooter(new File(text));
                    break;
                case "googleanalyticsuser":
                    ((HTMLSettings) format).setGoogleAnalyticsUser(text);
                    break;
                default:
                    break;
            }
        }

        switch (qName) {
            // General settings
            case "plaintextsettings":
            // Fall through
            case "htmlsettings":
                format = null;
                break;
            // Format settings
            case "note":
                noteFor = null;
                break;
            default:
                break;
        }
    }

    @Override
    public void characters(char buf[], int offset, int len) throws SAXException {
        String s = new String(buf, offset, len);

        if (textBuffer == null) {
            textBuffer = new StringBuilder(s);
        } else {
            textBuffer.append(s);
        }
    }
}
