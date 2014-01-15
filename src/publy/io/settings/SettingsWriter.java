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
import publy.data.category.OutputCategory;
import publy.data.category.conditions.Condition;
import publy.data.category.conditions.FieldContainsCondition;
import publy.data.category.conditions.FieldEqualsCondition;
import publy.data.category.conditions.FieldExistsCondition;
import publy.data.category.conditions.TypeCondition;
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
        out.write("    <allCategories>");
        out.newLine();

        for (OutputCategory c : settings.getAllCategories()) {
            out.write("      <category>");
            out.newLine();

            // Basic properties
            output(out, 8, "shortName", makeCData(c.getShortName()));
            output(out, 8, "name", makeCData(c.getName()));
            output(out, 8, "htmlNote", makeCData(c.getHtmlNote()));

            // type condition
            writeCondition(out, 8, c.getTypeCondition());

            // field conditions
            out.write("        <fieldConditions>");
            out.newLine();

            for (Condition condition : c.getFieldConditions()) {
                writeCondition(out, 10, condition);
            }

            out.write("        </fieldConditions>");
            out.newLine();
            
            // ignored fields
            output(out, 8, "ignoredFields", makeCData(c.getIgnoredFields()));

            out.write("      </category>");
            out.newLine();
        }

        out.write("    </allCategories>");
        out.newLine();

        // Category notes
        out.write("    <activeCategories>");

        // Reference by index in allCategories
        boolean first = true;
        for (OutputCategory c : settings.getActiveCategories()) {
            if (first) {
                first = false;
            } else {
                out.write(';');
            }

            out.write(Integer.toString(settings.getAllCategories().indexOf(c)));
        }

        out.write("</activeCategories>");
        out.newLine();

        out.write("  </categorySettings>");
        out.newLine();
        out.newLine();
    }

    private static void writeGeneralSettings(GeneralSettings settings, BufferedWriter out) throws IOException {
        out.write("  <!-- General settings -->");
        out.newLine();

        out.write("  <generalSettings>");
        out.newLine();

        output(out, 4, "myNames", makeCData(settings.getMyNames()));
        output(out, 4, "nameDisplay", makeString(settings.getNameDisplay()));
        output(out, 4, "reverseNames", makeString(settings.isReverseNames()));
        output(out, 4, "listAllAuthors", makeString(settings.isListAllAuthors()));
        output(out, 4, "titleFirst", makeString(settings.isTitleFirst()));
        output(out, 4, "useNewLines", makeString(settings.isUseNewLines()));
        output(out, 4, "numbering", makeString(settings.getNumbering()));
        output(out, 4, "reverseNumbering", makeString(settings.isReverseNumbering()));

        out.write("  </generalSettings>");
        out.newLine();
        out.newLine();
    }

    private static void writeHTMLSettings(HTMLSettings settings, BufferedWriter out) throws IOException {
        out.write("  <!-- HTML-specific settings -->");
        out.newLine();

        out.write("  <htmlSettings>");
        out.newLine();

        output(out, 4, "generateTextVersion", makeString(settings.isGenerateTextVersion()));
        output(out, 4, "generateBibtexVersion", makeString(settings.isGenerateBibtexVersion()));
        output(out, 4, "linkToAlternateVersions", makeString(settings.isLinkToAlternateVersions()));
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
        output(out, 4, "warnNoCategoryForItem", makeString(settings.isWarnNoCategoryForItem()));
        output(out, 4, "warnMandatoryFieldIgnored", makeString(settings.isWarnMandatoryFieldIgnored()));
        output(out, 4, "showLogs", makeString(settings.isShowLogs()));
        output(out, 4, "showStackTraces", makeString(settings.isShowStackTraces()));

        out.write("  </consoleSettings>");
        out.newLine();
        out.newLine();
    }

    private static void writeCondition(BufferedWriter out, int indent, Condition condition) throws IOException {
        if (condition instanceof TypeCondition) {
            output(out, indent, "TypeCondition", makeCData(((TypeCondition) condition).getTypes()), "inverted", makeString(condition.isInverted()));
        } else if (condition instanceof FieldExistsCondition) {
            output(out, indent, "FieldExistsCondition", "", "inverted", makeString(condition.isInverted()), "field", ((FieldExistsCondition) condition).getField());
        } else if (condition instanceof FieldEqualsCondition) {
            output(out, indent, "FieldEqualsCondition", makeCData(((FieldEqualsCondition) condition).getValues()), "inverted", makeString(condition.isInverted()), "field", ((FieldEqualsCondition) condition).getField());
        } else if (condition instanceof FieldContainsCondition) {
            output(out, indent, "FieldContainsCondition", makeCData(((FieldContainsCondition) condition).getValues()), "inverted", makeString(condition.isInverted()), "field", ((FieldContainsCondition) condition).getField());
        } else {
            throw new AssertionError("Unknown condition type: " + condition);
        }
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
            case 8:
                return "        ";
            case 10:
                return "          ";
            case 12:
                return "            ";
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

        if (content != null && !content.isEmpty()) {
            for (String part : content) {
                sb.append(';');
                sb.append(part);
            }
            
            sb.deleteCharAt(0);
        }

        return "<![CDATA[" + sb.toString() + "]]>";
    }
}
