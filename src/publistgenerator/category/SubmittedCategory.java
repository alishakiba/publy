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
public class SubmittedCategory extends OutputCategory {

    public SubmittedCategory() {
        super("Submitted", "Currently under review");
    }

    @Override
    public boolean fitsCategory(BibItem item) {
        String status = item.get("status");

        if (status != null) {
            return status.equals("submitted");
        } else {
            return false;
        }
    }

}
