/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.settings;

import java.io.File;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLSettings extends FormatSettings {

    public enum Mode {
        SINGLE_PAGE, TABS, MULTIPLE_PAGES;
    }
    
    private Mode mode;
    private boolean includeAbstracts;
    private boolean includeBibtext; // TODO: more specific
    private File header; // TODO: more specific, as I will be generating a lot of javascript for the head. I guess bodyHeader? No, has to include page title and stuff. Perhaps scan for end of head and insert custom js.
    private File footer;

    public Mode getMode() {
        return mode;
    }

    public File getHeader() {
        return header;
    }

    public File getFooter() {
        return footer;
    }
    
    
}
