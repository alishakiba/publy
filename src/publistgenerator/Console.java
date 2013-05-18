/*
 */
package publistgenerator;

import java.io.PrintWriter;
import javax.swing.JTextArea;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class Console {
    private static PrintWriter out = null;
    
    public static void log(String format, Object... args) {
        if (out == null) {
            init();
        }
        
        out.format(format, args);
    }
    
    public static void error(String format, Object... args) {
        if (out == null) {
            init();
        }
    }
    
    public static void setOutputTarget(JTextArea text) {
        
    }

    private static void init() {
        if (System.console() == null) {
            System.out.println("Console null.");
        } else {
            System.out.println("Console not null.");
            out = System.console().writer();
        }
    }
}
