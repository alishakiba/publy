/*
 * Copyright 2014-2016 Sander Verdonschot <sander.verdonschot at gmail.com>.
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import publy.Console;
import publy.data.Author;
import publy.data.Section;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.FieldData;
import publy.data.bibitem.Type;
import publy.data.category.OutputCategory;
import publy.data.settings.GeneralSettings;
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
    public static List<Section> postProcess(Settings settings, List<BibItem> items) {
        Map<String, BibItem> itemsById = removeDuplicateIDs(items);
        processAliases(items);
        applyCrossref(items, itemsById);
        removeItemsWithMissingMandatoryFields(items);
        detectArxiv(items);

        List<Section> sections = categorizePapers(settings, items);

        warnForMandatoryIgnoredFields(settings, sections);
        warnIfIAmNotAuthor(settings, items);
        warnForMissingReferences(settings, items, sections);

        return sections;
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
    private static Map<String, BibItem> removeDuplicateIDs(List<BibItem> items) {
        Map<String, BibItem> itemsById = new HashMap<>();
        Map<String, Integer> duplicateCount = new HashMap<>();

        ListIterator<BibItem> it = items.listIterator();

        while (it.hasNext()) {
            BibItem item = it.next();

            if (itemsById.containsKey(item.getId())) {
                it.remove();

                int currentCount = (duplicateCount.containsKey(item.getId()) ? duplicateCount.get(item.getId()) : 1);
                duplicateCount.put(item.getId(), currentCount + 1);
            } else {
                itemsById.put(item.getId(), item);
            }
        }

        if (!duplicateCount.isEmpty()) {
            StringBuilder sb = new StringBuilder();

            for (String id : duplicateCount.keySet()) {
                sb.append("  ").append(id).append(" (").append(duplicateCount.get(id)).append(")\n");
            }

            Console.warn(Console.WarningType.DUPLICATE_ID, "There were multiple publications with the following identifiers:%n%s Only the first publication with each identifier was included.", sb.toString());
        }

        return itemsById;
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
     * Fills missing fields with information from cross-referenced publications.
     * <p>
     * Specifically, if publication A has its "crossref" field set to the id of
     * publication B, then all fields of B that are not set on A will be copied
     * to A.
     *
     * @param items the publications to process
     */
    private static void applyCrossref(List<BibItem> items, Map<String, BibItem> itemsById) {
        for (BibItem item : items) {
            String id = item.get("crossref");

            if (id != null && !id.isEmpty()) {
                BibItem source = itemsById.get(id);

                if (source == null) {
                    Console.warn(Console.WarningType.MISSING_REFERENCE, "Publication \"%s\" (cross-referenced from \"%s\") does not exist.", id, item.getId());
                } else {
                    // Copy all missing fields from source to item
                    for (String field : source.getFields()) {
                        if (source.get(field) != null && !source.get(field).isEmpty()
                                && (item.get(field) == null || item.get(field).isEmpty())) {
                            item.put(field, source.get(field));
                        }
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
     * Groups the publications into the sections defined in the settings.
     * <p>
     * Each paper is tested against the conditions of each category (in the
     * order they appear in), and added to the section corresponding to the
     * first category that it matches. If there are papers that do not match any
     * category, a warning message is shown.
     * <p>
     * If grouping by year is desired, the categories are split further by year
     * or the years are split by category, depending on preferences.
     *
     * @param settings the configuration settings to use
     * @param items the publications to process
     * @return the sections containing all matched publications
     */
    private static List<Section> categorizePapers(Settings settings, List<BibItem> items) {
        // Create a virtual section with all papers.
        Section master = new Section("Master", null);
        master.getItems().addAll(items);

        if (settings.getGeneralSettings().getGrouping() == GeneralSettings.Grouping.NO_GROUPING) {
            groupByCategory(settings, master);
        } else if (settings.getGeneralSettings().getGrouping() == GeneralSettings.Grouping.GROUP_BY_YEAR) {
            if (settings.getGeneralSettings().isGroupWithinCategories()) {
                groupByCategory(settings, master);

                for (Section category : master.getSubsections()) {
                    groupByYear(category);
                }
            } else {
                groupByYear(master);

                for (Section year : master.getSubsections()) {
                    groupByCategory(settings, year);
                }
            }
        }

        return master.getSubsections();
    }

    /**
     * Splits the given section into sub-sections by category.
     *
     * @param settings the configuration settings to use
     * @param section the section to split
     */
    private static void groupByCategory(Settings settings, Section section) {
        // Create an empty section for each category
        Map<OutputCategory, Section> sections = new LinkedHashMap<>();

        for (OutputCategory c : settings.getCategorySettings().getActiveCategories()) {
            sections.put(c, new Section(c));
        }

        // Assign each paper to the correct category
        for (ListIterator<BibItem> it = section.getItems().listIterator(); it.hasNext();) {
            BibItem item = it.next();

            for (OutputCategory c : settings.getCategorySettings().getActiveCategories()) {
                if (c.fitsCategory(item)) {
                    sections.get(c).addItem(item);
                    it.remove();
                    break;
                }
            }
        }

        // Warn for remaining items
        if (!section.getItems().isEmpty()) {
            String ids = "";

            for (BibItem item : section.getItems()) {
                ids += "\"" + item.getId() + "\", ";
            }

            ids = ids.substring(0, ids.length() - 2); // Cut off the last ", "

            Console.warn(Console.WarningType.ITEM_DOES_NOT_FIT_ANY_CATEGORY, "%d %s did not fit any category:%n%s", section.getItems().size(), (section.getItems().size() == 1 ? "entry" : "entries"), ids);
        }

        // Add all non-empty sections as subsections
        List<Section> result = new ArrayList<>(sections.size());

        for (Section s : sections.values()) {
            if (!s.getItems().isEmpty()) {
                result.add(s);
            }
        }

        section.setSubsections(result);
    }

    /**
     * Splits the given section into sub-sections by year of publication.
     *
     * @param settings the configuration settings to use
     * @param section the section to split
     */
    private static void groupByYear(Section section) {
        Map<Integer, Section> sections = new HashMap<>();

        // Assign each paper to the correct section, creating one if it does not yet exist
        for (ListIterator<BibItem> it = section.getItems().listIterator(); it.hasNext();) {
            BibItem item = it.next();

            try {
                Integer year = Integer.parseInt(item.get("year"));
                Section yearSection = sections.get(year);

                if (yearSection == null) {
                    yearSection = new Section(year.toString(), year.toString());
                    sections.put(year, yearSection);
                }

                yearSection.addItem(item);
                it.remove();
            } catch (NumberFormatException nfe) {
                // Either the publication has no year set, or it is not a valid integer
                // Either way, we just leave it in the super-section
            }
        }

        // Warn for remaining items
        if (!section.getItems().isEmpty()) {
            String ids = "";

            for (BibItem item : section.getItems()) {
                ids += "\"" + item.getId() + "\", ";
            }

            ids = ids.substring(0, ids.length() - 2); // Cut off the last ", "

            Console.warn(Console.WarningType.ITEM_DOES_NOT_FIT_ANY_CATEGORY, "%d %s does not have a valid publication year:%n%s", section.getItems().size(), (section.getItems().size() == 1 ? "entry" : "entries"), ids);
        }

        // Add all non-empty sections as subsections, in sorted order
        List<Integer> years = new ArrayList<>(sections.keySet());
        Collections.sort(years, Collections.reverseOrder());

        List<Section> result = new ArrayList<>(sections.size());

        for (Integer year : years) {
            result.add(sections.get(year));
        }

        section.setSubsections(result);
    }

    /**
     * Prints a warning message if a section ignores a mandatory field of one of
     * its publications.
     *
     * @param settings the configuration settings to use
     * @param sections the sections that contain the publications to process
     */
    private static void warnForMandatoryIgnoredFields(Settings settings, List<Section> sections) {
        if (settings.getConsoleSettings().isShowWarnings() && settings.getConsoleSettings().isWarnMandatoryFieldIgnored()) {
            for (Section s : sections) {
                Map<Type, List<String>> idsPerType = new EnumMap<>(Type.class);

                for (BibItem item : s.getItems()) {
                    if (!idsPerType.containsKey(item.getType())) {
                        idsPerType.put(item.getType(), new ArrayList<String>());
                    }

                    idsPerType.get(item.getType()).add(item.getId());
                }

                for (Type type : idsPerType.keySet()) {
                    for (String mandatoryFields : FieldData.getMandatoryFields(type)) {
                        boolean allOptionsIgnored = true;

                        for (String mandatory : mandatoryFields.split(";")) {
                            if (!s.getIgnoredFields().contains(mandatory)) {
                                allOptionsIgnored = false;
                            }
                        }

                        if (allOptionsIgnored) {
                            Console.warn(Console.WarningType.MANDATORY_FIELD_IGNORED,
                                    "Category \"%s\" ignores field%s \"%s\", which %s mandatory for the following entr%s:%n%s.%nTh%s may not display properly.",
                                    s.getShortName(), (mandatoryFields.contains(";") ? "s" : ""), mandatoryFields,
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
            List<BibItem> notAuthoredByMe = new ArrayList<>();

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

                    for (Author editor : item.getEditors()) {
                        if (editor.isMe(settings.getGeneralSettings())) {
                            imEditor = true;
                            break;
                        }
                    }

                    if (!imEditor) {
                        notAuthoredByMe.add(item);
                    }
                }
            }

            if (!notAuthoredByMe.isEmpty()) {
                Console.warn(Console.WarningType.NOT_AUTHORED_BY_USER,
                        "None of the authors or editors of the following %s match %s (%s):%n%s",
                        (notAuthoredByMe.size() == 1 ? "publication" : notAuthoredByMe.size() + " publications"),
                        (settings.getGeneralSettings().getMyNames().size() == 1 ? "your name" : "any of your names"),
                        getMyNames(settings),
                        listPaperIDs(notAuthoredByMe));
            }
        }
    }

    private static String getMyNames(Settings settings) {
        StringBuilder myNames = new StringBuilder();

        for (int i = 0; i < settings.getGeneralSettings().getMyNames().size(); i++) {
            myNames.append('"').append(settings.getGeneralSettings().getMyNames().get(i)).append('"');

            if (i < settings.getGeneralSettings().getMyNames().size() - 2) {
                myNames.append(", ");
            } else if (i == settings.getGeneralSettings().getMyNames().size() - 2) {
                myNames.append(", or ");
            }
        }

        return myNames.toString();
    }

    private static String listPaperIDs(List<BibItem> items) {
        StringBuilder ids = new StringBuilder();

        for (BibItem item : items) {
            ids.append(item.getId()).append('\n');
        }

        if (ids.length() > 0) {
            ids.delete(ids.length() - 1, ids.length()); // Remove the last newline
        }

        return ids.toString();
    }

    /**
     * Prints warning messages for publications that reference other
     * publications or files that do not exist.
     *
     * @param settings
     * @param items
     * @param sections
     */
    private static void warnForMissingReferences(Settings settings, List<BibItem> items, List<Section> sections) {
        for (BibItem item : items) {
            // Check 'file' links
            String path = item.get("file");

            if (path != null && !path.isEmpty()) {
                checkFileExistance(settings, item.get("file"), "file", item);
            }

            // Check all 'link' links
            for (int i = -1; i < 20; i++) {
                String attribute = (i == -1 ? "link" : "link" + i);
                String link = item.get(attribute);

                if (link != null && !link.isEmpty()) {
                    int divider = link.indexOf('|');

                    if (divider > -1) {
                        String target = link.substring(divider + 1);

                        if (target.startsWith("#")) {
                            // Link to another paper
                            checkIdExistance(target.substring(1), attribute, item, sections);
                        } else if (target.contains(":")) {
                            // Most file systems prohibit colons in file names, so
                            // it seems safe to assume that this indicates an
                            // absolute URI and as such, should be fine.
                        } else {
                            // Most likely link to a file on disk
                            checkFileExistance(settings, target, attribute, item);
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks whether the given path corresponds to an existing file, when
     * resolved from the HTML target location, prints a warning if it doesn't
     *
     * @param settings
     * @param path
     * @param attr
     * @param item
     */
    private static void checkFileExistance(Settings settings, String path, String attr, BibItem item) {
        Path file = settings.getFileSettings().getTarget().resolveSibling(path);

        if (Files.notExists(file)) {
            Console.warn(Console.WarningType.MISSING_REFERENCE, "File \"%s\" (linked in attribute \"%s\" of publication \"%s\") cannot be found at \"%s\".", path, attr, item.getId(), file);
        }
    }

    /**
     * Checks whether a BibItem with the given id is in any of the given
     * sections or sub-sections, prints a warning if it doesn't.
     *
     * @param id
     */
    private static void checkIdExistance(String id, String attr, BibItem item, List<Section> sections) {
        for (Section s : sections) {
            if (checkIdExists(id, s)) {
                return;
            }
        }

        Console.warn(Console.WarningType.MISSING_REFERENCE, "Publication \"%s\" (linked in attribute \"%s\" of publication \"%s\") is not in the final list.", id, attr, item.getId());
    }

    private static boolean checkIdExists(String id, Section section) {
        for (BibItem i : section.getItems()) {
            if (i.getId().equals(id)) {
                return true;
            }
        }

        for (Section subsection : section.getSubsections()) {
            if (checkIdExists(id, subsection)) {
                return true;
            }
        }

        return false;
    }

    private PublicationPostProcessor() {
    }
}
