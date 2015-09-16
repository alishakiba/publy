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

import java.io.IOException;
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
public class BibItemParserTest {

    public BibItemParserTest() {
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
     * Test of parseBibItem method, of class BibItemParser.
     */
    @Test
    public void testParseBibItem() {
        System.out.println("parseBibItem");

        Object[][] tests = new Object[][]{
            new Object[]{"@comment{AW = \"Addison-Wesley\"}", new BibItem("comment", null)},
            new Object[]{"@preamble{AW = \"Addison-Wesley\"}", new BibItem("preamble", null)},
            new Object[]{"@string{AW = \"Addison-Wesley\"}", (new BibItem("string", null) {
                BibItem init() {
                    put("short", "AW");
                    put("full", "Addison-Wesley");
                    return this;
                }
            }).init()},
            new Object[]{"@string{AW = {Addison-Wesley}}", (new BibItem("string", null) {
                BibItem init() {
                    put("short", "AW");
                    put("full", "Addison-Wesley");
                    return this;
                }
            }).init()},
            new Object[]{"@book{companion,\n"
                + "author = \"Goossens, Michel and Mittelbach, Franck and Samarin, Alexander\",\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\",\n"
                + "booktitle = \"The {{\\LaTeX}} {C}ompanion\",\n"
                + "publisher = AW,\n"
                + "year = 1993,\n"
                + "month = \"December\",\n"
                + "ISBN = {0-201-54199-8},\n"
                + "library = \"Yes\",\n"
                + "}",
                (new BibItem("book", "companion") {
                    BibItem init() {
                        put("author", "Goossens, Michel and Mittelbach, Franck and Samarin, Alexander");
                        put("title", "The {{\\LaTeX}} {C}ompanion");
                        put("booktitle", "The {{\\LaTeX}} {C}ompanion");
                        put("publisher", "<<AW>>");
                        put("year", "1993");
                        put("month", "December");
                        put("isbn", "0-201-54199-8");
                        put("library", "Yes");
                        return this;
                    }
                }).init()},
            new Object[]{"@book{companion}", new BibItem("book", "companion")},};

        for (Object[] test : tests) {
            try {
                BibItem expResult = (BibItem) test[1];
                BibItem result = BibItemParser.parseBibItem((String) test[0]);
                assertEqualItems("Input: <" + test[0] + ">", expResult, result);
            } catch (IOException ioe) {
                if (!"EX".equals(test[1])) {
                    fail("parseBibItem threw IOException \"" + ioe + "\" with input \"" + test[0] + "\"");
                }
            } catch (Exception ex) {
                System.err.println("Input: <" + test[0] + ">");
                throw ex;
            }
        }
    }

    private void assertEqualItems(String message, BibItem item1, BibItem item2) {
        assertEquals(message, item1.getOriginalType(), item2.getOriginalType());
        assertEquals(message, item1.getId(), item2.getId());
        assertEquals(message, item1.getFields(), item2.getFields());

        for (String field : item1.getFields()) {
            assertEquals(message, item1.get(field), item2.get(field));
        }
    }

    /**
     * Test of parseValue method, of class BibItemParser.
     */
    @Test
    public void testParseValue() {
        System.out.println("parseValue");

        String[][] tests = new String[][]{
            // Input, output
            new String[]{"\"Yes\"", "Yes"},
            new String[]{"{No}", "No"},
            new String[]{"11", "11"},
            new String[]{"this", "<<this>>"},
            new String[]{"\"Goossens, Michel and Mittelbach, Franck and Samarin, Alexander\"", "Goossens, Michel and Mittelbach, Franck and Samarin, Alexander"},
            new String[]{"\"The {{\\LaTeX}} {C}ompanion\"", "The {{\\LaTeX}} {C}ompanion"},
            new String[]{"AW", "<<AW>>"},
            new String[]{"1993", "1993"},
            new String[]{"\"December\"", "December"},
            new String[]{"{0-201-54199-8}", "0-201-54199-8"},
            new String[]{"\"Comments on {\"}Filenames and Fonts{\"}\"", "Comments on {\"}Filenames and Fonts{\"}"},
            new String[]{"{Comments on \"Filenames and Fonts\"}", "Comments on \"Filenames and Fonts\""},
            new String[]{"goossens # and # mittelbach # and # samarin", "<<goossens>><<and>><<mittelbach>><<and>><<samarin>>"},
            new String[]{"goossens # \" and \" # mittelbach # and # samarin", "<<goossens>> and <<mittelbach>><<and>><<samarin>>"},
            new String[]{"\"goossens\" # \" and \" # mittelbach # and # samarin", "goossens and <<mittelbach>><<and>><<samarin>>"},
            new String[]{"\"goossens #  and \" # mittelbach # and # samarin", "goossens #  and <<mittelbach>><<and>><<samarin>>"},
            new String[]{"\"goos,sens #  and \" # mittelbach # and # samarin", "goos,sens #  and <<mittelbach>><<and>><<samarin>>"},
            new String[]{"goossens # and # {mit,telbach} # and # samarin", "<<goossens>><<and>>mit,telbach<<and>><<samarin>>"},
            new String[]{"goossens # and # {mit, \"tel\" # bach} # and # samarin", "<<goossens>><<and>>mit, \"tel\" # bach<<and>><<samarin>>"},};

        for (String[] test : tests) {
            try {
                String expResult = test[1];
                String result = BibItemParser.parseValue(test[0]);
                assertEquals("Input: <" + test[0] + ">", expResult, result);
            } catch (Exception ex) {
                System.err.println("Input: <" + test[0] + ">");
                throw ex;
            }
        }
    }

}
