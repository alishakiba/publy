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

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public enum Type {

    ARTICLE, BOOK, INBOOK, BOOKLET, COLLECTION, INCOLLECTION, MANUAL, MISC,
    ONLINE, PATENT, PERIODICAL, PROCEEDINGS, INPROCEEDINGS, REPORT, THESIS,
    UNPUBLISHED;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public static Type fromString(String type) {
        switch (type) {
            // The types themselves
            case "article":
                return ARTICLE;
            case "book":
                return BOOK;
            case "inbook":
                return INBOOK;
            case "booklet":
                return BOOKLET;
            case "collection":
                return COLLECTION;
            case "incollection":
                return INCOLLECTION;
            case "manual":
                return MANUAL;
            case "misc":
                return MISC;
            case "online":
                return ONLINE;
            case "patent":
                return PATENT;
            case "periodical":
                return PERIODICAL;
            case "proceedings":
                return PROCEEDINGS;
            case "inproceedings":
                return INPROCEEDINGS;
            case "report":
                return REPORT;
            case "thesis":
                return THESIS;
            case "unpublished":
                return UNPUBLISHED;

            // Aliases
            case "mvbook":
                return BOOK;
            case "bookinbook":
                return INBOOK;
            case "suppbook":
                return INBOOK;
            case "mvcollection":
                return COLLECTION;
            case "suppcollection":
                return INCOLLECTION;
            case "suppperiodical":
                return ARTICLE;
            case "mvproceedings":
                return PROCEEDINGS;
            case "reference":
                return COLLECTION;
            case "mvreference":
                return COLLECTION;
            case "inreference":
                return INCOLLECTION;
            case "conference":
                return INPROCEEDINGS;
            case "electronic":
                return ONLINE;
            case "mastersthesis":
                return THESIS;
            case "phdthesis":
                return THESIS;
            case "techreport":
                return REPORT;
            case "www":
                return ONLINE;

            // Anything else is treated as misc
            default:
                return MISC;
        }
    }
}
