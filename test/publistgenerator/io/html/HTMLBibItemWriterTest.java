/*
 */
package publistgenerator.io.html;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import publistgenerator.data.bibitem.Article;
import publistgenerator.data.bibitem.BibItem;
import publistgenerator.data.bibitem.InCollection;
import publistgenerator.data.bibitem.InProceedings;
import publistgenerator.data.bibitem.InvitedTalk;
import publistgenerator.data.bibitem.MastersThesis;
import publistgenerator.data.bibitem.PhDThesis;
import publistgenerator.data.bibitem.Unpublished;
import publistgenerator.data.settings.HTMLSettings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLBibItemWriterTest {
    
    public HTMLBibItemWriterTest() {
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
     * Test of matches method, of class HTMLBibItemWriter.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        
        HTMLSettings.PublicationType[] types = new HTMLSettings.PublicationType[]{HTMLSettings.PublicationType.NONE, HTMLSettings.PublicationType.PUBLISHED, HTMLSettings.PublicationType.ACCEPTED, HTMLSettings.PublicationType.ARXIV, HTMLSettings.PublicationType.ALL};
        
        // ALL
        BibItem submitted = new Article();
        submitted.put("author", "Thor, Au");
        submitted.put("title", "Title");
        submitted.put("journal", "Journal of Examples");
        submitted.put("year", "2013");
        submitted.put("status", "submitted");
        submitted.put("--test--", "4"); // Should match this type and up
        
        // ARXIV
        BibItem submittedArxiv = new Article();
        submittedArxiv.put("author", "Thor, Au");
        submittedArxiv.put("title", "Title");
        submittedArxiv.put("journal", "Journal of Examples");
        submittedArxiv.put("year", "2013");
        submittedArxiv.put("status", "submitted");
        submittedArxiv.put("arxiv", "X");
        submittedArxiv.put("--test--", "3"); // Should match this type and up
        
        // ACCEPTED
        BibItem accepted = new Article();
        accepted.put("author", "Thor, Au");
        accepted.put("title", "Title");
        accepted.put("journal", "Journal of Examples");
        accepted.put("year", "2013");
        accepted.put("status", "accepted");
        accepted.put("--test--", "2"); // Should match this type and up
        
        BibItem acceptedrev = new Article();
        acceptedrev.put("author", "Thor, Au");
        acceptedrev.put("title", "Title");
        acceptedrev.put("journal", "Journal of Examples");
        acceptedrev.put("year", "2013");
        acceptedrev.put("status", "acceptedrev");
        acceptedrev.put("--test--", "2"); // Should match this type and up
        
        BibItem acceptedArxiv = new Article();
        acceptedArxiv.put("author", "Thor, Au");
        acceptedArxiv.put("title", "Title");
        acceptedArxiv.put("journal", "Journal of Examples");
        acceptedArxiv.put("year", "2013");
        acceptedArxiv.put("status", "accepted");
        acceptedArxiv.put("arxiv", "X");
        acceptedArxiv.put("--test--", "2"); // Should match this type and up
        
        // PUBLISHED
        BibItem published = new Article();
        published.put("author", "Thor, Au");
        published.put("title", "Title");
        published.put("journal", "Journal of Examples");
        published.put("year", "2013");
        published.put("--test--", "1"); // Should match this type and up
        
        BibItem publishedArxiv = new Article();
        publishedArxiv.put("author", "Thor, Au");
        publishedArxiv.put("title", "Title");
        publishedArxiv.put("journal", "Journal of Examples");
        publishedArxiv.put("year", "2013");
        publishedArxiv.put("arxiv", "X");
        publishedArxiv.put("--test--", "1"); // Should match this type and up
        
        BibItem items[] = new BibItem[] {submitted, submittedArxiv, accepted, acceptedrev, acceptedArxiv, published, publishedArxiv};
        
        for (BibItem item : items) {
            for (int i = 0; i < types.length; i++) {
                boolean expResult = Integer.parseInt(item.get("--test--")) <= i;
                boolean result = HTMLBibItemWriter.matches(types[i], item);
                
                if (expResult != result) {
                    System.out.println("Wrong match!");
                    System.out.println("TYPE: " + types[i]);
                    System.out.println("ITEM:");
                    System.out.println(item.toString());
                }
                
                assertEquals(expResult, result);
            }
        }
    }
}