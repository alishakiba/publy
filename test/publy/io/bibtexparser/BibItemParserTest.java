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
            new Object[]{"@inproceedings{morin2013average,\n"
                + " title={On the Average Number of Edges in Theta Graphs},\n"
                + " author={<<pat>> and <<me>>},\n"
                + " booktitle={<<proc>> 11th <<analco>> (ANALCO14)},\n"
                + " year={2014},\n"
                + " abstract={Theta graphs are important geometric graphs that have many applications, including wireless networking, motion planning, real-time animation, and minimum-spanning tree construction. We give closed form expressions for the average degree of theta graphs of a homogeneous Poisson point process over the plane. We then show that essentially the same bounds—with vanishing error terms—hold for theta graphs of finite sets of points that are uniformly distributed in a square. Finally, we show that the number of edges in a theta graph of points uniformly distributed in a square is concentrated around its expected value.},\n"
                + " arxiv={1304.3402},\n"
                + " pubstate={submitted}\n"
                + "}",
                (new BibItem("inproceedings", "morin2013average") {
                    BibItem init() {
                        put("title", "On the Average Number of Edges in Theta Graphs");
                        put("author", "<<pat>> and <<me>>");
                        put("booktitle", "<<proc>> 11th <<analco>> (ANALCO14)");
                        put("year", "2014");
                        put("abstract", "Theta graphs are important geometric graphs that have many applications, including wireless networking, motion planning, real-time animation, and minimum-spanning tree construction. We give closed form expressions for the average degree of theta graphs of a homogeneous Poisson point process over the plane. We then show that essentially the same bounds—with vanishing error terms—hold for theta graphs of finite sets of points that are uniformly distributed in a square. Finally, we show that the number of edges in a theta graph of points uniformly distributed in a square is concentrated around its expected value.");
                        put("arxiv", "1304.3402");
                        put("pubstate", "submitted");
                        return this;
                    }
                }).init()},
            new Object[]{"@Book{Weyl:1922:STMb,\n"
                + "  author =       \"Hermann Weyl and Henry L. (Henry Leopold) Brose\",\n"
                + "  title =        \"Space--time--matter\",\n"
                + "  publisher =    pub-DOVER,\n"
                + "  address =      pub-DOVER:adr,\n"
                + "  edition =      \"Fourth\",\n"
                + "  pages =        \"xvi + 330\",\n"
                + "  year =         \"1922\",\n"
                + "  LCCN =         \"QC6 .W5413 1922; QC6 .W4 1920; QC6 .W4 1922\",\n"
                + "  bibdate =      \"Tue Oct 10 06:32:10 MDT 2006\",\n"
                + "  bibsource =    \"http://www.math.utah.edu/pub/tex/bib/einstein.bib;\n"
                + "                 melvyl.cdlib.org:210/CDL90\",\n"
                + "  acknowledgement = ack-nhfb,\n"
                + "  author-dates = \"1885--1955\",\n"
                + "  subject =      \"Relativity (physics); space and time\",\n"
                + "}",
                (new BibItem("book", "Weyl:1922:STMb") {
                    BibItem init() {
                        put("author", "Hermann Weyl and Henry L. (Henry Leopold) Brose");
                        put("title", "Space--time--matter");
                        put("publisher", "<<pub-DOVER>>");
                        put("address", "<<pub-DOVER:adr>>");
                        put("edition", "Fourth");
                        put("pages", "xvi + 330");
                        put("year", "1922");
                        put("lccn", "QC6 .W5413 1922; QC6 .W4 1920; QC6 .W4 1922");
                        put("bibdate", "Tue Oct 10 06:32:10 MDT 2006");
                        put("bibsource", "http://www.math.utah.edu/pub/tex/bib/einstein.bib;\n"
                + "                 melvyl.cdlib.org:210/CDL90");
                        put("acknowledgement", "<<ack-nhfb>>");
                        put("author-dates", "1885--1955");
                        put("subject", "Relativity (physics); space and time");
                        return this;
                    }
                }).init()},
            new Object[]{"@book{companion}", new BibItem("book", "companion")},};

        for (Object[] test : tests) {
            try {
                BibItem expResult = (BibItem) test[1];
                BibItem result = BibItemParser.parseBibItem((String) test[0]);
                assertEqualItems("Input: <" + test[0] + ">", expResult, result);
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
            new String[]{"goossens#and#mittelbach # and#samarin", "<<goossens>><<and>><<mittelbach>><<and>><<samarin>>"},
            new String[]{"goossens # \" and \" # mittelbach # and # samarin", "<<goossens>> and <<mittelbach>><<and>><<samarin>>"},
            new String[]{"\"goossens\" # \" and \" # mittelbach # and # samarin", "goossens and <<mittelbach>><<and>><<samarin>>"},
            new String[]{"\"goossens #  and \" # mittelbach # and # samarin", "goossens #  and <<mittelbach>><<and>><<samarin>>"},
            new String[]{"\"goos,sens #  and \" # mittelbach # and # samarin", "goos,sens #  and <<mittelbach>><<and>><<samarin>>"},
            new String[]{"goossens # and # {mit,telbach} # and # samarin", "<<goossens>><<and>>mit,telbach<<and>><<samarin>>"},
            new String[]{"goossens # and # {mit, \"tel\" # bach} # and # samarin", "<<goossens>><<and>>mit, \"tel\" # bach<<and>><<samarin>>"},
            new String[]{"pub-DOVER", "<<pub-DOVER>>"},
            new String[]{"pub-DOVER:adr", "<<pub-DOVER:adr>>"},
        };

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
