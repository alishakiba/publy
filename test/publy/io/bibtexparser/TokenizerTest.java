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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import publy.data.Pair;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class TokenizerTest {

    public TokenizerTest() {
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
     * Test of collectBibItem method, of class Tokenizer.
     */
    @Test
    public void testCollectBibItem() {
        System.out.println("collectBibItem");

        String[][] tests = new String[][]{
            new String[]{"comment{Comment}", "EX"}, // First char should be '@'
            new String[]{"@commentComment}", "EX"}, // There should be an open brace
            new String[]{"@comme\n\t\nComme\nt}", "EX"}, // There should be an open brace
            new String[]{"@comme{Comme\nt", "EX"}, // There should be a close brace
            new String[]{"@comme{Co{m}me\nt", "EX"}, // There should be a close brace
            new String[]{"@comme{{Co{}m}me\nt", "EX"}, // There should be a close brace
            new String[]{"@comment{Comment}", "@comment{Comment}"},
            new String[]{"@comment{Comment} @string{this = \"test\"}", "@comment{Comment}"},
            new String[]{"@article{bose,\n title = \"Title\", author = {Bose, {P}rosenjit}}", "@article{bose,\n title = \"Title\", author = {Bose, {P}rosenjit}}"},
            new String[]{"@comment{Comment\n\t  \t}", "@comment{Comment\n\t  \t}"},
            new String[]{"@comment{C{{o}m{m}}}ent}", "@comment{C{{o}m{m}}}"},};

        for (String[] test : tests) {
            try {
                String expResult = test[1];
                String result = Tokenizer.collectBibItem(new BufferedReader(new StringReader(test[0])), "");
                assertEquals("Input: <" + test[0] + ">", expResult, result);
            } catch (IOException ex) {
                if (!"EX".equals(test[1])) {
                    fail("collectBibItem threw IOException \"" + ex + "\" with input \"" + test[0] + "\"");
                }
            } catch (Exception ex) {
                System.err.println("Input: <" + test[0] + ">");
                throw ex;
            }
        }
    }

    @Test
    public void testCollectValue() {
        System.out.println("collectValue");

        String[][] tests = new String[][]{
            // Input, value, remainder
            new String[]{"\"Yes\",\n"
                + "}",
                "\"Yes\"",
                ",\n"
                + "}"},
            new String[]{"{No}}",
                "{No}",
                "}"},
            new String[]{"11,\n"
                + "}",
                "11",
                ",\n"
                + "}"},
            new String[]{"12}",
                "12",
                "}"},
            new String[]{"\"Goossens, Michel and Mittelbach, Franck and Samarin, Alexander\",\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\",\n"
                + "booktitle = \"The {{\\LaTeX}} {C}ompanion\",\n"
                + "publisher = AW,\n"
                + "year = 1993,\n"
                + "month = \"December\",\n"
                + "ISBN = {0-201-54199-8},\n"
                + "library = \"Yes\",\n"
                + "}",
                "\"Goossens, Michel and Mittelbach, Franck and Samarin, Alexander\"",
                ",\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\",\n"
                + "booktitle = \"The {{\\LaTeX}} {C}ompanion\",\n"
                + "publisher = AW,\n"
                + "year = 1993,\n"
                + "month = \"December\",\n"
                + "ISBN = {0-201-54199-8},\n"
                + "library = \"Yes\",\n"
                + "}"},
            new String[]{"\"The {{\\LaTeX}} {C}ompanion\",\n"
                + "booktitle = \"The {{\\LaTeX}} {C}ompanion\",\n"
                + "publisher = AW,\n"
                + "year = 1993,\n"
                + "month = \"December\",\n"
                + "ISBN = {0-201-54199-8},\n"
                + "library = \"Yes\",\n"
                + "}",
                "\"The {{\\LaTeX}} {C}ompanion\"",
                ",\n"
                + "booktitle = \"The {{\\LaTeX}} {C}ompanion\",\n"
                + "publisher = AW,\n"
                + "year = 1993,\n"
                + "month = \"December\",\n"
                + "ISBN = {0-201-54199-8},\n"
                + "library = \"Yes\",\n"
                + "}"},
            new String[]{"\"The {{\\LaTeX}} {C}ompanion\",\n"
                + "publisher = AW,\n"
                + "year = 1993,\n"
                + "month = \"December\",\n"
                + "ISBN = {0-201-54199-8},\n"
                + "library = \"Yes\",\n"
                + "}",
                "\"The {{\\LaTeX}} {C}ompanion\"",
                ",\n"
                + "publisher = AW,\n"
                + "year = 1993,\n"
                + "month = \"December\",\n"
                + "ISBN = {0-201-54199-8},\n"
                + "library = \"Yes\",\n"
                + "}"},
            new String[]{"AW,\n"
                + "year = 1993,\n"
                + "month = \"December\",\n"
                + "ISBN = {0-201-54199-8},\n"
                + "library = \"Yes\",\n"
                + "}",
                "AW",
                ",\n"
                + "year = 1993,\n"
                + "month = \"December\",\n"
                + "ISBN = {0-201-54199-8},\n"
                + "library = \"Yes\",\n"
                + "}"},
            new String[]{"1993,\n"
                + "month = \"December\",\n"
                + "ISBN = {0-201-54199-8},\n"
                + "library = \"Yes\",\n"
                + "}",
                "1993",
                ",\n"
                + "month = \"December\",\n"
                + "ISBN = {0-201-54199-8},\n"
                + "library = \"Yes\",\n"
                + "}"},
            new String[]{"\"December\",\n"
                + "ISBN = {0-201-54199-8},\n"
                + "library = \"Yes\",\n"
                + "}",
                "\"December\"",
                ",\n"
                + "ISBN = {0-201-54199-8},\n"
                + "library = \"Yes\",\n"
                + "}"},
            new String[]{"{0-201-54199-8},\n"
                + "library = \"Yes\",\n"
                + "}",
                "{0-201-54199-8}",
                ",\n"
                + "library = \"Yes\",\n"
                + "}"},
            new String[]{"\"Yes\",\n"
                + "}",
                "\"Yes\"",
                ",\n"
                + "}"},
            new String[]{"\"Comments on {\"}Filenames and Fonts{\"}\",\n"
                + "title = {Comments on \"Filenames and Fonts\"},",
                "\"Comments on {\"}Filenames and Fonts{\"}\"",
                ",\n"
                + "title = {Comments on \"Filenames and Fonts\"},"},
            new String[]{"{Comments on \"Filenames and Fonts\"},\n"
                + "title = \"Comments on {\"}Filenames and Fonts{\"}\",",
                "{Comments on \"Filenames and Fonts\"}",
                ",\n"
                + "title = \"Comments on {\"}Filenames and Fonts{\"}\","},
            new String[]{"goossens # and # mittelbach # and # samarin,\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\",",
                "goossens # and # mittelbach # and # samarin",
                ",\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\","},
            new String[]{"goossens # \" and \" # mittelbach # and # samarin,\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\",",
                "goossens # \" and \" # mittelbach # and # samarin",
                ",\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\","},
            new String[]{"\"goossens\" # \" and \" # mittelbach # and # samarin,\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\",",
                "\"goossens\" # \" and \" # mittelbach # and # samarin",
                ",\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\","},
            new String[]{"\"goossens #  and \" # mittelbach # and # samarin,\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\",",
                "\"goossens #  and \" # mittelbach # and # samarin",
                ",\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\","},
            new String[]{"\"goos,sens #  and \" # mittelbach # and # samarin,\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\",",
                "\"goos,sens #  and \" # mittelbach # and # samarin",
                ",\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\","},
            new String[]{"goossens # and # {mit,telbach} # and # samarin,\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\",",
                "goossens # and # {mit,telbach} # and # samarin",
                ",\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\","},
            new String[]{"goossens # and # {mit, \"tel\" # bach} # and # samarin,\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\",",
                "goossens # and # {mit, \"tel\" # bach} # and # samarin",
                ",\n"
                + "title = \"The {{\\LaTeX}} {C}ompanion\","},
            new String[]{"\"The {{\\LaTeX}} {C}ompanion\"}",
                "\"The {{\\LaTeX}} {C}ompanion\"",
                "}"},
            new String[]{"\"The {{\\LaTeX,}} {C}ompanion\"}",
                "\"The {{\\LaTeX,}} {C}ompanion\"",
                "}"},
            new String[]{"{The ,ompanion}}",
                "{The ,ompanion}",
                "}"},
            new String[]{"{The ,ompanion},}",
                "{The ,ompanion}",
                ",}"},
        };

        for (String[] test : tests) {
            try {
                Pair<String, String> expResult = new Pair<>(test[1], test[2]);
                Pair<String, String> result = Tokenizer.collectValue(test[0]);
                assertEquals("Input: <" + test[0] + ">", expResult, result);
            } catch (IOException ioe) {
               if (!"EX".equals(test[1])) {
                    fail("collectBibItem threw IOException \"" + ioe + "\" with input \"" + test[0] + "\"");
                }
            } catch (Exception ex) {
                System.err.println("Input: <" + test[0] + ">");
                throw ex;
            }
        }
    }
}
