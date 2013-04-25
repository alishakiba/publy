/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.plain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import publistgenerator.bibitem.BibItem;
import publistgenerator.category.OutputCategory;
import publistgenerator.io.PublicationListWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class PlainPublicationListWriter extends PublicationListWriter {

    private PlainBibItemWriter itemWriter;

    public PlainPublicationListWriter() {
        super("plain");
    }

    @Override
    public void writePublicationList(List<BibItem> items, Map<String, String> categoryNotes, BufferedWriter out) throws IOException {
        itemWriter = new PlainBibItemWriter(out);
        
        // Write the body
        out.write("My publications as of " + (new SimpleDateFormat("d MMMM yyyy")).format(new Date()) + ".");
        out.newLine();
        out.newLine();
        out.newLine();

        for (OutputCategory c : categories) {
            writeCategory(c, out);
        }
    }

    private void writeCategory(OutputCategory c, BufferedWriter out) throws IOException {
        out.write(c.getName() + ".");
        out.newLine();
        out.newLine();

        if (c.getNote() != null && !c.getNote().isEmpty()) {
            out.write(c.getNote());
            out.newLine();
            out.newLine();
        }

        for (BibItem item : c.getItems()) {
            itemWriter.write(item);
            out.newLine();
        }

        out.newLine();
    }
}
