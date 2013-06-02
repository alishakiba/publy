/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import publistgenerator.data.category.CategoryIdentifier;
import publistgenerator.data.settings.FormatSettings;
import publistgenerator.data.settings.Settings;
import publistgenerator.gui.MainFrame;
import publistgenerator.io.ResourceLocator;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class SettingsWriter {

    public static void writeSettings(Settings settings) throws IOException {
        File parentDir = new File(SettingsReader.DEFAULT_SETTINGS_LOCATION).getParentFile();

        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Could not create the directory \"" + parentDir.getPath() + "\" to store the settings.");
            }
        }

        try (BufferedWriter out = new BufferedWriter(new FileWriter(SettingsReader.DEFAULT_SETTINGS_LOCATION))) {
            // Write header
            out.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
            out.newLine();
            out.write("<plgsettings>");
            out.newLine();

            writeGeneralSettings(settings, out);
            writePlainTextSettings(settings, out);
            writeHTMLSettings(settings, out);

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

    private static void writePlainTextSettings(Settings settings, BufferedWriter out) throws IOException {
        out.write("  <!-- Settings for plain-text output -->");
        out.newLine();

        output(out, 2, "generateplaintext", makeString(settings.generateText()));
        out.write("  <plaintextsettings>");
        out.newLine();

        writeFormatSettings(settings.getPlainSettings(), out);

        out.write("  </plaintextsettings>");
        out.newLine();
        out.newLine();
    }

    private static void writeHTMLSettings(Settings settings, BufferedWriter out) throws IOException {
        out.write("  <!-- Settings for HTML output -->");
        out.newLine();

        output(out, 2, "generatehtml", makeString(settings.generateHTML()));
        out.write("  <htmlsettings>");
        out.newLine();

        writeFormatSettings(settings.getHtmlSettings(), out);

        out.write("    <!-- HTML-specific settings -->");
        out.newLine();

        output(out, 4, "linktotextversion", makeString(settings.getHtmlSettings().linkToTextVersion()));
        output(out, 4, "includeabstract", makeString(settings.getHtmlSettings().getIncludeAbstract()));
        output(out, 4, "includebibtex", makeString(settings.getHtmlSettings().getIncludeBibtex()));
        output(out, 4, "includepdf", makeString(settings.getHtmlSettings().getIncludePaper()));
        output(out, 4, "header", makeString(settings.getHtmlSettings().getHeader()));
        output(out, 4, "footer", makeString(settings.getHtmlSettings().getFooter()));
        output(out, 4, "googleanalyticsuser", makeCData(settings.getHtmlSettings().getGoogleAnalyticsUser()));

        out.write("  </htmlsettings>");
        out.newLine();
        out.newLine();
    }

    private static void writeFormatSettings(FormatSettings format, BufferedWriter out) throws IOException {
        output(out, 4, "target", makeString(format.getTarget()));
        output(out, 4, "listallauthors", makeString(format.isListAllAuthors()));
        output(out, 4, "presentedtext", makeCData(format.getPresentedText()));
        output(out, 4, "numbering", makeString(format.getNumbering()));

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

    private static String makeString(File f) {
        return ResourceLocator.getRelativePath(f);
    }

    private static String makeString(Enum e) {
        return e.name();
    }

    private static String makeCData(String content) {
        return "<![CDATA[" + content + "]]>";
    }
}
