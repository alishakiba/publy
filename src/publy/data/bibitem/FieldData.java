/*
 * Copyright 2013 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.data.bibitem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class FieldData {

    /**
     * The required and optional fields supported by Publy.
     */
    private static final Map<Type, List<String>> mandatoryFields;
    private static final Map<Type, List<String>> optionalFields;

    static {
        mandatoryFields = new HashMap<>();

        mandatoryFields.put(Type.ARTICLE, Arrays.asList("author", "title", "journal", "year"));
        mandatoryFields.put(Type.BOOK, Arrays.asList("author;editor", "title", "year"));
        mandatoryFields.put(Type.INBOOK, Arrays.asList("author;editor", "title", "year"));
        mandatoryFields.put(Type.BOOKLET, Arrays.asList("author;editor", "title", "year"));
        mandatoryFields.put(Type.COLLECTION, Arrays.asList("author;editor", "title", "year"));
        mandatoryFields.put(Type.INCOLLECTION, Arrays.asList("author", "title", "booktitle", "year"));
        mandatoryFields.put(Type.MANUAL, Arrays.asList("author", "title", "year"));
        mandatoryFields.put(Type.MISC, Arrays.asList("author;editor", "title", "year"));
        mandatoryFields.put(Type.ONLINE, Arrays.asList("author", "title", "year", "url"));
        mandatoryFields.put(Type.PATENT, Arrays.asList("author", "title", "number", "year"));
        mandatoryFields.put(Type.PERIODICAL, Arrays.asList("author;editor", "title", "year"));
        mandatoryFields.put(Type.PROCEEDINGS, Arrays.asList("author;editor", "title", "year"));
        mandatoryFields.put(Type.INPROCEEDINGS, Arrays.asList("author", "title", "booktitle", "year"));
        mandatoryFields.put(Type.REPORT, Arrays.asList("author", "title", "type", "institution", "year"));
        mandatoryFields.put(Type.THESIS, Arrays.asList("author", "title", "type", "school;institution", "year"));
        mandatoryFields.put(Type.UNPUBLISHED, Arrays.asList("author", "title", "year"));

        optionalFields = new HashMap<>();

        optionalFields.put(Type.ARTICLE, Arrays.asList("volume", "number", "pages", "series", "issn", "eid", "month", "note", "doi", "eprint", "eprintclass", "eprinttype", "url", "urldate"));
        optionalFields.put(Type.BOOK, Arrays.asList("editor", "publisher", "volume", "number", "series", "address", "edition", "isbn", "month", "note", "doi", "eprint", "eprintclass", "eprinttype", "url", "urldate"));
        optionalFields.put(Type.INBOOK, Arrays.asList("editor", "publisher", "chapter", "pages", "volume", "number", "series", "type", "address", "edition", "bookauthor", "booktitle", "isbn", "month", "note", "doi", "eprint", "eprintclass", "eprinttype", "url", "urldate"));
        optionalFields.put(Type.BOOKLET, Arrays.asList("editor", "howpublished", "type", "address", "isbn", "month", "note", "doi", "eprint", "eprintclass", "eprinttype", "url", "urldate"));
        optionalFields.put(Type.COLLECTION, Arrays.asList("editor", "publisher", "volume", "number", "series", "edition", "address", "isbn", "month", "note", "doi", "eprint", "eprintclass", "eprinttype", "url", "urldate"));
        optionalFields.put(Type.INCOLLECTION, Arrays.asList("editor", "publisher", "volume", "number", "series", "type", "chapter", "pages", "address", "edition", "isbn", "month", "note", "doi", "eprint", "eprintclass", "eprinttype", "url", "urldate"));
        optionalFields.put(Type.MANUAL, Arrays.asList("organization", "address", "edition", "type", "isbn", "month", "note", "doi", "eprint", "eprintclass", "eprinttype", "url", "urldate"));
        optionalFields.put(Type.MISC, Arrays.asList("editor", "howpublished", "type", "organization", "address", "isbn", "month", "note", "doi", "eprint", "eprintclass", "eprinttype", "url", "urldate"));
        optionalFields.put(Type.ONLINE, Arrays.asList("organization", "month", "note", "doi", "eprint", "eprintclass", "eprinttype", "urldate"));
        optionalFields.put(Type.PATENT, Arrays.asList("holder", "type", "address", "month", "note", "doi", "eprint", "eprintclass", "eprinttype", "url", "urldate"));
        optionalFields.put(Type.PERIODICAL, Arrays.asList("editor", "publisher", "series", "volume", "number", "issue", "issn", "month", "note", "doi", "eprint", "eprintclass", "eprinttype", "url", "urldate"));
        optionalFields.put(Type.PROCEEDINGS, Arrays.asList("editor", "publisher", "volume", "number", "series", "organization", "address", "isbn", "month", "note", "doi", "eprint", "eprintclass", "eprinttype", "url", "urldate"));
        optionalFields.put(Type.INPROCEEDINGS, Arrays.asList("editor", "publisher", "volume", "number", "series", "organization", "address", "pages", "isbn", "chapter", "month", "note", "doi", "eprint", "eprintclass", "eprinttype", "url", "urldate"));
        optionalFields.put(Type.REPORT, Arrays.asList("number", "address", "isrn", "month", "note", "doi", "eprint", "eprintclass", "eprinttype", "url", "urldate"));
        optionalFields.put(Type.THESIS, Arrays.asList("address", "isbn", "month", "note", "doi", "eprint", "eprintclass", "eprinttype", "url", "urldate"));
        optionalFields.put(Type.UNPUBLISHED, Arrays.asList("howpublished", "address", "month", "note", "doi", "eprint", "eprintclass", "eprinttype", "url", "urldate"));
    }

    public static List<String> getMandatoryFields(Type type) {
        return mandatoryFields.get(type);
    }

    public static List<String> getOptionalFields(Type type) {
        return optionalFields.get(type);
    }
}
