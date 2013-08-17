/*
 * Copyright 2013 Sander Verdonschot <sander.verdonschot at gmail.com>.
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

/**
 *
 * @author Sander
 */
public class Settings {

    private Path publications;
    private HTMLSettings htmlSettings;
    private FormatSettings generalSettings;
    private ConsoleSettings consoleSettings;

    /**
     * Creates a new empty Settings object.
     *
     * @param publications
     */
    public Settings() {
        publications = null;
        htmlSettings = new HTMLSettings();
        generalSettings = new FormatSettings();
        consoleSettings = new ConsoleSettings();
    }
    
    /**
     * Returns a new Settings object with default settings.
     * @return 
     */
    public static Settings defaultSettings() {
        Settings result = new Settings();
        
        result.generalSettings = FormatSettings.defaultSettings();
        
        return result;
    }

    public Path getPublications() {
        return publications;
    }
    
    public void setPublications(Path publications) {
        this.publications = publications;
    }

    public HTMLSettings getHtmlSettings() {
        return htmlSettings;
    }

    public FormatSettings getGeneralSettings() {
        return generalSettings;
    }

    public ConsoleSettings getConsoleSettings() {
        return consoleSettings;
    }
}
