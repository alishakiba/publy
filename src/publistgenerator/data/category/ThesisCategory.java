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
public class ThesisCategory extends OutputCategory {

    public ThesisCategory() {
        super("Theses", "Theses", CategoryIdentifier.THESIS);
    }

    @Override
    public boolean fitsCategory(BibItem item) {
        if ("mastersthesis".equals(item.getType()) || "phdthesis".equals(item.getType())) {
            return !item.anyNonEmpty("status") || item.get("status").startsWith("accepted");
        } else {
            return false;
        }
    }

    @Override
    public String getName() {
        if (getItems() != null && getItems().size() == 1) {
            return "Thesis";
        } else {
            return "Theses";
        }
    }

    @Override
    public String getShortName() {
        if (getItems() != null && getItems().size() == 1) {
            return "Thesis";
        } else {
            return "Theses";
        }
    }
}
