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
package publy.io.plain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.FieldData;
import publy.data.bibitem.Type;
import publy.io.html.HTMLBibItemWriter;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class PlainBibItemWriterTest {

    private static final HashMap<String, String> fieldDefaults;
    private static final HashMap<Type, String> methodNames;

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

        methodNames = new HashMap<>();

        methodNames.put(Type.ARTICLE, "writeArticle");
        methodNames.put(Type.BOOK, "writeBook");
        methodNames.put(Type.INBOOK, "writeInBook");
        methodNames.put(Type.BOOKLET, "writeBooklet");
        methodNames.put(Type.INCOLLECTION, "writeInCollection");
        methodNames.put(Type.MANUAL, "writeManual");
        methodNames.put(Type.MISC, "writeMisc");
        methodNames.put(Type.ONLINE, "writeOnline");
        methodNames.put(Type.PATENT, "writePatent");
        methodNames.put(Type.PROCEEDINGS, "writeProceedings");
        methodNames.put(Type.INPROCEEDINGS, "writeInProceedings");
        methodNames.put(Type.REPORT, "writeReport");
        methodNames.put(Type.THESIS, "writeThesis");
        methodNames.put(Type.UNPUBLISHED, "writeUnpublished");
    }

    public PlainBibItemWriterTest() {
    }
    private StringWriter htmlOutput = new StringWriter();
    private BufferedWriter htmlBuffer = new BufferedWriter(htmlOutput);
    private HTMLBibItemWriter htmlWriter = new HTMLBibItemWriter(htmlBuffer, null);

    private String runHtmlWriterMethod(Type type, BibItem item) throws Exception {
        // Ugly reflection hack to call methods with protected access
        Method method = htmlWriter.getClass().getDeclaredMethod(methodNames.get(type), BibItem.class);
        method.setAccessible(true);
        method.invoke(htmlWriter, item);

        htmlBuffer.flush();

        String output = htmlOutput.getBuffer().toString().replaceAll("<[^>]*>", "").replaceAll("&ndash;", "-").trim();

        // Clear the output
        htmlOutput.getBuffer().delete(0, htmlOutput.getBuffer().length());

        return output;
    }
    private StringWriter textOutput = new StringWriter();
    private BufferedWriter textBuffer = new BufferedWriter(textOutput);
    private PlainBibItemWriter textWriter = new PlainBibItemWriter(textBuffer, null);

    private String runTextWriterMethod(Type type, BibItem item) throws Exception {
        Method method = textWriter.getClass().getDeclaredMethod(methodNames.get(type), BibItem.class);
        method.invoke(textWriter, item);

        textBuffer.flush();

        String output = textOutput.getBuffer().toString().trim();
        
        // Clear the output
        textOutput.getBuffer().delete(0, textOutput.getBuffer().length());
        
        return output;
    }

    private Set<BibItem> generateExampleBibitems(Type type) {
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

    private void generateExamples(Set<BibItem> examples, Type type, Set<String> included, List<String> option) {
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

    private void addExample(Set<BibItem> examples, Type type, Set<String> included) {
        BibItem example = new BibItem(type.toString(), "test_" + examples.size());

        for (String field : included) {
            example.put(field, fieldDefaults.get(field));
        }

        examples.add(example);
    }

    @Test
    public void testWrites() {
        for (Type type : Type.values()) {
            Set<BibItem> items = generateExampleBibitems(type);

            for (BibItem item : items) {
                try {
                    String expected = runHtmlWriterMethod(type, item);
                    String result = runTextWriterMethod(type, item);

                    assertEquals(expected, result);
                } catch (Exception ex) {
                    fail("writeArticle threw Exception on input:\n" + item + "\nException:\n" + ex);
                }
            }
        }
    }
}