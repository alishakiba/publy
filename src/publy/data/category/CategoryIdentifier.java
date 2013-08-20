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
package publy.data.category;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public enum CategoryIdentifier {
    BOOK, CHAPTER, CONFERENCE, JOURNAL, OTHER, SUBMITTED, TALK, THESIS, UNPUBLISHED;

    @Override
    public String toString() {
        switch (this) {
            case BOOK: return "Books";
            case CHAPTER: return "Chapters in Books";
            case CONFERENCE: return "Conference papers";
            case JOURNAL: return "Journal papers";
            case OTHER: return "Other";
            case SUBMITTED: return "Manuscripts under review";
            case TALK: return "Invited Talks";
            case THESIS: return "Theses";
            case UNPUBLISHED: return "Unpublished manuscripts";
            default:
                throw new AssertionError("Unknown category identifier: " + this);
        }
    }
}
