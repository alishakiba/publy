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

    // General
    private File target;
    // Author info
    private boolean listAllAuthors = false;
    // Presented
    private String presentedText = null;
    // Numbering
    private boolean numberGlobally = false;
    private boolean numberLocally = false;
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

    public boolean isNumberGlobally() {
        return numberGlobally;
    }

    public void setNumberGlobally(boolean numberGlobally) {
        this.numberGlobally = numberGlobally;
    }

    public boolean isNumberLocally() {
        return numberLocally;
    }

    public void setNumberLocally(boolean numberLocally) {
        this.numberLocally = numberLocally;
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
