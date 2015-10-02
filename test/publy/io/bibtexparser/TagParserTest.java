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
import java.io.StringReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class TagParserTest {
    
    public TagParserTest() {
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
     * Test of parseTag method, of class TagParser.
     */
    @Test
    public void testParseTag() throws IOException, ParseException {
        System.out.println("parseTag");
        
        String[] text = new String[] {
            "<author short=\"me\" name=\"Verdonschot, Sander\">",
            "<author short=\"padawan\" name=\"Wan, Pada\" url=\"http://uni.versity.edu/~pada/\" group=\"student\">",
            "<abbr short=\"team\" full=\"<<padawan>> and <<me>>\">",
            "<abbr short=\"proc\" full=\"Proceedings of the\">",
            "<abbr short=\"acat\" full=\"Awesome Conference on Amazing Things\">"
        };
        
        Tag tag0 = new Tag(Tag.Type.AUTHOR);
        tag0.values.put("short", "me");
        tag0.values.put("name", "Verdonschot, Sander");
        
        Tag tag1 = new Tag(Tag.Type.AUTHOR);
        tag1.values.put("short", "padawan");
        tag1.values.put("name", "Wan, Pada");
        tag1.values.put("url", "http://uni.versity.edu/~pada/");
        tag1.values.put("group", "student");
        
        Tag tag2 = new Tag(Tag.Type.ABBREVIATION);
        tag2.values.put("short", "team");
        tag2.values.put("full", "<<padawan>> and <<me>>");
        
        Tag tag3 = new Tag(Tag.Type.ABBREVIATION);
        tag3.values.put("short", "proc");
        tag3.values.put("full", "Proceedings of the");
        
        Tag tag4 = new Tag(Tag.Type.ABBREVIATION);
        tag4.values.put("short", "acat");
        tag4.values.put("full", "Awesome Conference on Amazing Things");
        
        Tag[] expResult = new Tag[] {
            tag0, tag1, tag2, tag3, tag4
        };
        
        for (int i = 0; i < text.length; i++) {
            Tag result = TagParser.parseTag(new StringReader(text[i]));
            assertEquals(expResult[i], result);
        }
    }
    
}
