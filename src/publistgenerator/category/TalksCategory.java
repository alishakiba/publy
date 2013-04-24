/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.category;

import publistgenerator.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class TalksCategory extends OutputCategory {

    public TalksCategory() {
        super("Talks", "Invited Talks");
    }

    @Override
    public boolean fitsCategory(BibItem item) {
        String type = item.getType();

        if (type == null) {
            return false;
        } else {
            return type.equals("talk");
        }
    }
    
}
