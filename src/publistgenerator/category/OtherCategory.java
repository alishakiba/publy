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
public class OtherCategory extends OutputCategory {

    public OtherCategory() {
        super("Other", "Other");
    }

    @Override
    public boolean fitsCategory(BibItem item) {
        return true;
    }

}
