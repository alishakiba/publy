/*
 * Copyright 2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.io.bibtexparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import publy.data.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class PublicationListParserTest {

    public PublicationListParserTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of parseBibTeX method, of class PublicationListParser.
     */
    @Test
    public void testParseBibTeX() throws Exception {
        System.out.println("parseBibTeX");

        Object[][] tests = new Object[][]{
            new Object[]{"Some {{comments} with unbalanced braces\n"
                + "....and a \"commented\" entry...\n"
                + "\n"
                + "Book{landru21,\n"
                + "  author =	 {Landru, Henri D\\'esir\\'e},\n"
                + "  title =	 {A hundred recipes for you wife},\n"
                + "  publisher =	 {Culinary Expert Series},\n"
                + "  year =	 1921\n"
                + "}\n"
                + "\n"
                + "..some other comments..before a valid entry...\n"
                + "\n"
                + "@Book{steward03,\n"
                + "  author =	 { Martha Steward },\n"
                + "  title =	 {Cooking behind bars},\n"
                + "  publisher =	 {Culinary Expert Series},\n"
                + "  year =	 2003\n"
                + "}",
                Arrays.asList(
                (new BibItem("book", "steward03") {
                    BibItem init() {
                        put("author", "Martha Steward");
                        put("title", "Cooking behind bars");
                        put("publisher", "Culinary Expert Series");
                        put("year", "2003");
                        return this;
                    }
                }).init()
                )},
            new Object[]{
                "...and finally an entry commented by the use of the special @Comment entry type.\n"
                + "\n"
                + "@Comment{steward03,\n"
                + "  author =	 {Martha Steward},\n"
                + "  title =	 {Cooking behind bars},\n"
                + "  publisher =	 {Culinary Expert Series},\n"
                + "  year =	 2003\n"
                + "}",
                Collections.EMPTY_LIST
            },
            new Object[]{
                "@Comment{\n"
                + "  @Book{steward03,\n"
                + "    author =	 {Martha Steward},\n"
                + "    title =	 {Cooking behind bars},\n"
                + "    publisher =	 {Culinary Expert Series},\n"
                + "    year =	 2003\n"
                + "  }\n"
                + "}",
                Arrays.asList(
                (new BibItem("book", "steward03") {
                    BibItem init() {
                        put("author", "Martha Steward");
                        put("title", "Cooking behind bars");
                        put("publisher", "Culinary Expert Series");
                        put("year", "2003");
                        return this;
                    }
                }).init()
                )
            },
            new Object[]{
                "@Book(steward03,\n"
                + "  author =	 {Testing},\n"
                + ")",
                Arrays.asList(
                (new BibItem("book", "steward03") {
                    BibItem init() {
                        put("author", "Testing");
                        return this;
                    }
                }).init()
                )
            },
            new Object[]{
                "@String(test = \"Testing\")\n"
                + "\n"
                + "@Book{steward03,\n"
                + "  author =	 test,\n"
                + "}",
                Arrays.asList(
                (new BibItem("book", "steward03") {
                    BibItem init() {
                        put("author", "Testing");
                        return this;
                    }
                }).init()
                )
            },
            new Object[]{
                "@String{test = \"Testing\"}\n"
                + "\n"
                + "@Book{steward03,\n"
                + "  author =	 test,\n"
                + "}",
                Arrays.asList(
                (new BibItem("book", "steward03") {
                    BibItem init() {
                        put("author", "Testing");
                        return this;
                    }
                }).init()
                )
            },
            new Object[]{
                "@String{test = \"Testing\"}\n"
                + "\n"
                + "@Book{steward03,\n"
                + "  author =	 \"test\",\n"
                + "}",
                Arrays.asList(
                (new BibItem("book", "steward03") {
                    BibItem init() {
                        put("author", "test");
                        return this;
                    }
                }).init()
                )
            },
            new Object[]{
                "@String{test = \"Testing\"}\n"
                + "\n"
                + "@Book{steward03,\n"
                + "  author =	 \"Totally \" # test,\n"
                + "}",
                Arrays.asList(
                (new BibItem("book", "steward03") {
                    BibItem init() {
                        put("author", "Totally Testing");
                        return this;
                    }
                }).init()
                )
            },
            new Object[]{
                "@String{test = \"Test\"}\n"
                + "@String{test2 = test # \"ing\"}\n"
                + "\n"
                + "@Book{steward03,\n"
                + "  author =	 test2,\n"
                + "}",
                Arrays.asList(
                (new BibItem("book", "steward03") {
                    BibItem init() {
                        put("author", "Testing");
                        return this;
                    }
                }).init()
                )
            },
            new Object[]{
                "@String{test = \"Test\"}\n"
                + "@String{test2 = \"<<test>>ing\"}\n"
                + "\n"
                + "@Book{steward03,\n"
                + "  author =	 test2,\n"
                + "}",
                Arrays.asList(
                (new BibItem("book", "steward03") {
                    BibItem init() {
                        put("author", "Testing");
                        return this;
                    }
                }).init()
                )
            },
            new Object[]{
                "@String{test2 = \"<<test>>\"}\n"
                + "<author short=\"test\" name=\"Test Author\">\n"
                + "\n"
                + "@Book{steward03,\n"
                + "  author =	 test2,\n"
                + "}",
                Arrays.asList(
                (new BibItem("book", "steward03") {
                    BibItem init() {
                        put("author", "Test Author");
                        return this;
                    }
                }).init()
                )
            },
            new Object[]{
                "% @Book{steward03,\n"
                + "  author =	 {Testing},\n"
                + "}",
                Arrays.asList(
                (new BibItem("book", "steward03") {
                    BibItem init() {
                        put("author", "Testing");
                        return this;
                    }
                }).init()
                )
            },
            new Object[]{
                "@comment this entire line is a comment @Book{steward03,\n"
                + "  author =	 {Testing},\n"
                + "}",
                Arrays.asList(
                (new BibItem("book", "steward03") {
                    BibItem init() {
                        put("author", "Testing");
                        return this;
                    }
                }).init()
                )
            },
            new Object[]{
                "@comment{ this entire line is a comment @Book{steward03,}\n"
                + "  author =	 {Testing},\n"
                + "}",
                Arrays.asList(
                (new BibItem("book", "steward03") {
                    BibItem init() {
                        return this;
                    }
                }).init()
                )
            }
        };

        for (Object[] test : tests) {
            try {
                @SuppressWarnings("unchecked")
                List<BibItem> expResult = (List<BibItem>) test[1];
                List<BibItem> result = PublicationListParser.parseBibTeX(new BufferedReader(new StringReader((String) test[0])));
                assertEqualLists("Input: <" + test[0] + ">", expResult, result);
            } catch (IOException | ParseException ioe) {
                if (!"EX".equals(test[1])) {
                    fail("parseBibItem threw IOException \"" + ioe + "\" with input \"" + test[0] + "\"");
                }
            } catch (Exception ex) {
                System.err.println("Input: <" + test[0] + ">");
                throw ex;
            }
        }
    }

    private void assertEqualLists(String message, List<BibItem> expected, List<BibItem> actual) {
        assertEquals(message, expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            BibItem expectedItem = expected.get(i);
            BibItem actualItem = actual.get(i);
            assertEqualItems(message, expectedItem, actualItem);
        }
    }

    private void assertEqualItems(String message, BibItem expected, BibItem actual) {
        assertEquals(message, expected.getOriginalType(), actual.getOriginalType());
        assertEquals(message, expected.getId(), actual.getId());
        assertEquals(message, expected.getFields(), actual.getFields());

        for (String field : expected.getFields()) {
            assertEquals(message, expected.get(field), actual.get(field));
        }
    }
}
