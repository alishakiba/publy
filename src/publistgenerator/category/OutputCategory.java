/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package publistgenerator.category;

import java.util.ArrayList;
import java.util.List;
import publistgenerator.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public abstract class OutputCategory {
    private String shortName, name, note;
    private List<BibItem> items;

    public OutputCategory(String shortName, String name) {
        this.shortName = shortName;
        this.name = name;
        items = new ArrayList<BibItem>();
    }

    public List<BibItem> getItems() {
        return items;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void populate(List<BibItem> items) {
        for (BibItem item : items) {
            if (fitsCategory(item)) {
                this.items.add(item);
            }
        }

        for (BibItem item : this.items) {
            items.remove(item);
        }
    }

    public abstract boolean fitsCategory(BibItem item);
}
