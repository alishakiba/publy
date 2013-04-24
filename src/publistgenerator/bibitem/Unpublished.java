/*
 */
package publistgenerator.bibitem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class Unpublished extends BibItem {

    public Unpublished() {
        setMandatoryFields("author", "title", "year");
        setOptionalFields("month", "note", "key");
    }

    @Override
    public String getType() {
        return "unpublished";
    }
    
}
