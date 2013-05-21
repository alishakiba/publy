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
import publistgenerator.data.bibitem.BibItem;
import publistgenerator.data.category.CategoryIdentifier;
import publistgenerator.data.category.OutputCategory;
import publistgenerator.data.settings.FormatSettings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public abstract class PublicationListWriter {

    protected List<OutputCategory> categories;
    private FormatSettings settings;

    public PublicationListWriter(FormatSettings settings) {
        this.settings = settings;
        
        categories = new ArrayList<>(settings.getCategories().size());
        
        for (CategoryIdentifier category : settings.getCategories()) {
            categories.add(OutputCategory.fromIdentifier(category));
        }
    }

    public void writePublicationList(List<BibItem> items) {
        categorizePapers(items, settings);

        try (BufferedWriter out = new BufferedWriter(new FileWriter(settings.getTarget()))) {
            writePublicationList(out);
        } catch (IOException ioe) {
            System.err.println("Exception occurred.");
            ioe.printStackTrace();
        }
    }

    protected abstract void writePublicationList(BufferedWriter out) throws IOException;

    protected void categorizePapers(List<BibItem> items, FormatSettings settings) {
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
