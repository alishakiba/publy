/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.data.settings;

import java.io.File;
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
    private File target;
    // Author info
    private boolean listAllAuthors = true;
    // Presented
    private String presentedText = null;
    // Numbering
    private Numbering numbering = Numbering.NONE;
    // Categories
    private List<CategoryIdentifier> categories = new ArrayList<>();
    private Map<CategoryIdentifier, String> categoryNotes = new EnumMap<>(CategoryIdentifier.class);

    public File getTarget() {
        return target;
    }

    public void setTarget(File target) {
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
