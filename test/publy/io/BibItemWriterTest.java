/*
 */
package publy.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import publy.data.bibitem.Article;
import publy.data.bibitem.Book;
import publy.data.bibitem.InCollection;
import publy.data.bibitem.InProceedings;
import publy.data.bibitem.InvitedTalk;
import publy.data.bibitem.MastersThesis;
import publy.data.bibitem.PhDThesis;
import publy.data.bibitem.Unpublished;
import publy.data.settings.FormatSettings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class BibItemWriterTest {
    
    public BibItemWriterTest() {
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
     * Test of changeCaseT method, of class BibItemWriter.
     */
    @Test
    public void testChangeCaseT() {
        System.out.println("changeCaseT");
        
        HashMap<String, String> expected = new LinkedHashMap<>();
        
        // Simple titles
        expected.put("myTitLE", "Mytitle");
        expected.put("MyTitLE", "Mytitle");
        expected.put("Mytitle", "Mytitle");
        expected.put("myTitLE IS AWESOME", "Mytitle is awesome");
        expected.put("Mytitle rocks", "Mytitle rocks");
        
        // Braces
        expected.put("{mYtItLe}", "mYtItLe");
        expected.put("{mYtItLe ROCKS}", "mYtItLe ROCKS");
        expected.put("mYtItLe {ROCKS}", "Mytitle ROCKS");
        expected.put("mYtItLe {RO{C}KS}", "Mytitle ROCKS");
        expected.put("m{Yt}ItLe RO{C}KS", "MYtitle roCks");
        expected.put("Diagonal flips in {H}amiltonian triangulations on the sphere", "Diagonal flips in Hamiltonian triangulations on the sphere");
        
        // Escapes
        expected.put("m\\{Yt\\}ItLe RO\\{C\\}KS", "M\\{yt\\}itle ro\\{c\\}ks");
        expected.put("m\\\\{Yt}ItLe RO\\\\{C}KS", "M\\\\Ytitle ro\\\\Cks");
        expected.put("m{Yt\\}ItLe RO{C}KS", "MYt\\itle roCks");
        
        // Mixed
        expected.put("Konvexe {F}{\\\"u}nfecke in ebenen {P}unktmengen", "Konvexe F\\\"unfecke in ebenen Punktmengen");
        
        BibItemWriter testInstance = new TestBibItemWriter(null, null);
        
        for (String inputTitle : expected.keySet()) {
            String expectedResult = expected.get(inputTitle);
            String result = testInstance.changeCaseT(inputTitle);
            
            assertEquals(expectedResult, result);
        }
    }
    
    @Test
    public void testRemoveBraces() {
        System.out.println("removeBraces");
        
        HashMap<String, String> expected = new LinkedHashMap<>();
        
        // Simple tests
        expected.put("{mYtItLe}", "mYtItLe");
        expected.put("{mYtItLe ROCKS}", "mYtItLe ROCKS");
        expected.put("mYtItLe {ROCKS}", "mYtItLe ROCKS");
        expected.put("mYtItLe {RO{C}KS}", "mYtItLe ROCKS");
        expected.put("m{Yt}ItLe RO{C}KS", "mYtItLe ROCKS");
        expected.put("Diagonal flips in {H}amiltonian triangulations on the sphere", "Diagonal flips in Hamiltonian triangulations on the sphere");
        
        // Escapes
        expected.put("m{\\{Yt}ItLe RO\\{C\\}KS", "m{YtItLe RO{C}KS");
        expected.put("m\\{Yt}ItLe RO\\{C}KS", "m{YtItLe RO{CKS");
        expected.put("m{Yt\\\\}ItLe RO{C}KS", "mYt\\\\ItLe ROCKS");
        
        // Mixed
        expected.put("Konvexe {F}{\\\"u}nfecke in ebenen {P}unktmengen", "Konvexe F\\\"unfecke in ebenen Punktmengen");
        
        // LaTeX commands
        expected.put("a given region in $\\mathbb{R}^2$ realizing", "a given region in $\\mathbb{R}^2$ realizing");
        expected.put("Mori~\\etal\\ showed", "Mori~\\etal\\ showed");
        expected.put("The \\item{}s that", "The \\item{}s that");
        expected.put("The \\item{}ca{tego}ries that", "The \\item{}categories that");
        expected.put("It is of the \\emph[very]{utmost} importance", "It is of the \\emph[very]{utmost} importance");
        expected.put("It is of \\emph[a little]{great} importance", "It is of \\emph[a little]{great} importance");
        expected.put("It is of \\emph[less]{not too much} importance", "It is of \\emph[less]{not too much} importance");
        
        BibItemWriter testInstance = new TestBibItemWriter(null, null);
        
        for (String inputTitle : expected.keySet()) {
            String expectedResult = expected.get(inputTitle);
            String result = testInstance.removeBraces(inputTitle);
            
            assertEquals(expectedResult, result);
        }
    }
    
    private class TestBibItemWriter extends BibItemWriter {

        private TestBibItemWriter(BufferedWriter out, FormatSettings settings) {
            super(out, settings);
        }

        @Override
        protected void writeArticle(Article item) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void writeBook(Book item) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void writeInProceedings(InProceedings item) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void writeMastersThesis(MastersThesis item) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void writePhDThesis(PhDThesis item) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void writeInCollection(InCollection item) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void writeInvitedTalk(InvitedTalk item) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void writeUnpublished(Unpublished item) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
}