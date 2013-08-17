/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publy.io.settings;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import publy.data.category.CategoryIdentifier;
import publy.data.settings.ConsoleSettings;
import publy.data.settings.FormatSettings;
import publy.data.settings.HTMLSettings;
import publy.data.settings.Settings;
import publy.gui.UIConstants;
import publy.io.ResourceLocator;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class SettingsWriter {

    public static void writeSettings(Settings settings) throws IOException {
        Path settingsFile = SettingsReader.getSettingsFile();
        Path settingsDirectory = settingsFile.getParent();

        if (Files.notExists(settingsDirectory)) {
            try {
                Files.createDirectories(settingsDirectory);
            } catch (Exception ex) {
                throw new IOException("Could not create the directory \"" + settingsDirectory + "\" to store the settings.", ex);
            }
        }

        try (BufferedWriter out = Files.newBufferedWriter(settingsFile, Charset.forName("UTF-8"))) {
            // Write header
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.newLine();
            out.write("<plgsettings majorversion=\"" + UIConstants.MAJOR_VERSION + "\" minorversion=\"" + UIConstants.MINOR_VERSION + "\">");
            out.newLine();

            writeGeneralSettings(settings, out);
            writeFormatSettings(settings.getGeneralSettings(), out);
            writeHTMLSettings(settings.getHtmlSettings(), out);
            writeConsoleSettings(settings.getConsoleSettings(), out);

            // Write footer
            out.write("</plgsettings>");
            out.newLine();
        }
    }

    private static void writeGeneralSettings(Settings settings, BufferedWriter out) throws IOException {
        out.write("  <!-- Relative path to a BibTeX file with the publication list -->");
        out.newLine();
        output(out, 2, "publications", makeString(settings.getPublications()));
        out.newLine();
    }

    private static void writeFormatSettings(FormatSettings format, BufferedWriter out) throws IOException {
        out.write("  <!-- General settings -->");
        out.newLine();

        out.write("  <generalsettings>");
        out.newLine();

        output(out, 4, "target", makeString(format.getTarget()));
        output(out, 4, "mynames", makeCData(format.getMyNames()));
        output(out, 4, "listallauthors", makeString(format.isListAllAuthors()));
        output(out, 4, "namedisplay", makeString(format.getNameDisplay()));
        output(out, 4, "reversenames", makeString(format.isReverseNames()));
        output(out, 4, "titlefirst", makeString(format.isTitleFirst()));
        output(out, 4, "numbering", makeString(format.getNumbering()));
        output(out, 4, "reversenumbering", makeString(format.isReverseNumbering()));

        // Categories
        out.write("    <categories>");
        out.newLine();

        for (CategoryIdentifier cid : format.getCategories()) {
            output(out, 6, "category", makeString(cid));
        }

        out.write("    </categories>");
        out.newLine();

        // Category notes
        out.write("    <categorynotes>");
        out.newLine();

        for (Map.Entry<CategoryIdentifier, String> entry : format.getCategoryNotes().entrySet()) {
            output(out, 6, "note", makeCData(entry.getValue()), "category", makeString(entry.getKey()));
        }

        out.write("    </categorynotes>");
        out.newLine();

        out.write("  </generalsettings>");
        out.newLine();
        out.newLine();
    }

    private static void writeHTMLSettings(HTMLSettings settings, BufferedWriter out) throws IOException {
        out.write("  <!-- HTML-specific settings -->");
        out.newLine();

        out.write("  <htmlsettings>");
        out.newLine();

        output(out, 4, "generatetextversion", makeString(settings.generateTextVersion()));
        output(out, 4, "generatebibtexversion", makeString(settings.generateBibtexVersion()));
        output(out, 4, "linktoalternateversions", makeString(settings.linkToAlternateVersions()));
        output(out, 4, "navplacement", makeString(settings.getNavPlacement()));
        output(out, 4, "includeabstract", makeString(settings.getIncludeAbstract()));
        output(out, 4, "includebibtex", makeString(settings.getIncludeBibtex()));
        output(out, 4, "includepaper", makeString(settings.getIncludePaper()));
        output(out, 4, "titletarget", makeString(settings.getTitleTarget()));
        output(out, 4, "header", makeString(settings.getHeader()));
        output(out, 4, "footer", makeString(settings.getFooter()));
        output(out, 4, "googleanalyticsuser", makeCData(settings.getGoogleAnalyticsUser()));
        output(out, 4, "presentedtext", makeCData(settings.getPresentedText()));

        out.write("  </htmlsettings>");
        out.newLine();
        out.newLine();
    }
    
    private static void writeConsoleSettings(ConsoleSettings settings, BufferedWriter out) throws IOException {
        out.write("  <!-- Console settings -->");
        out.newLine();

        out.write("  <consolesettings>");
        out.newLine();

        output(out, 4, "showWarnings", makeString(settings.isShowWarnings()));
        output(out, 4, "warnMissingReferences", makeString(settings.isWarnMissingReferences()));
        output(out, 4, "warnNotAuthor", makeString(settings.isWarnNotAuthor()));
        output(out, 4, "showLogs", makeString(settings.isShowLogs()));
        output(out, 4, "showStackTraces", makeString(settings.isShowStackTraces()));

        out.write("  </consolesettings>");
        out.newLine();
        out.newLine();
    }

    private static void output(BufferedWriter out, int indent, String tag, String content) throws IOException {
        out.write(indent(indent));
        out.write("<");
        out.write(tag);
        out.write(">");

        if (content != null) {
            out.write(content);
        }

        out.write("</");
        out.write(tag);
        out.write(">");
        out.newLine();
    }

    private static void output(BufferedWriter out, int indent, String tag, String content, String... attributes) throws IOException {
        if (attributes.length % 2 > 0) {
            throw new AssertionError("Attributes must come in (key, value) pairs.");
        }

        out.write(indent(indent));
        out.write("<");
        out.write(tag);

        for (int i = 0; i < attributes.length; i += 2) {
            out.write(" ");
            out.write(attributes[i]);
            out.write("=\"");
            out.write(attributes[i + 1]);
            out.write("\"");
        }

        out.write(">");

        if (content != null) {
            out.write(content);
        }

        out.write("</");
        out.write(tag);
        out.write(">");
        out.newLine();
    }

    private static String indent(int indent) {
        switch (indent) {
            case 0:
                return "";
            case 2:
                return "  ";
            case 4:
                return "    ";
            case 6:
                return "      ";
            default:
                throw new AssertionError("Unexpected indentation number: " + indent);
        }
    }

    private static String makeString(boolean b) {
        return Boolean.toString(b);
    }

    private static String makeString(Path p) {
        // Store all paths in Unix notation
        return ResourceLocator.getRelativePath(p).replaceAll("\\\\", "/");
    }

    private static String makeString(Enum<?> e) {
        return e.name();
    }

    private static String makeCData(String content) {
        return "<![CDATA[" + (content == null ? "" : content) + "]]>";
    }
    
    /**
     * Saves a list of strings as a semicolon-separated string in a CDATA section.
     * @param content
     * @return 
     */
    private static String makeCData(List<String> content) {
        StringBuilder sb = new StringBuilder();
        
        if (content != null) {
            for (String part : content) {
                sb.append(';');
                sb.append(part);
            }
        }
        
        sb.deleteCharAt(0);
        
        return "<![CDATA[" + sb.toString() + "]]>";
    }
}
