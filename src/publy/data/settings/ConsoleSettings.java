/*
 * Copyright 2013-2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
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

/**
 * Configuration of the console.
 * <p>
 * In particular, what kinds of messages the user wants to see.
 */
public class ConsoleSettings {

    // Warnings
    private boolean showWarnings = true;
    private boolean warnDuplicateIDs = true;
    private boolean warnMissingReferences = true;
    private boolean warnNotAuthor = true;
    private boolean warnNoCategoryForItem = true;
    private boolean warnMandatoryFieldIgnored = true;
    // Log
    private boolean showLogs = true;
    // Debug
    private boolean showDebugLog = false;
    private boolean showStackTraces = false;

    /**
     * Gets whether the console should display warnings.
     * <p>
     * If this is false, all warnings will be hidden. Otherwise, each type of
     * warning obeys its own visibility setting.
     *
     * @return whether to show warnings
     */
    public boolean isShowWarnings() {
        return showWarnings;
    }

    /**
     * Sets whether the console should display warnings.
     * <p>
     * If this is false, all warnings will be hidden. Otherwise, each type of
     * warning obeys its own visibility setting.
     *
     * @param showWarnings whether to show warnings
     */
    public void setShowWarnings(boolean showWarnings) {
        this.showWarnings = showWarnings;
    }

    /**
     * Gets whether the console should display a warning when multiple
     * publications have the same identifier.
     * <p>
     * If this is false, all warnings of this kind will be hidden. Otherwise,
     * they will be shown unless warnings are disabled globally.
     *
     * @return whether to show warnings for duplicate identifiers
     */
    public boolean isWarnDuplicateIDs() {
        return warnDuplicateIDs;
    }

    /**
     * Sets whether the console should display a warning when multiple
     * publications have the same identifier.
     * <p>
     * If this is false, all warnings of this kind will be hidden. Otherwise,
     * they will be shown unless warnings are disabled globally.
     *
     * @param warnDuplicateIDs whether to show warnings for duplicate
     * identifiers
     */
    public void setWarnDuplicateIDs(boolean warnDuplicateIDs) {
        this.warnDuplicateIDs = warnDuplicateIDs;
    }

    /**
     * Gets whether the console should display a warning when a referenced item
     * does not exist.
     * <p>
     * If this is false, all warnings of this kind will be hidden. Otherwise,
     * they will be shown unless warnings are disabled globally.
     *
     * @return whether to show warnings for missing referenced items
     */
    public boolean isWarnMissingReferences() {
        return warnMissingReferences;
    }

    /**
     * Sets whether the console should display a warning when a referenced item
     * does not exist.
     * <p>
     * If this is false, all warnings of this kind will be hidden. Otherwise,
     * they will be shown unless warnings are disabled globally.
     *
     * @param warnMissingReferences whether to show warnings for missing
     * referenced items
     */
    public void setWarnMissingReferences(boolean warnMissingReferences) {
        this.warnMissingReferences = warnMissingReferences;
    }

    /**
     * Gets whether the console should display a warning when the user is not
     * among the authors or editors of a publication.
     * <p>
     * If this is false, all warnings of this kind will be hidden. Otherwise,
     * they will be shown unless warnings are disabled globally.
     *
     * @return whether to show warnings for publications not authored by the
     * user
     */
    public boolean isWarnNotAuthor() {
        return warnNotAuthor;
    }

    /**
     * Sets whether the console should display a warning when the user is not
     * among the authors or editors of a publication.
     * <p>
     * If this is false, all warnings of this kind will be hidden. Otherwise,
     * they will be shown unless warnings are disabled globally.
     *
     * @param warnNotAuthor whether to show warnings for publications not
     * authored by the user
     */
    public void setWarnNotAuthor(boolean warnNotAuthor) {
        this.warnNotAuthor = warnNotAuthor;
    }

    /**
     * Gets whether the console should display a warning when a publication does
     * not fit any of the defined categories.
     * <p>
     * If this is false, all warnings of this kind will be hidden. Otherwise,
     * they will be shown unless warnings are disabled globally.
     *
     * @return whether to show warnings for publications that do not fit any
     * category
     */
    public boolean isWarnNoCategoryForItem() {
        return warnNoCategoryForItem;
    }

    /**
     * Sets whether the console should display a warning when a publication does
     * not fit any of the defined categories.
     * <p>
     * If this is false, all warnings of this kind will be hidden. Otherwise,
     * they will be shown unless warnings are disabled globally.
     *
     * @param warnNoCategoryForItem whether to show warnings for publications
     * that do not fit any category
     */
    public void setWarnNoCategoryForItem(boolean warnNoCategoryForItem) {
        this.warnNoCategoryForItem = warnNoCategoryForItem;
    }

    /**
     * Gets whether the console should display a warning when a category is set
     * to ignore a mandatory field of one of its publications.
     * <p>
     * If this is false, all warnings of this kind will be hidden. Otherwise,
     * they will be shown unless warnings are disabled globally.
     *
     * @return whether to show warnings for ignored mandatory fields
     */
    public boolean isWarnMandatoryFieldIgnored() {
        return warnMandatoryFieldIgnored;
    }

    /**
     * Sets whether the console should display a warning when a category is set
     * to ignore a mandatory field of one of its publications.
     * <p>
     * If this is false, all warnings of this kind will be hidden. Otherwise,
     * they will be shown unless warnings are disabled globally.
     *
     * @param warnMandatoryFieldIgnored whether to show warnings for ignored
     * mandatory fields
     */
    public void setWarnMandatoryFieldIgnored(boolean warnMandatoryFieldIgnored) {
        this.warnMandatoryFieldIgnored = warnMandatoryFieldIgnored;
    }

    /**
     * Gets whether the console should display progress messages.
     *
     * @return whether to show progress messages
     */
    public boolean isShowLogs() {
        return showLogs;
    }

    /**
     * Sets whether the console should display progress messages.
     *
     * @param showLogs whether to show progress messages
     */
    public void setShowLogs(boolean showLogs) {
        this.showLogs = showLogs;
    }

    /**
     * Gets whether the console should display debug messages.
     *
     * @return whether to show debug messages
     */
    public boolean isShowDebugLog() {
        return showDebugLog;
    }

    /**
     * Sets whether the console should display debug messages.
     *
     * @param showDebugLog whether to show debug messages
     */
    public void setShowDebugLog(boolean showDebugLog) {
        this.showDebugLog = showDebugLog;
    }

    /**
     * Gets whether the console should display full stack traces of exceptions.
     *
     * @return whether the console should show stack traces
     */
    public boolean isShowStackTraces() {
        return showStackTraces;
    }

    /**
     * Sets whether the console should display full stack traces of exceptions.
     *
     * @param showStackTraces whether the console should show stack traces
     */
    public void setShowStackTraces(boolean showStackTraces) {
        this.showStackTraces = showStackTraces;
    }
}
