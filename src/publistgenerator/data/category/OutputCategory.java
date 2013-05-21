/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package publistgenerator.data.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import publistgenerator.data.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public abstract class OutputCategory {
    private String shortName, name;
    private CategoryIdentifier id;
    private List<BibItem> items;

    protected OutputCategory(String shortName, String name, CategoryIdentifier id) {
        this.shortName = shortName;
        this.name = name;
        this.id = id;
        items = new ArrayList<>();
    }
    
    public static OutputCategory fromIdentifier(CategoryIdentifier id) {
        switch (id) {
            case CHAPTER:
                return new BookChapterCategory();
            case CONFERENCE:
                return new ConferenceCategory();
            case JOURNAL:
                return new JournalCategory();
            case OTHER:
                return new OtherCategory();
            case SUBMITTED:
                return new SubmittedCategory();
            case TALK:
                return new TalksCategory();
            case THESIS:
                return new ThesisCategory();
            case UNPUBLISHED:
                return new UnpublishedCategory();
            default:
                throw new AssertionError("Unknown category identifier: " + id.name());
        }
    }

    public List<BibItem> getItems() {
        return items;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public CategoryIdentifier getId() {
        return id;
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
