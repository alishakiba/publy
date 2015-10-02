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
import java.util.regex.Pattern;
import publy.Console;
import publy.data.Pair;
import publy.data.bibitem.BibItem;

public class BibItemParser {

    private static final int MAX_RESET = 6000000; // ~6MB, Accomodates largest bib file I've found (all publications of and relating to Einstein)
    private static final Pattern number = Pattern.compile("(\\d)+");
    private static StreamTokenizer tokenizer;

    public static BibItem parseBibItem(Reader in) throws IOException, ParseException {
        if (in.markSupported()) {
            in.mark(MAX_RESET);
        }

        setupTokenizer(in);

        try {
            parse(StreamTokenizer.TT_WORD);
            String type = getLastTokenAsString().toLowerCase();

            switch (type) {
                case "comment":
                case "preamble":
                    return new BibItem(type, null); // Ignore contents
                case "string":
                    return parseString();
                default:
                    return parsePublication(type);
            }
        } catch (IOException | ParseException ex) {
            in.reset();
            throw ex;
        }
    }

    private static void setupTokenizer(Reader in) {
        tokenizer = new StreamTokenizer(in);

        tokenizer.resetSyntax(); // The default syntax is based on Java code
        tokenizer.eolIsSignificant(false);

        // Treat everything as a word by default
        tokenizer.wordChars(0, Character.MAX_CODE_POINT);

        // White space
        tokenizer.whitespaceChars(0, ' '); // ' ' = 20, and 0-19 are UTF-8 control characters, including \r, \n, and \t.

        // Characters that should never be treated as part of words
        tokenizer.ordinaryChar('{'); // Entry delimiters {...}
        tokenizer.ordinaryChar('}');
        tokenizer.ordinaryChar('('); // Entry delimiters (...)
        tokenizer.ordinaryChar(')');
        tokenizer.ordinaryChar(','); // Field separator
        tokenizer.ordinaryChar('='); // Field-value separator
        tokenizer.ordinaryChar('"'); // Value delimiter "..."
        tokenizer.ordinaryChar('#'); // String concatenation
    }

