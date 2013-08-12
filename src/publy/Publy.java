/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publy;

import com.beust.jcommander.JCommander;
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
        // Parse the command line arguments
        CommandLineArguments arguments = new CommandLineArguments();
        JCommander jc = new JCommander(arguments, args);

        if (arguments.isHelp()) {
            jc.setProgramName("Publy");
            jc.usage();
        } else if (arguments.isVersion()) {
            printVersionInfo();
        } else {
            // Apply output settings
            Console.setPrintLog(!arguments.isSilent());
            Console.setPrintWarning(!arguments.isHidewarnings());
            Console.setPrintStacktrace(arguments.isDebug());

            readSettings(arguments.getConfig());
            setLookAndFeel();

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
            Path pubList = settings.getPublications();
            Path target = settings.getGeneralSettings().getTarget();

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

    private static void readSettings(Path settingsLocation) {
        if (settingsLocation == null) {
            try {
                settings = SettingsReader.parseSettings();
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                exception = ex;
            }
        } else {
            if (Files.exists(settingsLocation)) {
                try {
                    settings = SettingsReader.parseSettings(settingsLocation);
                } catch (ParserConfigurationException | SAXException | IOException ex) {
                    exception = ex;
                }
            } else {
                exception = new IOException("The configuration file \"" + settingsLocation.getFileName().toString() + "\" could not be found at the indicated location.");
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
        if (arguments.getInput() != null) {
            settings.setPublications(arguments.getInput());
        }
        
        if (arguments.getOutput() != null) {
            settings.getGeneralSettings().setTarget(arguments.getOutput());
        }
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
        Path pubList = settings.getPublications();

        if (pubList == null) {
            Console.error("No publication list was set.");
        } else if (Files.notExists(pubList)) {
            Console.error("No publication list was found at: %s", pubList);
        } else {
            // Parse all publications
            List<BibItem> items = null;

            try {
                items = BibTeXParser.parseFile(settings.getPublications());
                Console.log("Publications list \"%s\" parsed successfully.", settings.getPublications().getFileName());
            } catch (Exception | AssertionError ex) {
                Console.except(ex, "Exception while parsing publications list:");
            }

            if (items != null && settings.getHtmlSettings().generateTextVersion()) {
                try {
                    PublicationListWriter writer = new PlainPublicationListWriter(settings.getGeneralSettings());
                    writer.writePublicationList(items, settings.getGeneralSettings().getPlainTextTarget());
                    Console.log("Plain text publication list written successfully.");
                } catch (Exception | AssertionError ex) {
                    Console.except(ex, "Exception while writing plain text publication list:");
                }
            }

            if (items != null && settings.getHtmlSettings().generateBibtexVersion()) {
                try {
                    PublicationListWriter writer = new BibtexPublicationListWriter(settings.getGeneralSettings());
                    writer.writePublicationList(items, settings.getGeneralSettings().getBibtexTarget());
                    Console.log("BibTeX publication list written successfully.");
                } catch (Exception | AssertionError ex) {
                    Console.except(ex, "Exception while writing BibTeX publication list:");
                }
            }

            if (items != null) {
                try {
                    PublicationListWriter writer = new HTMLPublicationListWriter(settings.getGeneralSettings(), settings.getHtmlSettings());
                    writer.writePublicationList(items, settings.getGeneralSettings().getTarget());
                    Console.log("HTML publication list written successfully.");
                } catch (Exception | AssertionError ex) {
                    Console.except(ex, "Exception while writing HTML publication list:");
                }
            }

            Console.log("Done.");
        }
    }
}