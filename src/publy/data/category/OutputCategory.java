/*
 * Copyright 2013 Sander Verdonschot <sander.verdonschot at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package publy.data.category;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import publy.data.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public abstract class OutputCategory {
    public static final CategoryIdentifier[] populateOrder = new CategoryIdentifier[] {
        // Submitted items first
        CategoryIdentifier.SUBMITTED,
        // Then everything that only takes accepted items
        CategoryIdentifier.BOOK, CategoryIdentifier.CHAPTER, CategoryIdentifier.CONFERENCE, CategoryIdentifier.JOURNAL, CategoryIdentifier.THESIS,
        // Then items for which it is unlikely that their status will ever matter
        CategoryIdentifier.TALK, CategoryIdentifier.UNPUBLISHED,
        // Finally all items that do not fit these categories
        CategoryIdentifier.OTHER
    };
    
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
            case BOOK:
                return new BookCategory();
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
        for (ListIterator<BibItem> it = items.listIterator(); it.hasNext();) {
            BibItem item = it.next();
            
            if (fitsCategory(item)) {
                this.items.add(item);
                it.remove();
            }
        }
    }

    public abstract boolean fitsCategory(BibItem item);

    /**
     * Equals and hashcode rely solely on the identifier, so categories that contain different items still appear the same.
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    /**
     * Equals and hashcode rely solely on the identifier, so categories that contain different items still appear the same.
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
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
}
