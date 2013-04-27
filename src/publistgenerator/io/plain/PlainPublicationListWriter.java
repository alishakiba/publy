/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.plain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import publistgenerator.bibitem.BibItem;
import publistgenerator.category.OutputCategory;
import publistgenerator.io.PublicationListWriter;
import publistgenerator.settings.FormatSettings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class PlainPublicationListWriter extends PublicationListWriter {

    private PlainBibItemWriter itemWriter;

    @Override
    protected void writePublicationList(BufferedWriter out, FormatSettings settings) throws IOException {
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
