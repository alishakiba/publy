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
public class BookChapterCategory extends OutputCategory {

    public BookChapterCategory() {
        super("Chapters", "Chapters in Books", CategoryIdentifier.CHAPTER);
    }
    
    @Override
    public boolean fitsCategory(BibItem item) {
        String type = item.getType();

        if (type == null) {
            return false;
        } else {
            return type.equals("incollection");
        }
    }
    
}
