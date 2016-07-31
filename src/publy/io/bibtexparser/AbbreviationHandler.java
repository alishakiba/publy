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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import publy.Console;
import publy.data.Author;
import publy.data.bibitem.BibItem;

public class AbbreviationHandler {

    // Pattern for detecting an abbreviation
    private static final Pattern abbrPattern = Pattern.compile("<<([^>]*)>>");
    // Pattern to detect mistakes of the form <abbr>, <<abbr>, or <abbr>>
    private static final Pattern mistakePattern = Pattern.compile("(?:^|[^<])<([^<>]*)>(?:$|[^>])|<<([^<>]*)>(?:$|[^>])|(?:^|[^<])<([^<>]*)>>");

    public static void handleAbbreviationsAndAuthors(List<BibItem> items, Map<String, String> abbreviations, Map<String, Author> authors) {
        ensureAbbreviationsAreUnique(abbreviations, authors);
        expandAbbreviationsInAbbreviations(abbreviations, authors);
        warnForPossibleAbbreviationMistakes(items, abbreviations, authors);
        expandAbbreviations(items, abbreviations, authors);
        replaceAuthorsAndEditors(items, authors);
    }

    private static void ensureAbbreviationsAreUnique(Map<String, String> abbreviations, Map<String, Author> authors) {
        Set<String> duplicate = new HashSet<>(abbreviations.keySet());
        duplicate.retainAll(authors.keySet());

        if (!duplicate.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;

            for (String abbr : duplicate) {
                if (!first) {
                    sb.append(", ");
                } else {
                    first = false;
                }

                sb.append('"').append(abbr).append('"');
            }

            if (duplicate.size() == 1) {
                Console.error("The abbreviation %s is used as both an author and a general abbreviation. This could lead to unspecified behaviour.", sb.toString());
            } else {
                Console.error("Some abbreviations are used as both an author and a general abbreviation. This could lead to unspecified behaviour. The abbreviations in question are:%n%s", sb.toString());
            }
        }
    }

    private static void expandAbbreviationsInAbbreviations(Map<String, String> abbreviations, Map<String, Author> authors) {
        for (Map.Entry<String, String> entry : abbreviations.entrySet()) {
            String fullText = entry.getValue();

            if (fullText != null && !fullText.isEmpty()) {
                entry.setValue(expandAbbreviations(fullText, abbreviations, authors));
            }
        }
    }

    private static void expandAbbreviations(List<BibItem> items, Map<String, String> abbreviations, Map<String, Author> authors) {
        for (BibItem item : items) {
            for (String field : item.getFields()) {
                String currentValue = item.get(field);

                if (currentValue != null && !currentValue.isEmpty()) {
                    item.put(field, expandAbbreviations(currentValue, abbreviations, authors));
                }
            }
        }
    }

    private static void warnForPossibleAbbreviationMistakes(List<BibItem> items, Map<String, String> abbreviations, Map<String, Author> authors) {
        for (BibItem item : items) {
            for (String field : item.getFields()) {
                String currentValue = item.get(field);

                if (currentValue != null && !currentValue.isEmpty()) {
                    Matcher matcher = mistakePattern.matcher(currentValue);

                    while (matcher.find()) {
                        // This is in the only non-null group
                        String abbreviation = (matcher.group(1) != null ? matcher.group(1) : (matcher.group(2) != null ? matcher.group(2) : matcher.group(3)));

                        if (abbreviations.containsKey(abbreviation) || authors.containsKey(abbreviation)) {
                            Console.warn(Console.WarningType.POSSIBLE_MISTAKEN_ABBREVIATION, "I found the text \"%s\" in field \"%s\" of publication \"%s\". Did you mean to use an abbreviation (\"%s\") here?", matcher.group().trim(), field, item.getId(), "<<" + abbreviation + ">>");
                        }
                    }
                }
            }
        }
    }

    private static String expandAbbreviations(String currentValue, Map<String, String> abbreviations, Map<String, Author> authors) {
        StringBuilder finalValue = new StringBuilder();
        Matcher matcher = abbrPattern.matcher(currentValue);
        int prevEnd = 0;

        while (matcher.find()) {
            finalValue.append(currentValue.substring(prevEnd, matcher.start()));

            String abbreviation = matcher.group(1);

            if (abbreviations.containsKey(abbreviation)) {
                finalValue.append(abbreviations.get(abbreviation));
            } else if (authors.containsKey(abbreviation)) {
                finalValue.append("<<").append(abbreviation).append(">>"); // Leave the author abbreviations
            } else {
                Console.error("Abbreviation \"%s\" is used, but never defined.", abbreviation);
            }

            prevEnd = matcher.end();
        }

        finalValue.append(currentValue.substring(prevEnd, currentValue.length()));

        return finalValue.toString();
    }

    private static void replaceAuthorsAndEditors(List<BibItem> items, Map<String, Author> authors) {
        for (BibItem item : items) {
            String author = item.get("author");
            if (author != null && !author.isEmpty()) {
                item.put("author", replaceAuthors(author, authors, item.getAuthors()));
            }

            String editor = item.get("editor");
            if (editor != null && !editor.isEmpty()) {
                item.put("editor", replaceAuthors(item.get("editor"), authors, item.getEditors()));
            }
        }
    }

    private static String replaceAuthors(String fieldValue, Map<String, Author> authors, List<Author> authorList) {
        String[] names = fieldValue.split("\\s+[aA][nN][dD]\\s+"); // " and ", ignoring case

        for (String name : names) {
            Matcher matcher = abbrPattern.matcher(name);

            if (matcher.find()) {
                Author a = authors.get(matcher.group(1));

                if (a == null) {
                    Console.error("Author abbreviation \"%s\" is used, but never defined.", matcher.group(1));
                } else {
                    authorList.add(a);
                }
            } else {
                if (!name.trim().isEmpty()) {
                    authorList.add(new Author(name));
                }
            }
        }

        // Update the author field
        StringBuilder newFieldValue = new StringBuilder();
        boolean first = true;

        for (Author a : authorList) {
            if (first) {
                first = false;
            } else {
                newFieldValue.append(" and ");
            }

            newFieldValue.append(a.getName());
        }

        return newFieldValue.toString();
    }

    private AbbreviationHandler() {
    }
}
