/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.data.bibitem;

import java.util.*;
import java.util.Map.Entry;
import publistgenerator.Console;

/**
 *
 * @author Sander
 */
public abstract class BibItem {

    private String id;
    private HashMap<String, String> values;
    private List<String> mandatoryFields;
    private List<String> optionalFields;
    private List<Author> authors;
    private Venue venue;

    public BibItem() {
        values = new LinkedHashMap<>();
        mandatoryFields = new ArrayList<>();
        optionalFields = new ArrayList<>();
        authors = new ArrayList<>();
    }

    public abstract String getType();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String get(String attr) {
        return values.get(attr);
    }

    public void put(String attr, String value) {
        values.put(attr, value);
    }

    public Set<String> getFields() {
        return values.keySet();
    }

    public List<String> getMandatoryFields() {
        return mandatoryFields;
    }

    public void setMandatoryFields(String... mandatoryFields) {
        this.mandatoryFields.clear();
        this.mandatoryFields.addAll(Arrays.asList(mandatoryFields));
    }

    public boolean checkMandatoryFields() {
        boolean complete = true;

        for (String field : mandatoryFields) {
            String v = values.get(field);

            if (v == null || v.isEmpty()) {
                complete = false;
                Console.error("Item %s is missing mandatory field %s.", id, field);
            }
        }

        return complete;
    }

    public List<String> getOptionalFields() {
        return optionalFields;
    }

    public void setOptionalFields(String... optionalFields) {
        this.optionalFields.clear();
        this.optionalFields.addAll(Arrays.asList(optionalFields));
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Author... authors) {
        this.authors.clear();
        this.authors.addAll(Arrays.asList(authors));
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public boolean anyNonEmpty(String... fields) {
        for (String field : fields) {
            String v = values.get(field);

            if (v != null && !v.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public String getBibTeX() {
        StringBuilder sb = new StringBuilder();

        sb.append("@");
        sb.append(getType());
        sb.append(" {");
        sb.append(id);
        sb.append(",\n");

        for (Entry<String, String> entry : values.entrySet()) {
            sb.append("  ");
            sb.append(entry.getKey());
            sb.append("={");
            sb.append(entry.getValue());
            sb.append("},\n");
        }

        // remove the last comma
        sb.deleteCharAt(sb.length() - 2);

        sb.append("}\n");

        return sb.toString();
    }

    @Override
    public String toString() {
        return getBibTeX();
    }
}
