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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import publy.data.Author;
import publy.data.bibitem.BibItem;

public class PublicationListParser {

    public static List<BibItem> parseFile(Path file) throws IOException {
        PublicationListParser parser = new PublicationListParser();

        parser.parseFileInternal(file);
        AbbreviationHandler.handleAbbreviationsAndAuthors(parser.items, parser.abbreviations, parser.authors);

        return parser.items;
    }

    private final List<BibItem> items = new ArrayList<>();
    private final Map<String, String> abbreviations = new HashMap<>();
    private final Map<String, Author> authors = new HashMap<>();

    private PublicationListParser() {
    }

    private void parseFileInternal(Path file) throws IOException {
        try (BufferedReader in = Files.newBufferedReader(file, Charset.forName("UTF-8"))) {
            for (String l = in.readLine(); l != null; l = in.readLine()) {
                String line = l.trim();

                if (line.startsWith("@")) {
                    // A Bibitem
                    BibItem item = BibItemParser.parseBibItem(Tokenizer.collectBibItem(in, line).replaceAll("\\s+", " "));

                    if (item != null) {
                        switch (item.getType()) {
                            case COMMENT:
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
                } else if (line.startsWith("<")) {
                    // A custom tag
                    Tag tag = TagParser.parseTag(Tokenizer.collectTag(in, line).replaceAll("\\s+", " "));

                    if (tag.type == Tag.Type.ABBREVIATION) {
                        abbreviations.put(tag.values.get("short"), tag.values.get("full"));
                    } else if (tag.type == Tag.Type.AUTHOR) {
                        authors.put(tag.values.get("short"), tag.toAuthor());
                    } else {
                        throw new InternalError("Tag with unexpected type: " + tag);
                    }
                }
            }
        }
    }
}
