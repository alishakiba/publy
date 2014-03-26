/*
 * Copyright 2013-2014 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import publy.data.PublicationStatus;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.Type;
import publy.data.settings.GeneralSettings;
import publy.data.settings.HTMLSettings;
import publy.data.settings.Settings;
import publy.io.TestUtils;

/**
 *
 *
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
        gs.setUseNewLines(false);
        gs.setNumbering(GeneralSettings.Numbering.NONE);

        HTMLSettings hs = bibtexSettings.getHtmlSettings();

        hs.setIncludeAbstract(PublicationStatus.NONE);
        hs.setIncludeBibtex(PublicationStatus.NONE);
        hs.setIncludePaper(PublicationStatus.NONE);
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
        testInstance.setIgnoredFields(Collections.<String>emptySet());

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
        testInstance.setIgnoredFields(Collections.<String>emptySet());

        for (BibItem input : expected.keySet()) {
            test(input, expected.get(input));
        }
    }

    public static void test(BibItem input, String expected) {
        TestUtils.setAuthors(input);
        TestUtils.setEditors(input);

        try {
            testInstance.write(input);
            buffer.flush();
        } catch (IOException ex) {
            fail("IOException on input:\n" + input + "\nException:\n" + ex);
        }

        String result = process(output.getBuffer().toString(), input);

        // Clear the output
        output.getBuffer().delete(0, output.getBuffer().length());

        assertEquals(input.toString(), expected, result);
    }

    public static void testIgnore(BibItem input, Set<String> ignoredFields) {
        // Build the comparable bibitem
        BibItem compare = new BibItem(input.getOriginalType(), input.getId());

        for (String field : input.getFields()) {
            if (!ignoredFields.contains(field)) {
                compare.put(field, input.get(field));
            }
        }

        TestUtils.setAuthors(compare);
        TestUtils.setEditors(compare);

        // Get the expected output
        String expected;

        testInstance.setIgnoredFields(Collections.<String>emptySet());

        try {
            testInstance.write(compare);
            buffer.flush();
        } catch (IOException ex) {
            fail("IOException on base item:\n" + compare + "\nException:\n" + ex);
        }

        expected = output.getBuffer().toString();

        // Clear the output
        output.getBuffer().delete(0, output.getBuffer().length());

        // Test
        testInstance.setIgnoredFields(ignoredFields);

        try {
            testInstance.write(input);
            buffer.flush();
        } catch (IOException ex) {
            fail("IOException on input:\n" + input + "\nException:\n" + ex);
        }

        String result = output.getBuffer().toString();

        // Clear the output
        output.getBuffer().delete(0, output.getBuffer().length());

        assertEquals(input.toString() + "Ignored: " + ignoredFields + "\n", expected, result);
    }

    private static String process(String output, BibItem input) {
        String pretty = trim(removeTags(output));

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
}
