/*
 */
package publy.data.settings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class ConsoleSettings {

    // Warnings
    private boolean showWarnings = true;
    private boolean warnMissingReferences = true;
    private boolean warnNotAuthor = true;
    // Log
    private boolean showLogs = true;
    // Debug
    private boolean showStackTraces = false;

    public boolean isShowWarnings() {
        return showWarnings;
    }

    public void setShowWarnings(boolean showWarnings) {
        this.showWarnings = showWarnings;
    }

    public boolean isWarnMissingReferences() {
        return warnMissingReferences;
    }

    public void setWarnMissingReferences(boolean warnMissingReferences) {
        this.warnMissingReferences = warnMissingReferences;
    }

    public boolean isWarnNotAuthor() {
        return warnNotAuthor;
    }

    public void setWarnNotAuthor(boolean warnNotAuthor) {
        this.warnNotAuthor = warnNotAuthor;
    }

    public boolean isShowLogs() {
        return showLogs;
    }

    public void setShowLogs(boolean showLogs) {
        this.showLogs = showLogs;
    }

    public boolean isShowStackTraces() {
        return showStackTraces;
    }

    public void setShowStackTraces(boolean showStackTraces) {
        this.showStackTraces = showStackTraces;
    }
}
