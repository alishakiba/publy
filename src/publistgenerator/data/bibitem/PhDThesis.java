/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package publistgenerator.data.bibitem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class PhDThesis extends BibItem {

    public PhDThesis() {
        setMandatoryFields("author", "title", "school", "year");
        setOptionalFields("address", "month", "note", "key");
    }

    @Override
    public String getType() {
        return "phdthesis";
    }

}
