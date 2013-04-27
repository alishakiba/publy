/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package publistgenerator.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import publistgenerator.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public abstract class OutputCategory {
    private String shortName, name;
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

    /**
     * Equals and hashcode rely solely on short name, so categories that contain different items still appear the same.
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.shortName);
        return hash;
    }

    /**
     * Equals and hashcode rely solely on short name, so categories that contain different items still appear the same.
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OutputCategory other = (OutputCategory) obj;
        if (!Objects.equals(this.shortName, other.shortName)) {
            return false;
        }
        return true;
    }
}
