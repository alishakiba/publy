/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import publistgenerator.bibitem.BibItem;
import publistgenerator.category.*;
import publistgenerator.settings.FormatSettings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public abstract class PublicationListWriter {

    protected List<OutputCategory> categories;

    public void writePublicationList(List<BibItem> items, FormatSettings settings) {
        categorizePapers(items, settings);

        try (BufferedWriter out = new BufferedWriter(new FileWriter(settings.getTarget()))) {
            writePublicationList(out, settings);
        } catch (IOException ioe) {
            System.err.println("Exception occurred.");
            ioe.printStackTrace();
        }
    }

    protected abstract void writePublicationList(BufferedWriter out, FormatSettings settings) throws IOException;

    protected void categorizePapers(List<BibItem> items, FormatSettings settings) {
        categories = new ArrayList<>(settings.getCategories());
        List<BibItem> tempItems = new ArrayList<>(items);

        for (OutputCategory c : categories) {
            c.populate(tempItems);
        }

        // Remove empty categories
        ListIterator<OutputCategory> it = categories.listIterator();

        while (it.hasNext()) {
            OutputCategory c = it.next();

            if (c.getItems().isEmpty()) {
                it.remove();
            }
        }
    }
}
