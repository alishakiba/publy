/*
 * Copyright 2013 Sander Verdonschot <sander.verdonschot at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import publy.data.settings.CategorySettings;
import publy.data.settings.ConsoleSettings;
import publy.data.settings.FileSettings;
import publy.data.settings.GeneralSettings;
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

            writeFileSettings(settings.getFileSettings(), out);
            writeCategorySettings(settings.getCategorySettings(), out);
            writeGeneralSettings(settings.getGeneralSettings(), out);
            writeHTMLSettings(settings.getHtmlSettings(), out);
            writeConsoleSettings(settings.getConsoleSettings(), out);

            // Write footer
            out.write("</plgsettings>");
            out.newLine();
        }
    }

    private static void writeFileSettings(FileSettings settings, BufferedWriter out) throws IOException {
        out.write("  <!-- File settings -->");
        out.newLine();

        out.write("  <fileSettings>");
        out.newLine();

        output(out, 4, "publications", makeString(settings.getPublications()));
        output(out, 4, "target", makeString(settings.getTarget()));
        output(out, 4, "header", makeString(settings.getHeader()));
        output(out, 4, "footer", makeString(settings.getFooter()));

        out.write("  </fileSettings>");
        out.newLine();
        out.newLine();
    }

    private static void writeCategorySettings(CategorySettings settings, BufferedWriter out) throws IOException {
        out.write("  <!-- Category settings -->");
        out.newLine();

        out.write("  <categorySettings>");
        out.newLine();

        // Categories
        out.write("    <categories>");
        out.newLine();

        for (CategoryIdentifier cid : settings.getCategories()) {
            output(out, 6, "category", makeString(cid));
        }

        out.write("    </categories>");
        out.newLine();

        // Category notes
        out.write("    <categorynotes>");
        out.newLine();

        for (Map.Entry<CategoryIdentifier, String> entry : settings.getCategoryNotes().entrySet()) {
            output(out, 6, "note", makeCData(entry.getValue()), "category", makeString(entry.getKey()));
        }

        out.write("    </categorynotes>");
        out.newLine();

        out.write("  </categorySettings>");
        out.newLine();
        out.newLine();
    }

    private static void writeGeneralSettings(GeneralSettings format, BufferedWriter out) throws IOException {
        out.write("  <!-- General settings -->");
        out.newLine();

        out.write("  <generalSettings>");
        out.newLine();

        output(out, 4, "myNames", makeCData(format.getMyNames()));
        output(out, 4, "nameDisplay", makeString(format.getNameDisplay()));
        output(out, 4, "reverseNames", makeString(format.reverseNames()));
        output(out, 4, "listAllAuthors", makeString(format.listAllAuthors()));
        output(out, 4, "titleFirst", makeString(format.titleFirst()));
        output(out, 4, "numbering", makeString(format.getNumbering()));
        output(out, 4, "reverseNumbering", makeString(format.reverseNumbering()));

        out.write("  </generalSettings>");
        out.newLine();
        out.newLine();
    }

    private static void writeHTMLSettings(HTMLSettings settings, BufferedWriter out) throws IOException {
        out.write("  <!-- HTML-specific settings -->");
        out.newLine();

        out.write("  <htmlSettings>");
        out.newLine();

        output(out, 4, "generateTextVersion", makeString(settings.generateTextVersion()));
        output(out, 4, "generateBibtexVersion", makeString(settings.generateBibtexVersion()));
        output(out, 4, "linkToAlternateVersions", makeString(settings.linkToAlternateVersions()));
        output(out, 4, "navPlacement", makeString(settings.getNavPlacement()));
        output(out, 4, "includeAbstract", makeString(settings.getIncludeAbstract()));
        output(out, 4, "includeBibtex", makeString(settings.getIncludeBibtex()));
        output(out, 4, "includePaper", makeString(settings.getIncludePaper()));
        output(out, 4, "titleTarget", makeString(settings.getTitleTarget()));
        output(out, 4, "presentedText", makeCData(settings.getPresentedText()));
        output(out, 4, "googleAnalyticsUser", makeCData(settings.getGoogleAnalyticsUser()));

        out.write("  </htmlSettings>");
        out.newLine();
        out.newLine();
    }

    private static void writeConsoleSettings(ConsoleSettings settings, BufferedWriter out) throws IOException {
        out.write("  <!-- Console settings -->");
        out.newLine();

        out.write("  <consoleSettings>");
        out.newLine();

        output(out, 4, "showWarnings", makeString(settings.isShowWarnings()));
        output(out, 4, "warnMissingReferences", makeString(settings.isWarnMissingReferences()));
        output(out, 4, "warnNotAuthor", makeString(settings.isWarnNotAuthor()));
        output(out, 4, "showLogs", makeString(settings.isShowLogs()));
        output(out, 4, "showStackTraces", makeString(settings.isShowStackTraces()));

        out.write("  </consoleSettings>");
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
     * Saves a list of strings as a semicolon-separated string in a CDATA
     * section.
     *
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
