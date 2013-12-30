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
import java.util.Objects;
import publy.data.bibitem.BibItem;
import publy.data.category.conditions.FieldCondition;
import publy.data.category.conditions.TypeCondition;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class OutputCategory implements Cloneable {

    // Category properties
    private String shortName, name, htmlNote;
    // Conditions to categorize bibitems
    private TypeCondition typeCondition;
    private List<FieldCondition> fieldConditions;
    // Bibitems in this category
    private List<BibItem> items;
    // Fields that should be ignored for this category
    private List<String> ignoredFields;

    public OutputCategory(String shortName, String name, TypeCondition typeCondition) {
        this.shortName = shortName;
        this.name = name;
        htmlNote = "";
        
        this.typeCondition = typeCondition;
        fieldConditions = new ArrayList<>();
        
        items = new ArrayList<>();
        ignoredFields = new ArrayList<>();
    }
    
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHtmlNote() {
        return htmlNote;
    }

    public void setHtmlNote(String htmlNote) {
        this.htmlNote = htmlNote;
    }

    public TypeCondition getTypeCondition() {
        return typeCondition;
    }

    public void setTypeCondition(TypeCondition typeCondition) {
        this.typeCondition = typeCondition;
    }

    public List<FieldCondition> getFieldConditions() {
        return fieldConditions;
    }

    public void setFieldConditions(List<FieldCondition> fieldConditions) {
        this.fieldConditions = fieldConditions;
    }
    
    public List<BibItem> getItems() {
        return items;
    }

    public List<String> getIgnoredFields() {
        return ignoredFields;
    }

    public void setIgnoredFields(List<String> ignoredFields) {
        this.ignoredFields = ignoredFields;
    }

    /**
     * Removes all items that match this category's conditions from the given
     * list and adds them to the category.
     *
     * @param items
     */
    public void populate(List<BibItem> items) {
        for (ListIterator<BibItem> it = items.listIterator(); it.hasNext();) {
            BibItem item = it.next();

            if (fitsCategory(item)) {
                this.items.add(item);
                it.remove();
            }
        }
    }

    private boolean fitsCategory(BibItem item) {
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
     * Equals and hashcode rely solely on the names, so categories that contain
     * different items still appear the same.
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.shortName);
        hash = 83 * hash + Objects.hashCode(this.name);
        return hash;
    }

    /**
     * Equals and hashcode rely solely on the names, so categories that contain
     * different items still appear the same.
     *
     * @param obj
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
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        OutputCategory result = (OutputCategory) super.clone();
        
        result.items = new ArrayList<>(items);
        
        return result;
    }

    @Override
    public String toString() {
        return (shortName == null || shortName.isEmpty() ? " (no name) " : shortName);
    }
}
