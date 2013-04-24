/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package publistgenerator.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import publistgenerator.bibitem.BibItem;
import publistgenerator.category.*;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public abstract class PublicationListWriter {
    protected List<OutputCategory> categories;
    protected String type;

    public PublicationListWriter(String type) {
        this.type = type;
    }

    public void writePublicationList(List<BibItem> items, Map<String, String> categoryNotes, File outputFile) {
        categorizePapers(items);
        setNotes(categoryNotes);
        
        try (BufferedWriter out = new BufferedWriter(new FileWriter(outputFile))) {
            writePublicationList(items, categoryNotes, out);
        } catch (IOException ioe) {
            System.err.println("Exception occurred.");
            ioe.printStackTrace();
        }
    }
    
    public abstract void writePublicationList(List<BibItem> items, Map<String, String> categoryNotes, BufferedWriter out) throws IOException;

    protected void categorizePapers(List<BibItem> items) {
        categories = new ArrayList<>();
        categories.add(new SubmittedCategory());
        categories.add(new JournalCategory());
        categories.add(new ConferenceCategory());
        categories.add(new BookChapterCategory());
        categories.add(new ThesisCategory());
        categories.add(new TalksCategory());
        categories.add(new UnpublishedCategory());
        categories.add(new OtherCategory());

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
    
    protected void setNotes(Map<String, String> categoryNotes) {
        for (OutputCategory c : categories) {
            if (categoryNotes.containsKey(c.getShortName() + type)) {
                c.setNote(categoryNotes.get(c.getShortName() + type));
            }
        }
    }
}
