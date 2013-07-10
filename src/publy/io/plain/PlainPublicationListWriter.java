/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publy.io.plain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import publy.data.bibitem.BibItem;
import publy.data.category.OutputCategory;
import publy.data.settings.FormatSettings;
import publy.io.PublicationListWriter;

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
        count = 0;

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
                out.write(count + ".");
                out.newLine();
            }

            itemWriter.write(item);
            out.newLine();
        }

        out.newLine();

        // Reset the count if necessary
        if (getSettings().getNumbering() == FormatSettings.Numbering.LOCAL) {
            count = 0;
        }
    }
}
