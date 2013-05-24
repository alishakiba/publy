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
public class SubmittedCategory extends OutputCategory {

    public SubmittedCategory() {
        super("Submitted", "Currently under review", CategoryIdentifier.SUBMITTED);
    }

    @Override
    public boolean fitsCategory(BibItem item) {
        return "submitted".equals(item.get("status"));
    }

}
