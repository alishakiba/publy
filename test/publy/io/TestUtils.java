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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import publy.data.Author;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.FieldData;
import publy.data.bibitem.Type;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class TestUtils {

    private static final HashMap<String, String> fieldDefaults;

    static {
        fieldDefaults = new HashMap<>();

        fieldDefaults.put("address", "Nederweert");
        fieldDefaults.put("author", "An Author");
        fieldDefaults.put("booktitle", "Proceedings");
        fieldDefaults.put("chapter", "5");
        fieldDefaults.put("edition", "Third");
        fieldDefaults.put("editor", "Ed Editor");
        fieldDefaults.put("howpublished", "Message in a bottle");
        fieldDefaults.put("institution", "University");
        fieldDefaults.put("journal", "Journal");
        fieldDefaults.put("month", "January");
        fieldDefaults.put("note", "Note to self");
        fieldDefaults.put("number", "42");
        fieldDefaults.put("organization", "Test Group");
        fieldDefaults.put("pages", "1--13");
        fieldDefaults.put("publisher", "Bottomless Pit");
        fieldDefaults.put("school", "School");
        fieldDefaults.put("series", "Notes");
        fieldDefaults.put("title", "Meaningful Title");
        fieldDefaults.put("type", "Typo");
        fieldDefaults.put("volume", "1337");
        fieldDefaults.put("year", "2010");
    }

    public static Set<BibItem> generateExampleBibitems(Type type) {
        // Figure out which fields to include, exclude, and vary
        Set<String> included = new LinkedHashSet<>();

        for (String req : FieldData.getMandatoryFields(type)) {
            if (req.contains(";")) {
                included.add(req.substring(0, req.indexOf(';'))); // Only use the first option
            } else {
                included.add(req);
            }
        }

        List<String> option = new ArrayList<>(fieldDefaults.keySet());
        option.retainAll(FieldData.getOptionalFields(type));
        option.removeAll(included);

        // Generate a test for each combination of optional fields
        Set<BibItem> examples = new LinkedHashSet<>((int) Math.pow(2, option.size()));

        generateExamples(examples, type, included, option);

        // Generate a test including all ignored fields
        addExample(examples, type, fieldDefaults.keySet());

        if (type == Type.REPORT) {
            BibItem exampleReport = new BibItem("techreport", "techreport");
            exampleReport.put("author", fieldDefaults.get("author"));
            exampleReport.put("title", fieldDefaults.get("title"));
            exampleReport.put("institution", fieldDefaults.get("institution"));
            exampleReport.put("year", fieldDefaults.get("year"));
            examples.add(exampleReport);
        } else if (type == Type.THESIS) {
            BibItem msThesis = new BibItem("mastersthesis", "mastersthesis");
            msThesis.put("author", fieldDefaults.get("author"));
            msThesis.put("title", fieldDefaults.get("title"));
            msThesis.put("school", fieldDefaults.get("school"));
            msThesis.put("year", fieldDefaults.get("year"));
            examples.add(msThesis);

            BibItem phdThesis = new BibItem("phdthesis", "phdthesis");
            phdThesis.put("author", fieldDefaults.get("author"));
            phdThesis.put("title", fieldDefaults.get("title"));
            phdThesis.put("school", fieldDefaults.get("school"));
            phdThesis.put("year", fieldDefaults.get("year"));
            examples.add(phdThesis);
        }

        return examples;
    }

    private static void generateExamples(Set<BibItem> examples, Type type, Set<String> included, List<String> option) {
        if (option.isEmpty()) {
            // This test is complete
            addExample(examples, type, included);
        } else {
            // Pick an option and add it or not
            String field = option.get(option.size() - 1);

            List<String> option2 = new ArrayList<>(option);
            option2.remove(option.size() - 1);

            generateExamples(examples, type, included, option2); // Without

            Set<String> included2 = new LinkedHashSet<>(included);
            included2.add(field);

            generateExamples(examples, type, included2, option2); // With
        }
    }

    private static void addExample(Set<BibItem> examples, Type type, Set<String> included) {
        BibItem example = new BibItem(type.toString(), "test_" + examples.size());

        for (String field : included) {
            example.put(field, fieldDefaults.get(field));
        }

        setAuthors(example);
        setEditors(example);

        examples.add(example);
    }

    public static void setAuthors(BibItem item) {
        String author = item.get("author");

        if (author != null && !author.isEmpty()) {
            String[] paperAuthors = author.split(" and ");

            for (String paperAuthor : paperAuthors) {
                item.getAuthors().add(new Author(paperAuthor));
            }
        }
    }

    public static void setEditors(BibItem item) {
        String editor = item.get("editor");

        if (editor != null && !editor.isEmpty()) {
            String[] names = editor.split(" and ");

            for (String name : names) {
                item.getEditors().add(new Author(name));
            }
        }
    }

    public static List<Set<String>> getAllSubsets(Set<String> fields) {
        List<Set<String>> result = new ArrayList<>((int) Math.pow(2, fields.size()));
        addAllSubsets(result, new LinkedHashSet<String>(2 * fields.size()), new LinkedHashSet<>(fields));
        return result;
    }
    
    private static void addAllSubsets(List<Set<String>> subsets, Set<String> included, Set<String> fields) {
        if (fields.isEmpty()) {
            // This is done
            subsets.add(new HashSet<>(included));
        } else {
            // Handle one field
            String next = fields.iterator().next();
            fields.remove(next);
            
            // Don't include it
            addAllSubsets(subsets, included, fields);
            
            // Include it
            included.add(next);
            addAllSubsets(subsets, included, fields);
            
            // Return the sets to their previous state
            included.remove(next);
            fields.add(next);
        }
    }

    public static Set<String> getMandatoryFields(Type type) {
        Set<String> result = new HashSet<>();
        for (String req : FieldData.getMandatoryFields(type)) {
            if (req.contains(";")) {
                result.add(req.substring(0, req.indexOf(';')));
            } else {
                result.add(req);
            }
        }
        return result;
    }
}
