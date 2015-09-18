/*
 * Copyright 2013-2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.Type;
import publy.io.html.HTMLBibItemWriter;
import static org.junit.Assert.*;
import publy.io.TestUtils;
import publy.io.html.HTMLTestUtils;

/**
 *
 *
 */
public class PlainBibItemWriterTest {

    public PlainBibItemWriterTest() {
    }
    private StringWriter htmlOutput = new StringWriter();
    private BufferedWriter htmlBuffer = new BufferedWriter(htmlOutput);
    private HTMLBibItemWriter htmlWriter = new HTMLBibItemWriter(htmlBuffer, HTMLTestUtils.getBibtexSettings());

    private String runHtmlWriterMethod(BibItem item) throws Exception {
        htmlWriter.write(item);
        htmlBuffer.flush();

        String output = htmlOutput.getBuffer().toString();
        output = output.replaceAll("<[^>]*>", "").replaceAll("&ndash;", "-");
        output = output.replaceAll("\\s+", " ").trim(); // Reduce whitespace

        // Clear the output
        htmlOutput.getBuffer().delete(0, htmlOutput.getBuffer().length());

        return output;
    }
    
    private StringWriter textOutput = new StringWriter();
    private BufferedWriter textBuffer = new BufferedWriter(textOutput);
    private PlainBibItemWriter textWriter = new PlainBibItemWriter(textBuffer, HTMLTestUtils.getBibtexSettings());

    private String runTextWriterMethod(BibItem item) throws Exception {
        textWriter.write(item);
        textBuffer.flush();

        String output = textOutput.getBuffer().toString();
        output = output.replaceAll(" +", " ").trim(); // Reduce whitespace

        // Clear the output
        textOutput.getBuffer().delete(0, textOutput.getBuffer().length());

        return output;
    }

    public void testIgnore(BibItem input, Set<String> ignoredFields) {
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
        String expected = null;

        try {
            textWriter.setIgnoredFields(Collections.<String>emptySet());
            textWriter.write(compare);
            textBuffer.flush();
            expected = textOutput.getBuffer().toString();
        } catch (IOException ex) {
            fail("IOException on base item:\n" + compare + "\nException:\n" + ex);
        }

        // Clear the output
        textOutput.getBuffer().delete(0, textOutput.getBuffer().length());

        // Test
        try {
            textWriter.setIgnoredFields(ignoredFields);
            textWriter.write(input);
            textBuffer.flush();
            String result = textOutput.getBuffer().toString();

            assertEquals(input.toString() + "Ignored: " + ignoredFields + "\n", expected, result);
        } catch (IOException ex) {
            fail("IOException on input:\n" + input + "\nException:\n" + ex);
        }

        // Clear the output
        textOutput.getBuffer().delete(0, textOutput.getBuffer().length());
    }

    @Test
    public void testWrites() {
        for (Type type : Type.getEntryTypes()) {
            Set<BibItem> items = TestUtils.generateExampleBibitems(type);

            for (BibItem item : items) {
                try {
                    String expected = runHtmlWriterMethod(item);
                    String result = runTextWriterMethod(item);

                    assertEquals(item.toString(), expected, result);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    fail("write threw Exception on input:\n" + item + "\nException:\n" + ex);
                }
            }

            System.out.println("PlainBibItemWriter - Tests for " + type + " were successful");
        }
    }

    @Test
    public void testWriteIgnore() {
        System.out.println("writeIgnore");

        for (Type type : Type.getEntryTypes()) {
            BibItem item = TestUtils.getFullBibItem(type);
            Set<String> mandatoryFields = TestUtils.getMandatoryFields(type);

            Set<String> optionalFields = new HashSet<>(item.getFields());
            optionalFields.removeAll(mandatoryFields);

            List<Set<String>> subsets = TestUtils.getAllSubsets(optionalFields);

            for (Set<String> set : subsets) {
                testIgnore(item, set);
            }
        }
    }
}