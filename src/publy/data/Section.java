/*
 * Copyright 2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.data;

import java.util.ArrayList;
import java.util.List;
import publy.data.bibitem.BibItem;
import publy.data.category.OutputCategory;

/**
 * A section of the publication list.
 * <p>
 * This is a named part of the publication list that can contain publications
 * and sub-sections. It also has some settings that modify how the publications
 * in this setting are displayed.
 */
public class Section {

    // Section properties
    private String shortName, name, htmlNote;
    // Bibitems in this section
    private final List<BibItem> items;
    // Sub-sections in this section
    private final List<Section> subsections;
    // Fields that should be ignored for publications in this section
    private final List<String> ignoredFields;

    /**
     * Creates a new section with the given names.
     *
     * @param shortName the one-word description of this section - used to refer
     * to it via links or in the GUI
     * @param name the full description of this section - used as a heading in
     * the publication lists
     */
    public Section(String shortName, String name) {
        this.shortName = shortName;
        this.name = name;
        htmlNote = "";

        items = new ArrayList<>();
        subsections = new ArrayList<>();
        ignoredFields = new ArrayList<>();
    }

    /**
     * Creates a new section and copies all relevant settings from the given
     * category.
     *
     * @param category the OutputCategory that this section is based on.
     */
    public Section(OutputCategory category) {
        shortName = category.getShortName();
        name = category.getName();
        htmlNote = category.getHtmlNote();

        items = new ArrayList<>();
        subsections = new ArrayList<>();
        ignoredFields = category.getIgnoredFields();
    }

    /**
     * Gets the short name of this section.
     * <p>
     * This is a one-word description that is used to refer to this section via
     * links or in the GUI.
     *
     * @return the short name
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the short name of this section.
     * <p>
     * This is a one-word description that is used to refer to this section via
     * links or in the GUI.
     *
     * @param shortName the new short name
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the name of this section.
     * <p>
     * This is a longer description of this section that is used as a heading in
     * the publication lists.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this section.
     * <p>
     * This is a longer description of this section that is used as a heading in
     * the publication lists.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the HTML note of this section.
     * <p>
     * This is text that is displayed immediately after the section heading in
     * the HTML version of the publication list. The text can contain arbitrary
     * HTML code, including figures and structural elements.
     *
     * @return the HTML note
     */
    public String getHtmlNote() {
        return htmlNote;
    }

    /**
     * Sets the HTML note of this section.
     * <p>
     * This is text that is displayed immediately after the section heading in
     * the HTML version of the publication list. The text can contain arbitrary
     * HTML code, including figures and structural elements.
     *
     * @param htmlNote the new HTML note
     */
    public void setHtmlNote(String htmlNote) {
        this.htmlNote = htmlNote;
    }

    /**
     * Gets the items that have been accepted into this section.
     *
     * @return the items
     */
    public List<BibItem> getItems() {
        return items;
    }

    /**
     * Adds a publication to this section.
     *
     * @param item the publication to add
     */
    public void addItem(BibItem item) {
        items.add(item);
    }

    /**
     * Counts all publications in this section and its sub-sections.
     *
     * @return the total number of publications in this section
     */
    public int countAllItems() {
        int count = items.size();

        for (Section subsection : subsections) {
            count += subsection.countAllItems();
        }

        return count;
    }

    /**
     * Gets the sub-sections of this section.
     *
     * @return the sub-sections
     */
    public List<Section> getSubsections() {
        return subsections;
    }

    /**
     * Sets the sub-sections of this section.
     *
     * @param subsections the new sub-sections
     */
    public void setSubsections(List<Section> subsections) {
        this.subsections.clear();
        this.subsections.addAll(subsections);
    }

    /**
     * Gets the ignored fields of this section.
     * <p>
     * All publications in this section will be formatted as if they have no
     * information set for these fields. This helps to make the presentation of
     * publications in the same section more uniform.
     *
     * @return the ignored fields
     */
    public List<String> getIgnoredFields() {
        return ignoredFields;
    }

    /**
     * Sets the ignored fields of this section.
     * <p>
     * All publications in this section will be formatted as if they have no
     * information set for these fields. This helps to make the presentation of
     * publications in the same section more uniform.
     *
     * @param ignoredFields the new ignored fields
     */
    public void setIgnoredFields(List<String> ignoredFields) {
        this.ignoredFields.clear();
        this.ignoredFields.addAll(ignoredFields);
    }

    @Override
    public String toString() {
        return (shortName == null || shortName.isEmpty() ? " (no name) " : shortName);
    }
}
