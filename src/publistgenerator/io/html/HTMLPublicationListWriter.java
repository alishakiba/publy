/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.html;

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
import publistgenerator.Console;
import publistgenerator.data.bibitem.BibItem;
import publistgenerator.data.category.OutputCategory;
import publistgenerator.data.settings.HTMLSettings;
import publistgenerator.io.PublicationListWriter;
import publistgenerator.io.ResourceLocator;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLPublicationListWriter extends PublicationListWriter {

    public static final String DEFAULT_BASEJS_LOCATION = "data/basejs.html";
    public static final String DEFAULT_GAJS_LOCATION = "data/gajs.html";
    public static final String DEFAULT_HEADER_LOCATION = "data/defaultHeader.html";
    public static final String DEFAULT_FOOTER_LOCATION = "data/defaultFooter.html";
    private HTMLBibItemWriter itemWriter;
    private HTMLSettings settings;
    private int globalCount;
    private Pattern hrefPattern = Pattern.compile("href\\s*=\\s*\"([^\"]*)\"");

    public HTMLPublicationListWriter(HTMLSettings settings) {
        super(settings);
        this.settings = settings;
    }

    @Override
    protected void writePublicationList(BufferedWriter out) throws IOException {
        itemWriter = new HTMLBibItemWriter(out, settings);
        globalCount = 0;

        if (settings.getHeader() == null) {
            publistgenerator.Console.error("No header found. The generated HTML file will not be valid.");
        } else {
            // Copy the header from the header file
            copyFile(settings.getHeader(), out);
        }

        // Write the body
        out.write("    <p>My publications as of " + (new SimpleDateFormat("d MMMM yyyy")).format(new Date()) + ".");

        if (settings.linkToTextVersion() && settings.getSettings().generateText() && settings.getSettings().getPlainSettings().getTarget() != null) {
            Path htmlPage = settings.getTarget().getParent();
            Path plainText = settings.getSettings().getPlainSettings().getTarget();

            out.write(" Also available as <a href=\"");
            out.write(htmlPage.relativize(plainText).toString());
            out.write("\" rel=\"alternate\">plain text</a>.");
        }

        out.write("</p>");
        out.newLine();

        for (OutputCategory c : categories) {
            writeCategory(c, out);
        }

        if (settings.getFooter() == null) {
            publistgenerator.Console.error("No footer found. The generated HTML file will not be valid.");
        } else {
            // Copy the footer from the footer file
            copyFile(settings.getFooter(), out);
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
                if (line.contains("href")) {
                    // See if this is a reference to a file in the data directory
                    Matcher m = hrefPattern.matcher(line);

                    while (m.find()) {
                        ensureReferencedFileExists(m.group(1));
                    }
                }
            }
        }
    }

    private void ensureReferencedFileExists(String file) throws IOException {
        // Check if this file already exists; if so, do nothing
        // Resolve this via URI, to properly handle escaped characters like %20
        Path path = Paths.get(settings.getTarget().getParent().toUri().resolve(file));

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
        int localCount = 0;

        out.write("    <div class=\"section\"><h1 class=\"sectiontitle\"><a id=\"" + c.getShortName().toLowerCase() + "\">" + c.getName() + "</a></h1>");
        out.newLine();
        out.newLine();
        writeNavigation(c, out);

        String note = settings.getCategoryNotes().get(c.getId());

        if (note != null && !note.isEmpty()) {
            out.write("      <p class=\"indent\">");
            out.write(note);
            out.write("</p>");
            out.newLine();
            out.newLine();
        }

        for (BibItem item : c.getItems()) {
            globalCount++;
            localCount++;

            out.write("      <div class=\"bibentry\">");
            out.newLine();

            switch (settings.getNumbering()) {
                case NONE:
                    itemWriter.write(item, -1);
                    break;
                case LOCAL:
                    itemWriter.write(item, localCount);
                    break;
                case GLOBAL:
                    itemWriter.write(item, globalCount);
                    break;
            }

            out.write("      </div>");
            out.newLine();
            out.newLine();
        }

        out.write("    </div>");
        out.newLine();
        out.newLine();
    }

    private void writeNavigation(OutputCategory current, BufferedWriter out) throws IOException {
        out.write("      <p class=\"pubnav\">");
        out.newLine();

        for (int i = 0; i < categories.size(); i++) {
            OutputCategory c = categories.get(i);

            out.write("        <a href=\"#");
            out.write(c.getShortName().toLowerCase());
            out.write("\" class=\"");

            if (c == current) {
                out.write("navcurrent\"");
            } else {
                out.write("nav\"");
            }

            out.write(" id=\"nav_");
            out.write(current.getShortName());
            out.write("To");
            out.write(c.getShortName());
            out.write("\">");

            out.write(c.getShortName());
            out.write("</a>");

            if (i < categories.size() - 1) {
                out.write(" -");
            }

            out.newLine();
        }

        out.write("      </p>");
        out.newLine();
        out.newLine();
    }

    private void writeJavascript(BufferedWriter out) throws IOException {
        Path baseJs = ResourceLocator.getBaseDirectory().resolve(DEFAULT_BASEJS_LOCATION);

        if (Files.exists(baseJs)) {
            copyFile(baseJs, out);
        } else {
            publistgenerator.Console.error("Cannot find base javascript file \"%s\".", baseJs);
        }

        // Google Analytics code
        if (settings.getGoogleAnalyticsUser() != null && !settings.getGoogleAnalyticsUser().isEmpty()) {
            Path gaJs = ResourceLocator.getBaseDirectory().resolve(DEFAULT_GAJS_LOCATION);

            if (Files.exists(gaJs)) {
                out.newLine();

                try (BufferedReader reader = Files.newBufferedReader(gaJs, Charset.forName("UTF-8"))) {
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        if (line.contains("~GAUSERACCOUNT~")) {
                            // Replace the user account place-holder with the actual value
                            out.write(line.replaceAll("~GAUSERACCOUNT~", settings.getGoogleAnalyticsUser()));
                            out.newLine();
                        } else {
                            out.write(line);
                            out.newLine();
                        }
                    }
                }
            } else {
                publistgenerator.Console.error("Cannot find Google Analytics javascript file \"%s\".", gaJs);
            }
        }
    }
}
