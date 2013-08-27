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
import publy.data.category.conditions.Condition;
import publy.data.category.conditions.FieldEqualsCondition;
import publy.data.category.conditions.InverseCondition;
import publy.data.category.conditions.TypeCondition;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class OutputCategory {

    // Category properties
    private String shortName, name;
    private CategoryIdentifier id;
    // Conditions to categorize bibitems
    private TypeCondition typeCondition;
    private List<Condition> fieldConditions;
    // Bibitems in this category
    private List<BibItem> items;

    protected OutputCategory(String shortName, String name, CategoryIdentifier id) {
        this.shortName = shortName;
        this.name = name;
        this.id = id;
        items = new ArrayList<>();
    }

    public static OutputCategory fromIdentifier(CategoryIdentifier id) {
        OutputCategory result = new OutputCategory(null, null, id);
        List<Condition> fieldConditions = new ArrayList<>(2);
        
        switch (id) {
            case BOOK:
                result.setShortName("Books");
                result.setName("Books");
                result.setTypeCondition(new TypeCondition("book"));
                fieldConditions.add(new InverseCondition(new FieldEqualsCondition("status", "submitted")));
                break;
            case CHAPTER:
                result.setShortName("Chapters");
                result.setName("Chapters in Books");
                result.setTypeCondition(new TypeCondition("incollection"));
                fieldConditions.add(new InverseCondition(new FieldEqualsCondition("status", "submitted")));
                break;
            case CONFERENCE:
                result.setShortName("Conference");
                result.setName("Conference papers");
                result.setTypeCondition(new TypeCondition("inproceedings", "conference"));
                fieldConditions.add(new InverseCondition(new FieldEqualsCondition("status", "submitted")));
                break;
            case JOURNAL:
                result.setShortName("Journal");
                result.setName("Journal papers");
                result.setTypeCondition(new TypeCondition("article"));
                fieldConditions.add(new InverseCondition(new FieldEqualsCondition("status", "submitted")));
                break;
            case OTHER:
                result.setShortName("Other");
                result.setName("Other");
                result.setTypeCondition(new TypeCondition("*"));
                fieldConditions.add(new InverseCondition(new FieldEqualsCondition("status", "submitted")));
                break;
            case SUBMITTED:
                result.setShortName("Submitted");
                result.setName("Currently under review");
                result.setTypeCondition(new TypeCondition("*"));
                fieldConditions.add(new FieldEqualsCondition("status", "submitted"));
                break;
            case TALK:
                result.setShortName("Talks");
                result.setName("Invited Talks");
                result.setTypeCondition(new TypeCondition("talk"));
                fieldConditions.add(new InverseCondition(new FieldEqualsCondition("status", "submitted")));
                break;
            case THESIS:
                result.setShortName("Theses");
                result.setName("Theses");
                result.setTypeCondition(new TypeCondition("mastersthesis", "phdthesis"));
                fieldConditions.add(new InverseCondition(new FieldEqualsCondition("status", "submitted")));
                break;
            case UNPUBLISHED:
                result.setShortName("Unpublished");
                result.setName("Unpublished manuscripts");
                result.setTypeCondition(new TypeCondition("unpublished"));
                break;
            default:
                throw new AssertionError("Unknown category identifier: " + id.name());
        }
        
        result.setFieldConditions(fieldConditions);
        
        return result;
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

    public CategoryIdentifier getId() {
        return id;
    }

    public void setId(CategoryIdentifier id) {
        this.id = id;
    }

    public TypeCondition getTypeCondition() {
        return typeCondition;
    }

    public void setTypeCondition(TypeCondition typeCondition) {
        this.typeCondition = typeCondition;
    }

    public List<Condition> getFieldConditions() {
        return fieldConditions;
    }

    public void setFieldConditions(List<Condition> fieldConditions) {
        this.fieldConditions = fieldConditions;
    }
    
    public List<BibItem> getItems() {
        return items;
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
            for (Condition condition : fieldConditions) {
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
}
