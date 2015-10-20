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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import publy.Console;
import publy.data.Author;
import publy.data.bibitem.BibItem;

public class PublicationListParser {

    public static List<BibItem> parseFile(Path file) throws IOException, ParseException {
        try (BufferedReader in = Files.newBufferedReader(file, Charset.forName("UTF-8"))) {
            return parseBibTeX(in);
        }
    }

    public static List<BibItem> parseBibTeX(Reader in) throws IOException, ParseException {
        PublicationListParser parser = new PublicationListParser();

        parser.parseBibTeXInternal(in);
        AbbreviationHandler.handleAbbreviationsAndAuthors(parser.items, parser.abbreviations, parser.authors);

        return parser.items;
    }

    private final List<BibItem> items = new ArrayList<>();
    private final Map<String, String> abbreviations = new LinkedHashMap<>(); // Ensure that order is preserved, so that abbreviations that use earlier abbreviations can be expanded properly
    private final Map<String, Author> authors = new HashMap<>();

    private PublicationListParser() {
    }

    private void parseBibTeXInternal(Reader in) throws IOException {
        int lineNumber = 1;

        for (int c = in.read(); c != -1; c = in.read()) {
            try {
                switch (c) {
                    case '@':
                        handleBibItem(BibItemParser.parseBibItem(in));
                        break;
                    case '<':
                        handleTag(TagParser.parseTag(in));
                        break;
                    case '\n':
                        lineNumber++;
                        break;
                }
            } catch (ParseException ex) {
                ex.setLineNumber(ex.getLineNumber() + lineNumber - 1);
                Console.error(ex.getErrorText());
            }
        }
    }

    private void handleBibItem(BibItem item) {
        if (item == null) {
            return;
        }

        switch (item.getType()) {
            case COMMENT: // fallthrough
            case PREAMBLE:
                break; // Ignore
            case STRING:
                // Add to abbreviations
                abbreviations.put(item.get("short"), item.get("full"));
                break;
            default:
                items.add(item);
        }
    }

    private void handleTag(Tag tag) throws InternalError {
        if (tag == null) {
            return;
        }

        switch (tag.type) {
            case ABBREVIATION:
                abbreviations.put(tag.values.get("short"), tag.values.get("full"));
                break;
            case AUTHOR:
                authors.put(tag.values.get("short"), tag.toAuthor());
                break;
            default:
                throw new InternalError("Tag with unexpected type: " + tag);
        }
    }
}
