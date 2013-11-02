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
package publy.io.html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import publy.data.Author;
import publy.data.PublicationType;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.Type;
import publy.data.settings.GeneralSettings;
import publy.data.settings.HTMLSettings;
import publy.data.settings.Settings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLTestUtils {

    private static final Settings bibtexSettings = new Settings();
    private static final HashMap<String, String> fieldDefaults;
    private static final StringWriter output;
    private static final BufferedWriter buffer;
    private static final HTMLBibItemWriter testInstance;

    static {
        GeneralSettings gs = bibtexSettings.getGeneralSettings();

        gs.setNameDisplay(GeneralSettings.NameDisplay.FULL);
        gs.setReverseNames(false);
        gs.setListAllAuthors(true);
        gs.setTitleFirst(false);
        gs.setNumbering(GeneralSettings.Numbering.NONE);

        HTMLSettings hs = bibtexSettings.getHtmlSettings();

        hs.setIncludeAbstract(PublicationType.NONE);
        hs.setIncludeBibtex(PublicationType.NONE);
        hs.setIncludePaper(PublicationType.NONE);
        hs.setTitleTarget(HTMLSettings.TitleLinkTarget.NONE);

        // Initialize default values for fields
        fieldDefaults = new HashMap<>();

        fieldDefaults.put("address", "Nederweert");
        fieldDefaults.put("author", "An Author and Writer, Wenda");
        fieldDefaults.put("booktitle", "Proceedings");
        fieldDefaults.put("chapter", "5");
        fieldDefaults.put("edition", "Third");
        fieldDefaults.put("editor", "Ed Editor and Collector, Charles");
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

        // Initialize writers
        output = new StringWriter();
        buffer = new BufferedWriter(output);
        testInstance = new HTMLBibItemWriter(buffer, null, bibtexSettings);
    }

    public static Settings getBibtexSettings() {
        return bibtexSettings;
    }

    public static void testWithDefaultValues(Type type, Properties prop) {
        for (String key : prop.stringPropertyNames()) {
            if (key.endsWith("_fields")) {
                String test = key.substring(0, key.length() - "_fields".length());
                List<String> fields = Arrays.asList(prop.getProperty(key).split("\\|"));
                String expected = prop.getProperty(test + "_expected");

                testWithDefaultValues(type, test, fields, expected);
            }
        }
    }

    public static void testWithDefaultValues(Type type, String id, List<String> fields, String expected) {
        BibItem item = new BibItem(type.toString(), id);

        for (String field : fields) {
            item.put(field, fieldDefaults.get(field));
        }

        test(item, expected);
    }

    public static void test(HashMap<BibItem, String> expected) {
        for (BibItem input : expected.keySet()) {
            test(input, expected.get(input));
        }
    }

    public static void test(BibItem input, String expected) {
        try {
            setAuthors(input);
            setEditors(input);
            testInstance.write(input);
            buffer.flush();
            String result = process(output.getBuffer().toString(), input);

            assertEquals(input.toString(), expected, result);
        } catch (IOException ex) {
            fail("IOException on input:\n" + input + "\nException:\n" + ex);
        }

        // Clear the output
        output.getBuffer().delete(0, output.getBuffer().length());
    }

    public static void testIgnore(BibItem input, Set<String> ignoredFields) {
        // Build the comparable bibitem
        BibItem compare = new BibItem(input.getOriginalType(), input.getId());

        for (String field : input.getFields()) {
            if (!ignoredFields.contains(field)) {
                compare.put(field, input.get(field));
            }
        }

        setAuthors(compare);
        setEditors(compare);

        // Get the expected output
        String expected = null;
        
        try {
            testInstance.write(compare);
            buffer.flush();
            expected = output.getBuffer().toString();
        } catch (IOException ex) {
            fail("IOException on base item:\n" + compare + "\nException:\n" + ex);
        }
        
        // Clear the output
        output.getBuffer().delete(0, output.getBuffer().length());

        // Test
        try {
            testInstance.write(input, ignoredFields);
            buffer.flush();
            String result = output.getBuffer().toString();

            assertEquals(input.toString() + "Ignored: " + ignoredFields + "\n", expected, result);
        } catch (IOException ex) {
            fail("IOException on input:\n" + input + "\nException:\n" + ex);
        }

        // Clear the output
        output.getBuffer().delete(0, output.getBuffer().length());
    }

    private static String process(String output, BibItem input) {
        // Because the title is on a line by itself, we don't add a period where bibtex does
        String pretty = addPeriodAfterTitle(output, input.get("title"));

        pretty = trim(removeTags(pretty));

        // We print more information for @misc
        if (input.getType() == Type.MISC) {
            pretty = pretty.replaceAll("Ed Editor and Charles Collector, editors. ", "");
            pretty = pretty.replaceAll("Nederweert, ", "");
        }

        return pretty;
    }

    private static String removeTags(String html) {
        return html.replaceAll("<[^>]*>", "");
    }

    private static String trim(String input) {
        String reduced = input.replaceAll("\\s+", " ");
        return reduced.trim();
    }

    private static String addPeriodAfterTitle(String input, String title) {
        int index = input.toLowerCase().indexOf(title.toLowerCase());

        if (index < 0) {
            return input;
        } else {
            int nextNewLine = input.indexOf("<br>", index);

            StringBuilder sb = new StringBuilder(input);
            sb.insert(nextNewLine, '.');
            return sb.toString();
        }
    }

    private static void setAuthors(BibItem item) {
        String author = item.get("author");

        if (author != null && !author.isEmpty()) {
            String[] paperAuthors = author.split(" and ");

            for (String paperAuthor : paperAuthors) {
                item.getAuthors().add(new Author(paperAuthor));
            }
        }
    }

    private static void setEditors(BibItem item) {
        String editor = item.get("editor");

        if (editor != null && !editor.isEmpty()) {
            String[] names = editor.split(" and ");

            for (String name : names) {
                item.getEditors().add(new Author(name));
            }
        }
    }
}
