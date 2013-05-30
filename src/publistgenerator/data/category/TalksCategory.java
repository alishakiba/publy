/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.data.category;

import publistgenerator.data.bibitem.BibItem;

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
            if (item.anyNonEmpty("status") && !item.get("status").startsWith("accepted")) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    
}
