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
     * Test of extractDelimitedToken method, of class Tokenizer.
     */
    @Test
    public void testExtractDelimitedToken_3args() {
        System.out.println("extractDelimitedToken_String");

        String[][] tests = new String[][]{
            new String[]{"\"abc def\" ghi", "\"", "\"", "\"abc def\"", " ghi"},
            new String[]{"\"\"", "\"", "\"", "\"\"", ""},
            new String[]{"  \"abc def\" ghi", "\"", "\"", "\"abc def\"", " ghi"},
            new String[]{" \n \t\n\r \"abc def\" ghi", "\"", "\"", "\"abc def\"", " ghi"},
            new String[]{"abc def\" ghi", "\"", "\"", "EX", ""},
            new String[]{"\" ghi", "\"", "\"", "EX", ""},
            new String[]{" ghi", "\"", "\"", "EX", ""},
            new String[]{" ", "\"", "\"", "EX", ""},
            new String[]{"", "\"", "\"", "EX", ""},
            new String[]{"\"ab\\\"c def\" ghi", "\"", "\"", "\"ab\\\"c def\"", " ghi"},
            new String[]{"\"ab\\\"c\\\" def\" ghi", "\"", "\"", "\"ab\\\"c\\\" def\"", " ghi"},
            new String[]{"\"ab\\\\\"c def\" ghi", "\"", "\"", "\"ab\\\\\"", "c def\" ghi"},
        };

        for (String[] test : tests) {
            try {
                Pair<String, String> expResult = new Pair<>(test[3], test[4]);
                Pair<String, String> result = Tokenizer.extractDelimitedToken(test[0], test[1].charAt(0), test[2].charAt(0));
                assertEquals("Input: <" + test[0] + "> Open: '" + test[1] + "' Close: '" + test[2] + "'", expResult, result);
            } catch (IOException ex) {
                if (!"EX".equals(test[3])) {
                    fail("extractDelimitedToken threw IOException \"" + ex + "\" with input \"" + test[0] + "\"");
                }
            }
        }
    }

    /**
     * Test of extractDelimitedToken method, of class Tokenizer.
     */
    @Test
    public void testExtractDelimitedToken_4args() {
        System.out.println("extractDelimitedToken_Reader");
        
        fail("The test case is a prototype.");
    }

}
