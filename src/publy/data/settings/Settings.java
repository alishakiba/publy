/*
 * Copyright 2013-2016 Sander Verdonschot <sander.verdonschot at gmail.com>.
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

import java.nio.file.Path;
import publy.io.ResourceLocator;

/**
 * All configuration information, divided into five categories:
 * <p>
 * <ul>
 * <li>{@link FileSettings}
 * <li>{@link CategorySettings}
 * <li>{@link GeneralSettings}
 * <li>{@link HTMLSettings}
 * <li>{@link ConsoleSettings}
 * </ul>
 */
public class Settings {

    // Static properties that concern Settings
    public static final String DEFAULT_SETTINGS_FILENAME = "PublySettings.xml";
    public static final String DEFAULT_SETTINGS_PATH = "data/" + DEFAULT_SETTINGS_FILENAME;
    private static Path settingsPath = ResourceLocator.getFullPath(DEFAULT_SETTINGS_PATH);

    private final FileSettings fileSettings;
    private final CategorySettings categorySettings;
    private final GeneralSettings generalSettings;
    private final HTMLSettings htmlSettings;
    private final ConsoleSettings consoleSettings;

    /**
     * Creates a new, empty, Settings object.
     */
    public Settings() {
        fileSettings = new FileSettings();
        categorySettings = new CategorySettings();
        htmlSettings = new HTMLSettings();
        generalSettings = new GeneralSettings();
        consoleSettings = new ConsoleSettings();
    }

    /**
     * Returns a new Settings object with the default configuration.
     *
     * @return the default configuration
     */
    public static Settings defaultSettings() {
        Settings result = new Settings();
        result.categorySettings.setToDefault();
        return result;
    }

    /**
     * Returns the path to the current settings file. This may not yet exist -
     * it is where the settings will be saved when Publy closes.
     *
     * @return the path to the current settings file
     */
    public static Path getSettingsPath() {
        return settingsPath;
    }

    /**
     * Sets the path to which the settings will be saved when Publy closes.
     *
     * @param settingsFile the new path
     */
    public static void setSettingsPath(Path settingsFile) {
        Settings.settingsPath = settingsFile;
    }

    /**
     * Gets the file settings.
     *
     * @return the file settings
     */
    public FileSettings getFileSettings() {
        return fileSettings;
    }

    /**
     * Gets the category settings.
     *
     * @return the category settings
     */
    public CategorySettings getCategorySettings() {
        return categorySettings;
    }

    /**
     * Gets the formatting settings.
     *
     * @return the formatting settings
     */
    public GeneralSettings getGeneralSettings() {
        return generalSettings;
    }

    /**
     * Gets the HTML settings.
     *
     * @return the HTML settings
     */
    public HTMLSettings getHtmlSettings() {
        return htmlSettings;
    }

    /**
     * Gets the console settings.
     *
     * @return the console settings
     */
    public ConsoleSettings getConsoleSettings() {
        return consoleSettings;
    }
}
