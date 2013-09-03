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
