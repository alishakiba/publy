/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.bibtex;

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
public class BibtexPublicationListWriter extends PublicationListWriter {

    private int count;

    public BibtexPublicationListWriter(FormatSettings settings) {
        super(settings);
    }

    @Override
    protected void writePublicationList(BufferedWriter out) throws IOException {
        if (getSettings().getNumbering() == FormatSettings.Numbering.NONE) {
            count = -1;
        } else {
            count = 0;
        }

        // Write the body
        out.write("-- My publications as of " + (new SimpleDateFormat("d MMMM yyyy")).format(new Date()) + ".");
        out.newLine();
        out.newLine();
        out.newLine();

        for (OutputCategory c : getCategories()) {
            writeCategory(c, out);
        }
    }

    private void writeCategory(OutputCategory c, BufferedWriter out) throws IOException {
        out.write("-- " + c.getName() + ".");
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

                out.write("-- " + count + ".");
                out.newLine();
            }

            writeBibtex(item, out);

            out.newLine();
        }

        out.newLine();

        // Reset the count if necessary
        if (getSettings().getNumbering() == FormatSettings.Numbering.LOCAL) {
            count = 0;
        }
    }

    private void writeBibtex(BibItem item, BufferedWriter out) throws IOException {
        // Item type
        out.write("@" + item.getType() + "{" + item.getId() + ",");
        out.newLine();

        // The first field should omit the connecting ",".
        boolean first = true;

        // Get the proper format for authors
        if (item.anyNonEmpty("author")) {
            out.write("  author={");

            for (int i = 0; i < item.getAuthors().size(); i++) {
                out.write(item.getAuthors().get(i).getLatexName());

                if (i < item.getAuthors().size() - 1) {
                    out.write(" and ");
                }
            }

            out.write("}");

            first = false;
        }

        for (String field : item.getMandatoryFields()) {
            if (!field.equals("author")) {
                if (first) {
                    first = false;
                } else {
                    out.write(",");
                    out.newLine();
                }

                out.write("  ");
                out.write(field);
                out.write("={");
                out.write(item.get(field));
                out.write("}");
            }
        }

        for (String field : item.getOptionalFields()) {
            String v = item.get(field);

            if (!field.equals("author") && v != null && !v.isEmpty()) {
                if (first) {
                    first = false;
                } else {
                    out.write(",");
                    out.newLine();
                }

                out.write("  ");
                out.write(field);
                out.write("={");
                out.write(item.get(field));
                out.write("}");
            }
        }
        
        out.newLine();
        out.write("}");
        out.newLine();
    }
}
