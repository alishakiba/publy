/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publy.io.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import publy.data.PublicationType;
import publy.data.category.CategoryIdentifier;
import publy.data.settings.FormatSettings;
import publy.data.settings.HTMLSettings;
import publy.data.settings.Settings;
import publy.io.ResourceLocator;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class SettingsReader extends DefaultHandler {

    static final String DEFAULT_SETTINGS_LOCATION = "data/PublySettings.xml";
    private StringBuilder textBuffer; // Contains the characters that are read between start and end elements (e.g. <item>Text</item>)
    private Settings settings; // Contains the read settings after parsing.
    private CategoryIdentifier noteFor = null; // The category the current note is for. (null if there is none)

    private SettingsReader(Settings settings) {
        this.settings = settings;
    }

    public static Settings parseSettings() throws ParserConfigurationException, SAXException, IOException {
        Path settingsFile = ResourceLocator.getFullPath(DEFAULT_SETTINGS_LOCATION);
        Settings settings = null;

        if (Files.exists(settingsFile)) {
            settings = new Settings();
            parseSettings(settings, settingsFile);
        }

        return settings;
    }

    private static void parseSettings(Settings settings, Path inputFile) throws ParserConfigurationException, SAXException, IOException {
        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // Create a new instance of this class as handler
        SettingsReader handler = new SettingsReader(settings);

        // Parse the input
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(inputFile.toFile(), handler);
    }

    @Override
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        switch (qName) {
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
                // Publications settings
                case "publications":
                    settings.setPublications(ResourceLocator.getFullPath(text));
                    break;
                // General settings
                case "target":
                    settings.getGeneralSettings().setTarget(ResourceLocator.getFullPath(text));
                    break;
                case "mynames":
                    settings.getGeneralSettings().setMyNames(Arrays.asList(text.split(";")));
                    break;
                case "listallauthors":
                    settings.getGeneralSettings().setListAllAuthors(Boolean.parseBoolean(text));
                    break;
                case "namedisplay":
                    settings.getGeneralSettings().setNameDisplay(FormatSettings.NameDisplay.valueOf(text));
                    break;
                case "reversenames":
                    settings.getGeneralSettings().setReverseNames(Boolean.parseBoolean(text));
                    break;
                case "titlefirst":
                    settings.getGeneralSettings().setTitleFirst(Boolean.parseBoolean(text));
                    break;
                case "numbering":
                    settings.getGeneralSettings().setNumbering(FormatSettings.Numbering.valueOf(text));
                    break;
                case "reversenumbering":
                    settings.getGeneralSettings().setReverseNumbering(Boolean.parseBoolean(text));
                    break;
                case "category":
                    settings.getGeneralSettings().addCategory(CategoryIdentifier.valueOf(text));
                    break;
                case "note":
                    settings.getGeneralSettings().setNote(noteFor, text);
                    break;
                // HTML-specific settings
                case "generatetextversion":
                    settings.getHtmlSettings().setGenerateTextVersion(Boolean.parseBoolean(text));
                    break;
                case "generatebibtexversion":
                    settings.getHtmlSettings().setGenerateBibtexVersion(Boolean.parseBoolean(text));
                    break;
                case "linktoalternateversions":
                    settings.getHtmlSettings().setLinkToAlternateVersions(Boolean.parseBoolean(text));
                    break;
                case "navplacement":
                    settings.getHtmlSettings().setNavPlacement(HTMLSettings.NavigationPlacement.valueOf(text));
                    break;
                case "includeabstract":
                    settings.getHtmlSettings().setIncludeAbstract(PublicationType.valueOf(text));
                    break;
                case "includebibtex":
                    settings.getHtmlSettings().setIncludeBibtex(PublicationType.valueOf(text));
                    break;
                case "includepaper":
                    settings.getHtmlSettings().setIncludePaper(PublicationType.valueOf(text));
                    break;
                case "titletarget":
                    settings.getHtmlSettings().setTitleTarget(HTMLSettings.TitleLinkTarget.valueOf(text));
                    break;
                case "header":
                    settings.getHtmlSettings().setHeader(ResourceLocator.getFullPath(text));
                    break;
                case "footer":
                    settings.getHtmlSettings().setFooter(ResourceLocator.getFullPath(text));
                    break;
                case "googleanalyticsuser":
                    settings.getHtmlSettings().setGoogleAnalyticsUser(text);
                    break;
                case "presentedtext":
                    settings.getHtmlSettings().setPresentedText(text);
                    break;
                default:
                    break;
            }
        }

        switch (qName) {
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
