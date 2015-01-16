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
package publy;

import com.beust.jcommander.Parameter;
import publy.data.settings.ConsoleSettings;
import publy.data.settings.FileSettings;
import publy.data.settings.Settings;
import publy.io.ResourceLocator;

/**
 * The command-line arguments for Publy.
 *
 * These are parsed using JCommander.
 */
public class CommandLineArguments {

    @Parameter(names = {"-c", "--config"}, description = "Configuration file to use (XML)", arity = 1)
    private String config;
    @Parameter(names = {"-d", "--debug"}, description = "Enable debug output")
    private boolean debug = false;
    @Parameter(names = {"-g", "--gui"}, description = "Open the configuration GUI")
    private boolean gui = false;
    @Parameter(names = {"-h", "--help"}, description = "Display this usage information", help = true)
    private boolean help = false;
    @Parameter(names = {"-f", "--input"}, description = "Publication list to use (BibTeX)", arity = 1)
    private String input;
    @Parameter(names = {"-o", "--output"}, description = "Output file to use (HTML)", arity = 1)
    private String output;
    @Parameter(names = {"-q", "-s", "--quiet", "--silent"}, description = "Hide regular log output")
    private boolean silent = false;
    @Parameter(names = {"-v", "-V", "--version"}, description = "Display version information")
    private boolean version = false;
    @Parameter(names = {"-w", "--no-warn"}, description = "Hide warnings")
    private boolean hidewarnings = false;

    /**
     * Gets the name of the configuration file to be used.
     * <p>
     * If no configuration file was specified at the command line, this returns
     * null, and the
     * {@link publy.io.settings.SettingsReader#DEFAULT_SETTINGS_LOCATION default configuration file location}
     * is used instead.
     *
     * @return the file name of the configuration file
     */
    public String getConfig() {
        return config;
    }

    /**
     * Gets whether debug output should be printed.
     * <p>
     * This affects whether stack traces are shown for exceptions. This setting
     * overrides the option in the configuration file.
     *
     * @return whether debug output should be printed
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Gets whether the program should be run in GUI mode.
     *
     * @return whether the program should be run in GUI mode
     */
    public boolean isGui() {
        return gui;
    }

    /**
     * Gets whether the command-line usage information should be printed.
     * <p>
     * If this is true, the program will exit after printing the usage
     * information.
     *
     * @return whether the command-line usage information should be printed
     */
    public boolean isHelp() {
        return help;
    }

    /**
     * Gets the name of the input publication list.
     * <p>
     * This should be a BibTeX file. This setting overrides the option in the
     * configuration file.
     *
     * @return the name of the input publication list
     */
    public String getInput() {
        return input;
    }

    /**
     * Gets the name of the output file.
     * <p>
     * This should be an HTML file. This setting overrides the option in the
     * configuration file.
     *
     * @return the name of the output file
     */
    public String getOutput() {
        return output;
    }

    /**
     * Gets whether to run the program in silent mode, without printing progress
     * messages.
     * <p>
     * This setting overrides the option in the configuration file.
     *
     * @return whether to run the program in silent mode
     */
    public boolean isSilent() {
        return silent;
    }

    /**
     * Gets whether to print the current program version.
     * <p>
     * If this is true, the program will exit after printing the version
     * information.
     *
     * @return whether to print the current program version
     */
    public boolean isVersion() {
        return version;
    }

    /**
     * Gets whether to hide warnings.
     * <p>
     * This setting overrides the option in the configuration file.
     *
     * @return whether to hide warnings
     */
    public boolean isHidewarnings() {
        return hidewarnings;
    }

    /**
     * Overrides the relevant settings with the values given by these
     * command-line arguments.
     * <p>
     * The following fields are potentially affected:
     * <p><ul>
     * <li> {@link FileSettings#publications} is set to
     * {@link #getInput()}, if the latter is not null and non-empty.
     * <li> {@link FileSettings#target} is set to
     * {@link #getOutput()}, if the latter is not null and non-empty.
     * <li> {@link ConsoleSettings#showLogs} is set to the inverse of {@link #isSilent()}.
     * <li> {@link ConsoleSettings#showWarnings} is set to the inverse of {@link #hidewarnings()}.
     * <li> {@link ConsoleSettings#showStackTraces} is set to {@link #isDebug()}.
     * </ul><p>
     *
     * @param settings
     */
    public void applyOverrides(Settings settings) {
        // File settings
        if (input != null && !input.isEmpty()) {
            settings.getFileSettings().setPublications(ResourceLocator.getFullPath(input));
        }

        if (output != null && !output.isEmpty()) {
            settings.getFileSettings().setTarget(ResourceLocator.getFullPath(output));
        }

        // Console settings
        if (silent) {
            settings.getConsoleSettings().setShowLogs(false);
        }

        if (hidewarnings) {
            settings.getConsoleSettings().setShowWarnings(false);
        }

        if (debug) {
            settings.getConsoleSettings().setShowStackTraces(true);
        }
    }
}
