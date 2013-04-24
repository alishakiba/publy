/*
 */
package publistgenerator.category;

import publistgenerator.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class UnpublishedCategory extends OutputCategory {

    public UnpublishedCategory() {
        super("Unpublished", "Unpublished manuscripts");
    }

    @Override
    public boolean fitsCategory(BibItem item) {
        return "unpublished".equals(item.getType());
    }
    
}
