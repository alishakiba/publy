/*
 */
package publy.io.html;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;
import publy.data.bibitem.Type;

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
}