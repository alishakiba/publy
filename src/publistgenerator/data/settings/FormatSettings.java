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
    // Link to parent, to access the settings of other formats
    private Settings settings;
    // General
    private Path target;
    // Author info
    private boolean listAllAuthors = true;
    // Presented
    private String presentedText = null;
    // Numbering
    private Numbering numbering = Numbering.NONE;
    // Categories
    private List<CategoryIdentifier> categories = new ArrayList<>();
    private Map<CategoryIdentifier, String> categoryNotes = new EnumMap<>(CategoryIdentifier.class);

    public FormatSettings(Settings settings) {
        this.settings = settings;
        
        // Default categories
        categories.add(CategoryIdentifier.JOURNAL);
        categories.add(CategoryIdentifier.CONFERENCE);
        categories.add(CategoryIdentifier.CHAPTER);
        categories.add(CategoryIdentifier.THESIS);
    }

    public Settings getSettings() {
        return settings;
    }

    public Path getTarget() {
        return target;
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

    public String getPresentedText() {
        return presentedText;
    }

    public void setPresentedText(String presentedText) {
        this.presentedText = presentedText;
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
