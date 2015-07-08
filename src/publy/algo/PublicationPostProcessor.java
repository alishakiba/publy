/*
 * Copyright 2014-2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import publy.Console;
import publy.data.Author;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.FieldData;
import publy.data.bibitem.Type;
import publy.data.category.OutputCategory;
import publy.data.settings.Settings;

/**
 *
 *
 */
public class PublicationPostProcessor {

    /**
     * A map containing the supported aliases for entry fields. If aliases
     * contains the mapping "A" -> "B", it means that "A" can be used instead of
     * "B". In other words, if a publication does not have field "B" set, the
     * value of field "A" (if any) will be used instead.
     */
    private static final HashMap<String, String> aliases;

    static {
        aliases = new HashMap<>();
        aliases.put("journaltitle", "journal"); // biblatex uses journaltitle for @article entries
    }

    /**
     * Runs all defined post-processing tasks for the given publications.
     * <p>
     * <ul>
     * <li> Removes publications with duplicate identifiers.
     * <li> Checks for, and replaces certain fields with possible aliases.
     * <li> Removes publications that miss mandatory information.
     * <li> Checks for an arXiv version.
     * <li> Categorizes the papers.
     * <li> Presents warnings for several common mistakes.
     * </ul>
     *
     * @param settings the configuration settings to use
     * @param items the publications to process
     * @return a list of categories that contain the post-processed publications
     */
    public static List<OutputCategory> postProcess(Settings settings, List<BibItem> items) {
        removeDuplicateIDs(items);
        processAliases(items);
        removeItemsWithMissingMandatoryFields(items);
        detectArxiv(items);

        List<OutputCategory> categories = categorizePapers(settings, items);

        warnForMandatoryIgnoredFields(settings, categories);
        warnIfIAmNotAuthor(settings, items);

        return categories;
    }

