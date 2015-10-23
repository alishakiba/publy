/*
 * Copyright 2013-2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.io.html;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import publy.Console;
import publy.data.Section;
import publy.data.bibitem.BibItem;
import publy.data.settings.GeneralSettings;
import publy.data.settings.HTMLSettings;
import publy.data.settings.Settings;
import publy.Constants;
import publy.io.PublicationListWriter;
import publy.io.ResourceLocator;
import publy.io.TempWriter;

/**
 *
 *
 */
public class HTMLPublicationListWriter extends PublicationListWriter {

    private static final String GENERATED_COMMENT
            = "DO NOT EDIT! This file was automatically generated by Publy. "
            + "All changes to this file will be lost the next time Publy is run. "
            + "Change the template files in the 'data' directory instead.";
    private static final String DEFAULT_BASEJS_LOCATION = "data/base.js";
    private static final String DEFAULT_GAJS_LOCATION = "data/ga.js";
    private static final Pattern LINK_PATTERN = Pattern.compile("(href|src)\\s*=\\s*\"([^\"]*)\"");
    private HTMLBibItemWriter itemWriter;
    private int count;

    public HTMLPublicationListWriter(Settings settings) {
        super(settings);
    }

    @Override
    protected void writePublicationList(List<Section> sections, BufferedWriter out) throws IOException {
        itemWriter = new HTMLBibItemWriter(out, settings);

        copyAuxiliaryFiles();
        initializeCount(sections);
        writePreamble(out, sections);

        for (Section s : sections) {
            writeSection(s, sections, new ArrayList<Section>(), out);
        }

        writePostamble(sections, out);
    }

    private void writePreamble(BufferedWriter out, List<Section> sections) throws IOException {
        // Header
        if (settings.getFileSettings().getHeader() == null) {
            publy.Console.error("No header found. The generated HTML file will not be valid.");
        } else {
            // Copy the header from the header file
            copyFileToWriter(settings.getFileSettings().getHeader(), out);
        }

        // Alternate version links
        if (settings.getHtmlSettings().isLinkToAlternateVersions() && (settings.getHtmlSettings().isGenerateTextVersion() || settings.getHtmlSettings().isGenerateBibtexVersion())) {
            Path htmlDir = settings.getFileSettings().getTarget().getParent();

            out.write("    <p id=\"alternates\">This list is also available as <a href=\"");

            if (settings.getHtmlSettings().isGenerateTextVersion()) {
                out.write(htmlDir.relativize(settings.getFileSettings().getPlainTextTarget()).toString());
                out.write("\" rel=\"alternate\">plain text</a>");

                if (settings.getHtmlSettings().isGenerateBibtexVersion()) {
                    out.write(" or <a href=\"");
                    out.write(htmlDir.relativize(settings.getFileSettings().getBibtexTarget()).toString());
                    out.write("\" rel=\"alternate\">BibTeX</a>.");
                } else {
                    out.write(".");
                }
            } else {
                out.write(htmlDir.relativize(settings.getFileSettings().getBibtexTarget()).toString());
                out.write("\" rel=\"alternate\">BibTeX</a>.");
            }

            out.write("</p>");
            out.newLine();
        }

        // Start publication list
        out.write("    <div id=\"publicationlist\">");
        out.newLine();

        // Navigation?
        if (settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.TOP
                || settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.TOP_AND_BOTTOM) {
            writeNavigation(sections, out);
        }

        out.newLine();
    }

    private void writePostamble(List<Section> sections, BufferedWriter out) throws IOException {
        // Navigation?
        if (settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.TOP_AND_BOTTOM
                || settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.BEFORE_SECTION_AND_BOTTOM) {
            writeNavigation(sections, out);
        }

        // End publication list
        out.write("    </div>");
        out.newLine();
        out.newLine();

        // Credit line and last modified
        out.write("    <p>Generated by <a href=\"https://bitbucket.org/Mangara/publy\">Publy " + Constants.MAJOR_VERSION + "." + Constants.MINOR_VERSION + "</a>.&nbsp;&nbsp;Last modified on " + (new SimpleDateFormat("d MMMM yyyy")).format(new Date()) + ".</p>");
        out.newLine();

        if (settings.getFileSettings().getFooter() == null) {
            publy.Console.error("No footer found. The generated HTML file will not be valid.");
        } else {
            // Copy the footer from the footer file
            copyFileToWriter(settings.getFileSettings().getFooter(), out);
        }
    }

    private void initializeCount(List<Section> sections) {
        if (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.GLOBAL) {
            if (settings.getGeneralSettings().isReverseNumbering()) {
                count = 0;

                for (Section s : sections) {
                    count += s.countAllItems();
                }
            } else {
                count = 1;
            }
        }
    }

    private void copyFileToWriter(Path inputFile, BufferedWriter out) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(inputFile, Charset.forName("UTF-8"))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                // Detect the end of the HEAD element, so we can insert the Google Analytics Javascript
                int headIndex = line.indexOf("</head>");