    private static void setWhiteSpaceMatters(boolean whiteSpaceMatters) {
        if (whiteSpaceMatters) {
            tokenizer.ordinaryChars(0, ' ');
            tokenizer.wordChars(0, ' ');
        } else {
            tokenizer.ordinaryChars(0, ' ');
            tokenizer.whitespaceChars(0, ' ');
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
     */
    private static int parse(int... characters) throws ParseException, IOException {
        int token = tokenizer.nextToken();

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
     * @throws ParseException
     * @throws IOException
     */
    private static boolean nextTokenIs(int... characters) throws IOException {
        int token = tokenizer.nextToken();
        tokenizer.pushBack();

        for (int c : characters) {
            if (c == token) {
                return true;
            }
        }

        return false;
    }

    private static String getLastTokenAsString() {
        switch (tokenizer.ttype) {
            case StreamTokenizer.TT_EOF:
                return "EOF (end of file)";
            case StreamTokenizer.TT_EOL:
                return "EOL (end of line)";
            case StreamTokenizer.TT_NUMBER:
                if (!Double.isNaN(tokenizer.nval) && !Double.isInfinite(tokenizer.nval) && tokenizer.nval == Math.rint(tokenizer.nval)) {
                    return Integer.toString((int) tokenizer.nval);
                } else {
                    return Double.toString(tokenizer.nval);
                }
            case StreamTokenizer.TT_WORD:
                return tokenizer.sval;
            default:
                return Character.toString((char) tokenizer.ttype);
        }
    }

    /**
     * Parses the body of an "@string" entry.
     *
     * @return
     * @throws ParseException
     * @throws IOException
     */
    private static BibItem parseString() throws ParseException, IOException {
        // <string> ::= "(" <short> "=" <value> ")" | "{" <short> "=" <value> "}"
        int bracket = parse('{', '(');

        parse(StreamTokenizer.TT_WORD);
        String shortName = getLastTokenAsString();

        parse('=');

        String fullText = parseValue();

        if (bracket == '{') {
            parse('}');
        } else {
            parse(')');
        }

        BibItem result = new BibItem("string", null);
        result.put("short", shortName);
        result.put("full", fullText);

        return result;
    }

    /**
     * Parses the body of a publication entry.
     *
     * @param type The publication type.
     * @return
     * @throws ParseException
     */
    private static BibItem parsePublication(String type) throws ParseException, IOException {
        // <body> ::= "{" <id> ("," <field>)* "}" | "(" <id> ("," <field>)* ")"
        int bracket = parse('{', '(');

        parse(StreamTokenizer.TT_WORD);
        String id = getLastTokenAsString();

        BibItem result = new BibItem(type, id);

        while (nextTokenIs(',')) {
            parse(',');
            Pair<String, String> field = parseField();

            if (field != null) {
                result.put(field.getFirst(), field.getSecond());
            }
        }

        if (bracket == '{') {
            parse('}');
        } else {
            parse(')');
        }

        return result;
    }

    private static Pair<String, String> parseField() throws IOException, ParseException {
        // <field> ::= (<name> "=" <value>)?
        if (nextTokenIs(StreamTokenizer.TT_WORD)) {
            parse(StreamTokenizer.TT_WORD);
            String name = getLastTokenAsString().toLowerCase();

            parse('=');

            return new Pair<>(name, parseValue());
        } else {
            return null;
        }
    }

    private static String parseValue() throws IOException, ParseException {
        // <value> ::= (<simple-value> ("#" <simple-value>)*)?
        if (nextTokenIs(StreamTokenizer.TT_WORD, '{', '"')) {
            StringBuilder value = new StringBuilder(parseSimpleValue());

            while (nextTokenIs('#')) {
                parse('#');
                value.append(parseSimpleValue());
            }

            return value.toString();
        } else {
            return "";
        }
    }

    private static String parseSimpleValue() throws IOException, ParseException {
        // <simple-value> ::= <abbreviation> | <number> | "{" <braced-value> "}" | "\"" <quoted-value> "\""
        int token = parse(StreamTokenizer.TT_WORD, '{', '"');

        if (token == StreamTokenizer.TT_WORD) {
            String value = getLastTokenAsString();

            if (isNumeric(value)) {
                return value;
            } else {
                return "<<" + value + ">>";
            }
        } else {
            setWhiteSpaceMatters(true);
            String value = parseDelimitedValue(token);
            setWhiteSpaceMatters(false);

            if (token == '{') {
                parse('}');
            } else {
                parse('"');
            }
            
            return value;
        }
    }

    private static boolean isNumeric(String input) {
        return number.matcher(input).matches();
    }

    private static String parseDelimitedValue(int delimiter) throws IOException, ParseException {
        // <braced-value> ::= (word | nonBraceSpecialChars | "{" <braced-value> "}")*
        // <quoted-value> ::= (word | nonBraceAndQuoteSpecialChars | "{" <braced-value> "}")*
        int[] wordCharacters = (delimiter == '{'
                ? new int[]{StreamTokenizer.TT_WORD, '(', ')', ',', '#', '=', '{', '"'}
                : new int[]{StreamTokenizer.TT_WORD, '(', ')', ',', '#', '=', '{'});

        StringBuilder value = new StringBuilder();

        while (nextTokenIs(wordCharacters)) {
            int token = parse(wordCharacters);
            value.append(getLastTokenAsString());

            if (token == '{') {
                value.append(parseDelimitedValue('{'));
                parse('}');
                value.append('}');
            }
        }

        return value.toString();
    }

    private static BibItem parsePublication(String type, String body) throws ParseException {
        // Syntax: id, (field-value-pair)*
        int idEnd = body.indexOf(',');

        if (idEnd == -1) {
            // No fields
            return new BibItem(type, body.substring(0, body.length() - 1));
        }

        String id = body.substring(0, idEnd).trim();
        body = body.substring(idEnd + 1).trim();

        BibItem result = new BibItem(type, id);

        while (!body.isEmpty() && !body.equals("}")) {
            // Parse the next field-value pair
            int valueStart = body.indexOf('=');

            if (valueStart == -1) {
                // No more field-value pairs, but text left: warn
                System.err.printf("After parsing all fields of publication \"%s\", the following text was left and not part of any field:\n%s\n", id, body);
                Console.warn(Console.WarningType.OTHER, "After parsing all fields of publication \"%s\", the following text was left and not part of any field:\n%s\n", id, body);
                break;
            }

            String field = body.substring(0, valueStart).trim().toLowerCase();
            body = body.substring(valueStart + 1).trim();

            Pair<String, String> value = Tokenizer.collectValue(body);
            result.put(field, parseValue(value.getFirst()));
            body = value.getSecond().trim();

            if (body.startsWith(",")) {
                body = body.substring(1).trim();
            }
        }

        return result;
    }

    public static String parseValue(String text) {
        // Drop outer pair of separators (braces or quotes)
        // Turn @string abbreviations into publy abbreviations ("<<short>>")
        // Process string concatenation

        StringBuilder result = new StringBuilder();
        int braceLevel = 0;
        boolean inQuotes = false;
        boolean inAbbreviation = false;

        for (int i = 0; i < text.length(); i++) {
            int c = text.codePointAt(i);

            if (braceLevel > 0) {
                if ((char) c == '{') {
                    braceLevel++;
                } else if ((char) c == '}') {
                    braceLevel--;
                }

                if (braceLevel > 0 || inQuotes) {
                    // Add everything but the closing brace or quote
                    result.appendCodePoint(c);
                }
            } else if (inQuotes) {
                if ((char) c == '"') {
                    inQuotes = false;
                } else {
                    result.appendCodePoint(c);

                    if ((char) c == '{') {
                        braceLevel++;
                    } else if (braceLevel > 0 && (char) c == '}') {
                        braceLevel--;
                    }
                }
            } else if (inAbbreviation) {
                if (Character.isWhitespace(c) || (char) c == '#' || (char) c == '{' || (char) c == '"') {
                    // End of abbreviation
                    result.append(">>");
                    inAbbreviation = false;

                    if ((char) c == '{') {
                        braceLevel = 1;
                    } else if ((char) c == '"') {
                        inQuotes = true;
                    }
                } else {
                    result.appendCodePoint(c);
                }
            } else {
                // Brace or quote start new tokens, pound is ignored, numbers just get parsed, text starts a new abbreviation token
                if ((char) c == '{') {
                    braceLevel = 1;
                } else if ((char) c == '"') {
                    inQuotes = true;
                } else if (Character.isDigit(c)) {
                    result.appendCodePoint(c);
                } else if (Character.isAlphabetic(c)) {
                    result.append("<<");
                    result.appendCodePoint(c);
                    inAbbreviation = true;
                } // else ignore
            }
        }

        if (inAbbreviation) {
            result.append(">>");
        }

        return result.toString();
    }

    private BibItemParser() {
    }
}
