/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.bibitem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class InvitedTalk extends BibItem {

    public InvitedTalk() {
        setMandatoryFields("title", "year", "address");
        setOptionalFields("month");
    }
    
    @Override
    public String getType() {
        return "talk";
    }
    
}
