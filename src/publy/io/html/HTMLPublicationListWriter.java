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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import publy.Console;
import publy.data.bibitem.BibItem;
import publy.data.category.OutputCategory;
import publy.data.settings.GeneralSettings;
import publy.data.settings.HTMLSettings;
import publy.data.settings.Settings;
import publy.gui.UIConstants;
import publy.io.PublicationListWriter;
import publy.io.ResourceLocator;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLPublicationListWriter extends PublicationListWriter {

    public static final String DEFAULT_BASEJS_LOCATION = "data/base.js";
    public static final String DEFAULT_GAJS_LOCATION = "data/ga.js";
    public static final String DEFAULT_HEADER_LOCATION = "data/defaultHeader.html";
    public static final String DEFAULT_FOOTER_LOCATION = "data/defaultFooter.html";
    private HTMLBibItemWriter itemWriter;
    private int count;
    private Pattern linkPattern = Pattern.compile("(href|src)\\s*=\\s*\"([^\"]*)\"");

    public HTMLPublicationListWriter(Settings settings) {
        super(settings);
    }

    @Override
    protected void writePublicationList(List<OutputCategory> categories, BufferedWriter out) throws IOException {
        itemWriter = new HTMLBibItemWriter(out, categories, settings);

        // Initialize the count
        if (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.GLOBAL) {
            if (settings.getGeneralSettings().reverseNumbering()) {
                count = 0;

                for (OutputCategory c : categories) {
                    count += c.getItems().size();
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
        if (settings.getHtmlSettings().linkToAlternateVersions() && (settings.getHtmlSettings().generateTextVersion() || settings.getHtmlSettings().generateBibtexVersion())) {
            Path htmlDir = settings.getFileSettings().getTarget().getParent();

            out.write("    <p id=\"alternates\">This list is also available as <a href=\"");

            if (settings.getHtmlSettings().generateTextVersion()) {
                out.write(htmlDir.relativize(settings.getFileSettings().getPlainTextTarget()).toString());
                out.write("\" rel=\"alternate\">plain text</a>");

                if (settings.getHtmlSettings().generateBibtexVersion()) {
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
            writeNavigation(categories, out);
        }

        out.newLine();
        for (OutputCategory c : categories) {
            writeCategory(c, categories, out);
        }

        // Navigation?
        if (settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.TOP_AND_BOTTOM
                || settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.BEFORE_SECTION_AND_BOTTOM) {
            writeNavigation(categories, out);
        }

        // Credit line and last modified
        out.write("    <p>Generated from a BibTeX file by Publy " + UIConstants.MAJOR_VERSION + "." + UIConstants.MINOR_VERSION + ".&nbsp;&nbsp;Last modified on " + (new SimpleDateFormat("d MMMM yyyy")).format(new Date()) + ".</p>");
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
                    Matcher m = linkPattern.matcher(line);

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

    private void ensureReferencedFileExists(String file) throws IOException {
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

    private void writeCategory(OutputCategory c, List<OutputCategory> categories, BufferedWriter out) throws IOException {
        // Section start
        out.write("    <div id=\"" + c.getShortName().toLowerCase() + "\" class=\"section\">");
        out.newLine();

        // Navigation?
        if (settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.BEFORE_SECTION_TITLE
                || settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.BEFORE_SECTION_AND_BOTTOM) {
            writeNavigation(categories, c, out);
        }

        // Section title
        out.write("      <h2 class=\"section-title\">" + c.getName() + "</h2>");
        out.newLine();

        // Navigation?
        if (settings.getHtmlSettings().getNavPlacement() == HTMLSettings.NavigationPlacement.AFTER_SECTION_TITLE) {
            writeNavigation(categories, c, out);
        }

        // Note
        String note = c.getHtmlNote();

        if (note != null && !note.isEmpty()) {
            out.write("      <p class=\"section-note\">");
            out.write(note);
            out.write("</p>");
            out.newLine();
            out.newLine();
        }

        // Section list start
        if (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.NONE) {
            out.write("      <ul class=\"section-list\">"); // Unordered list
        } else if (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.LOCAL) {
            out.write("      <ol class=\"section-list\">");
            // Reset the count
            if (settings.getGeneralSettings().reverseNumbering()) {
                count = c.getItems().size();
                // There is limited browser support for the reversed attribute, so we'll add values manually if needed
            } else {
                count = 0;
            }
        } else if (settings.getGeneralSettings().reverseNumbering()) { // GLOBAL reversed
            assert settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.GLOBAL && settings.getGeneralSettings().reverseNumbering();
            out.write("      <ol class=\"section-list\">");
        } else { // GLOBAL, not reversed
            assert settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.GLOBAL && !settings.getGeneralSettings().reverseNumbering();
            out.write("      <ol class=\"section-list\" start=\"" + count + "\">");
        }
        out.newLine();

        itemWriter.setIgnoredFields(new HashSet<>(c.getIgnoredFields()));
        
        // The actual entries
        for (BibItem item : c.getItems()) {
            if (settings.getGeneralSettings().reverseNumbering()) {
                out.write("        <li id=\"" + item.getId() + "\" value=\"" + count + "\" class=\"bibentry " + item.getOriginalType() + "\">");
                count--;
            } else {
                out.write("        <li id=\"" + item.getId() + "\" class=\"bibentry " + item.getOriginalType() + "\">");
                count++;
            }

            out.newLine();

            itemWriter.write(item);

            out.write("        </li>");
            out.newLine();
            out.newLine();
        }

        // Section list end
        if (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.NONE) {
            out.write("      </ul>");
        } else { // LOCAL or GLOBAL
            assert (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.LOCAL || settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.GLOBAL);
            out.write("      </ol>");
        }
        out.newLine();

        // Section end
        out.write("    </div>");
        out.newLine();
        out.newLine();
    }

    private void writeNavigation(List<OutputCategory> categories, BufferedWriter out) throws IOException {
        writeNavigation(categories, null, out);
    }

    private void writeNavigation(List<OutputCategory> categories, OutputCategory current, BufferedWriter out) throws IOException {
        out.newLine();
        out.write("      <p class=\"navigation\">");
        out.newLine();

        for (int i = 0; i < categories.size(); i++) {
            OutputCategory c = categories.get(i);

            if (c == current) {
                out.write("        <span class=\"current\">" + c.getShortName() + "</span>");
            } else {
                out.write("        <a href=\"#" + c.getShortName().toLowerCase() + "\">" + c.getShortName() + "</a>");
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
                        BufferedWriter writer = Files.newBufferedWriter(gaJsTarget, Charset.forName("UTF-8"))) {
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        writer.write(line.replaceAll("~GAUSERACCOUNT~", settings.getHtmlSettings().getGoogleAnalyticsUser()));
                        writer.newLine();
                    }
                }
            } else {
                publy.Console.error("Cannot find Google Analytics JavaScript file \"%s\".", gaJs);
            }
        }
    }
}
