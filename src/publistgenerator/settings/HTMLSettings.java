/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.settings;

import java.io.File;
import publistgenerator.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLSettings extends FormatSettings {

    public enum PublicationType {

        NONE, PUBLISHED, ACCEPTED, ARXIV, ALL;

        @Override
        public String toString() {
            switch (this) {
                case NONE:
                    return "None";
                case PUBLISHED:
                    return "Published papers";
                case ACCEPTED:
                    return "Accepted manuscripts";
                case ARXIV:
                    return "Accepted or on the arXiv";
                case ALL:
                    return "All";
                default:
                    throw new InternalError("Unrecognized PublicationType: " + this);
            }
        }

        public boolean matches(BibItem item) {
            if (this == ALL) {
                return true;
            } else if (this == NONE) {
                return false;
            } else {
                if (item.anyNonEmpty("arxiv")) {
                    return true;
                } else if (this == ARXIV) {
                    return false;
                } else {
                    if (item.anyNonEmpty("status")) {
                        if (this == PUBLISHED) {
                            return false;
                        } else {
                            return item.get("status").startsWith("accepted");
                        }
                    } else {
                        return true;
                    }
                }
            }
        }
    }
    
    private PublicationType includeAbstracts = PublicationType.ALL;
    private PublicationType includeBibtex = PublicationType.ARXIV;
    private PublicationType includePDF = PublicationType.ARXIV;
    private File header;
    private File footer;
    private String googleAnalyticsUser = null;

    public File getHeader() {
        return header;
    }

    public File getFooter() {
        return footer;
    }
}
