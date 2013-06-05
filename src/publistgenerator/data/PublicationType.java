/*
 */
package publistgenerator.data;

import publistgenerator.data.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public enum PublicationType {

    NONE, PUBLISHED, ACCEPTED, ARXIV, ALL;

    @Override
    public String toString() {
        switch (this) {
            case NONE:
                return "No papers";
            case PUBLISHED:
                return "Published papers";
            case ACCEPTED:
                return "Accepted papers";
            case ARXIV:
                return "Accepted or arXiv papers";
            case ALL:
                return "All papers";
            default:
                throw new AssertionError("Unrecognized PublicationType: " + this);
        }
    }
    
    public boolean matches(BibItem item) {
        return matches(this, item);
    }
    
    public static boolean matches(PublicationType type, BibItem item) {
        if (type == ALL) {
            return true;
        } else if (type == NONE) {
            return false;
        } else {
            if (item.anyNonEmpty("status")) {
                if (type == PUBLISHED) {
                    return false;
                } else {
                    if (item.get("status").startsWith("accepted")) {
                        return true;
                    } else {
                        if (type == ACCEPTED) {
                            return false;
                        } else {
                            // Type is ARXIV
                            return item.anyNonEmpty("arxiv");
                        }
                    }
                }
            } else {
                return true;
            }
        }
    }
}
