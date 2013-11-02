/*
 */
package publy.io.html;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.FieldData;
import publy.data.bibitem.Type;
import publy.io.TestUtils;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLBibItemWriterTest {

    public HTMLBibItemWriterTest() {
    }

    /**
     * Test of write method, of class HTMLBibItemWriter.
     */
    @Test
    public void testWrite() {
        System.out.println("write");

        for (Type type : Type.values()) {
            if (type == Type.ONLINE || type == Type.PATENT) {
                continue;
            }
            
            try (InputStream in = HTMLBibItemWriterTest.class.getResource(type + "_test.properties").openStream()) {
                Properties props = new Properties();
                props.load(in);
                HTMLTestUtils.testWithDefaultValues(type, props);
            } catch (IOException ex) {
                ex.printStackTrace();
                fail("IOException when reading properties file.");
            }
        }
    }
    
    @Test
    public void testWriteIgnore() {
        System.out.println("writeIgnore");

        for (Type type : Type.values()) {
            Set<BibItem> items = TestUtils.generateExampleBibitems(type);
            Set<String> mandatoryFields = getMandatoryFields(type);
            
            for (BibItem item : items) {
                Set<String> optionalFields = new HashSet<>(item.getFields());
                optionalFields.removeAll(mandatoryFields);
                
                List<Set<String>> subsets = TestUtils.getAllSubsets(optionalFields);
                
                for (Set<String> set : subsets) {
                    HTMLTestUtils.testIgnore(item, set);
                }
            }
        }
    }

    private Set<String> getMandatoryFields(Type type) {
        Set<String> result = new HashSet<>();
        
        for (String req : FieldData.getMandatoryFields(type)) {
            if (req.contains(";")) {
                result.add(req.substring(0, req.indexOf(';'))); // Only use the first option
            } else {
                result.add(req);
            }
        }
        
        return result;
    }
}