/*
 */
package publy.data;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import publy.data.bibitem.BibItem;

/**
 *
 *
 */
public class PublicationTypeTest {
    
    public PublicationTypeTest() {
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
     * Test of matches method, of class PublicationType.
     */
    @Test
    public void testMatches_BibItem() {
        System.out.println("matches");
        
        PublicationStatus[] types = new PublicationStatus[]{PublicationStatus.NONE, PublicationStatus.PUBLISHED, PublicationStatus.ACCEPTED, PublicationStatus.ARXIV, PublicationStatus.ALL};
        
        // ALL
        BibItem submitted = new BibItem("article", "test");
        submitted.put("author", "Thor, Au");
        submitted.put("title", "Title");
        submitted.put("journal", "Journal of Examples");
        submitted.put("year", "2013");
        submitted.put("pubstate", "submitted");
        submitted.put("--test--", "4"); // Should match this type and up
        
        // ARXIV
        BibItem submittedArxiv = new BibItem("article", "test");
        submittedArxiv.put("author", "Thor, Au");
        submittedArxiv.put("title", "Title");
        submittedArxiv.put("journal", "Journal of Examples");
        submittedArxiv.put("year", "2013");
        submittedArxiv.put("pubstate", "submitted");
        submittedArxiv.put("arxiv", "X");
        submittedArxiv.put("--test--", "3"); // Should match this type and up
        
        // ACCEPTED
        BibItem accepted = new BibItem("article", "test");
        accepted.put("author", "Thor, Au");
        accepted.put("title", "Title");
        accepted.put("journal", "Journal of Examples");
        accepted.put("year", "2013");
        accepted.put("pubstate", "accepted");
        accepted.put("--test--", "2"); // Should match this type and up
        
        BibItem acceptedrev = new BibItem("article", "test");
        acceptedrev.put("author", "Thor, Au");
        acceptedrev.put("title", "Title");
        acceptedrev.put("journal", "Journal of Examples");
        acceptedrev.put("year", "2013");
        acceptedrev.put("pubstate", "acceptedrev");
        acceptedrev.put("--test--", "2"); // Should match this type and up
        
        BibItem acceptedArxiv = new BibItem("article", "test");
        acceptedArxiv.put("author", "Thor, Au");
        acceptedArxiv.put("title", "Title");
        acceptedArxiv.put("journal", "Journal of Examples");
        acceptedArxiv.put("year", "2013");
        acceptedArxiv.put("pubstate", "accepted");
        acceptedArxiv.put("arxiv", "X");
        acceptedArxiv.put("--test--", "2"); // Should match this type and up
        
        BibItem forthcoming = new BibItem("article", "test");
        forthcoming.put("author", "Thor, Au");
        forthcoming.put("title", "Title");
        forthcoming.put("journal", "Journal of Examples");
        forthcoming.put("year", "2013");
        forthcoming.put("pubstate", "forthcoming");
        forthcoming.put("--test--", "2"); // Should match this type and up
        
        BibItem inpress = new BibItem("article", "test");
        inpress.put("author", "Thor, Au");
        inpress.put("title", "Title");
        inpress.put("journal", "Journal of Examples");
        inpress.put("year", "2013");
        inpress.put("pubstate", "inpress");
        inpress.put("--test--", "2"); // Should match this type and up
        
        BibItem prepublished = new BibItem("article", "test");
        prepublished.put("author", "Thor, Au");
        prepublished.put("title", "Title");
        prepublished.put("journal", "Journal of Examples");
        prepublished.put("year", "2013");
        prepublished.put("pubstate", "prepublished");
        prepublished.put("--test--", "2"); // Should match this type and up
        
        // PUBLISHED
        BibItem published = new BibItem("article", "test");
        published.put("author", "Thor, Au");
        published.put("title", "Title");
        published.put("journal", "Journal of Examples");
        published.put("year", "2013");
        published.put("--test--", "1"); // Should match this type and up
        
        BibItem publishedArxiv = new BibItem("article", "test");
        publishedArxiv.put("author", "Thor, Au");
        publishedArxiv.put("title", "Title");
        publishedArxiv.put("journal", "Journal of Examples");
        publishedArxiv.put("year", "2013");
        publishedArxiv.put("arxiv", "X");
        publishedArxiv.put("--test--", "1"); // Should match this type and up
        
        BibItem items[] = new BibItem[] {submitted, submittedArxiv, prepublished, acceptedrev, acceptedArxiv, published, publishedArxiv};
        
        // Static method
        for (BibItem item : items) {
            for (int i = 0; i < types.length; i++) {
                boolean expResult = Integer.parseInt(item.get("--test--")) <= i;
                boolean result = PublicationStatus.matches(types[i], item);
                
                assertEquals("Type: " + types[i] + ". Item: " + item.toString(), expResult, result);
            }
        }
        
        // Non-static method
        for (BibItem item : items) {
            for (int i = 0; i < types.length; i++) {
                boolean expResult = Integer.parseInt(item.get("--test--")) <= i;
                boolean result = types[i].matches(item);
                
                assertEquals("Type: " + types[i] + ". Item: " + item.toString(), expResult, result);
            }
        }
    }
}