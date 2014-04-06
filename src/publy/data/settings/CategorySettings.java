/*
 * Copyright 2013-2014 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.data.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import publy.data.category.OutputCategory;
import publy.data.category.conditions.FieldEqualsCondition;
import publy.data.category.conditions.TypeCondition;

/**
 *
 *
 */
public class CategorySettings {

    private List<OutputCategory> allCategories = new ArrayList<>();
    private List<OutputCategory> activeCategories = new ArrayList<>();

    public List<OutputCategory> getAllCategories() {
        return allCategories;
    }

    public void setAllCategories(List<OutputCategory> allCategories) {
        this.allCategories = new ArrayList<>(allCategories);
    }

    /**
     * Adds a category to the collection of all possible categories. Does not
     * automatically activate this category.
     *
     * @param category
     */
    public void addCategory(OutputCategory category) {
        allCategories.add(category);
    }

    /**
     * Deactivates this category and removes it from the list of all possible
     * categories.
     *
     * @param category
     */
    public void removeCategory(OutputCategory category) {
        allCategories.remove(category);
        activeCategories.remove(category);
    }

    public List<OutputCategory> getActiveCategories() {
        return activeCategories;
    }

    public void setActiveCategories(List<OutputCategory> activeCategories) {
        this.activeCategories = new ArrayList<>(activeCategories);
    }

    /**
     * Adds this category to the list of active categories. The category should
     * already exist in the list of all possible categories.
     *
     * @see #addCategory(publy.data.category.OutputCategory)
     * @param category
     */
    public void activate(OutputCategory category) {
        activeCategories.add(category);
    }

    /**
     * Removes this category from the list of active categories.
     *
     * @param category
     */
    public void deactivate(OutputCategory category) {
        activeCategories.remove(category);
    }
    
    public void setToDefault() {
        // Default categories
        // BOOK
        OutputCategory books = new OutputCategory("Books", "Books", new TypeCondition(false, "book"));
        books.getFieldConditions().add(new FieldEqualsCondition(true, "pubstate", "submitted"));
        books.getIgnoredFields().addAll(Arrays.asList("address"));
        // CHAPTER
        OutputCategory chapters = new OutputCategory("Chapters", "Chapters in Books", new TypeCondition(false, "incollection"));
        chapters.getFieldConditions().add(new FieldEqualsCondition(true, "pubstate", "submitted"));
        chapters.getIgnoredFields().addAll(Arrays.asList("address"));
        // CONFERENCE
        OutputCategory conference = new OutputCategory("Conference", "Conference papers", new TypeCondition(false, "inproceedings", "conference"));
        conference.getFieldConditions().add(new FieldEqualsCondition(true, "pubstate", "submitted"));
        conference.getIgnoredFields().addAll(Arrays.asList("address", "publisher", "editor", "volume", "number", "series"));
        // JOURNAL
        OutputCategory journal = new OutputCategory("Journal", "Journal papers", new TypeCondition(false, "article"));
        journal.getFieldConditions().add(new FieldEqualsCondition(true, "pubstate", "submitted"));
        // OTHER
        OutputCategory other = new OutputCategory("Other", "Other", new TypeCondition(false, "*"));
        other.getFieldConditions().add(new FieldEqualsCondition(true, "pubstate", "submitted"));
        // SUBMITTED
        OutputCategory submitted = new OutputCategory("Submitted", "Currently under review", new TypeCondition(false, "*"));
        submitted.getFieldConditions().add(new FieldEqualsCondition(false, "pubstate", "submitted"));
        // TALK
        OutputCategory talks = new OutputCategory("Talks", "Invited Talks", new TypeCondition(false, "talk"));
        talks.getFieldConditions().add(new FieldEqualsCondition(true, "pubstate", "submitted"));
        // THESIS
        OutputCategory theses = new OutputCategory("Theses", "Theses", new TypeCondition(false, "mastersthesis", "phdthesis"));
        theses.getFieldConditions().add(new FieldEqualsCondition(true, "pubstate", "submitted"));
        // UNPUBLISHED
        OutputCategory unpublished = new OutputCategory("Unpublished", "Unpublished manuscripts", new TypeCondition(false, "unpublished"));

        setAllCategories(Arrays.asList(books, chapters, conference, journal, other, submitted, talks, theses, unpublished));
        
        // Active categories
        setActiveCategories(Arrays.asList(submitted, journal, conference, books, chapters, theses, other));
    }
}
