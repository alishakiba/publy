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
package publy;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import publy.data.bibitem.BibItem;
import publy.data.settings.Settings;
import publy.gui.MainFrame;
import publy.gui.UIConstants;
import publy.io.BibTeXParser;
import publy.io.PublicationListWriter;
import publy.io.ResourceLocator;
import publy.io.bibtex.BibtexPublicationListWriter;
import publy.io.html.HTMLPublicationListWriter;
import publy.io.plain.PlainPublicationListWriter;
import publy.io.settings.SettingsReader;

/**
 *
 * @author Sander
 */
public class Publy {

    private static Settings settings = null;
    private static Throwable exception = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        setLookAndFeel();

        // Parse the command line arguments
        CommandLineArguments arguments = new CommandLineArguments();
        JCommander jc;

        try {
            jc = new JCommander(arguments, args);
        } catch (ParameterException ex) {
            Console.error(ex.getMessage());
            return;
        }

        if (arguments.isHelp()) {
            jc.setProgramName("java -jar Publy.jar");
            jc.usage();
        } else if (arguments.isVersion()) {
            printVersionInfo();
        } else {
            readSettings(arguments.getConfig());

            if (arguments.isGui()) {
                runInGuiMode(arguments);
            } else if (System.console() == null) {
                runInMixedMode(arguments);
            } else {
                runInCommandlineMode(arguments);
            }
        }
    }

    private static void printVersionInfo() {
        System.out.printf("Publy %d.%d%n"
                + "Copyright (c) 2013 Sander Verdonschot%n"
                + "License Apache v2%n"
                + "This is free software. You are free to change and redistribute it.",
                UIConstants.MAJOR_VERSION, UIConstants.MINOR_VERSION);
    }

    private static void runInGuiMode(CommandLineArguments arguments) {
        if (settings == null) {
            notifyForMissingSettings();
            settings = Settings.defaultSettings();
        }

        applyCommandlineOverwites(arguments);

        launchGUI();
    }

    private static void runInMixedMode(CommandLineArguments arguments) {
        // Decide whether to show the settings GUI
        boolean showSettings = false;

        if (settings == null) {
            showSettings = true;
            notifyForMissingSettings();
            settings = Settings.defaultSettings();
            applyCommandlineOverwites(arguments);
        } else {
            applyCommandlineOverwites(arguments);

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
            launchGUI();
        } else {
            generatePublicationList(settings);
        }
    }

    private static void runInCommandlineMode(CommandLineArguments arguments) {
        if (settings == null) {
            // Notify the user
            if (exception == null) {
                Console.error("No configuration information was found. Please set up your preferences by running Publy with the \"--gui\" option.");
            } else {
                Console.except(exception, "An exception occurred while parsing the configuration:");
            }
        } else {
            applyCommandlineOverwites(arguments);
            generatePublicationList(settings);
        }
    }

    private static void readSettings(String settingsLocation) {
        if (settingsLocation == null || settingsLocation.isEmpty()) {
            // Parse the settings in the default location
            try {
                settings = SettingsReader.parseSettings();
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                exception = ex;
            }
        } else {
            Path settingsFile = ResourceLocator.getFullPath(settingsLocation);

            if (Files.exists(settingsFile)) {
                try {
                    settings = SettingsReader.parseSettings(settingsFile);
                } catch (ParserConfigurationException | SAXException | IOException ex) {
                    exception = ex;
                }
            } else {
                Console.error("The configuration file \"%s\" could not be found at \"%s\".", settingsLocation, settingsFile);
            }
        }
    }

    private static void setLookAndFeel() {
        try {
            if (UIManager.getSystemLookAndFeelClassName().contains("GTK") || UIManager.getSystemLookAndFeelClassName().contains("Motif")) {
                // Running on Linux. The system L&F has issues with font rendering, so reverting to the (nice) cross-platform "Metal" L&F
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            // Unavailable: use default L&F
        }
    }

    private static void notifyForMissingSettings() {
        if (exception == null) {
            JOptionPane.showMessageDialog(null, "No configuration information was found. Please set up your preferences.", "Publy - Launching Settings Window", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "An exception occurred while parsing the configuration. Loading the default configuration.", "Publy - Launching Settings Window", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void applyCommandlineOverwites(CommandLineArguments arguments) {
        // File settings
        if (arguments.getInput() != null && !arguments.getInput().isEmpty()) {
            settings.getFileSettings().setPublications(ResourceLocator.getFullPath(arguments.getInput()));
        }

        if (arguments.getOutput() != null && !arguments.getOutput().isEmpty()) {
            settings.getFileSettings().setTarget(ResourceLocator.getFullPath(arguments.getOutput()));
        }

        // Console settings
        if (arguments.isSilent()) {
            settings.getConsoleSettings().setShowLogs(false);
        }
        
        if (arguments.isHidewarnings()) {
            settings.getConsoleSettings().setShowWarnings(false);
        }
        
        if (arguments.isDebug()) {
            settings.getConsoleSettings().setShowStackTraces(true);
        }
        
        Console.setSettings(settings.getConsoleSettings());
    }

    private static void launchGUI() {
        // Variables need to be final in order to be shared
        final Settings guiSettings = (settings == null ? Settings.defaultSettings() : settings);
        final Throwable guiException = exception;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame mf = new MainFrame(guiSettings);

                // Report an Exception, if one occurred
                if (guiException != null) {
                    Console.except(guiException, "Exception occurred while parsing the configuration:");
                }

                mf.setVisible(true);
            }
        });
    }

    public static void generatePublicationList(Settings settings) {
        // Check if the publication list is set and exists
        Path pubList = settings.getFileSettings().getPublications();

        if (pubList == null) {
            Console.error("No publication list was set.");
        } else if (Files.notExists(pubList)) {
            Console.error("No publication list was found at: %s", pubList);
        } else if (settings.getFileSettings().getTarget() == null) {
            Console.error("No output file was set.");
        } else {
            // Parse all publications
            List<BibItem> items = null;

            try {
                items = BibTeXParser.parseFile(settings.getFileSettings().getPublications());
                Console.log("Publications list \"%s\" parsed successfully.", settings.getFileSettings().getPublications().getFileName());
            } catch (Exception | AssertionError ex) {
                Console.except(ex, "Exception while parsing publications list:");
            }

            if (items != null && settings.getHtmlSettings().generateTextVersion()) {
                try {
                    PublicationListWriter writer = new PlainPublicationListWriter(settings);
                    writer.writePublicationList(items, settings.getFileSettings().getPlainTextTarget());
                    Console.log("Plain text publication list written successfully.");
                } catch (Exception | AssertionError ex) {
                    Console.except(ex, "Exception while writing plain text publication list:");
                }
            }

            if (items != null && settings.getHtmlSettings().generateBibtexVersion()) {
                try {
                    PublicationListWriter writer = new BibtexPublicationListWriter(settings);
                    writer.writePublicationList(items, settings.getFileSettings().getBibtexTarget());
                    Console.log("BibTeX publication list written successfully.");
                } catch (Exception | AssertionError ex) {
                    Console.except(ex, "Exception while writing BibTeX publication list:");
                }
            }

            if (items != null) {
                try {
                    PublicationListWriter writer = new HTMLPublicationListWriter(settings);
                    writer.writePublicationList(items, settings.getFileSettings().getTarget());
                    Console.log("HTML publication list written successfully.");
                } catch (Exception | AssertionError ex) {
                    Console.except(ex, "Exception while writing HTML publication list:");
                }
            }

            Console.log("Done.");
        }
    }
}
