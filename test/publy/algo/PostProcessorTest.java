/*
 * Copyright 2014 Sander.
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

package publy.algo;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import publy.data.bibitem.BibItem;

/**
 *
 * @author Sander
 */
public class PostProcessorTest {
    
    public PostProcessorTest() {
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
     * Test of detectArxiv method, of class PostProcessor.
     */
    @Test
    public void testDetectArxiv() {
        System.out.println("detectArxiv");
        
        // No arxiv
        BibItem item1 = new BibItem("article", "test");
        item1.put("author", "Thor, Au");
        item1.put("title", "Title");
        item1.put("journal", "Journal of Examples");
        item1.put("year", "2013");
        item1.put("--testArxiv", "");
        item1.put("--testClass", "");
        
        // Just arxiv
        BibItem item2 = new BibItem("article", "test");
        item2.put("author", "Thor, Au");
        item2.put("title", "Title");
        item2.put("journal", "Journal of Examples");
        item2.put("year", "2013");
        item2.put("arxiv", "1234.1234");
        item2.put("--testArxiv", "1234.1234");
        item2.put("--testClass", "");
        
        // arxiv and primary class
        BibItem item3 = new BibItem("article", "test");
        item3.put("author", "Thor, Au");
        item3.put("title", "Title");
        item3.put("journal", "Journal of Examples");
        item3.put("year", "2013");
        item3.put("arxiv", "1234.1234");
        item3.put("primaryClass", "cs.cg");
        item3.put("--testArxiv", "1234.1234");
        item3.put("--testClass", "cs.cg");
        
        // Other arxiv versions
        BibItem item4 = new BibItem("article", "test");
        item4.put("author", "Thor, Au");
        item4.put("title", "Title");
        item4.put("journal", "Journal of Examples");
        item4.put("year", "2013");
        item4.put("arxiv", "1234.1234v2");
        item4.put("--testArxiv", "1234.1234v2");
        item4.put("--testClass", "");
        
        BibItem item5 = new BibItem("article", "test");
        item5.put("author", "Thor, Au");
        item5.put("title", "Title");
        item5.put("journal", "Journal of Examples");
        item5.put("year", "2013");
        item5.put("arxiv", "cs.cg/1234.1234");
        item5.put("--testArxiv", "1234.1234");
        item5.put("--testClass", "cs.cg");
        
        BibItem item6 = new BibItem("article", "test");
        item6.put("author", "Thor, Au");
        item6.put("title", "Title");
        item6.put("journal", "Journal of Examples");
        item6.put("year", "2013");
        item6.put("arxiv", "1234.1234 [cs.cg]");
        item6.put("--testArxiv", "1234.1234");
        item6.put("--testClass", "cs.cg");
        
        BibItem item7 = new BibItem("article", "test");
        item7.put("author", "Thor, Au");
        item7.put("title", "Title");
        item7.put("journal", "Journal of Examples");
        item7.put("year", "2013");
        item7.put("arxiv", "1234.1234v5 [math]");
        item7.put("--testArxiv", "1234.1234v5");
        item7.put("--testClass", "math");
        
        // Other fields
        BibItem item8 = new BibItem("article", "test");
        item8.put("author", "Thor, Au");
        item8.put("title", "Title");
        item8.put("journal", "Journal of Examples");
        item8.put("year", "2013");
        item8.put("eprint", "math/0307200v3");
        item8.put("--testArxiv", "0307200v3");
        item8.put("--testClass", "math");
        
        BibItem item9 = new BibItem("article", "test");
        item9.put("author", "Thor, Au");
        item9.put("title", "Title");
        item9.put("journal", "Journal of Examples");
        item9.put("year", "2013");
        item9.put("archivePrefix", "arXiv");
        item9.put("eprint", "0707.3168");
        item9.put("primaryClass", "hep-th");
        item9.put("--testArxiv", "0707.3168");
        item9.put("--testClass", "hep-th");
        
        BibItem item10 = new BibItem("article", "test");
        item10.put("author", "Thor, Au");
        item10.put("title", "Title");
        item10.put("journal", "Journal of Examples");
        item10.put("year", "2013");
        item10.put("archivePrefix", "Snarxiv"); // NOT arxiv
        item10.put("eprint", "0707.3168");
        item10.put("primaryClass", "hep-th");
        item10.put("--testArxiv", "");
        item10.put("--testClass", "hep-th");
        
        BibItem item11 = new BibItem("article", "test");
        item11.put("author", "Thor, Au");
        item11.put("title", "Title");
        item11.put("journal", "Journal of Examples");
        item11.put("year", "2013");
        item11.put("ee", "http://arxiv.org/abs/1110.6473");
        item11.put("--testArxiv", "1110.6473");
        item11.put("--testClass", "");
        
        BibItem item12 = new BibItem("article", "test");
        item12.put("author", "Thor, Au");
        item12.put("title", "Title");
        item12.put("journal", "Journal of Examples");
        item12.put("year", "2013");
        item12.put("url", "http://arxiv.org/abs/1110.6473");
        item12.put("--testArxiv", "1110.6473");
        item12.put("--testClass", "");
        
        BibItem items[] = new BibItem[] {item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12};
        
        for (BibItem item : items) {
            PostProcessor.detectArxiv(item);
            assertEquals("arXiv mismatch: " + item, item.get("--testArxiv"), (item.get("arxiv") == null ? "" : item.get("arxiv")));
            assertEquals("class mismatch: " + item, item.get("--testClass"), (item.get("primaryClass") == null ? "" : item.get("primaryClass")));
        }
    }
    
}
