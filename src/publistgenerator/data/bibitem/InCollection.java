package publistgenerator.data.bibitem;

/**
 * incollection: A part of a book having its own title.
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class InCollection extends BibItem {
    public InCollection() {
        setMandatoryFields("author", "title", "booktitle", "publisher", "year");
        setOptionalFields("editor", "volume", "number", "series", "type", "chapter", "pages", "address", "edition", "month", "note", "key", "doi");
    }

    @Override
    public String getType() {
        return "incollection";
    }
}
