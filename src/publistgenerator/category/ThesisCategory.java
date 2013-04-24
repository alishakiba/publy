/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package publistgenerator.category;

import java.util.List;
import publistgenerator.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class ThesisCategory extends OutputCategory {

    public ThesisCategory() {
        super("Theses", "Theses");
    }

    @Override
    public boolean fitsCategory(BibItem item) {
        String type = item.getType();

        if (type == null) {
            return false;
        } else {
            return type.equals("mastersthesis") || type.equals("phdthesis");
        }
    }

    @Override
    public void populate(List<BibItem> items) {
        super.populate(items);

        if (getItems() != null && getItems().size() == 1) {
            setName("Thesis");
            setShortName("Thesis");
        }
    }

}
