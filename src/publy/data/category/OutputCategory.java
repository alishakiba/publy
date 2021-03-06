/*
 * Copyright 2013-2016 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
import java.util.Objects;
import publy.data.bibitem.BibItem;
import publy.data.category.conditions.FieldCondition;
import publy.data.category.conditions.TypeCondition;

/**
 * A publication category.
 * <p>
 * This is a group of publications, with several properties that make it easier
 * to manage large numbers of publications. In the output, the publications in
 * each category are grouped together under a heading. Publications that are not
 * included in any category will not be listed.
 * <p>
 * Publications that match the type and field conditions of a category are
 * automatically assigned to that category. If a publication matches multiple
 * categories, it is assigned to the first in the list of categories.
 */
public class OutputCategory {

    // Category properties
    private String shortName, name, htmlNote;
    // Conditions to categorize bibitems
    private TypeCondition typeCondition;
    private List<FieldCondition> fieldConditions;
    // Fields that should be ignored for this category
    private List<String> ignoredFields;

    /**
     * Creates a new category with the given names and type condition.
     *
     * @param shortName the one-word description of this category - used to
     * refer to it via links or in the GUI
     * @param name the full description of this category - used as a heading in
     * the publication lists
     * @param typeCondition a condition on the types of publications that will
     * be accepted into this category
     */
    public OutputCategory(String shortName, String name, TypeCondition typeCondition) {
        this.shortName = shortName;
        this.name = name;
        htmlNote = "";

        this.typeCondition = typeCondition;
        fieldConditions = new ArrayList<>();

        ignoredFields = new ArrayList<>();
    }

    /**
     * Gets the short name of this category.
     * <p>
     * This is a one-word description that is used to refer to this category via
     * links or in the GUI.
     *
     * @return the short name
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the short name of this category.
     * <p>
     * This is a one-word description that is used to refer to this category via
     * links or in the GUI.
     *
     * @param shortName the new short name
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the name of this category.
     * <p>
     * This is a longer description of this category that is used as a heading
     * in the publication lists.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this category.
     * <p>
     * This is a longer description of this category that is used as a heading
     * in the publication lists.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the HTML note of this category.
     * <p>
     * This is text that is displayed immediately after the category heading in
     * the HTML version of the publication list. The text can contain arbitrary
     * HTML code, including figures and structural elements.
     *
     * @return the HTML note
     */
    public String getHtmlNote() {
        return htmlNote;
    }

    /**
     * Sets the HTML note of this category.
     * <p>
     * This is text that is displayed immediately after the category heading in
     * the HTML version of the publication list. The text can contain arbitrary
     * HTML code, including figures and structural elements.
     *
     * @param htmlNote the new HTML note
     */
    public void setHtmlNote(String htmlNote) {
        this.htmlNote = htmlNote;
    }

    /**
     * Gets the type condition of this category.
     * <p>
     * The type condition restricts the types of publications that are accepted
     * into this category. A publication must match both the type and fields
     * conditions to be accepted.
     *
     * @return the type condition
     */
    public TypeCondition getTypeCondition() {
        return typeCondition;
    }

    /**
     * Sets the type condition of this category.
     * <p>
     * The type condition restricts the types of publications that are accepted
     * into this category. A publication must match both the type and fields
     * conditions to be accepted.
     *
     * @param typeCondition the new type condition
     */
    public void setTypeCondition(TypeCondition typeCondition) {
        this.typeCondition = typeCondition;
    }

    /**
     * Gets the field conditions of this category.
     * <p>
     * The field conditions place restrictions on the fields and field values of
     * publications that are accepted into this category. A publication must
     * match both the type and fields conditions to be accepted.
     *
     * @return the field conditions
     */
    public List<FieldCondition> getFieldConditions() {
        return fieldConditions;
    }

    /**
     * Sets the field conditions of this category.
     * <p>
     * The field conditions place restrictions on the fields and field values of
     * publications that are accepted into this category. A publication must
     * match both the type and fields conditions to be accepted.
     *
     * @param fieldConditions the new field conditions
     */
    public void setFieldConditions(List<FieldCondition> fieldConditions) {
        this.fieldConditions = fieldConditions;
    }

    /**
     * Gets the ignored fields of this category.
     * <p>
     * All publications in this category will be formatted as if they have no
     * information set for these fields. This helps to make the presentation of
     * publications in the same category more uniform.
     *
     * @return the ignored fields
     */
    public List<String> getIgnoredFields() {
        return ignoredFields;
    }

    /**
     * Sets the ignored fields of this category.
     * <p>
     * All publications in this category will be formatted as if they have no
     * information set for these fields. This helps to make the presentation of
     * publications in the same category more uniform.
     *
     * @param ignoredFields the new ignored fields
     */
    public void setIgnoredFields(List<String> ignoredFields) {
        this.ignoredFields = ignoredFields;
    }

    /**
     * Checks whether the given publication matches the conditions for inclusion
     * in this category.
     *
     * @param item the publication to check
     * @return whether the publication belongs in this category
     */
    public boolean fitsCategory(BibItem item) {
        if (typeCondition.matches(item)) {
            for (FieldCondition condition : fieldConditions) {
                if (!condition.matches(item)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Implementation note:</b> The hash of this category depends only on
     * {@code shortName} and {@code name}.
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.shortName);
        hash = 83 * hash + Objects.hashCode(this.name);
        return hash;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Implementation note:</b> Two categories are considered equal if they
     * have the same {@code shortName} and {@code name}.
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
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return (shortName == null || shortName.isEmpty() ? " (no name) " : shortName);
    }
}
