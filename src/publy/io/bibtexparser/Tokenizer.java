/*
 * Copyright 2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package publy.io.bibtexparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import publy.data.Pair;

public class Tokenizer {

    public static Pair<String, String> extractDelimitedToken(String input, char open, char close) throws IOException {
        StringReader str = new StringReader(input);
        String prefix = extractDelimitedToken(str, open, close);
        String postfix = emptyReader(str);
        return new Pair<>(prefix, postfix);
    }

    public static Pair<String, String> extractDelimitedToken(String currentLine, BufferedReader reader, char open, char close) throws IOException {
        return null;
    }

    /**
     * Returns the shortest prefix of the input that starts with the open
     * character and ends with the close character, ignoring white-space before
     * the open character and escaped closing characters.
     *
     * Throws an IOException if such a prefix cannot be found.
     *
     * @param input
     * @param open
     * @param close
     * @return
     */
    private static String extractDelimitedToken(Reader input, char open, char close) throws IOException {
        // Skip whitespace
        int c = input.read();
        
        while (c != -1 && Character.isWhitespace(c)) {
            c = input.read();
        }
        
        if (c == -1) {
            throw new IOException("Reached end of input before open character.");
        }
        
        // Match open character
        if (c != open) {
            throw new IOException("Input does not start with open character.");
        }
        
        StringBuilder prefix = new StringBuilder();
        prefix.appendCodePoint(c);
        
        c = input.read();
        
        // Parse the rest
        boolean escape = false;
        
        while (c != -1 && (escape || c != close)) {
            if (escape) {
                escape = false;
            } else if (c == '\\') {
                escape = true;
            }
            
            prefix.appendCodePoint(c);
            c = input.read();
        }
        
        if (c == -1) {
            throw new IOException("Reached end of input before close character.");
        }
        
        return prefix.appendCodePoint(c).toString();
    }

    private static String emptyReader(StringReader str) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c = str.read();
        
        while (c != -1) {
            sb.appendCodePoint(c);
            c = str.read();
        }
        
        return sb.toString();
    }

    private Tokenizer() {
    }
}
