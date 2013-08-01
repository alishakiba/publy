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
public class TalksCategory extends OutputCategory {

    public TalksCategory() {
        super("Talks", "Invited Talks", CategoryIdentifier.TALK);
    }

    @Override
    public boolean fitsCategory(BibItem item) {
        if ("talk".equals(item.getType())) {
            return !item.anyNonEmpty("status") || item.get("status").startsWith("accepted");
        } else {
            return false;
        }
    }
    
}
