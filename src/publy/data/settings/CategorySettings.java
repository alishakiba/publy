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
import java.util.Collections;
import java.util.List;
import publy.data.category.OutputCategory;
import publy.data.category.conditions.FieldEqualsCondition;
import publy.data.category.conditions.TypeCondition;

/**
 * All settings that relate to the publication categories.
 * <p>
 * This contains both default and user-defined categories (these are treated in
 * the exact same way. Not all categories need to be in active use. This class
 * provides methods to activate and deactivate categories.
 */
public class CategorySettings {

    private final List<OutputCategory> allCategories = new ArrayList<>();
    private final List<OutputCategory> activeCategories = new ArrayList<>();

    /**
     * Gets all available categories.
     *
     * @return an unmodifiable view of the list of all categories
     */
    public List<OutputCategory> getAllCategories() {
        return Collections.unmodifiableList(allCategories);
    }

    /**
     * Sets all available categories.
     *
     * @param allCategories a list of the new categories
     */
    public void setAllCategories(List<OutputCategory> allCategories) {
        this.allCategories.clear();
        this.allCategories.addAll(allCategories);
    }

    /**
     * Adds a category to the list of all possible categories.
     * <p>
     * The new category is not yet active.
     *
     * @param category the new category
     */
    public void addCategory(OutputCategory category) {
        allCategories.add(category);
    }

    /**
     * Removes the category from the list of all possible categories.
     * <p>
     * If the category is active, it is first deactivated.
     *
     * @param category the category to remove
     */
    public void removeCategory(OutputCategory category) {
        allCategories.remove(category);
        activeCategories.remove(category);
    }

    /**
     * Gets all active categories.
     *
     * @return an unmodifiable view of the list of active categories
     */
    public List<OutputCategory> getActiveCategories() {
        return Collections.unmodifiableList(activeCategories);
    }

    /**
     * Sets all active categories.
     * <p>
     * These should already be in the list of all categories. If they are not,
     * the behaviour is undefined.
     *
     * @param activeCategories the new active categories
     */
    public void setActiveCategories(List<OutputCategory> activeCategories) {
        this.activeCategories.clear();
        this.activeCategories.addAll(activeCategories);
    }

    /**
     * Adds this category to the list of active categories.
     * <p>
     * The category should already exist in the list of all possible categories.
     * If it does not, the behaviour is undefined.
     *
     * @see #addCategory(publy.data.category.OutputCategory)
     * @param category the category to activate
     */
    public void activate(OutputCategory category) {
        activeCategories.add(category);
    }

    /**
     * Removes this category from the list of active categories.
     *
     * @param category the category to deactivate
     */
    public void deactivate(OutputCategory category) {
        activeCategories.remove(category);
    }

    /**
     * Sets the list of all categories and active categories to their default values.
     */
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
