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
package publy.io.bibtex;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import publy.data.Author;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.FieldData;
import publy.data.settings.Settings;
import publy.io.BibItemWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class BibtexBibItemWriter extends BibItemWriter {

    public BibtexBibItemWriter(BufferedWriter out, Settings settings) {
        super(out, settings);
    }

    @Override
    public void write(BibItem item, Set<String> ignoredFields) throws IOException {
        // Item type
        out.write("@" + item.getOriginalType() + "{" + item.getId() + ",");
        out.newLine();

        // The first field should omit the connecting ",".
        boolean first = true;

        // Get the proper format for authors
        if (item.anyNonEmpty("author")) {
            out.write("  author={");

            for (int i = 0; i < item.getAuthors().size(); i++) {
                out.write(item.getAuthors().get(i).getName(Author.NameOutputType.LATEX));

                if (i < item.getAuthors().size() - 1) {
                    out.write(" and ");
                }
            }

            out.write("}");

            first = false;
        }

        Set<String> fieldsToPrint = getFieldsToPrint(item);

        for (String field : fieldsToPrint) {
            String v = item.get(field);

            if (v != null && !v.isEmpty()) {
                if (first) {
                    first = false;
                } else {
                    out.write(",");
                    out.newLine();
                }

                out.write("  " + field + "={" + v + "}");
            }
        }

        out.newLine();
        out.write("}");
        out.newLine();
    }

    private Set<String> getFieldsToPrint(BibItem item) {
        Set<String> fieldsToPrint = new LinkedHashSet<>();
        
        for (String field : FieldData.getMandatoryFields(item.getType())) {
            if (field.contains(";")) {
                fieldsToPrint.addAll(Arrays.asList(field.split(";")));
            } else {
                fieldsToPrint.add(field);
            }
        }
        
        for (String field : FieldData.getOptionalFields(item.getType())) {
            if (field.contains(";")) {
                fieldsToPrint.addAll(Arrays.asList(field.split(";")));
            } else {
                fieldsToPrint.add(field);
            }
        }
        
        // We already printed author
        fieldsToPrint.remove("author");
        
        return fieldsToPrint;
    }
}
