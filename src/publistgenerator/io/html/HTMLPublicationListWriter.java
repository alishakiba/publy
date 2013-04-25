/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.html;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import publistgenerator.bibitem.BibItem;
import publistgenerator.category.OutputCategory;
import publistgenerator.io.PublicationListWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLPublicationListWriter extends PublicationListWriter {

    private HTMLBibItemWriter itemWriter;
    private File header;
    private File footer;

    public HTMLPublicationListWriter(File header, File footer) {
        super("html");
        this.header = header;
        this.footer = footer;
    }

    @Override
    protected void writePublicationList(List<BibItem> items, BufferedWriter out) throws IOException {
        itemWriter = new HTMLBibItemWriter(out);
        
        // Copy the header from the header file
        copyFile(header, out);

        // Write the body
        out.write(" <p>My publications as of " + (new SimpleDateFormat("d MMMM yyyy")).format(new Date()) + ". Also available as <a href=\"publications.txt\" rel=\"alternate\">plain text</a>.</p>");
        out.newLine();

        for (OutputCategory c : categories) {
            writeCategory(c, out);
        }
        
        // Copy the footer from the footer file
        copyFile(footer, out);
    }
    
    private void copyFile(File inputFile, BufferedWriter out) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line = reader.readLine();

            while (line != null) {
                out.write(line);
                out.newLine();
                line = reader.readLine();
            }
        }
    }

    private void writeCategory(OutputCategory c, BufferedWriter out) throws IOException {
        out.write(" <div class=\"section\"><h1 class=\"sectiontitle\"><a id=\"" + c.getShortName().toLowerCase() + "\">" + c.getName() + "</a></h1>");
        out.newLine();
        out.newLine();
        writeNavigation(c, out);

        if (c.getNote() != null && !c.getNote().isEmpty()) {
            out.write(" <p class=\"indent\">" + c.getNote() + "</p>");
            out.newLine();
            out.newLine();
        }

        for (BibItem item : c.getItems()) {
            out.write("  <div class=\"bibentry\">");
            out.newLine();
            itemWriter.write(item);
            out.write("  </div>");
            out.newLine();
            out.newLine();
        }

        out.write(" </div>");
        out.newLine();
        out.newLine();
    }

    private void writeNavigation(OutputCategory current, BufferedWriter out) throws IOException {
        out.write(" <div class=\"pubnav\">");
        out.newLine();

        for (int i = 0; i < categories.size(); i++) {
            OutputCategory c = categories.get(i);

            out.write("  <a href=\"#");
            out.write(c.getShortName().toLowerCase());
            out.write("\" class=\"");

            if (c == current) {
                out.write("navcurrent\"");
            } else {
                out.write("nav\"");
            }
            
            out.write(" id=\"");
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

        out.write(" </div>");
        out.newLine();
        out.newLine();
    }
}
