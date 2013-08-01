/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import publy.Console;
import publy.data.bibitem.BibItem;
import publy.data.category.OutputCategory;
import publy.data.settings.FormatSettings;
import publy.data.settings.HTMLSettings;
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
    private HTMLSettings htmlSettings;

    public HTMLPublicationListWriter(FormatSettings generalSettings, HTMLSettings htmlSettings) {
        super(generalSettings);
        this.htmlSettings = htmlSettings;
    }

    @Override
    protected void writePublicationList(BufferedWriter out) throws IOException {
        itemWriter = new HTMLBibItemWriter(out, getSettings(), htmlSettings);

        // Initialize the count
        if (getSettings().getNumbering() == FormatSettings.Numbering.GLOBAL) {
            if (getSettings().isReverseNumbering()) {
                count = 0;

                for (OutputCategory c : getCategories()) {
                    count += c.getItems().size();
                }
            } else {
                count = 1;
            }
        }

        if (htmlSettings.getHeader() == null) {
            publy.Console.error("No header found. The generated HTML file will not be valid.");
        } else {
            // Copy the header from the header file
            copyFile(htmlSettings.getHeader(), out);
        }

        // Write the body
        out.write("    <p>My publications as of " + (new SimpleDateFormat("d MMMM yyyy")).format(new Date()) + ".");

        if (htmlSettings.linkToAlternateVersions() && (htmlSettings.generateTextVersion() || htmlSettings.generateBibtexVersion())) {
            Path htmlDir = getSettings().getTarget().getParent();

            out.write(" Also available as <a href=\"");

            if (htmlSettings.generateTextVersion()) {
                out.write(htmlDir.relativize(getSettings().getPlainTextTarget()).toString());
                out.write("\" rel=\"alternate\">plain text</a>");

                if (htmlSettings.generateBibtexVersion()) {
                    out.write(" or <a href=\"");
                    out.write(htmlDir.relativize(getSettings().getBibtexTarget()).toString());
                    out.write("\" rel=\"alternate\">BibTeX</a>.");
                } else {
                    out.write(".");
                }
            } else {
                out.write(htmlDir.relativize(getSettings().getBibtexTarget()).toString());
                out.write("\" rel=\"alternate\">BibTeX</a>.");
            }
        }

        out.write("</p>");
        out.newLine();

        // Navigation?
        if (htmlSettings.getNavPlacement() == HTMLSettings.NavigationPlacement.TOP
                || htmlSettings.getNavPlacement() == HTMLSettings.NavigationPlacement.TOP_AND_BOTTOM) {
            writeNavigation(out);
        }

        out.newLine();
        for (OutputCategory c : getCategories()) {
            writeCategory(c, out);
        }
        
        // Navigation?
        if (htmlSettings.getNavPlacement() == HTMLSettings.NavigationPlacement.TOP_AND_BOTTOM
                || htmlSettings.getNavPlacement() == HTMLSettings.NavigationPlacement.BEFORE_SECTION_AND_BOTTOM) {
            writeNavigation(out);
        }

        // Credit line
        out.write("    <p>Generated from a BibTeX file by Publy " + UIConstants.MAJOR_VERSION + "." + UIConstants.MINOR_VERSION + ".</p>");
        out.newLine();

        if (htmlSettings.getFooter() == null) {
            publy.Console.error("No footer found. The generated HTML file will not be valid.");
        } else {
            // Copy the footer from the footer file
            copyFile(htmlSettings.getFooter(), out);
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
        // Check if this file already exists; if so, do nothing
        // Resolve this via URI, to properly handle escaped characters like %20
        Path path = Paths.get(getSettings().getTarget().getParent().toUri().resolve(file));

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
                Console.log("Warning: Referenced file \"%s\" not found at \"%s\".", file, path);
            }
        }
    }

    private void writeCategory(OutputCategory c, BufferedWriter out) throws IOException {
        // Section start
        out.write("    <div id=\"" + c.getShortName().toLowerCase() + "\" class=\"section\">");
        out.newLine();

        // Navigation?
        if (htmlSettings.getNavPlacement() == HTMLSettings.NavigationPlacement.BEFORE_SECTION_TITLE
                || htmlSettings.getNavPlacement() == HTMLSettings.NavigationPlacement.BEFORE_SECTION_AND_BOTTOM) {
            writeNavigation(c, out);
        }

        // Section title
        out.write("      <h2 class=\"section-title\">" + c.getName() + "</h2>");
        out.newLine();

        // Navigation?
        if (htmlSettings.getNavPlacement() == HTMLSettings.NavigationPlacement.AFTER_SECTION_TITLE) {
            writeNavigation(c, out);
        }

        // Note
        String note = getSettings().getCategoryNotes().get(c.getId());

        if (note != null && !note.isEmpty()) {
            out.write("      <p class=\"section-note\">");
            out.write(note);
            out.write("</p>");
            out.newLine();
            out.newLine();
        }

        // Section list start
        if (getSettings().getNumbering() == FormatSettings.Numbering.NONE) {
            out.write("      <ul class=\"section-list\">"); // Unordered list
        } else if (getSettings().getNumbering() == FormatSettings.Numbering.LOCAL) {
            out.write("      <ol class=\"section-list\">");
            // Reset the count
            if (getSettings().isReverseNumbering()) {
                count = c.getItems().size();
                // There is limited browser support for the reversed attribute, so we'll add values manually if needed
            } else {
                count = 0;
            }
        } else if (getSettings().isReverseNumbering()) { // GLOBAL reversed
            assert getSettings().getNumbering() == FormatSettings.Numbering.GLOBAL && getSettings().isReverseNumbering();
            out.write("      <ol class=\"section-list\">");
        } else { // GLOBAL, not reversed
            assert getSettings().getNumbering() == FormatSettings.Numbering.GLOBAL && !getSettings().isReverseNumbering();
            out.write("      <ol class=\"section-list\" start=\"" + count + "\">");
        }
        out.newLine();

        // The actual entries
        for (BibItem item : c.getItems()) {
            if (getSettings().isReverseNumbering()) {
                out.write("        <li id=\"" + item.getId() + "\" value=\"" + count + "\" class=\"bibentry\">");
                count--;
            } else {
                out.write("        <li id=\"" + item.getId() + "\" class=\"bibentry\">");
                count++;
            }

            out.newLine();

            itemWriter.write(item);

            out.write("        </li>");
            out.newLine();
            out.newLine();
        }

        // Section list end
        if (getSettings().getNumbering() == FormatSettings.Numbering.NONE) {
            out.write("      </ul>");
        } else { // LOCAL or GLOBAL
            assert (getSettings().getNumbering() == FormatSettings.Numbering.LOCAL || getSettings().getNumbering() == FormatSettings.Numbering.GLOBAL);
            out.write("      </ol>");
        }
        out.newLine();

        // Section end
        out.write("    </div>");
        out.newLine();
        out.newLine();
    }

    private void writeNavigation(BufferedWriter out) throws IOException {
        writeNavigation(null, out);
    }

    private void writeNavigation(OutputCategory current, BufferedWriter out) throws IOException {
        out.newLine();
        out.write("      <p class=\"navigation\">");
        out.newLine();

        for (int i = 0; i < getCategories().size(); i++) {
            OutputCategory c = getCategories().get(i);

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
        if (htmlSettings.getGoogleAnalyticsUser() != null && !htmlSettings.getGoogleAnalyticsUser().isEmpty()) {
            Path gaJs = ResourceLocator.getBaseDirectory().resolve(DEFAULT_GAJS_LOCATION);

            if (Files.exists(gaJs)) {
                // Include the JavaScript file
                out.newLine();
                out.write("    <!-- Google Analytics JavaScript file -->");
                out.newLine();
                out.write("    <script src=\"" + gaJs.getFileName() + "\"></script>");
                out.newLine();

                // Copy the JavaScript file, substituting the user account placeholder
                Path gaJsTarget = getSettings().getTarget().getParent().resolve(gaJs.getFileName());

                try (BufferedReader reader = Files.newBufferedReader(gaJs, Charset.forName("UTF-8"));
                        BufferedWriter writer = Files.newBufferedWriter(gaJsTarget, Charset.forName("UTF-8"))) {
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        writer.write(line.replaceAll("%%GAUSERACCOUNT%%", htmlSettings.getGoogleAnalyticsUser()));
                        writer.newLine();
                    }
                }
            } else {
                publy.Console.error("Cannot find Google Analytics JavaScript file \"%s\".", gaJs);
            }
        }
    }
}
