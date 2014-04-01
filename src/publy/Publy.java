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
package publy;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import publy.algo.PublicationPostProcessor;
import publy.data.bibitem.BibItem;
import publy.data.category.OutputCategory;
import publy.data.settings.Settings;
import publy.gui.UIConstants;
import publy.io.BibTeXParser;
import publy.io.PublicationListWriter;
import publy.io.bibtex.BibtexPublicationListWriter;
import publy.io.html.HTMLPublicationListWriter;
import publy.io.plain.PlainPublicationListWriter;

/**
 *
 *
 */
public class Publy {

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
            if (arguments.isGui()) {
                Runner.runWithGUI(arguments);
            } else if (System.console() == null) {
                Runner.runInMixedMode(arguments);
            } else {
                Runner.runOnCommandLine(arguments);
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

    private static void printVersionInfo() {
        System.out.printf("Publy %d.%d%n"
                + "Copyright (c) 2013-2014 Sander Verdonschot%n"
                + "License Apache v2%n"
                + "This is free software. You are free to change and redistribute it.",
                UIConstants.MAJOR_VERSION, UIConstants.MINOR_VERSION);
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
                Console.log("Publications list \"%s\" parsed.", settings.getFileSettings().getPublications().getFileName());
            } catch (Exception | AssertionError ex) {
                Console.except(ex, "Exception while parsing publications list:");
            }

            if (items != null) {
                List<OutputCategory> categories = PublicationPostProcessor.postProcess(settings, items);

                if (settings.getHtmlSettings().isGenerateTextVersion()) {
                    try {
                        PublicationListWriter writer = new PlainPublicationListWriter(settings);
                        writer.writePublicationList(categories, settings.getFileSettings().getPlainTextTarget());
                        Console.log("Plain text publication list written.");
                    } catch (Exception | AssertionError ex) {
                        Console.except(ex, "Exception while writing plain text publication list:");
                    }
                }

                if (settings.getHtmlSettings().isGenerateBibtexVersion()) {
                    try {
                        PublicationListWriter writer = new BibtexPublicationListWriter(settings);
                        writer.writePublicationList(categories, settings.getFileSettings().getBibtexTarget());
                        Console.log("BibTeX publication list written.");
                    } catch (Exception | AssertionError ex) {
                        Console.except(ex, "Exception while writing BibTeX publication list:");
                    }
                }

                try {
                    PublicationListWriter writer = new HTMLPublicationListWriter(settings);
                    writer.writePublicationList(categories, settings.getFileSettings().getTarget());
                    Console.log("HTML publication list written.");
                } catch (Exception | AssertionError ex) {
                    Console.except(ex, "Exception while writing HTML publication list:");
                }
            }

            Console.log("Done.");
        }
    }
}
