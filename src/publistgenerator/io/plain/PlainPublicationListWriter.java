/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.plain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import publistgenerator.data.bibitem.BibItem;
import publistgenerator.data.category.OutputCategory;
import publistgenerator.data.settings.FormatSettings;
import publistgenerator.io.PublicationListWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class PlainPublicationListWriter extends PublicationListWriter {

    private PlainBibItemWriter itemWriter;
    private int count;

    public PlainPublicationListWriter(FormatSettings settings) {
        super(settings);
    }

    @Override
    protected void writePublicationList(BufferedWriter out) throws IOException {
        itemWriter = new PlainBibItemWriter(out, getSettings());

        if (getSettings().getNumbering() == FormatSettings.Numbering.NONE) {
            count = -1;
        } else {
            count = 0;
        }

        // Write the body
        out.write("My publications as of " + (new SimpleDateFormat("d MMMM yyyy")).format(new Date()) + ".");
        out.newLine();
        out.newLine();
        out.newLine();

        for (OutputCategory c : getCategories()) {
            writeCategory(c, out);
        }
    }

    private void writeCategory(OutputCategory c, BufferedWriter out) throws IOException {
        out.write(c.getName() + ".");
        out.newLine();
        out.newLine();

        String note = getSettings().getCategoryNotes().get(c.getId());

        if (note != null && !note.isEmpty()) {
            out.write(note);
            out.newLine();
            out.newLine();
        }

        for (BibItem item : c.getItems()) {
            if (getSettings().getNumbering() != FormatSettings.Numbering.NONE) {
                count++;
            }

            itemWriter.write(item, count);
            out.newLine();
        }

        out.newLine();

        // Reset the count if necessary
        if (getSettings().getNumbering() == FormatSettings.Numbering.LOCAL) {
            count = 0;
        }
    }
}
