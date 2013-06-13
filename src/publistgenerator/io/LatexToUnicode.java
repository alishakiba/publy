/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class LatexToUnicode {
    
    private static final Set<Character> ONE_CHAR_COMMANDS = 
            Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            '\'', // Acute
            '`',  // Grave
            '^',  // Circumflex (hat)
            '"',  // Diaeresis (trema)
            '~',  // Tilde
            '=',  // Macron (bar above)
            '.'  // Dot above
            )));
    
    private static final Map<String, Character> specialCharacters = populateSpecialCharacters();
    
    private LatexToUnicode() {};
    
    public static String convertToUnicode(String s) {
        // Find all occurrences of commands via a regex.
        // Look them up in the specialCharacters map and if found, replace them.
        
        return s;
    }

    private static Map<String, Character> populateSpecialCharacters() {
        Map<String, Character> characters = new HashMap<>();
        
        // TODO
        characters.put("\'o", 'ó');
        
        return Collections.unmodifiableMap(characters);
    }
}
