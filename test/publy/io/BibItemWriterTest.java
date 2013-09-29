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
import publy.data.Pair;
import publy.data.bibitem.BibItem;
import publy.data.settings.Settings;

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
     * Test of formatDate method, of class BibItemWriter.
     */
    @Test
    public void testFormatDate() {
        System.out.println("formatDate");

        HashMap<Pair<String, String>, String> expected = new LinkedHashMap<>();

        // Month only (just for consistency's sake, all current formats require year)
        expected.put(new Pair<>("January", ""), "January");
        expected.put(new Pair<>("Januari", ""), "Januari");
        expected.put(new Pair<>("Jan.", ""), "Jan.");
        expected.put(new Pair<>("jan", ""), "January");
        expected.put(new Pair<>("1", ""), "January");

        // Year only
        expected.put(new Pair<>("", "1991"), "1991");

        // Both
        expected.put(new Pair<>("January", "2010"), "January 2010");
        expected.put(new Pair<>("Januari", "2010"), "Januari 2010");
        expected.put(new Pair<>("F{\'e}vrier", "2010"), "F{\'e}vrier 2010");
        expected.put(new Pair<>("Jan.", "2010"), "Jan. 2010");

        expected.put(new Pair<>("jan", "2010"), "January 2010");
        expected.put(new Pair<>("feb", "2010"), "February 2010");
        expected.put(new Pair<>("mar", "2010"), "March 2010");
        expected.put(new Pair<>("apr", "2010"), "April 2010");
        expected.put(new Pair<>("may", "2010"), "May 2010");
        expected.put(new Pair<>("jun", "2010"), "June 2010");
        expected.put(new Pair<>("jul", "2010"), "July 2010");
        expected.put(new Pair<>("aug", "2010"), "August 2010");
        expected.put(new Pair<>("sep", "2010"), "September 2010");
        expected.put(new Pair<>("oct", "2010"), "October 2010");
        expected.put(new Pair<>("nov", "2010"), "November 2010");
        expected.put(new Pair<>("dec", "2010"), "December 2010");

        expected.put(new Pair<>("1", "2010"), "January 2010");
        expected.put(new Pair<>("2", "2010"), "February 2010");
        expected.put(new Pair<>("3", "2010"), "March 2010");
        expected.put(new Pair<>("4", "2010"), "April 2010");
        expected.put(new Pair<>("5", "2010"), "May 2010");
        expected.put(new Pair<>("6", "2010"), "June 2010");
        expected.put(new Pair<>("7", "2010"), "July 2010");
        expected.put(new Pair<>("8", "2010"), "August 2010");
        expected.put(new Pair<>("9", "2010"), "September 2010");
        expected.put(new Pair<>("10", "2010"), "October 2010");
        expected.put(new Pair<>("11", "2010"), "November 2010");
        expected.put(new Pair<>("12", "2010"), "December 2010");

        BibItemWriter testInstance = new TestBibItemWriter(null, null);

        for (Pair<String, String> input : expected.keySet()) {
            BibItem item = new BibItem("misc", "id");
            item.put("month", input.getFirst());
            item.put("year", input.getSecond());

            String expectedResult = expected.get(input);
            String result = testInstance.formatDate(item);

            assertEquals(expectedResult, result);
        }
    }

    /**
     * Test of toTitleCase method, of class BibItemWriter.
     */
    @Test
    public void testToTitleCase() {
        System.out.println("toTitleCase");

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
            String result = testInstance.toTitleCase(inputTitle);

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

        private TestBibItemWriter(BufferedWriter out, Settings settings) {
            super(out, settings);
        }

        @Override
        public void write(BibItem item) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}