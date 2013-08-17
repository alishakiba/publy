/*
 * Copyright 2013 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.data.settings;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import publy.data.category.CategoryIdentifier;

/**
 *
 * @author Sander
 */
public class FormatSettings {

    public enum Numbering {

        NONE, LOCAL, GLOBAL;
    }

    public enum NameDisplay {

        FULL, ABBREVIATED, NONE;
    }
    // General
    private Path target;
    // Identification
    private List<String> myNames = Arrays.asList("me");
    // Author info
    private boolean listAllAuthors = true;
    private NameDisplay nameDisplay = NameDisplay.ABBREVIATED;
    private boolean reverseNames = false;
    // Publication Structure
    private boolean titleFirst = true;
    // Numbering
    private Numbering numbering = Numbering.NONE;
    private boolean reverseNumbering = false;
    // Categories
    private List<CategoryIdentifier> categories = new ArrayList<>();
    private Map<CategoryIdentifier, String> categoryNotes = new EnumMap<>(CategoryIdentifier.class);

    public static FormatSettings defaultSettings() {
        FormatSettings result = new FormatSettings();

        // Default categories
        result.addCategory(CategoryIdentifier.JOURNAL);
        result.addCategory(CategoryIdentifier.CONFERENCE);
        result.addCategory(CategoryIdentifier.CHAPTER);
        result.addCategory(CategoryIdentifier.THESIS);

        return result;
    }

    public Path getTarget() {
        return target;
    }

    public List<String> getMyNames() {
        return myNames;
    }

    public void setMyNames(List<String> myNames) {
        this.myNames = myNames;
    }

    /**
     * Returns the path to the location where the plain text version of the
     * publication list will be written to, if any. Returns
     * <code>null</code> if no such version will be written. The target is
     * guaranteed to be in the same directory as the HTML target.
     *
     * @return
     */
    public Path getPlainTextTarget() {
        String baseName = target.getFileName().toString();
        int extension = baseName.lastIndexOf('.');

        if (extension > -1) {
            baseName = baseName.substring(0, extension);
        }

        return target.resolveSibling(baseName + ".utf8.txt");
        // Add .utf8 to indicate to the web server that this file should be 
        // served with an UTF-8 header. This won't always work (it depends on 
        // the server settings), but it is the best we can do for a plain text file.
    }

    /**
     * Returns the path to the location where the bibtex version of the
     * publication list will be written to, if any. Returns
     * <code>null</code> if no such version will be written. The target is
     * guaranteed to be in the same directory as the HTML target.
     *
     * @return
     */
    public Path getBibtexTarget() {
        String baseName = target.getFileName().toString();
        int extension = baseName.lastIndexOf('.');

        if (extension > -1) {
            baseName = baseName.substring(0, extension);
        }

        return target.resolveSibling(baseName + ".bib");
    }

    public void setTarget(Path target) {
        this.target = target;
    }

    public boolean isListAllAuthors() {
        return listAllAuthors;
    }

    public void setListAllAuthors(boolean listAllAuthors) {
        this.listAllAuthors = listAllAuthors;
    }

    public NameDisplay getNameDisplay() {
        return nameDisplay;
    }

    public void setNameDisplay(NameDisplay nameDisplay) {
        this.nameDisplay = nameDisplay;
    }

    public boolean isReverseNames() {
        return reverseNames;
    }

    public void setReverseNames(boolean reverseNames) {
        this.reverseNames = reverseNames;
    }

    public boolean isTitleFirst() {
        return titleFirst;
    }

    public void setTitleFirst(boolean titleFirst) {
        this.titleFirst = titleFirst;
    }

    public Numbering getNumbering() {
        return numbering;
    }

    public void setNumbering(Numbering numbering) {
        this.numbering = numbering;
    }

    public boolean isReverseNumbering() {
        return reverseNumbering;
    }

    public void setReverseNumbering(boolean reverseNumbering) {
        this.reverseNumbering = reverseNumbering;
    }

    public List<CategoryIdentifier> getCategories() {
        return categories;
    }

    public void addCategory(CategoryIdentifier c) {
        categories.add(c);
    }

    public Map<CategoryIdentifier, String> getCategoryNotes() {
        return categoryNotes;
    }

    public void setNote(CategoryIdentifier c, String note) {
        categoryNotes.put(c, note);
    }
}
