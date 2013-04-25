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
    private String format;
    private File target;
    // Author info
    private boolean listAllAuthors = false;
    // Presented
    private boolean underlinePresented = false;
    private String presentedText = null;
    private File presentedImage = null;
    // Numbering
    private boolean numberGlobally = false;
    private boolean numberLocally = false;
    // Categories
    private List<OutputCategory> categories;
    private Map<OutputCategory, String> categoryNotes;
}
