/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.html;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import publistgenerator.data.bibitem.BibItem;
import publistgenerator.data.category.OutputCategory;
import publistgenerator.data.settings.HTMLSettings;
import publistgenerator.io.PublicationListWriter;

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

        if (settings.linkToTextVersion()) {
            out.write(" Also available as <a href=\"");
            out.write(settings.getSettings().getPlainSettings().getTarget().getPath());
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

    private void copyFile(File inputFile, BufferedWriter out) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
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
        File baseJs = new File(DEFAULT_BASEJS_LOCATION);

        if (baseJs.exists()) {
            copyFile(baseJs, out);
        } else {
            publistgenerator.Console.error("Cannot find base javascript file \"%s\".", baseJs.getPath());
        }

        // Google Analytics code
        if (settings.getGoogleAnalyticsUser() != null && !settings.getGoogleAnalyticsUser().isEmpty()) {
            File gaJs = new File(DEFAULT_GAJS_LOCATION);

            if (gaJs.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(gaJs))) {
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
                publistgenerator.Console.error("Cannot find Google Analytics javascript file \"%s\".", gaJs.getPath());
            }
        }
    }
}
