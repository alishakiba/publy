/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.settings;

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
    
    public HTMLSettings() {
        super("html");
    }
    
}
