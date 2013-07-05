/*
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
