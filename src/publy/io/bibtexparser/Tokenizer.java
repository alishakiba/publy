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

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

public class Tokenizer extends StreamTokenizer {

    public Tokenizer(Reader reader) {
        super(reader);

        resetSyntax(); // The default syntax is based on Java code
        eolIsSignificant(false);

        // Treat everything as a word by default
        wordChars(0, Character.MAX_CODE_POINT);

        // White space
        whitespaceChars(0, ' '); // ' ' = 20, and 0-19 are UTF-8 control characters, including \r, \n, and \t.
    }

    public void setWhiteSpaceMatters(boolean whiteSpaceMatters) {
        if (whiteSpaceMatters) {
            ordinaryChars(0, ' ');
            wordChars(0, ' ');
        } else {
            ordinaryChars(0, ' ');
            whitespaceChars(0, ' ');
        }
    }

    public void setSpecialCharacters(int[] specialCharacters) {
        for (int c : specialCharacters) {
            // StreamTokenizer calls a character 'ordinary'
            // if it is always a token by itself
            ordinaryChar(c);
        }
    }

    /**
     * Consumes the next token and checks whether it matches any of the given
     * characters.
     *
     * @param characters the characters to accept
     * @return the string representation of the parsed token
     * @throws ParseException If the next token is not among the given
     * characters.
     * @throws java.io.IOException
     */
    public int match(int... characters) throws ParseException, IOException {
        int token = nextToken();

        for (int c : characters) {
            if (c == token) {
                return token;
            }
        }

        StringBuilder expected = new StringBuilder();
        expected.append('[');

        for (int c : characters) {
            switch (c) {
                case StreamTokenizer.TT_EOF:
                    expected.append("EOF (end of file)");
                    break;
                case StreamTokenizer.TT_EOL:
                    expected.append("EOL (end of line)");
                    break;
                case StreamTokenizer.TT_NUMBER:
                    expected.append("NUMBER");
                    break;
                case StreamTokenizer.TT_WORD:
                    expected.append("WORD");
                    break;
                default:
                    expected.append(Character.toString((char) c));
                    break;
            }
            expected.append(',');
        }

        expected.deleteCharAt(expected.length() - 1); // Delete last ','
        expected.append(']');

        throw new ParseException(String.format(
                "I expected one of these tokens: %s, but found \"%s\".",
                expected.toString(), getLastTokenAsString()));
    }

    /**
     * Checks whether the next token is any of the given types, without
     * consuming the token.
     *
     * @param characters the token types to check for
     * @return true if the next token is of the given types, false otherwise
     * @throws IOException
     */
    public boolean nextTokenIs(int... characters) throws IOException {
        int token = nextToken();
        pushBack();

        for (int c : characters) {
            if (c == token) {
                return true;
            }
        }

        return false;
    }

    public String getLastTokenAsString() {
        switch (ttype) {
            case StreamTokenizer.TT_EOF:
                return "EOF (end of file)";
            case StreamTokenizer.TT_EOL:
                return "EOL (end of line)";
            case StreamTokenizer.TT_NUMBER:
                if (!Double.isNaN(nval) && !Double.isInfinite(nval) && nval == Math.rint(nval)) {
                    return Integer.toString((int) nval);
                } else {
                    return Double.toString(nval);
                }
            case StreamTokenizer.TT_WORD:
                return sval;
            default:
                return Character.toString((char) ttype);
        }
    }
}
