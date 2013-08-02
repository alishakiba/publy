/*
 */
package publy.io.html;

import java.util.HashMap;
import java.util.LinkedHashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLBibItemWriterTest {
    
    public HTMLBibItemWriterTest() {
    }

    @Test
    public void testChangeQuotes() {
        System.out.println("changeQuotes");
        
        HashMap<String, String> expected = new LinkedHashMap<>();
        
        // Simple tests
        expected.put("O'Rourke", "O’Rourke");
        expected.put("This is `simple'.", "This is ‘simple’.");
        expected.put("``This is also simple.''", "“This is also simple.”");
        expected.put("As is ``this\".", "As is “this”.");
        
        // Ignore quotes in HTML tags
        expected.put("<span class=\"author\">O'Rourke</span>", "<span class=\"author\">O’Rourke</span>");
        expected.put("<a href=\"http://www.google.com\">O'Rourke</a>", "<a href=\"http://www.google.com\">O’Rourke</a>");
        
        HTMLBibItemWriter testInstance = new HTMLBibItemWriter(null, null, null);
        
        for (String input : expected.keySet()) {
            String expectedResult = expected.get(input);
            String result = testInstance.changeQuotes(input);
            
            assertEquals(expectedResult, result);
        }
    }
}