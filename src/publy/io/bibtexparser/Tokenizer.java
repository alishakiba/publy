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

    public static String collectBibItem(BufferedReader input, String firstLine) throws IOException {
        CombinedReader in = new CombinedReader(input, firstLine);
        StringBuilder bibitem = new StringBuilder();

        // Test for starting with '@'
        int c = in.read();

        if ((char) c != '@') {
            throw new IOException("First character of bibitem should be '@'.");
        }

        // Scan for first open brace ('{')
        bibitem.appendCodePoint(c);
        c = in.read();

        while (c != -1 && (char) c != '{') {
            bibitem.appendCodePoint(c);
            c = in.read();
        }

        if (c == -1) {
            throw new IOException("No opening brace found when trying to parse bibitem.");
        } else {
            bibitem.appendCodePoint(c);
        }

        // Collect the body
        collectMatchedToken(in, '{', '}', bibitem);

        return bibitem.toString();
    }

    public static String collectTag(BufferedReader input, String firstLine) throws IOException {
        CombinedReader in = new CombinedReader(input, firstLine);
        StringBuilder tag = new StringBuilder();

        // Test for starting with '<'
        int c = in.read();

        if ((char) c != '<') {
            throw new IOException("First character of tag should be '<'.");
        }

        tag.appendCodePoint(c);

        // Collect the body
        collectMatchedToken(in, '<', '>', tag);

        return tag.toString();
    }

    public static Pair<String, String> collectValue(String body) throws IOException {
        // Collect until first "level-0" comma or close brace (end of bibitem)
        // When encountering an open brace, collect until we've matched it
        // When encountering a quote ("), collect until next quote
        
        int braceLevel = 0;
        boolean inQuotes = false;

        for (int i = 0; i < body.length(); i++) {
            int c = body.codePointAt(i);

            // Check braces
            if ((char) c == '{') {
                braceLevel++;
            } else if (braceLevel > 0 && (char) c == '}') {
                braceLevel--;
            } else if (braceLevel == 0) {
                // Check quotes
                if ((char) c == '"') {
                    inQuotes = !inQuotes;
                } else if (!inQuotes) {
                    if ((char) c == ',' || (char) c == '}') {
                        // zero-level end-of-value: we're done!
                        return new Pair<>(body.substring(0, i), body.substring(i));
                    }
                }
            }
        }
        
        throw new IOException("End of input reached while collecting value.");
    }

    /**
     * Collects characters from the input stream until the first time the number
     * of close characters seen is larger than the number of open characters.
     *
     * @param in
     * @param open
     * @param close
     * @return
     */
    private static void collectMatchedToken(SingleByteReader in, char open, char close, StringBuilder result) throws IOException {
        int openCount = 1;

        while (openCount > 0) {
            int c = in.read();

            if (c == -1) {
                if (open == '{') {
                    throw new IOException("End of input reached while trying to match braces in bibitem body.");
                } else if (open == '<') {
                    throw new IOException("End of input reached while trying to match angle brackets in tag body.");
                } else {
                    throw new IOException("End of input reached while trying to match.");
                }
            }

            result.appendCodePoint(c);

            if ((char) c == open) {
                openCount++;
            } else if ((char) c == close) {
                openCount--;
            }
        }
    }

    private static void collectUntil(SingleByteReader in, char stop, StringBuilder result) throws IOException {
        int c;
        do {
            c = in.read();

            if (c == -1) {
                throw new IOException("End of input reached while trying to find \"" + stop + "\".");
            }

            result.appendCodePoint(c);
        } while ((char) c != stop);
    }

    private static class CombinedReader implements SingleByteReader {

        boolean endOfString = false;
        StringReader sr;
        BufferedReader br;

        public CombinedReader(BufferedReader br, String s) {
            this.sr = new StringReader(s);
            this.br = br;
        }

        public int read() throws IOException {
            if (endOfString) {
                return br.read();
            } else {
                int c = sr.read();

                if (c == -1) {
                    endOfString = true;
                    return br.read();
                } else {
                    return c;
                }
            }
        }
    }

    private static interface SingleByteReader {

        public int read() throws IOException;
    }

    private static class SingleByteWrapper implements SingleByteReader {

        Reader r;

        public SingleByteWrapper(Reader r) {
            this.r = r;
        }

        @Override
        public int read() throws IOException {
            return r.read();
        }
    }

    private Tokenizer() {
    }
}
