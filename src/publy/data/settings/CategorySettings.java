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
package publy.data.settings;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import publy.data.category.CategoryIdentifier;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class CategorySettings {

    private List<CategoryIdentifier> categories = new ArrayList<>();
    private Map<CategoryIdentifier, String> categoryNotes = new EnumMap<>(CategoryIdentifier.class);

    public static CategorySettings defaultSettings() {
        CategorySettings result = new CategorySettings();

        // Default categories
        result.addCategory(CategoryIdentifier.JOURNAL);
        result.addCategory(CategoryIdentifier.CONFERENCE);
        result.addCategory(CategoryIdentifier.CHAPTER);
        result.addCategory(CategoryIdentifier.THESIS);

        return result;
    }

    public void addCategory(CategoryIdentifier c) {
        categories.add(c);
    }

    public Map<CategoryIdentifier, String> getCategoryNotes() {
        return categoryNotes;
    }

    public List<CategoryIdentifier> getCategories() {
        return categories;
    }

    public void setNote(CategoryIdentifier c, String note) {
        categoryNotes.put(c, note);
    }
}
