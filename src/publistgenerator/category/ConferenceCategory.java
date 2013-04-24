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
public class ConferenceCategory extends OutputCategory {

    public ConferenceCategory() {
        super("Conference", "Conference papers");
        //setNote("Conference papers that I presented are marked with <img src=\"images/presentation.png\" alt=\"(presented)\" class=\"presented\">.  <span class=\"attribution\">(Icon by <a href=\"http://www.doublejdesign.co.uk/\">Double-J Design</a>)</span>");
    }

    @Override
    public boolean fitsCategory(BibItem item) {
        String type = item.getType();

        if (type == null) {
            return false;
        } else {
            return type.equals("inproceedings");
        }
    }

}
