/*
 */
package publistgenerator.io;

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
import publistgenerator.data.bibitem.Article;
import publistgenerator.data.bibitem.InCollection;
import publistgenerator.data.bibitem.InProceedings;
import publistgenerator.data.bibitem.InvitedTalk;
import publistgenerator.data.bibitem.MastersThesis;
import publistgenerator.data.bibitem.PhDThesis;
import publistgenerator.data.bibitem.Unpublished;
import publistgenerator.data.settings.FormatSettings;

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
        
        BibItemWriter testInstance = new TestBibItemWriter(null, null);
        
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
        expected.put("m\\{Yt\\}ItLe RO\\{C\\}KS", "M{yt}itle ro{c}ks");
        expected.put("m\\\\{Yt}ItLe RO\\\\{C}KS", "M\\Ytitle ro\\Cks");
        
        // Mixed
        expected.put("Konvexe {F}{\\\"u}nfecke in ebenen {P}unktmengen", "Konvexe F\\\"unfecke in ebenen Punktmengen");
        
        for (String inputTitle : expected.keySet()) {
            String expectedResult = expected.get(inputTitle);
            String result = testInstance.changeCaseT(inputTitle);
            
            assertEquals(expectedResult, result);
        }
    }
    
    private class TestBibItemWriter extends BibItemWriter {

        private TestBibItemWriter(BufferedWriter out, FormatSettings settings) {
            super(out, settings);
        }

        @Override
        public void write(Article item, int number) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void write(InProceedings item, int number) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void write(MastersThesis item, int number) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void write(PhDThesis item, int number) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void write(InCollection item, int number) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void write(InvitedTalk item, int number) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void write(Unpublished item, int number) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
}