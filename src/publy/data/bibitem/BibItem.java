/*
 * Copyright 2013-2016 Sander Verdonschot <sander.verdonschot at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package publy.data.bibitem;

import publy.data.Author;
import java.util.*;
import java.util.Map.Entry;
import publy.Console;

/**
 * A publication.
 */
public class BibItem {

    private final String id;
    private final Type type;
    private final String originalType; // The type as specified in the input file (for example 'article', or 'inproceedings')
    private final HashMap<String, String> values;
    private final List<Author> authors;
    private final List<Author> editors;

    /**
     * Creates a new publication with the given type and identifier.
     *
     * @param type the type as specified in the input file (for example
     * 'article', or 'inproceedings')
     * @param id the unique identifier used to refer to this publication
     */
    public BibItem(String type, String id) {
        this.type = Type.fromString(type);
        this.originalType = type;
        this.id = id;

        values = new LinkedHashMap<>();
        authors = new ArrayList<>();
        editors = new ArrayList<>();

        handleSpecialTypes(type);
    }

    /**
     * Certain types are grouped together by {@link Type}. These require extra
     * information to be set for proper display.
     *
     * @param originalType the type as specified in the input file (for example
     * 'article', or 'inproceedings')
     */
    private void handleSpecialTypes(String originalType) {
        switch (originalType) {
            case "mastersthesis":
                values.put("type", "Master's thesis");
                break;
            case "phdthesis":
                values.put("type", "PhD thesis");
                break;
            case "techreport":
                values.put("type", "technical report");
                break;
        }
    }

    /**
     * Gets the type of this publication.
     *
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets the type of this publication, as specified in the input file.
     *
     * @return the original type
     */
    public String getOriginalType() {
        return originalType;
    }

    /**
     * Gets the unique identifier of this publication.
     *
     * @return the unique identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the value associated with the specified field.
     *
     * @param field the field to look up
     * @return the value associated with this field, or null if no such value
     * exists
     */
    public String get(String field) {
        return values.get(field);
    }

    /**
     * Associates the given value with the specified field.
     *
     * @param field the field to modify
     * @param value the value to associate with this field
     */
    public void put(String field, String value) {
        values.put(field, value);
    }

    /**
     * Gets all fields of this publication.
     * <p>
     * This includes fields that were set in the input file, and fields that
     * have been set by Publy afterwards. There is no guarantee that these
     * fields have non-null or non-empty values.
     *
     * @return a set containing all fields
     */
    public Set<String> getFields() {
        return values.keySet();
    }

    /**
     * Checks whether this publication includes information for all its
     * mandatory fields.
     * <p>
     * Which fields are mandatory depends on the type of this publication, as
     * specified by {@link FieldData#getMandatoryFields}. If any mandatory
     * fields are missing, this method prints a helpful error message to the
     * console.
     *
     * @return true if this publication includes information for all its
     * mandatory fields, false otherwise
     */
    public boolean checkMandatoryFields() {
        List<String> missingFields = null;

        for (String field : FieldData.getMandatoryFields(type)) {
            if (!anyNonEmpty(field.split(";"))) {
                if (missingFields == null) {
                    missingFields = new ArrayList<>();
                }

                missingFields.add(field.replaceAll(";", " or "));
            }
        }

        // Nice error
        if (missingFields != null) {
            if (missingFields.size() == 1) {
                Console.error("Publication \"%s\" is missing the mandatory field \"%s\".", id, missingFields.get(0));
            } else {
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < missingFields.size(); i++) {
                    if (i > 0) {
                        if (missingFields.size() > 2) {
                            sb.append(", ");
                        } else {
                            sb.append(" ");
                        }

                        if (i == missingFields.size() - 1) {
                            sb.append("and ");
                        }
                    }

                    sb.append("\"").append(missingFields.get(i)).append("\"");
                }

                Console.error("Publication \"%s\" is missing the mandatory fields %s.", id, sb.toString());
            }
        }

        return missingFields == null;
    }

    /**
     * Gets all authors of this publication.
     *
     * @return a list of all authors
     */
    public List<Author> getAuthors() {
        return authors;
    }

    /**
     * Sets the authors of this publication.
     *
     * @param authors the new authors
     */
    public void setAuthors(Author... authors) {
        this.authors.clear();
        this.authors.addAll(Arrays.asList(authors));
    }

    /**
     * Gets all editors of this publication.
     *
     * @return a list of all editors
     */
    public List<Author> getEditors() {
        return editors;
    }

    /**
     * Sets the editors of this publication.
     *
     * @param editors the new editors
     */
    public void setEditors(Author... editors) {
        this.editors.clear();
        this.editors.addAll(Arrays.asList(editors));
    }

    /**
     * Gets the BibTeX representation of this publication.
     * <p>
     * This representation uses the original type, as specified in the input
     * file.
     *
     * @return a (multi-line) String with the BibTeX representation
     */
    public String getBibTeX() {
        StringBuilder sb = new StringBuilder();

        sb.append("@");
        sb.append(getOriginalType());
        sb.append(" {");
        sb.append(id);
        sb.append(",\n");

        for (Entry<String, String> entry : values.entrySet()) {
            if (entry.getValue() != null) {
                sb.append("  ");
                sb.append(entry.getKey());
                sb.append("={");
                sb.append(entry.getValue());
                sb.append("},\n");
            }
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

    private boolean anyNonEmpty(String... fields) {
        for (String field : fields) {
            String v = values.get(field);

            if (v != null && !v.isEmpty()) {
                return true;
            }
        }

        return false;
    }
}
