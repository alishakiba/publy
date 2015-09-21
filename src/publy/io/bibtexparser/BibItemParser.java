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
import publy.Console;
import publy.data.Pair;
import publy.data.bibitem.BibItem;

public class BibItemParser {

    public static BibItem parseBibItem(String text) throws IOException, ParseException {
        int bodyStart = text.indexOf('{');
        String type = text.substring(1, bodyStart).trim().toLowerCase();
        String body = text.substring(bodyStart + 1).trim();

        switch (type) {
            case "comment":
            case "preamble":
                return new BibItem(type, null); // Ignore contents
            case "string":
                return parseString(body);
            default:
                return parsePublication(type, body);
        }
    }

    private static BibItem parseString(String body) {
        // Syntax: Short = "Full" or Short = {Full}
        int split = body.indexOf('=');
        String shortName = body.substring(0, split).trim();
        String fullText = body.substring(split + 1, body.length() - 1).trim(); // Remove outer '}'
        fullText = fullText.substring(1, fullText.length() - 1); // Remove outer pair of braces or quotation marks

        BibItem result = new BibItem("string", null);
        result.put("short", shortName);
        result.put("full", fullText);

        return result;
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
