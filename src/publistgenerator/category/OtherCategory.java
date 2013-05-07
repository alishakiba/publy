/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package publistgenerator.category;

import plgsettings.settings.CategoryIdentifier;
import publistgenerator.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class OtherCategory extends OutputCategory {

    public OtherCategory() {
        super("Other", "Other", CategoryIdentifier.OTHER);
    }

    @Override
    public boolean fitsCategory(BibItem item) {
        return true;
    }

}
