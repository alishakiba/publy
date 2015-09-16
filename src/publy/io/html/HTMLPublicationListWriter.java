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
import java.nio.file.DirectoryStream;
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
import publy.gui.UIConstants;
import publy.io.PublicationListWriter;
import publy.io.ResourceLocator;
import publy.io.TempWriter;

/**
 *
 *
 */
public class HTMLPublicationListWriter extends PublicationListWriter {

    public static final String DEFAULT_BASEJS_LOCATION = "data/base.js";
    public static final String DEFAULT_GAJS_LOCATION = "data/ga.js";
    public static final String DEFAULT_HEADER_LOCATION = "data/defaultHeader.html";
    public static final String DEFAULT_FOOTER_LOCATION = "data/defaultFooter.html";
    private final static Pattern LINK_PATTERN = Pattern.compile("(href|src)\\s*=\\s*\"([^\"]*)\"");
    private HTMLBibItemWriter itemWriter;
    private int count;

    public HTMLPublicationListWriter(Settings settings) {
        super(settings);
    }

    @Override
    protected void writePublicationList(List<Section> sections, BufferedWriter out) throws IOException {
        itemWriter = new HTMLBibItemWriter(out, sections, settings);

        // Initialize the count
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

        // Header
        if (settings.getFileSettings().getHeader() == null) {
            publy.Console.error("No header found. The generated HTML file will not be valid.");
        } else {
            // Copy the header from the header file
            copyFile(settings.getFileSettings().getHeader(), out);
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

        // Navigation?
        if (settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.TOP
                || settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.TOP_AND_BOTTOM) {
            writeNavigation(sections, out);
        }

        out.newLine();
        for (Section s : sections) {
            writeSection(s, sections, new ArrayList<Section>(), out);
        }

        // Navigation?
        if (settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.TOP_AND_BOTTOM
                || settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.BEFORE_SECTION_AND_BOTTOM) {
            writeNavigation(sections, out);
        }

        // Credit line and last modified
        out.write("    <p>Generated by <a href=\"https://bitbucket.org/Mangara/publy\">Publy " + UIConstants.MAJOR_VERSION + "." + UIConstants.MINOR_VERSION + "</a>.&nbsp;&nbsp;Last modified on " + (new SimpleDateFormat("d MMMM yyyy")).format(new Date()) + ".</p>");
        out.newLine();

        if (settings.getFileSettings().getFooter() == null) {
            publy.Console.error("No footer found. The generated HTML file will not be valid.");
        } else {
            // Copy the footer from the footer file
            copyFile(settings.getFileSettings().getFooter(), out);
        }
    }

    private void copyFile(Path inputFile, BufferedWriter out) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(inputFile, Charset.forName("UTF-8"))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                int index = line.indexOf("</head>");

                if (index > -1) {
                    // Splice in required Javascript
                    out.write(line.substring(0, index));
                    out.newLine();

                    writeJavascript(out);

                    out.write(line.substring(index));
                    out.newLine();
                } else {
                    out.write(line);
                    out.newLine();
                }

                // Copy referenced files from the data directory to the output directory
                if (line.contains("href") || line.contains("src")) {
                    // See if this is a reference to a file in the data directory
                    Matcher m = LINK_PATTERN.matcher(line);

                    while (m.find()) {
                        String path = m.group(2);

                        if (!path.startsWith("http")) {
                            ensureReferencedFileExists(m.group(2));
                        }
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
            }

            // Resolve this via URI, to properly handle escaped characters like %20
            Path path = Paths.get(settings.getFileSettings().getTarget().getParent().toUri().resolve(strippedFile));

            // Check if this file already exists; if so, do nothing
            if (Files.notExists(path)) {
                // Grab the file name
                Path fileName = path.getFileName();

                // See if there is a file with this name in data
                boolean found = false;
                Path dataDir = ResourceLocator.getBaseDirectory().resolve(DEFAULT_BASEJS_LOCATION).getParent();

                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dataDir)) {
                    for (Path dataFile : stream) {
                        if (Files.isRegularFile(dataFile) && fileName.equals(dataFile.getFileName())) {
                            // Copy this file to the target directory
                            Files.copy(dataFile, path);
                            found = true;
                            break;
                        }
                    }
                }

                if (!found) {
                    Console.warn(Console.WarningType.MISSING_REFERENCE, "Referenced file \"%s\" not found at \"%s\".", file, path);
                }
            }
        } catch (Exception e) {
            Console.warn(Console.WarningType.MISSING_REFERENCE, "Exception while checking if file \"%s\" exists: %s", file, e.toString());
        }
    }

    private void writeSection(Section section, List<Section> sections, List<Section> parents, BufferedWriter out) throws IOException {
        // Section start
        indent(out, 2 * parents.size());
        out.write("    <div id=\"" + getSectionId(section, parents) + "\" class=\"section\">");
        out.newLine();

        // Navigation?
        if (parents.isEmpty() && (settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.BEFORE_SECTION_TITLE
                || settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.BEFORE_SECTION_AND_BOTTOM)) {
            writeNavigation(sections, section, out);
        }

        // Section title
        if (parents.isEmpty()) {
            out.write("      <h2 class=\"section-title\">" + section.getName() + "</h2>");
        } else {
            indent(out, 2 * parents.size());
            out.write("      <h3 class=\"section-title\">" + section.getName() + "</h3>");
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
            out.write("      <p class=\"section-note\">");
            out.write(note);
            out.write("</p>");
            out.newLine();
            out.newLine();
        }

        if (!section.getItems().isEmpty()) {
            // Section list start
            indent(out, 2 * parents.size());
            if (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.NO_NUMBERS) {
                out.write("      <ul class=\"section-list\">"); // Unordered list
            } else if (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.WITHIN_CATEGORIES) {
                out.write("      <ol class=\"section-list\">");
                // Reset the count
                if (settings.getGeneralSettings().isReverseNumbering()) {
                    count = section.getItems().size();
                    // There is limited browser support for the reversed attribute, so we'll add values manually if needed
                } else {
                    count = 0;
                }
            } else if (settings.getGeneralSettings().isReverseNumbering()) { // GLOBAL reversed
                assert settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.GLOBAL && settings.getGeneralSettings().isReverseNumbering();
                out.write("      <ol class=\"section-list\">");
            } else { // GLOBAL, not reversed
                assert settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.GLOBAL && !settings.getGeneralSettings().isReverseNumbering();
                out.write("      <ol class=\"section-list\" start=\"" + count + "\">");
            }
            out.newLine();

            itemWriter.setIndentationLevel(10 + 2 * parents.size());
            itemWriter.setIgnoredFields(new HashSet<>(section.getIgnoredFields()));

            // The actual entries
            for (BibItem item : section.getItems()) {
                indent(out, 2 * parents.size());
                
                if (settings.getGeneralSettings().isReverseNumbering()) {
                    out.write("        <li id=\"" + item.getId() + "\" value=\"" + count + "\" class=\"bibentry " + item.getOriginalType() + "\">");
                    count--;
                } else {
                    out.write("        <li id=\"" + item.getId() + "\" class=\"bibentry " + item.getOriginalType() + "\">");
                    count++;
                }

                out.newLine();

                itemWriter.write(item);

                indent(out, 2 * parents.size());
                out.write("        </li>");
                out.newLine();
                out.newLine();
            }

            // Section list end
            indent(out, 2 * parents.size());
            if (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.NO_NUMBERS) {
                out.write("      </ul>");
            } else { // LOCAL or GLOBAL
                assert (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.WITHIN_CATEGORIES || settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.GLOBAL);
                out.write("      </ol>");
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
        out.write("    </div>");
        out.newLine();
        out.newLine();
    }

    private static String getSectionId(Section section, List<Section> parents) {
        if (parents == null || parents.isEmpty()) {
            return section.getShortName().toLowerCase();
        } else {
            StringBuilder sb = new StringBuilder();

            for (Section parent : parents) {
                sb.append(parent.getShortName().toLowerCase());
                sb.append('-');
            }

            sb.append(section.getShortName().toLowerCase());
            return sb.toString();
        }
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
        if (settings.getHtmlSettings().getGoogleAnalyticsUser() != null && !settings.getHtmlSettings().getGoogleAnalyticsUser().isEmpty()) {
            Path gaJs = ResourceLocator.getBaseDirectory().resolve(DEFAULT_GAJS_LOCATION);

            if (Files.exists(gaJs)) {
                // Include the JavaScript file
                out.newLine();
                out.write("    <!-- Google Analytics JavaScript file -->");
                out.newLine();
                out.write("    <script src=\"" + gaJs.getFileName() + "\"></script>");
                out.newLine();

                // Copy the JavaScript file, substituting the user account placeholder
                Path gaJsTarget = settings.getFileSettings().getTarget().getParent().resolve(gaJs.getFileName());

                try (BufferedReader reader = Files.newBufferedReader(gaJs, Charset.forName("UTF-8"));
                        TempWriter writer = TempWriter.newTempWriter(gaJsTarget)) {
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        writer.write(line.replaceAll("~GAUSERACCOUNT~", settings.getHtmlSettings().getGoogleAnalyticsUser()));
                        writer.newLine();
                    }

                    writer.copyWrittenFileOnClose();
                }
            } else {
                publy.Console.error("Cannot find Google Analytics JavaScript file \"%s\".", gaJs);
            }
        }
    }

    private void indent(BufferedWriter out, int indentationDepth) throws IOException {
        for (int i = 0; i < indentationDepth; i++) {
            out.write(" ");
        }
    }
}
