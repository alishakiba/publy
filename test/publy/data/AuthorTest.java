/*
 */
package publy.data;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class AuthorTest {

    public AuthorTest() {
    }

    /**
     * Tests name splitting.
     */
    @Test
    public void testSplit() {
        System.out.println("Split");

        Map<String, String[]> expected = new LinkedHashMap<>();

        expected.put("Sander Verdonschot", new String[]{"Sander", "", "Verdonschot", ""});
        expected.put("Verdonschot, Sander", new String[]{"Sander", "", "Verdonschot", ""});
        expected.put("Andr\\'e van Renssen", new String[]{"Andr\\'e", "van", "Renssen", ""});
        expected.put("van Renssen, Andr\\'e", new String[]{"Andr\\'e", "van", "Renssen", ""});
        expected.put("Jean-Lou de Carufel", new String[]{"Jean-Lou", "de", "Carufel", ""});
        expected.put("Jean-Lou De Carufel", new String[]{"Jean-Lou De", "", "Carufel", ""});
        expected.put("De Carufel, Jean-Lou", new String[]{"Jean-Lou", "", "De Carufel", ""});

        // Tests copied from http://maverick.inria.fr/~Xavier.Decoret/resources/xdkbibtex/bibtex_summary.html#splitting_examples
        // Test suite for the first name specification form First von Last
        expected.put("AA BB", new String[]{"AA", "", "BB", ""}); // Testing simple case with no von.
        expected.put("AA", new String[]{"", "", "AA", ""}); // Testing that Last cannot be empty.
        expected.put("AA bb", new String[]{"AA", "", "bb", ""}); // Idem.
        expected.put("aa", new String[]{"", "", "aa", ""}); // Idem.
        expected.put("AA bb CC", new String[]{"AA", "bb", "CC", ""}); // Testing simple von.
        expected.put("AA bb CC dd EE", new String[]{"AA", "bb CC dd", "EE", ""}); // Testing simple von (with inner uppercase words)
        expected.put("AA 1B cc dd", new String[]{"AA 1B", "cc", "dd", ""}); // Testing that digits are caseless (B fixes the case of 1B to uppercase).
        expected.put("AA 1b cc dd", new String[]{"AA", "1b cc", "dd", ""}); // Testing that digits are caseless (b fixes the case of 1b to lowercase)
        expected.put("AA {b}B cc dd", new String[]{"AA {b}B", "cc", "dd", ""}); // Testing that pseudo letters are caseless.
        expected.put("AA {b}b cc dd", new String[]{"AA", "{b}b cc", "dd", ""}); // Idem.
        expected.put("AA {B}b cc dd", new String[]{"AA", "{B}b cc", "dd", ""}); // Idem.
        expected.put("AA {B}B cc dd", new String[]{"AA {B}B", "cc", "dd", ""}); // Idem.
        expected.put("AA \\BB{b} cc dd", new String[]{"AA \\BB{b}", "cc", "dd", ""}); // Testing that non letters are case less (in particular show how latex command are considered).
        expected.put("AA \\bb{b} cc dd", new String[]{"AA", "\\bb{b} cc", "dd", ""}); // Idem.
        expected.put("AA {bb} cc DD", new String[]{"AA {bb}", "cc", "DD", ""}); // Testing that caseless words are grouped with First primilarily and then with Last.
        expected.put("AA bb {cc} DD", new String[]{"AA", "bb", "{cc} DD", ""}); // Idem.
        expected.put("AA {bb} CC", new String[]{"AA {bb}", "", "CC", ""}); // Idem.
        
        // Test suite for the second,third specification form von Last First
        expected.put("bb CC, AA", new String[]{"AA", "bb", "CC", ""}); // Simple case. Case do not matter for First.
        expected.put("bb CC, aa", new String[]{"aa", "bb", "CC", ""}); // Idem.
        expected.put("bb CC dd EE, AA", new String[]{"AA", "bb CC dd", "EE", ""}); // Testing simple von (with inner uppercase).
        expected.put("bb, AA", new String[]{"AA", "", "bb", ""}); // Testing that the Last part cannot be empty.
        expected.put("BB,", new String[]{"", "", "BB", ""}); // Testing that first can be empty after coma
        expected.put("bb CC,XX, AA", new String[]{"AA", "bb", "CC", "XX"}); // Simple Jr. Case do not matter for it.
        expected.put("bb CC,xx, AA", new String[]{"AA", "bb", "CC", "xx"}); // Idem.
        expected.put("BB,, AA", new String[]{"AA", "", "BB", ""}); // Testing that jr can be empty in between comas.
        
        // Test for whitespace normalization
        expected.put("  Sander       Verdonschot ", new String[]{"Sander", "", "Verdonschot", ""});
        
        for (String name : expected.keySet()) {
            Author a = new Author(name);
            
            assertEquals(name + " - First name: ", expected.get(name)[0], a.getFirstName());
            assertEquals(name + " - Von part: ", expected.get(name)[1], a.getVonPart());
            assertEquals(name + " - Last name: ", expected.get(name)[2], a.getLastName());
            assertEquals(name + " - Jr part: ", expected.get(name)[3], a.getJuniorPart());
        }
    }
}