    /**
     * Removes all but the first occurrence of entries with duplicate
     * identifiers.
     * <p>
     * If there are duplicate identifiers, this method also prints a helpful
     * error message.
     *
     * @param items the publications to process
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

    /**
     * Checks the publications for aliases of standard fields.
     * <p>
     * If the alias is present while the standard field is not, the standard
     * field is assigned the value of the alias. Otherwise, no change is made.
     *
     * @param items the publications to process
     */
    private static void processAliases(List<BibItem> items) {
        for (BibItem item : items) {
            for (String aliasField : aliases.keySet()) {
                String aliasValue = item.get(aliasField);

                if (aliasValue != null && !aliasValue.isEmpty()) {
                    String standardField = aliases.get(aliasField);
                    String standardValue = item.get(standardField);

                    if (standardValue == null || standardValue.isEmpty()) {
                        item.put(standardField, aliasValue);
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
     * @param items the publications to process
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

    /**
     * Uses information from other fields to infer whether this publication has
     * an arXiv version that we can link to.
     * <p>
     * If one is found, the {@code arxiv} and {@code primaryClass} fields are
     * updated to reflect the new information.
     *
     * @param items the publications to process
     */
    private static void detectArxiv(List<BibItem> items) {
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

    /**
     * Groups the publications into the categories defined in the settings.
     * <p>
     * Each paper is tested against the conditions of each category (in the
     * order they appear in), and added to the first category that it matches.
     * If there are papers that do not match any category, a warning message is
     * shown.
     *
     * @param settings the configuration settings to use
     * @param items the publications to process
     * @return the categories containing all matched publications
     */
    private static List<OutputCategory> categorizePapers(Settings settings, List<BibItem> items) {
        // Create the list of empty categories
        List<OutputCategory> categories = new ArrayList<>(settings.getCategorySettings().getActiveCategories().size());

        for (OutputCategory c : settings.getCategorySettings().getActiveCategories()) {
            try {
                categories.add((OutputCategory) c.clone());
            } catch (CloneNotSupportedException ex) {
                // Should never happen
                Console.except(ex, "Category \"%s\" could not be copied.", c.getShortName());
            }
        }

        // Assign each paper to the correct category
        // Make a copy so the categories can remove items without removing them from the main list
        List<BibItem> tempItems = new ArrayList<>(items);

        for (OutputCategory c : categories) {
            c.populate(tempItems);
        }

        // Remove empty categories
        ListIterator<OutputCategory> it = categories.listIterator();

        while (it.hasNext()) {
            OutputCategory c = it.next();

            if (c.getItems().isEmpty()) {
                it.remove();
            }
        }

        // Warn for remaining items
        if (!tempItems.isEmpty()) {
            String ids = "";

            for (BibItem item : tempItems) {
                ids += "\"" + item.getId() + "\", ";
            }

            ids = ids.substring(0, ids.length() - 2); // Cut off the last ", "

            Console.warn(Console.WarningType.ITEM_DOES_NOT_FIT_ANY_CATEGORY, "%d %s did not fit any category:%n%s", tempItems.size(), (tempItems.size() == 1 ? "entry" : "entries"), ids);
        }

        return categories;
    }

    /**
     * Prints a warning message if a category ignores a mandatory field of one
     * of its publications.
     *
     * @param settings the configuration settings to use
     * @param categories the categories that contain the publications to process
     */
    private static void warnForMandatoryIgnoredFields(Settings settings, List<OutputCategory> categories) {
        if (settings.getConsoleSettings().isShowWarnings() && settings.getConsoleSettings().isWarnMandatoryFieldIgnored()) {
            for (OutputCategory c : categories) {
                Map<Type, List<String>> idsPerType = new EnumMap<>(Type.class);

                for (BibItem item : c.getItems()) {
                    if (!idsPerType.containsKey(item.getType())) {
                        idsPerType.put(item.getType(), new ArrayList<String>());
                    }

                    idsPerType.get(item.getType()).add(item.getId());
                }

                for (Type type : idsPerType.keySet()) {
                    for (String mandatoryFields : FieldData.getMandatoryFields(type)) {
                        boolean allOptionsIgnored = true;

                        for (String mandatory : mandatoryFields.split(";")) {
                            if (!c.getIgnoredFields().contains(mandatory)) {
                                allOptionsIgnored = false;
                            }
                        }

                        if (allOptionsIgnored) {
                            Console.warn(Console.WarningType.MANDATORY_FIELD_IGNORED,
                                    "Category \"%s\" ignores field%s \"%s\", which %s mandatory for the following entr%s:%n%s.%nTh%s may not display properly.",
                                    c.getShortName(), (mandatoryFields.contains(";") ? "s" : ""), mandatoryFields,
                                    (mandatoryFields.contains(";") ? "are" : "is"), (idsPerType.get(type).size() == 1 ? "y" : "ies"),
                                    idsPerType.get(type).toString(), (idsPerType.get(type).size() == 1 ? "is entry" : "ese entries"));
                        }
                    }
                }
            }
        }
    }

    /**
     * Prints a warning message for publications that were not authored by the
     * user.
     *
     * @param settings the configuration settings to use
     * @param items the publications to process
     */
    private static void warnIfIAmNotAuthor(Settings settings, List<BibItem> items) {
        if (settings.getConsoleSettings().isShowWarnings() && settings.getConsoleSettings().isWarnNotAuthor()) {
            for (BibItem item : items) {
                boolean imAuthor = false;

                for (Author author : item.getAuthors()) {
                    if (author.isMe(settings.getGeneralSettings())) {
                        imAuthor = true;
                        break;
                    }
                }

                if (!imAuthor) {
                    boolean imEditor = false;

                    for (Author author : item.getEditors()) {
                        if (author.isMe(settings.getGeneralSettings())) {
                            imEditor = true;
                            break;
                        }
                    }

                    if (!imEditor) {
                        Console.warn(Console.WarningType.NOT_AUTHORED_BY_USER, "None of the authors or editors of entry \"%s\" match your name.", item.getId());
                    }
                }
            }
        }
    }

    private PublicationPostProcessor() {
    }
}
