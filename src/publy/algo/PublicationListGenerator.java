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
package publy.algo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import publy.Console;
import publy.data.Section;
import publy.data.bibitem.BibItem;
import publy.data.settings.Settings;
import publy.io.PublicationListWriter;
import publy.io.bibtex.BibtexPublicationListWriter;
import publy.io.bibtexparser.ParseException;
import publy.io.bibtexparser.PublicationListParser;
import publy.io.html.HTMLPublicationListWriter;
import publy.io.plain.PlainPublicationListWriter;

/**
 * Utility class that writes the publication list.
 */
public class PublicationListGenerator {

    /**
     * Parses the publication list, and generates all required versions.
     * <p>
     * If anything goes wrong, helpful error messages are shown to the user (via
     * the {@link Console}). Otherwise, progress messages are shown.
     *
     * @param settings the configuration
     */
    public static void generatePublicationList(Settings settings) {
        Console.debug("Generating publication list.");
        if (checkFileSettings(settings)) {
            List<BibItem> items = parsePublications(settings);

            if (items != null) {
                List<Section> sections = PublicationPostProcessor.postProcess(settings, items);

                writeTextVersion(settings, sections);
                writeBibtexVersion(settings, sections);
                writeHtmlVersion(settings, sections);
            }

            Console.log("Done.");
        }
    }

    /**
     * Verifies that the publication list is specified and exists, and that an
     * output file is specified.
     * <p>
     * If any of these conditions are not met, an error message is shown to the
     * user.
     *
     * @param settings the configuration
     * @return true if all conditions are satisfied, false otherwise
     */
    private static boolean checkFileSettings(Settings settings) {
        Console.debug("Checking file settings.");
        Path pubList = settings.getFileSettings().getPublications();

        if (pubList == null) {
            Console.error("No publication list was set.");
            return false;
        } else if (Files.notExists(pubList)) {
            Console.error("No publication list was found at: %s", pubList);
            return false;
        } else if (settings.getFileSettings().getTarget() == null) {
            Console.error("No output file was set.");
            return false;
        } else {
            // Everything is okay
            Console.debug("File settings okay.");
            return true;
        }
    }

    /**
     * Parses the publication list specified in the settings.
     * <p>
     * If anything goes wrong in this process, the exception is presented to the
     * user.
     *
     * @param settings the configuration
     * @return a list of publications if no problems occurred, null otherwise
     */
    private static List<BibItem> parsePublications(Settings settings) {
        List<BibItem> items = null;

        try {
            items = PublicationListParser.parseFile(settings.getFileSettings().getPublications());
            Console.log("Publications list \"%s\" parsed.", settings.getFileSettings().getPublications().getFileName());
        } catch (IOException | ParseException ex) {
            Console.except(ex, "Exception while parsing publications list:");
        }

        return items;
    }

    /**
     * Writes a text version of the publication list, if required.
     *
     * @param settings the configuration
     * @param sections the publication list sections
     */
    private static void writeTextVersion(Settings settings, List<Section> sections) {
        if (settings.getHtmlSettings().isGenerateTextVersion()) {
            try {
                PublicationListWriter writer = new PlainPublicationListWriter(settings);
                writer.writePublicationList(sections, settings.getFileSettings().getPlainTextTarget());
                Console.log("Plain text publication list written.");
            } catch (Exception | AssertionError ex) {
                Console.except(ex, "Exception while writing plain text publication list:");
            }
        }
    }

    /**
     * Writes a BibTeX version of the publication list, if required.
     *
     * @param settings the configuration
     * @param sections the publication list sections
     */
    private static void writeBibtexVersion(Settings settings, List<Section> sections) {
        if (settings.getHtmlSettings().isGenerateBibtexVersion()) {
            try {
                PublicationListWriter writer = new BibtexPublicationListWriter(settings);
                writer.writePublicationList(sections, settings.getFileSettings().getBibtexTarget());
                Console.log("BibTeX publication list written.");
            } catch (Exception | AssertionError ex) {
                Console.except(ex, "Exception while writing BibTeX publication list:");
            }
        }
    }

    /**
     * Writes an HTML version of the publication list.
     *
     * @param settings the configuration
     * @param sections the publication list sections
     */
    private static void writeHtmlVersion(Settings settings, List<Section> sections) {
        try {
            PublicationListWriter writer = new HTMLPublicationListWriter(settings);
            writer.writePublicationList(sections, settings.getFileSettings().getTarget());
            Console.log("HTML publication list written.");
        } catch (Exception | AssertionError ex) {
            Console.except(ex, "Exception while writing HTML publication list:");
        }
    }

    private PublicationListGenerator() {
    }
}
