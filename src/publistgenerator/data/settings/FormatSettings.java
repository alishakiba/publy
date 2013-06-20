/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.data.settings;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import publistgenerator.data.category.CategoryIdentifier;

/**
 *
 * @author Sander
 */
public class FormatSettings {

    public enum Numbering {

        NONE, LOCAL, GLOBAL;
    }
    // General
    private Path target;
    // Author info
    private boolean listAllAuthors = true;
    // Numbering
    private Numbering numbering = Numbering.NONE;
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

        return target.resolveSibling(baseName + ".txt");
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

    public Numbering getNumbering() {
        return numbering;
    }

    public void setNumbering(Numbering numbering) {
        this.numbering = numbering;
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
