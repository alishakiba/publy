/*
 * Copyright 2014 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.algo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import publy.Console;
import publy.data.bibitem.BibItem;

/**
 *
 *
 */
public class PublicationPostProcessor {

    private static final HashMap<String, String> aliases; // key can be used instead of value

    static {
        aliases = new HashMap<>();
        aliases.put("journaltitle", "journal");
    }

    public static void postProcess(List<BibItem> items) {
        removeDuplicateIDs(items);
        processAliases(items);
        removeItemsWithMissingMandatoryFields(items);
        detectArxiv(items);
    }

    /**
     * Removes all but the first occurrence of entries with duplicate
     * identifiers.
     * <p>
     * If there are duplicate identifiers, this method also prints a helpful
     * error message.
     *
     * @param items
     */
    private static void removeDuplicateIDs(List<BibItem> items) {
        HashSet<String> ids = new HashSet<>();
        Map<String, Integer> duplicateCount = new HashMap<>();

        ListIterator<BibItem> it = items.listIterator();

        while (it.hasNext()) {
            BibItem item = it.next();

            if (!ids.add(item.getId())) { // add returns false if the id was already in the set
                it.remove();

                int currentCount = (duplicateCount.containsKey(item.getId()) ? duplicateCount.get(item.getId()) : 1);
                duplicateCount.put(item.getId(), currentCount + 1);
            }
        }

        if (!duplicateCount.isEmpty()) {
            StringBuilder sb = new StringBuilder();

            for (String id : duplicateCount.keySet()) {
                sb.append("  ").append(id).append(" (").append(duplicateCount.get(id)).append(")\n");
            }

            Console.error("There were multiple publications with the following identifiers:%n%s Please make sure all identifiers are unique.", sb.toString());
        }
    }

    private static void processAliases(List<BibItem> items) {
        for (BibItem item : items) {
            for (String field : aliases.keySet()) {
                String value = item.get(field); // The possible alias

                if (value != null && !value.isEmpty()) {
                    String value2 = item.get(aliases.get(field)); // The field it replaces

                    if (value2 == null || value2.isEmpty()) {
                        item.put(aliases.get(field), value);
                    }
                }
            }
        }
    }

    /**
     * Removes all publications that miss information for mandatory fields.
     * <p>
     * If any publications are removed, this method also prints a helpful error
     * message.
     *
     * @param items
     */
    private static void removeItemsWithMissingMandatoryFields(List<BibItem> items) {
        ListIterator<BibItem> it = items.listIterator();
        int removedCount = 0;

        while (it.hasNext()) {
            BibItem item = it.next();

            if (!item.checkMandatoryFields()) {
                it.remove();
                removedCount++;
            }
        }

        if (removedCount > 0) {
            if (removedCount == 1) {
                Console.error("This publication was omitted from the list.");
            } else {
                Console.error("These publications were omitted from the list.");
            }
        }
    }

    protected static void detectArxiv(List<BibItem> items) {
        for (BibItem item : items) {
            // If this entry has an arxiv and primaryclass field, it's done
            String arxiv = item.get("arxiv");
            String primaryClass = item.get("primaryclass");

            if (arxiv != null) {
                if (arxiv.startsWith("http://arxiv.org/abs/")) {
                    arxiv = arxiv.substring("http://arxiv.org/abs/".length()).trim();
                }
            }

            if (arxiv == null || primaryClass == null) {
                if (arxiv == null) {
                    // Other fields might specify the arxiv identifier
                    String eprint = item.get("eprint");

                    if (eprint == null) {
                        for (String field : item.getFields()) {
                            String value = item.get(field);

                            if (value != null && value.startsWith("http://arxiv.org/abs/")) {
                                arxiv = value.substring("http://arxiv.org/abs/".length()).trim();
                            }
                        }
                    } else if (eprint.startsWith("http")) {
                        if (eprint.startsWith("http://arxiv.org/abs/")) {
                            arxiv = eprint.substring("http://arxiv.org/abs/".length()).trim();
                        }
                    } else {
                        String prefix = item.get("archiveprefix");

                        if (prefix == null || prefix.equalsIgnoreCase("arXiv")) {
                            // eprint is most likely an old arXiv identifier of the form "class/arxivid"
                            if (eprint.contains("/")) {
                                // class/arxivid
                                int index = eprint.indexOf('/');
                                primaryClass = eprint.substring(0, index).trim();
                                arxiv = eprint.substring(index + 1).trim();
                            } else if (eprint.contains("[") && eprint.contains("]")) {
                                // arxivid [class]
                                int index1 = eprint.indexOf('[');
                                int index2 = eprint.indexOf(']');

                                primaryClass = eprint.substring(index1 + 1, index2).trim();
                                arxiv = eprint.substring(0, index1).trim();
                            } else {
                                arxiv = eprint;
                            }
                        }
                    }
                } else {
                    // Arxiv identifier, but no primary class yet
                    if (arxiv.contains("/")) {
                        // class/arxivid
                        int index = arxiv.indexOf('/');
                        primaryClass = arxiv.substring(0, index).trim();
                        arxiv = arxiv.substring(index + 1).trim();
                    } else if (arxiv.contains("[") && arxiv.contains("]")) {
                        // arxivid [class]
                        int index1 = arxiv.indexOf('[');
                        int index2 = arxiv.indexOf(']');

                        primaryClass = arxiv.substring(index1 + 1, index2).trim();
                        arxiv = arxiv.substring(0, index1).trim();
                    }
                }
            }

            if (arxiv != null) {
                item.put("arxiv", arxiv);
            }

            if (primaryClass != null) {
                item.put("primaryclass", primaryClass);
            }
        }
    }
}
