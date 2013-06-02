/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
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

    public void writePublicationList(List<BibItem> items) throws IOException {
        categorizePapers(items, settings);

        try (BufferedWriter out = Files.newBufferedWriter(settings.getTarget(), Charset.forName("UTF-8"))) {
            writePublicationList(out);
        }
    }

    protected abstract void writePublicationList(BufferedWriter out) throws IOException;

    protected void categorizePapers(List<BibItem> items, FormatSettings settings) {
        // Make a copy so the population can remove items without removing them from the main list
        List<BibItem> tempItems = new ArrayList<>(items);
        
        // Make a copy with the same categories, but in the right order for the population logic
        List<OutputCategory> populateOrderedCategories = new ArrayList<>(categories.size());
        
        for (CategoryIdentifier id : OutputCategory.populateOrder) {
            for (OutputCategory c : categories) {
                if (c.getId() == id) {
                    // This category is next
                    populateOrderedCategories.add(c);
                    break;
                }
            }
        }

        for (OutputCategory c : populateOrderedCategories) {
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
