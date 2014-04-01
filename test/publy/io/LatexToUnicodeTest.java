/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publy.io;

import java.util.HashMap;
import java.util.LinkedHashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 *
 */
public class LatexToUnicodeTest {

    public LatexToUnicodeTest() {
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
     * Test of convertToUnicode method, of class LatexToUnicode.
     */
    @Test
    public void testConvertToUnicode() {
        System.out.println("convertToUnicode");

        HashMap<String, String> expected = new LinkedHashMap<>();
        
        // Simple ones
        expected.put("\\`{o}", "ò");
        expected.put("\\'{o}", "ó");
        expected.put("\\\"{o}", "ö");
        expected.put("\\\"{i}", "ï");
        expected.put("\\.{o}", "ȯ");
        expected.put("\\^{o}", "ô");
        expected.put("\\H{o}", "ő");
        expected.put("\\~{o}", "õ");
        expected.put("\\={o}", "ō");

        // In-text
        expected.put("Test\\`otest\\'otest\\^otest\\\"otest\\~otest\\=otest\\.otest",
                "Testòtestótestôtestötestõtestōtestȯtest");
        expected.put("Test\\`{o}test\\'{o}test\\^{o}test\\\"{o}test\\~{o}test\\={o}test\\.{o}test",
                "Testòtestótestôtestötestõtestōtestȯtest");
        expected.put("Test\\u{o}test\\v{s}test\\H{o}test\\c{c}test",
                "Testŏtestštestőtestçtest");
        expected.put("Test\\`itest\\'itest\\^itest\\\"itest\\.itest",
                "Testìtestítestîtestïtestitest");
        expected.put("Test\\`{i}test\\'{i}test\\^{i}test\\\"{i}test\\.{i}test",
                "Testìtestítestîtestïtestitest");
        expected.put("Test\\`{\\i}test\\'{\\i}test\\^{\\i}test\\\"{\\i}test\\~{\\i}test\\={\\i}test\\.{\\i}test",
                "Testìtestítestîtestïtest\u0129test\u012Btestitest");

        // Don't touch things in math-mode
        expected.put("$\\'{o}$", "$\\'{o}$");
        expected.put("\\(\\'{o}\\)", "\\(\\'{o}\\)");
        expected.put("Test$\\'{o}$test", "Test$\\'{o}$test");
        expected.put("Test$\\'{o}$test\\'{o}test", "Test$\\'{o}$testótest");
        expected.put("Test\\(\\'{o}\\)test", "Test\\(\\'{o}\\)test");
        expected.put("Test\\(\\'{o}\\)test\\'{o}test", "Test\\(\\'{o}\\)testótest");
        expected.put("Test\\(\\'{o}\\)test$\\'{o}$test", "Test\\(\\'{o}\\)test$\\'{o}$test");
        
        // If there is no version with the argument specified, try without
        expected.put("Test\\l{ipsus}Test", "Testł{ipsus}Test");

        // Other
        expected.put("Alejandro L{\\'o}pez-Ortiz", "Alejandro L{ó}pez-Ortiz");
        expected.put("<span class=\"author\">A. L{\\'o}pez-Ortiz</span>, <span class=\"author\">P. Morin</span>, and <span class=\"author\">J. Munro</span>",
                     "<span class=\"author\">A. L{ó}pez-Ortiz</span>, <span class=\"author\">P. Morin</span>, and <span class=\"author\">J. Munro</span>");
        expected.put("Discrete {\\&} Computational Geometry", "Discrete {&} Computational Geometry");
        expected.put("Georgy Theodosiyovych Vorono\\\"i", "Georgy Theodosiyovych Voronoï");
        
        // From http://www.tex.ac.uk/ctan/biblio/bibtex/utils/bib2xhtml/example.bib
        expected.put("Albert-L\\'{a}szl\\'{o} Barab\\'{a}si", "Albert-László Barabási");
        expected.put("M\\^{o}nica Ferreira da Silva", "Mônica Ferreira da Silva");
        expected.put("F. Patern\\`{o}", "F. Paternò");
        expected.put("W{\\l}adys{\\l}aw M. Turski", "W{ł}adys{ł}aw M. Turski");
        expected.put("Sten-\\AA{ke} T\\\"{a}rnlund", "Sten-Å{ke} Tärnlund");
        expected.put("Ketil B{\\o}", "Ketil B{ø}");
        expected.put("J. Encarna\\c{c}{\\~a}o", "J. Encarnaç{ã}o");
        expected.put("\\Delta\\iota\\omicron\\mu\\eta\\delta\\eta\\varsigma \\Sigma\\pi\\iota\\nu\\epsilon\\lambda\\lambda\\eta\\varsigma", "Διομηδης Σπινελλης");
        expected.put("P\\\"{e}\\ss{}t\\^{e}r \\v{C}\\u{e}\\.{z}\\={o}\\.{g}", "Pëßtêr Čĕżōġ");
        
        for (String input : expected.keySet()) {
            String expectedResult = expected.get(input);
            String result = LatexToUnicode.convertToUnicode(input);

            assertEquals(expectedResult, result);
        }
    }
}