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
import publy.data.Pair;
import publy.data.bibitem.BibItem;

public class BibItemParser {

    private static final int[] SPECIAL_CHARACTERS = new int[]{
        '{', // Entry delimiters {...}
        '}',
        '(', // Entry delimiters (...)
        ')',
        ',', // Field separator
        '=', // Field-value separator
        '"', // Value delimiter "..."
        '#' // String concatenation
    };
    private static final int MAX_RESET = 6000000; // ~12MB, Easily accomodates largest bib file I've found
    private static final Pattern number = Pattern.compile("(\\d)+");
    private static Tokenizer tokenizer;

    public static BibItem parseBibItem(Reader in) throws IOException, ParseException {
        if (in.markSupported()) {
            in.mark(MAX_RESET);
        }

        tokenizer = new Tokenizer(in);
        tokenizer.setSpecialCharacters(SPECIAL_CHARACTERS);

        try {
            tokenizer.match(StreamTokenizer.TT_WORD);
            String type = tokenizer.getLastTokenAsString().toLowerCase();

            switch (type) {
                case "comment":
                case "preamble":
                    return new BibItem(type, null); // Ignore contents
                case "string":
                    return parseString();
                default:
                    return parsePublication(type);
            }
        } catch (ParseException ex) { // Do not reset upon IOException, as that is likely to be unrecoverable
            if (in.markSupported()) {
                in.reset();
            }
            
            ex.setLineNumber(tokenizer.lineno());

            if (ex.getType() == null || ex.getType().isEmpty()) {
                ex.setType("entry");
            }

            throw ex;
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
        String shortName = null;

        try {
            int bracket = tokenizer.match('{', '(');

            tokenizer.match(StreamTokenizer.TT_WORD);
            shortName = tokenizer.getLastTokenAsString();

            tokenizer.match('=');

            String fullText = parseValue();

            if (bracket == '{') {
                tokenizer.match('}');
            } else {
                tokenizer.match(')');
            }

            BibItem result = new BibItem("string", null);
            result.put("short", shortName);
            result.put("full", fullText);

            return result;
        } catch (ParseException pe) {
            pe.setType("@string entry");

            if (shortName != null) {
                pe.setItem(shortName);
            }

            throw pe;
        }
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
        String id = null;

        try {
            int bracket = tokenizer.match('{', '(');

            tokenizer.match(StreamTokenizer.TT_WORD);
            id = tokenizer.getLastTokenAsString();

            BibItem result = new BibItem(type, id);

            while (tokenizer.nextTokenIs(',')) {
                tokenizer.match(',');
                Pair<String, String> field = parseField();

                if (field != null) {
                    result.put(field.getFirst(), normalizeValue(field.getSecond()));
                }
            }

            if (bracket == '{') {
                tokenizer.match('}');
            } else {
                tokenizer.match(')');
            }

            return result;
        } catch (ParseException pe) {
            pe.setType('@' + type + " entry");

            if (id != null) {
                pe.setItem(id);
            }

            throw pe;
        }
    }

    private static Pair<String, String> parseField() throws IOException, ParseException {
        // <field> ::= (<name> "=" <value>)?
        if (tokenizer.nextTokenIs(StreamTokenizer.TT_WORD)) {
            tokenizer.match(StreamTokenizer.TT_WORD);
            String name = tokenizer.getLastTokenAsString().toLowerCase();

            tokenizer.match('=');

            return new Pair<>(name, parseValue());
        } else {
            return null;
        }
    }

    private static String parseValue() throws IOException, ParseException {
        // <value> ::= (<simple-value> ("#" <simple-value>)*)?
        if (tokenizer.nextTokenIs(StreamTokenizer.TT_WORD, '{', '"')) {
            StringBuilder value = new StringBuilder(parseSimpleValue());

            while (tokenizer.nextTokenIs('#')) {
                tokenizer.match('#');
                value.append(parseSimpleValue());
            }

            return value.toString();
        } else {
            return "";
        }
    }

    private static String parseSimpleValue() throws IOException, ParseException {
        // <simple-value> ::= <abbreviation> | <number> | "{" <braced-value> "}" | "\"" <quoted-value> "\""
        int token = tokenizer.match(StreamTokenizer.TT_WORD, '{', '"');

        if (token == StreamTokenizer.TT_WORD) {
            String value = tokenizer.getLastTokenAsString();

            if (isNumeric(value)) {
                return value;
            } else {
                return "<<" + value + ">>";
            }
        } else {
            tokenizer.setWhiteSpaceMatters(true);
            String value = parseDelimitedValue(token);
            tokenizer.setWhiteSpaceMatters(false);

            if (token == '{') {
                tokenizer.match('}');
            } else {
                tokenizer.match('"');
            }

            return value;
        }
    }

    private static String parseDelimitedValue(int delimiter) throws IOException, ParseException {
        // <braced-value> ::= (word | nonBraceSpecialChars | "{" <braced-value> "}")*
        // <quoted-value> ::= (word | nonBraceAndQuoteSpecialChars | "{" <braced-value> "}")*
        int[] wordCharacters = (delimiter == '{'
                ? new int[]{StreamTokenizer.TT_WORD, '(', ')', ',', '#', '=', '{', '"'}
                : new int[]{StreamTokenizer.TT_WORD, '(', ')', ',', '#', '=', '{'});

        StringBuilder value = new StringBuilder();

        while (tokenizer.nextTokenIs(wordCharacters)) {
            int token = tokenizer.match(wordCharacters);
            value.append(tokenizer.getLastTokenAsString());

            if (token == '{') {
                value.append(parseDelimitedValue('{'));
                tokenizer.match('}');
                value.append('}');
            }
        }

        return value.toString();
    }

    private static boolean isNumeric(String input) {
        return number.matcher(input).matches();
    }

    private static String normalizeValue(String value) {
        return value.replaceAll("\\s+", " ").trim();
    }

    private BibItemParser() {
    }
}
