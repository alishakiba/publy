/*
 * Copyright 2013-2014 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
 *
 *
 */
public class BibItem {

    private String id;
    private Type type;
    private String originalType;
    private HashMap<String, String> values;
    private List<Author> authors;
    private List<Author> editors;

    public BibItem(String originalType, String id) {
        this.type = Type.fromString(originalType);
        this.originalType = originalType;
        this.id = id;

        values = new LinkedHashMap<>();
        authors = new ArrayList<>();
        editors = new ArrayList<>();

        handleSpecialTypes(originalType);
    }

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

    public Type getType() {
        return type;
    }

    public String getOriginalType() {
        return originalType;
    }

    public String getId() {
        return id;
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
                Console.error("Item \"%s\" is missing mandatory field \"%s\".", id, missingFields.get(0));
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

                Console.error("Item \"%s\" is missing mandatory fields %s.", id, sb.toString());
            }
        }

        return missingFields == null;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Author... authors) {
        this.authors.clear();
        this.authors.addAll(Arrays.asList(authors));
    }
    
    public List<Author> getEditors() {
        return editors;
    }

    public void setEditors(Author... editors) {
        this.editors.clear();
        this.editors.addAll(Arrays.asList(editors));
    }

    public String getBibTeX() {
        StringBuilder sb = new StringBuilder();

        sb.append("@");
        sb.append(getOriginalType());
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