                if (headIndex > -1) {
                    // Splice in required Javascript
                    out.write(line.substring(0, headIndex));
                    out.newLine();

                    writeJavascript(out);

                    out.write(line.substring(headIndex));
                    out.newLine();
                } else {
                    out.write(line);
                    out.newLine();
                }

                if (line.toLowerCase().contains("doctype")) {
                    // Generated file warning. This needs to be after the
                    // DOCTYPE, otherwise it triggers quirks mode in older
                    // versions of IE.
                    out.write("<!-- " + GENERATED_COMMENT + " -->");
                    out.newLine();
                    out.newLine();
                }

                // Check if referenced files exist, warn if they do not
                Matcher m = LINK_PATTERN.matcher(line);

                while (m.find()) {
                    String path = m.group(2);

                    if (!path.startsWith("http")) {
                        ensureReferencedFileExists(path);
                    }
                }
            }
        }
    }

    private void ensureReferencedFileExists(String file) {
        try {
            // First strip anything following a '#', as that's most likely a link inside the document
            String strippedFile = file;

            if (strippedFile.contains("#")) {
                strippedFile = strippedFile.substring(0, strippedFile.indexOf('#'));

                if (strippedFile.isEmpty()) {
                    return;
                }
            }

            // Resolve this via URI, to properly handle escaped characters like %20
            Path path = Paths.get(settings.getFileSettings().getTarget().getParent().toUri().resolve(strippedFile));

            if (Files.notExists(path)) {
                Console.warn(Console.WarningType.MISSING_REFERENCE, "Referenced file \"%s\" was not found at \"%s\".", file, path);
            }
        } catch (Exception e) {
            Console.warn(Console.WarningType.MISSING_REFERENCE, "Exception while checking if file \"%s\" exists: %s", file, e.toString());
        }
    }

    private void writeSection(Section section, List<Section> sections, List<Section> parents, BufferedWriter out) throws IOException {
        // Section start
        indent(out, 2 * parents.size());
        out.write("      <div id=\"" + getSectionId(section, parents) + "\" class=\"section\">");
        out.newLine();

        // Navigation?
        if (parents.isEmpty() && (settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.BEFORE_SECTION_TITLE
                || settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.BEFORE_SECTION_AND_BOTTOM)) {
            writeNavigation(sections, section, out);
        }

        // Section title
        if (parents.isEmpty()) {
            out.write("        <h2 class=\"section-title\">" + section.getName() + "</h2>");
        } else {
            indent(out, 2 * parents.size());
            out.write("        <h3 class=\"section-title\">" + section.getName() + "</h3>");
        }
        out.newLine();

        // Navigation?
        if (parents.isEmpty() && settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.AFTER_SECTION_TITLE) {
            writeNavigation(sections, section, out);
        }

        // Note
        String note = section.getHtmlNote();

        if (note != null && !note.isEmpty()) {
            indent(out, 2 * parents.size());
            out.write("        <p class=\"section-note\">");
            out.write(note);
            out.write("</p>");
            out.newLine();
            out.newLine();
        }

        if (!section.getItems().isEmpty()) {
            // Section list start
            indent(out, 2 * parents.size());
            if (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.NO_NUMBERS) {
                out.write("        <ul class=\"section-list\">"); // Unordered list
            } else if (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.WITHIN_CATEGORIES) {
                out.write("        <ol class=\"section-list\">");
                // Reset the count
                if (settings.getGeneralSettings().isReverseNumbering()) {
                    count = section.getItems().size();
                    // There is limited browser support for the reversed attribute, so we'll add values manually if needed
                } else {
                    count = 0;
                }
            } else if (settings.getGeneralSettings().isReverseNumbering()) { // GLOBAL reversed
                assert settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.GLOBAL && settings.getGeneralSettings().isReverseNumbering();
                out.write("        <ol class=\"section-list\">");
            } else { // GLOBAL, not reversed
                assert settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.GLOBAL && !settings.getGeneralSettings().isReverseNumbering();
                out.write("        <ol class=\"section-list\" start=\"" + count + "\">");
            }
            out.newLine();

            itemWriter.setIndentationLevel(12 + 2 * parents.size());
            itemWriter.setIgnoredFields(new HashSet<>(section.getIgnoredFields()));

            // The actual entries
            for (BibItem item : section.getItems()) {
                indent(out, 2 * parents.size());

                if (settings.getGeneralSettings().isReverseNumbering()) {
                    out.write("          <li id=\""
                            + (Character.isDigit(item.getId().charAt(0)) ? 'p' : "")
                            + item.getId()
                            + "\" value=\""
                            + count
                            + "\" "
                            + "class=\"bibentry "
                            + item.getOriginalType()
                            + "\">");
                    count--;
                } else {
                    out.write("          <li id=\""
                            + (Character.isDigit(item.getId().charAt(0)) ? 'p' : "")
                            + item.getId()
                            + "\" class=\"bibentry "
                            + item.getOriginalType()
                            + "\">");
                    count++;
                }

                out.newLine();

                itemWriter.write(item);

                indent(out, 2 * parents.size());
                out.write("          </li>");
                out.newLine();
                out.newLine();
            }

            // Section list end
            indent(out, 2 * parents.size());
            switch (settings.getGeneralSettings().getNumbering()) {
                case NO_NUMBERS:
                    out.write("        </ul>");
                    break;
                case WITHIN_CATEGORIES: // fallthrough
                case GLOBAL:
                    out.write("        </ol>");
                    break;
                default:
                    throw new AssertionError("Unknown Numbering: " + settings.getGeneralSettings().getNumbering());
            }
            out.newLine();
        } else {
            out.newLine();
        }

        // Write the sub-sections
        parents.add(section);
        for (Section subsection : section.getSubsections()) {
            writeSection(subsection, sections, parents, out);
        }
        parents.remove(section);

        // Section end
        indent(out, 2 * parents.size());
        out.write("      </div>");
        out.newLine();
        out.newLine();
    }

    private static String getSectionId(Section section, List<Section> parents) {
        String id;

        if (parents == null || parents.isEmpty()) {
            id = section.getShortName().toLowerCase();
        } else {
            StringBuilder sb = new StringBuilder();

            for (Section parent : parents) {
                sb.append(parent.getShortName().toLowerCase());
                sb.append('-');
            }

            sb.append(section.getShortName().toLowerCase());

            id = sb.toString();
        }

        // In HTML4, ids are not allowed to start with a number. This might cause CSS selector rules to fail.
        return (Character.isDigit(id.charAt(0)) ? 's' + id : id);
    }

    private void writeNavigation(List<Section> sections, BufferedWriter out) throws IOException {
        writeNavigation(sections, null, out);
    }

    private void writeNavigation(List<Section> sections, Section current, BufferedWriter out) throws IOException {
        out.newLine();
        out.write("      <p class=\"navigation\">");
        out.newLine();

        for (Section s : sections) {
            if (s == current) {
                out.write("        <span class=\"current\">" + s.getShortName() + "</span>");
            } else {
                out.write("        <a href=\"#" + getSectionId(s, null) + "\">" + s.getShortName() + "</a>");
            }
            out.newLine();
        }

        out.write("      </p>");
        out.newLine();
        out.newLine();
    }

    private void writeJavascript(BufferedWriter out) throws IOException {
        // Google Analytics code
        if (settings.getHtmlSettings().getGoogleAnalyticsUser() != null
                && !settings.getHtmlSettings().getGoogleAnalyticsUser().isEmpty()) {
            out.newLine();
            out.write("    <!-- Google Analytics JavaScript file -->");
            out.newLine();
            out.write("    <script src=\"ga.js\"></script>");
            out.newLine();
        }
    }

    private void indent(BufferedWriter out, int indentationDepth) throws IOException {
        for (int i = 0; i < indentationDepth; i++) {
            out.write(" ");
        }
    }

    private void copyAuxiliaryFiles() throws IOException {
        Path targetDir = settings.getFileSettings().getTarget().getParent();

        // Copy style to 'pubstyle.css'
        Path styleFile = settings.getHtmlSettings().getTheme();
        Path targetStyleFile = targetDir.resolve("pubstyle.css");
        copyAuxiliaryFile(styleFile, targetStyleFile);

        // Copy base.js
        Path baseJS = ResourceLocator.getFullPath(DEFAULT_BASEJS_LOCATION);
        Path targetBaseJS = targetDir.resolve("base.js");
        copyAuxiliaryFile(baseJS, targetBaseJS);

        // Copy ga.js
        if (settings.getHtmlSettings().getGoogleAnalyticsUser() != null
                && !settings.getHtmlSettings().getGoogleAnalyticsUser().isEmpty()) {
            Path gaJS = ResourceLocator.getBaseDirectory().resolve(DEFAULT_GAJS_LOCATION);
            Path targetGaJS = targetDir.resolve("ga.js");

            copyAuxiliaryFile(gaJS, targetGaJS, "/* ", " */", "~GAUSERACCOUNT~", settings.getHtmlSettings().getGoogleAnalyticsUser());
        }
    }

    private void copyAuxiliaryFile(Path source, Path target) throws IOException {
        copyAuxiliaryFile(source, target, "/* ", " */", null, null);
    }

    private void copyAuxiliaryFile(Path source, Path target, String commentStart, String commentEnd, String toReplace, String replaceWith) throws IOException {
        if (Files.notExists(source)) {
            Console.warn(Console.WarningType.MISSING_REFERENCE, "Auxiliary file \"%s\" not found at \"%s\".", source.getFileName(), source);
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(source, Charset.forName("UTF-8"));
                TempWriter writer = TempWriter.newTempWriter(target)) {
            // Add warning that this is a generated file
            writer.write(commentStart + GENERATED_COMMENT + commentEnd);
            writer.newLine();
            writer.newLine();

            // Copy the file contents
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                writer.write(toReplace == null ? line : line.replaceAll(toReplace, replaceWith));
                writer.newLine();
            }

            writer.copyWrittenFileOnClose();
        }
    }
}
