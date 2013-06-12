/*
 */
package publistgenerator.data.category;

import publistgenerator.data.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class BookCategory extends OutputCategory {

    public BookCategory() {
        super("Books", "Books", CategoryIdentifier.BOOK);
    }

    @Override
    public boolean fitsCategory(BibItem item) {
        if ("book".equals(item.getType())) {
            return !item.anyNonEmpty("status") || item.get("status").startsWith("accepted");
        } else {
            return false;
        }
    }
    
}
