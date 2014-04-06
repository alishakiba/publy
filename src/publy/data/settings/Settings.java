/*
 * Copyright 2013-2014 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
 * All configuration information, divided into five categories:
 * <p><ul>
 * <li>{@link FileSettings}
 * <li>{@link CategorySettings}
 * <li>{@link GeneralSettings}
 * <li>{@link HTMLSettings}
 * <li>{@link ConsoleSettings}
 * </ul>
 */
public class Settings {

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
     * Returns a new Settings object with default settings.
     *
     * @return the default settings
     */
    public static Settings defaultSettings() {
        Settings result = new Settings();
        result.categorySettings.setToDefault();
        return result;
    }

    public FileSettings getFileSettings() {
        return fileSettings;
    }

    public CategorySettings getCategorySettings() {
        return categorySettings;
    }

    public GeneralSettings getGeneralSettings() {
        return generalSettings;
    }

    public HTMLSettings getHtmlSettings() {
        return htmlSettings;
    }

    public ConsoleSettings getConsoleSettings() {
        return consoleSettings;
    }
}
