/*
 */
package publy.data.category;

import publy.data.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class UnpublishedCategory extends OutputCategory {

    public UnpublishedCategory() {
        super("Unpublished", "Unpublished manuscripts", CategoryIdentifier.UNPUBLISHED);
    }

    @Override
    public boolean fitsCategory(BibItem item) {
        return "unpublished".equals(item.getType());
    }
    
}
