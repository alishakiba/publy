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
public class ThesisCategory extends OutputCategory {

    public ThesisCategory() {
        super("Theses", "Theses", CategoryIdentifier.THESIS);
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
