/*
 */
package publistgenerator.data.bibitem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class Book extends BibItem {

    public Book() {
        setMandatoryFields("author", "title", "publisher", "year");
        setOptionalFields("volume", "number", "editor", "series", "address", "edition", "month", "note", "key", "doi", "isbn", "lccn");
    }

    @Override
    public String getType() {
        return "book";
    }
    
}
