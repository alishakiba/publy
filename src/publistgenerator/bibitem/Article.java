/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.bibitem;

/**
 *
 * @author Sander
 */
public class Article extends BibItem {

    public Article() {
        setMandatoryFields("author", "title", "journal", "year");
        setOptionalFields("volume", "number", "pages", "month", "note", "key", "doi");
    }

    @Override
    public String getType() {
        return "article";
    }

}
