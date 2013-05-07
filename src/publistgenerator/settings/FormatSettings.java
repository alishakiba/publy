/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.settings;

import java.io.File;
import java.util.List;
import java.util.Map;
import publistgenerator.category.OutputCategory;

/**
 *
 * @author Sander
 */
public class FormatSettings {

    public enum Numbering {

        NONE, LOCAL, GLOBAL;

        @Override
        public String toString() {
            switch (this) {
                case NONE:
                    return "None";
                case LOCAL:
                    return "Within sections";
                case GLOBAL:
                    return "Globally";
                default:
                    throw new InternalError("Unknown Numbering.");
            }
        }
    }
    // General
    private File target;
    // Author info
    private boolean listAllAuthors = false;
    // Presented
    private String presentedText = null;
    // Numbering
    private Numbering numbering = Numbering.NONE;
    // Categories
    private List<OutputCategory> categories;
    private Map<OutputCategory, String> categoryNotes;

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

    public List<OutputCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<OutputCategory> categories) {
        this.categories = categories;
    }

    public Map<OutputCategory, String> getCategoryNotes() {
        return categoryNotes;
    }

    public void setCategoryNotes(Map<OutputCategory, String> categoryNotes) {
        this.categoryNotes = categoryNotes;
    }
}
