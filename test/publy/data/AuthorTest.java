/*
 */
package publy.data;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import publy.data.settings.GeneralSettings;

/**
 *
 *
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

        expected.put("von Last, Jr., First", new String[]{"First", "von", "Last", "Jr."});
        
        expected.put("Sander Verdonschot", new String[]{"Sander", "", "Verdonschot", ""});
        expected.put("Verdonschot, Sander", new String[]{"Sander", "", "Verdonschot", ""});
        expected.put("Andr\\'e van Renssen", new String[]{"Andr\\'e", "van", "Renssen", ""});
        expected.put("van Renssen, Andr\\'e", new String[]{"Andr\\'e", "van", "Renssen", ""});
        expected.put("Jean-Lou de Carufel", new String[]{"Jean-Lou", "de", "Carufel", ""});
        expected.put("Jean-Lou De Carufel", new String[]{"Jean-Lou De", "", "Carufel", ""});
        expected.put("Jean-Lou {D}e Carufel", new String[]{"Jean-Lou", "{D}e", "Carufel", ""});
        expected.put("De Carufel, Jean-Lou", new String[]{"Jean-Lou", "", "De Carufel", ""});
        
        expected.put("\\'Emile Gaudreault", new String[]{"\\'Emile", "", "Gaudreault", ""});
        expected.put("{\\'E}mile Gaudreault", new String[]{"{\\'E}mile", "", "Gaudreault", ""});

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
        expected.put("BB, , AA", new String[]{"AA", "", "BB", ""}); // Idem.

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

    /**
     * Tests name formatting.
     */
    @Test
    public void testFormatFull() {
        System.out.println("Format - Full");

        Map<String, String> expected = new LinkedHashMap<>();
        
        expected.put("von Last, Jr., First", "First von Last, Jr.");

        expected.put("Sander Verdonschot", "Sander Verdonschot");
        expected.put("Verdonschot, Sander", "Sander Verdonschot");
        expected.put("Andr\\'e van Renssen", "Andr\\'e van Renssen");
        expected.put("van Renssen, Andr\\'e", "Andr\\'e van Renssen");
        expected.put("Jean-Lou de Carufel", "Jean-Lou de Carufel");
        expected.put("Jean-Lou De Carufel", "Jean-Lou De Carufel");
        expected.put("Jean-Lou {D}e Carufel", "Jean-Lou {D}e Carufel");
        expected.put("De Carufel, Jean-Lou", "Jean-Lou De Carufel");
        
        expected.put("\\'Emile Gaudreault", "\\'Emile Gaudreault");
        expected.put("{\\'E}mile Gaudreault", "{\\'E}mile Gaudreault");

        // Tests copied from http://maverick.inria.fr/~Xavier.Decoret/resources/xdkbibtex/bibtex_summary.html#splitting_examples
        // Test suite for the first name specification form First von Last
        expected.put("AA BB", "AA BB"); // Testing simple case with no von.
        expected.put("AA", "AA"); // Testing that Last cannot be empty.
        expected.put("AA bb", "AA bb"); // Idem.
        expected.put("aa", "aa"); // Idem.
        expected.put("AA bb CC", "AA bb CC"); // Testing simple von.
        expected.put("AA bb CC dd EE", "AA bb CC dd EE"); // Testing simple von (with inner uppercase words)
        expected.put("AA 1B cc dd", "AA 1B cc dd"); // Testing that digits are caseless (B fixes the case of 1B to uppercase).
        expected.put("AA 1b cc dd", "AA 1b cc dd"); // Testing that digits are caseless (b fixes the case of 1b to lowercase)
        expected.put("AA {b}B cc dd", "AA {b}B cc dd"); // Testing that pseudo letters are caseless.
        expected.put("AA {b}b cc dd", "AA {b}b cc dd"); // Idem.
        expected.put("AA {B}b cc dd", "AA {B}b cc dd"); // Idem.
        expected.put("AA {B}B cc dd", "AA {B}B cc dd"); // Idem.
        expected.put("AA \\BB{b} cc dd", "AA \\BB{b} cc dd"); // Testing that non letters are case less (in particular show how latex command are considered).
        expected.put("AA \\bb{b} cc dd", "AA \\bb{b} cc dd"); // Idem.
        expected.put("AA {bb} cc DD", "AA {bb} cc DD"); // Testing that caseless words are grouped with First primilarily and then with Last.
        expected.put("AA bb {cc} DD", "AA bb {cc} DD"); // Idem.
        expected.put("AA {bb} CC", "AA {bb} CC"); // Idem.

        // Test suite for the second,third specification form von Last First
        expected.put("bb CC, AA", "AA bb CC"); // Simple case. Case do not matter for First.
        expected.put("bb CC, aa", "aa bb CC"); // Idem.
        expected.put("bb CC dd EE, AA", "AA bb CC dd EE"); // Testing simple von (with inner uppercase).
        expected.put("bb, AA", "AA bb"); // Testing that the Last part cannot be empty.
        expected.put("BB,", "BB"); // Testing that first can be empty after coma
        expected.put("bb CC,XX, AA", "AA bb CC, XX"); // Simple Jr. Case do not matter for it.
        expected.put("bb CC,xx, AA", "AA bb CC, xx"); // Idem.
        expected.put("BB,, AA", "AA BB"); // Testing that jr can be empty in between comas.

        for (String name : expected.keySet()) {
            Author a = new Author(name);

            assertEquals(name + " - ", expected.get(name), a.getFormattedName(GeneralSettings.FirstNameDisplay.FULL, false));
        }
    }
    
    /**
     * Tests name formatting.
     */
    @Test
    public void testFormatFullReversed() {
        System.out.println("Format - Full Reversed");

        Map<String, String> expected = new LinkedHashMap<>();
        
        expected.put("von Last, Jr., First", "von Last, Jr., First");

        expected.put("Sander Verdonschot", "Verdonschot, Sander");
        expected.put("Verdonschot, Sander", "Verdonschot, Sander");
        expected.put("Andr\\'e van Renssen", "van Renssen, Andr\\'e");
        expected.put("van Renssen, Andr\\'e", "van Renssen, Andr\\'e");
        expected.put("Jean-Lou de Carufel", "de Carufel, Jean-Lou");
        expected.put("Jean-Lou De Carufel", "Carufel, Jean-Lou De");
        expected.put("Jean-Lou {D}e Carufel", "{D}e Carufel, Jean-Lou");
        expected.put("De Carufel, Jean-Lou", "De Carufel, Jean-Lou");
        
        expected.put("\\'Emile Gaudreault", "Gaudreault, \\'Emile");
        expected.put("{\\'E}mile Gaudreault", "Gaudreault, {\\'E}mile");

        // Tests copied from http://maverick.inria.fr/~Xavier.Decoret/resources/xdkbibtex/bibtex_summary.html#splitting_examples
        // Test suite for the first name specification form First von Last
        expected.put("AA BB", "BB, AA"); // Testing simple case with no von.
        expected.put("AA", "AA"); // Testing that Last cannot be empty.
        expected.put("AA bb", "bb, AA"); // Idem.
        expected.put("aa", "aa"); // Idem.
        expected.put("AA bb CC", "bb CC, AA"); // Testing simple von.
        expected.put("AA bb CC dd EE", "bb CC dd EE, AA"); // Testing simple von (with inner uppercase words)
        expected.put("AA 1B cc dd", "cc dd, AA 1B"); // Testing that digits are caseless (B fixes the case of 1B to uppercase).
        expected.put("AA 1b cc dd", "1b cc dd, AA"); // Testing that digits are caseless (b fixes the case of 1b to lowercase)
        expected.put("AA {b}B cc dd", "cc dd, AA {b}B"); // Testing that pseudo letters are caseless.
        expected.put("AA {b}b cc dd", "{b}b cc dd, AA"); // Idem.
        expected.put("AA {B}b cc dd", "{B}b cc dd, AA"); // Idem.
        expected.put("AA {B}B cc dd", "cc dd, AA {B}B"); // Idem.
        expected.put("AA \\BB{b} cc dd", "cc dd, AA \\BB{b}"); // Testing that non letters are case less (in particular show how latex command are considered).
        expected.put("AA \\bb{b} cc dd", "\\bb{b} cc dd, AA"); // Idem.
        expected.put("AA {bb} cc DD", "cc DD, AA {bb}"); // Testing that caseless words are grouped with First primilarily and then with Last.
        expected.put("AA bb {cc} DD", "bb {cc} DD, AA"); // Idem.
        expected.put("AA {bb} CC", "CC, AA {bb}"); // Idem.

        // Test suite for the second,third specification form von Last First
        expected.put("bb CC, AA", "bb CC, AA"); // Simple case. Case do not matter for First.
        expected.put("bb CC, aa", "bb CC, aa"); // Idem.
        expected.put("bb CC dd EE, AA", "bb CC dd EE, AA"); // Testing simple von (with inner uppercase).
        expected.put("bb, AA", "bb, AA"); // Testing that the Last part cannot be empty.
        expected.put("BB,", "BB"); // Testing that first can be empty after coma
        expected.put("bb CC,XX, AA", "bb CC, XX, AA"); // Simple Jr. Case do not matter for it.
        expected.put("bb CC,xx, AA", "bb CC, xx, AA"); // Idem.
        expected.put("BB,, AA", "BB, AA"); // Testing that jr can be empty in between comas.

        for (String name : expected.keySet()) {
            Author a = new Author(name);

            assertEquals(name + " - ", expected.get(name), a.getFormattedName(GeneralSettings.FirstNameDisplay.FULL, true));
        }
    }
    
    /**
     * Tests name formatting.
     */
    @Test
    public void testFormatNone() {
        System.out.println("Format - None");

        Map<String, String> expected = new LinkedHashMap<>();
        
        expected.put("von Last, Jr., First", "von Last, Jr.");

        expected.put("Sander Verdonschot", "Verdonschot");
        expected.put("Verdonschot, Sander", "Verdonschot");
        expected.put("Andr\\'e van Renssen", "van Renssen");
        expected.put("van Renssen, Andr\\'e", "van Renssen");
        expected.put("Jean-Lou de Carufel", "de Carufel");
        expected.put("Jean-Lou De Carufel", "Carufel");
        expected.put("Jean-Lou {D}e Carufel", "{D}e Carufel");
        expected.put("De Carufel, Jean-Lou", "De Carufel");
        
        expected.put("\\'Emile Gaudreault", "Gaudreault");
        expected.put("{\\'E}mile Gaudreault", "Gaudreault");

        // Tests copied from http://maverick.inria.fr/~Xavier.Decoret/resources/xdkbibtex/bibtex_summary.html#splitting_examples
        // Test suite for the first name specification form First von Last
        expected.put("AA BB", "BB"); // Testing simple case with no von.
        expected.put("AA", "AA"); // Testing that Last cannot be empty.
        expected.put("AA bb", "bb"); // Idem.
        expected.put("aa", "aa"); // Idem.
        expected.put("AA bb CC", "bb CC"); // Testing simple von.
        expected.put("AA bb CC dd EE", "bb CC dd EE"); // Testing simple von (with inner uppercase words)
        expected.put("AA 1B cc dd", "cc dd"); // Testing that digits are caseless (B fixes the case of 1B to uppercase).
        expected.put("AA 1b cc dd", "1b cc dd"); // Testing that digits are caseless (b fixes the case of 1b to lowercase)
        expected.put("AA {b}B cc dd", "cc dd"); // Testing that pseudo letters are caseless.
        expected.put("AA {b}b cc dd", "{b}b cc dd"); // Idem.
        expected.put("AA {B}b cc dd", "{B}b cc dd"); // Idem.
        expected.put("AA {B}B cc dd", "cc dd"); // Idem.
        expected.put("AA \\BB{b} cc dd", "cc dd"); // Testing that non letters are case less (in particular show how latex command are considered).
        expected.put("AA \\bb{b} cc dd", "\\bb{b} cc dd"); // Idem.
        expected.put("AA {bb} cc DD", "cc DD"); // Testing that caseless words are grouped with First primilarily and then with Last.
        expected.put("AA bb {cc} DD", "bb {cc} DD"); // Idem.
        expected.put("AA {bb} CC", "CC"); // Idem.

        // Test suite for the second,third specification form von Last First
        expected.put("bb CC, AA", "bb CC"); // Simple case. Case do not matter for First.
        expected.put("bb CC, aa", "bb CC"); // Idem.
        expected.put("bb CC dd EE, AA", "bb CC dd EE"); // Testing simple von (with inner uppercase).
        expected.put("bb, AA", "bb"); // Testing that the Last part cannot be empty.
        expected.put("BB,", "BB"); // Testing that first can be empty after coma
        expected.put("bb CC,XX, AA", "bb CC, XX"); // Simple Jr. Case do not matter for it.
        expected.put("bb CC,xx, AA", "bb CC, xx"); // Idem.
        expected.put("BB,, AA", "BB"); // Testing that jr can be empty in between comas.

        for (String name : expected.keySet()) {
            Author a = new Author(name);

            // Reversed doesn't matter
            assertEquals(name + " - ", expected.get(name), a.getFormattedName(GeneralSettings.FirstNameDisplay.NONE, false));
            assertEquals(name + " reversed - ", expected.get(name), a.getFormattedName(GeneralSettings.FirstNameDisplay.NONE, true));
        }
    }
    
    /**
     * Tests name formatting.
     */
    @Test
    public void testFormatAbbreviated() {
        System.out.println("Format - Abbreviated");

        Map<String, String> expected = new LinkedHashMap<>();
        
        expected.put("von Last, Jr., First", "F. von Last, Jr.");

        expected.put("Sander Verdonschot", "S. Verdonschot");
        expected.put("Verdonschot, Sander", "S. Verdonschot");
        expected.put("Andr\\'e van Renssen", "A. van Renssen");
        expected.put("van Renssen, Andr\\'e", "A. van Renssen");
        
        // Dash in first name
        expected.put("Jean-Lou de Carufel", "J.-L. de Carufel");
        expected.put("Jean-Lou De Carufel", "J.-L. D. Carufel");
        expected.put("Jean-Lou {D}e Carufel", "J.-L. {D}e Carufel");
        expected.put("De Carufel, Jean-Lou", "J.-L. De Carufel");
        
        // Special character as first letter
        expected.put("\\'Emile Gaudreault", "E. Gaudreault");
        expected.put("{\\'E}mile Gaudreault", "{\\'E}. Gaudreault");

        // Tests copied from http://maverick.inria.fr/~Xavier.Decoret/resources/xdkbibtex/bibtex_summary.html#splitting_examples
        // Test suite for the first name specification form First von Last
        expected.put("AA BB", "A. BB"); // Testing simple case with no von.
        expected.put("AA", "AA"); // Testing that Last cannot be empty.
        expected.put("AA bb", "A. bb"); // Idem.
        expected.put("aa", "aa"); // Idem.
        expected.put("AA bb CC", "A. bb CC"); // Testing simple von.
        expected.put("AA bb CC dd EE", "A. bb CC dd EE"); // Testing simple von (with inner uppercase words)
        expected.put("AA 1B cc dd", "A. B. cc dd"); // Testing that digits are caseless (B fixes the case of 1B to uppercase).
        expected.put("AA 1b cc dd", "A. 1b cc dd"); // Testing that digits are caseless (b fixes the case of 1b to lowercase)
        expected.put("AA {b}B cc dd", "A. {b}. cc dd"); // Testing that pseudo letters are caseless.
        expected.put("AA {b}b cc dd", "A. {b}b cc dd"); // Idem.
        expected.put("AA {B}b cc dd", "A. {B}b cc dd"); // Idem.
        expected.put("AA {B}B cc dd", "A. {B}. cc dd"); // Idem.
        expected.put("AA \\EF{b} cc dd", "A. E. cc dd"); // Testing that non letters are case less (in particular show how latex command are considered).
        expected.put("AA \\bb{b} cc dd", "A. \\bb{b} cc dd"); // Idem.
        expected.put("AA {bb} cc DD", "A. {b}. cc DD"); // Testing that caseless words are grouped with First primilarily and then with Last.
        expected.put("AA bb {cc} DD", "A. bb {cc} DD"); // Idem.
        expected.put("AA {bb} CC", "A. {b}. CC"); // Idem.

        // Test suite for the second,third specification form von Last First
        expected.put("bb CC, AA", "A. bb CC"); // Simple case. Case do not matter for First.
        expected.put("bb CC, aa", "a. bb CC"); // Idem.
        expected.put("bb CC dd EE, AA", "A. bb CC dd EE"); // Testing simple von (with inner uppercase).
        expected.put("bb, AA", "A. bb"); // Testing that the Last part cannot be empty.
        expected.put("BB,", "BB"); // Testing that first can be empty after coma
        expected.put("bb CC,XX, AA", "A. bb CC, XX"); // Simple Jr. Case do not matter for it.
        expected.put("bb CC,xx, AA", "A. bb CC, xx"); // Idem.
        expected.put("BB,, AA", "A. BB"); // Testing that jr can be empty in between comas.

        for (String name : expected.keySet()) {
            Author a = new Author(name);

            assertEquals(name + " - ", expected.get(name), a.getFormattedName(GeneralSettings.FirstNameDisplay.INITIAL, false));
        }
    }
    
    /**
     * Tests name formatting.
     */
    @Test
    public void testFormatAbbreviatedReversed() {
        System.out.println("Format - Abbreviated Reversed");

        Map<String, String> expected = new LinkedHashMap<>();
        
        expected.put("von Last, Jr., First", "von Last, Jr., F.");

        expected.put("Sander Verdonschot", "Verdonschot, S.");
        expected.put("Verdonschot, Sander", "Verdonschot, S.");
        expected.put("Andr\\'e van Renssen", "van Renssen, A.");
        expected.put("van Renssen, Andr\\'e", "van Renssen, A.");
        
        // Dash in first name
        expected.put("Jean-Lou de Carufel", "de Carufel, J.-L.");
        expected.put("Jean-Lou De Carufel", "Carufel, J.-L. D.");
        expected.put("Jean-Lou {D}e Carufel", "{D}e Carufel, J.-L.");
        expected.put("De Carufel, Jean-Lou", "De Carufel, J.-L.");
        
        // Special character as first letter
        expected.put("\\'Emile Gaudreault", "Gaudreault, E.");
        expected.put("{\\'E}mile Gaudreault", "Gaudreault, {\\'E}.");

        // Tests copied from http://maverick.inria.fr/~Xavier.Decoret/resources/xdkbibtex/bibtex_summary.html#splitting_examples
        // Test suite for the first name specification form First von Last
        expected.put("AA BB", "BB, A."); // Testing simple case with no von.
        expected.put("AA", "AA"); // Testing that Last cannot be empty.
        expected.put("AA bb", "bb, A."); // Idem.
        expected.put("aa", "aa"); // Idem.
        expected.put("AA bb CC", "bb CC, A."); // Testing simple von.
        expected.put("AA bb CC dd EE", "bb CC dd EE, A."); // Testing simple von (with inner uppercase words)
        expected.put("AA 1B cc dd", "cc dd, A. B."); // Testing that digits are caseless (B fixes the case of 1B to uppercase).
        expected.put("AA 1b cc dd", "1b cc dd, A."); // Testing that digits are caseless (b fixes the case of 1b to lowercase)
        expected.put("AA {b}B cc dd", "cc dd, A. {b}."); // Testing that pseudo letters are caseless.
        expected.put("AA {b}b cc dd", "{b}b cc dd, A."); // Idem.
        expected.put("AA {B}b cc dd", "{B}b cc dd, A."); // Idem.
        expected.put("AA {B}B cc dd", "cc dd, A. {B}."); // Idem.
        expected.put("AA \\EF{b} cc dd", "cc dd, A. E."); // Testing that non letters are case less (in particular show how latex command are considered).
        expected.put("AA \\bb{b} cc dd", "\\bb{b} cc dd, A."); // Idem.
        expected.put("AA {bb} cc DD", "cc DD, A. {b}."); // Testing that caseless words are grouped with First primilarily and then with Last.
        expected.put("AA bb {cc} DD", "bb {cc} DD, A."); // Idem.
        expected.put("AA {bb} CC", "CC, A. {b}."); // Idem.

        // Test suite for the second,third specification form von Last First
        expected.put("bb CC, AA", "bb CC, A."); // Simple case. Case do not matter for First.
        expected.put("bb CC, aa", "bb CC, a."); // Idem.
        expected.put("bb CC dd EE, AA", "bb CC dd EE, A."); // Testing simple von (with inner uppercase).
        expected.put("bb, AA", "bb, A."); // Testing that the Last part cannot be empty.
        expected.put("BB,", "BB"); // Testing that first can be empty after coma
        expected.put("bb CC,XX, AA", "bb CC, XX, A."); // Simple Jr. Case do not matter for it.
        expected.put("bb CC,xx, AA", "bb CC, xx, A."); // Idem.
        expected.put("BB,, AA", "BB, A."); // Testing that jr can be empty in between comas.

        for (String name : expected.keySet()) {
            Author a = new Author(name);

            assertEquals(name + " - ", expected.get(name), a.getFormattedName(GeneralSettings.FirstNameDisplay.INITIAL, true));
        }
    }
}