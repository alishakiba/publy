/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publy.data.bibitem;

/**
 *
 * @author Sander
 */
public class InProceedings extends BibItem {

    public InProceedings() {
        setMandatoryFields("author", "title", "booktitle", "year");
        setOptionalFields("editor", "volume", "number", "series", "pages", "organization", "publisher", "address", "month", "note", "key", "doi");
    }

    @Override
    public String getType() {
        return "inproceedings";
    }

}
