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
package publy.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import publy.data.bibitem.BibItem;
import publy.data.category.CategoryIdentifier;
import publy.data.category.OutputCategory;
import publy.data.settings.Settings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public abstract class PublicationListWriter {

    private List<OutputCategory> categories;
    protected Settings settings;

    public PublicationListWriter(Settings settings) {
        this.settings = settings;

        categories = new ArrayList<>(settings.getCategorySettings().getCategories().size());

        for (CategoryIdentifier category : settings.getCategorySettings().getCategories()) {
            categories.add(OutputCategory.fromIdentifier(category));
        }
    }

    public void writePublicationList(List<BibItem> items, Path target) throws IOException {
        categorizePapers(items);

        try (BufferedWriter out = Files.newBufferedWriter(target, Charset.forName("UTF-8"))) {
            writePublicationList(out);
        }
    }

    protected abstract void writePublicationList(BufferedWriter out) throws IOException;

    private void categorizePapers(List<BibItem> items) {
        // Make a copy so the categories can remove items without removing them from the main list
        List<BibItem> tempItems = new ArrayList<>(items);

        for (OutputCategory c : categories) {
            c.populate(tempItems);
        }

        // Remove empty categories
        ListIterator<OutputCategory> it = categories.listIterator();

        while (it.hasNext()) {
            OutputCategory c = it.next();

            if (c.getItems().isEmpty()) {
                it.remove();
            }
        }
    }

    public List<OutputCategory> getCategories() {
        return categories;
    }
}
