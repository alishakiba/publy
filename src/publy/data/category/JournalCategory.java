/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package publy.data.category;

import publy.data.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class JournalCategory extends OutputCategory {

    public JournalCategory() {
        super("Journal", "Journal papers", CategoryIdentifier.JOURNAL);
    }

    @Override
    public boolean fitsCategory(BibItem item) {
        if ("article".equals(item.getType())) {
            return !item.anyNonEmpty("status") || item.get("status").startsWith("accepted");
        } else {
            return false;
        }
    }

}