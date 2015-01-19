/*
 * Copyright 2014-2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import publy.algo.PublicationListGenerator;
import publy.data.settings.Settings;
import publy.gui.MainFrame;
import publy.gui.WelcomeDialog;
import publy.io.ResourceLocator;
import publy.io.settings.SettingsReaderCurrent;

/**
 * A utility class that implements different ways to run Publy.
 */
public class Runner {

    private static Throwable settingsParseException;

    /**
     * Runs Publy in command-line mode with the given command-line arguments.
     *
     * @param arguments the command-line arguments
     */
    public static void runOnCommandLine(CommandLineArguments arguments) {
        Settings settings = readSettings(arguments.getConfig());

        if (settings == null) {
            // Notify the user
            if (settingsParseException == null) {
                Console.error("Hey, it looks like this is the first time you're using Publy! Before we can do anything, you should set up your configuration by running Publy with the \"--gui\" option. ");
            } else {
                Console.except(settingsParseException, "An exception occurred while parsing the configuration:");
            }
        } else {
            arguments.applyOverrides(settings);
            Console.setSettings(settings.getConsoleSettings());

            PublicationListGenerator.generatePublicationList(settings);
        }
    }

    /**
     * Runs Publy in GUI mode with the given command-line arguments.
     *
     * @param arguments the command-line arguments
     */
    public static void runWithGUI(CommandLineArguments arguments) {
        Settings settings = readSettings(arguments.getConfig());

        if (settings == null) {
            settings = showMissingSettingsDialog();
        }

        arguments.applyOverrides(settings);
        Console.setSettings(settings.getConsoleSettings());

        launchGUI(settings);
    }

    /**
     * Runs Publy in mixed mode with the given command-line arguments.
     * <p>
     * This opens the configuration GUI if there is not enough information to
     * proceed (missing settings, etc.). Otherwise, it immediately generates the
     * publication list with output to a small console dialog.
     *
     * @param arguments the command-line arguments
     */
    public static void runInMixedMode(CommandLineArguments arguments) {
        Settings settings = readSettings(arguments.getConfig());

        // Decide whether to show the settings GUI
        boolean showSettings = false;

        if (settings == null) {
            showSettings = true;
            settings = showMissingSettingsDialog();
            arguments.applyOverrides(settings);
            Console.setSettings(settings.getConsoleSettings());
        } else {
            arguments.applyOverrides(settings);
            Console.setSettings(settings.getConsoleSettings());

            // Basic checks, give the user a chance to fix issues instead of simply throwing an error
            Path pubList = settings.getFileSettings().getPublications();
            Path target = settings.getFileSettings().getTarget();

            if (pubList == null || target == null) {
                showSettings = true;
                JOptionPane.showMessageDialog(null, "Some critical settings have not been specified yet. Please complete your configuration.", "Publy - Launching Settings Window", JOptionPane.INFORMATION_MESSAGE);
            } else if (Files.notExists(pubList)) {
                showSettings = true;
                JOptionPane.showMessageDialog(null, "The publication list \"" + pubList.getFileName().toString() + "\" could not be found at the indicated location.", "Publy - Launching Settings Window", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        if (showSettings) {
            launchGUI(settings);
        } else {
            PublicationListGenerator.generatePublicationList(settings);
        }
    }

    /**
     * Reads the configuration file at the given location.
     * <p>
     * If the file cannot be found, this method reports an error to the user and
     * returns null. If something goes wrong while reading the file, the
     * exception is stored in {@code settingsParseException}, and this method
     * returns null.
     *
     * @param settingsLocation the location of a configuration file
     * @return the program configuration, or null if the configuration was not
     * successfully read
     */
    private static Settings readSettings(String settingsLocation) {
        // Change the default settings location, if another location was specified
        if (settingsLocation != null && !settingsLocation.isEmpty()) {
            Path settingsFile = ResourceLocator.getFullPath(settingsLocation);

            if (Files.exists(settingsFile)) {
                Settings.setSettingsPath(settingsFile);
            } else {
                Console.error("The configuration file \"%s\" could not be found at \"%s\".", settingsLocation, settingsFile);
                return null;
            }
        }

        try {
            return (new SettingsReaderCurrent()).parseSettings();
        } catch (IOException ex) {
            settingsParseException = ex;
            return null;
        }
    }

    /**
     * Shows a message dialog informing the user of the missing settings. If
     * there was no parse exception, the dialog gives the user the option to
     * import settings from an existing installation of Publy.
     * 
     * @return the Settings to use - either default or imported
     */
    private static Settings showMissingSettingsDialog() {
        if (settingsParseException == null) {
            WelcomeDialog wd = new WelcomeDialog(null);
            wd.setLocationRelativeTo(null); // Center
            wd.setVisible(true);
            return wd.getSettings();
        } else {
            JOptionPane.showMessageDialog(null, "An exception occurred while parsing the configuration. Loading the default configuration.", "Publy - Launching Settings Window", JOptionPane.ERROR_MESSAGE);
            return Settings.defaultSettings();
        }
    }

    /**
     * Launches the configuration GUI.
     *
     * @param settings the configuration to use
     */
    private static void launchGUI(final Settings settings) {
        // Variables need to be final in order to be shared
        final Throwable guiException = settingsParseException;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame mf = new MainFrame(settings);

                // Report an Exception, if one occurred
                if (guiException != null) {
                    Console.except(guiException, "Exception occurred while parsing the configuration:");
                }

                mf.setVisible(true);
            }
        });
    }

    private Runner() {
    }
}
