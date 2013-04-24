/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package publistgenerator.bibitem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class MastersThesis extends BibItem {

    public MastersThesis() {
        setMandatoryFields("author", "title", "school", "year");
        setOptionalFields("address", "month", "note", "key");
    }

    @Override
    public String getType() {
        return "mastersthesis";
    }

}
