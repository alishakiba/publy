/*
 * Copyright 2018 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.io.latex;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import publy.Console;
import publy.data.Author;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.Type;
import publy.data.settings.Settings;
import publy.io.BibItemWriter;

public class LatexBibItemWriter extends BibItemWriter {

    public LatexBibItemWriter(BufferedWriter out, Settings settings) {
        super(out, settings);
    }

    @Override
    public void write(BibItem item) throws IOException {
        writeTitleAndAuthors(item);
        writePublicationInfo(item);
        writeNote(item);
    }

    private void writeTitleAndAuthors(BibItem item) throws IOException {
        if (settings.getGeneralSettings().isTitleFirst()) {
            writeTitle(item);
            writeAuthors(item);
        } else {
            writeAuthors(item);
            writeTitle(item);
        }
    }

    private void writePublicationInfo(BibItem item) throws IOException {
        if (isPresent(item, "pubstate")) {
            writeStatus(item);
        } else {
            writeSpecificPublicationInfo(item);
        }
    }

    private void writeNote(BibItem item) throws IOException {
        if (item.getType() == Type.UNPUBLISHED) {
            return; // Unpublished writes note with the publication info
        }

        output("\\publyNote{", get(item, "note"), ".}", true);
    }

    private void writeSpecificPublicationInfo(BibItem item) throws IOException {
        output("\\publyInfo{", false);
        
        switch (item.getType()) {
            case ARTICLE:
                writeArticle(item);
                break;
            case BOOK:
                writeBook(item);
                break;
            case INBOOK:
                writeInBook(item);
                break;
            case BOOKLET:
                writeBooklet(item);
                break;
            case INCOLLECTION:
                writeInCollection(item);
                break;
            case MANUAL:
                writeManual(item);
                break;
            case MISC:
                writeMisc(item);
                break;
            case ONLINE:
                writeOnline(item);
                break;
            case PATENT:
                writePatent(item);
                break;
            case PROCEEDINGS:
                writeProceedings(item);
                break;
            case INPROCEEDINGS:
                writeInProceedings(item);
                break;
            case REPORT:
                writeReport(item);
                break;
            case THESIS:
                writeThesis(item);
                break;
            case UNPUBLISHED:
                writeUnpublished(item);
                break;
            default:
                throw new AssertionError("Item \"" + item.getId() + "\" has an unexpected publication type: " + item.getType());
        }

        if ((item.getType() == Type.PROCEEDINGS || item.getType() == Type.INPROCEEDINGS) && isPresent(item, "address")) {
            // The year has already been written
            output("}", true);
        } else {
            output(formatDate(item), ".}", true);
        }
    }

    private void writeTitle(BibItem item) throws IOException {
        output("\\publyTitle{", formatTitle(item), "");

        if (item.getType() == Type.INBOOK && !isPresent(item, "volume")) {
            if (isPresent(item, "chapter")) {
                out.write(", ");
                writeChapter(item, false);
            }

            output(", ", formatPages(item, true), "");
        }

        output(".}", true);
    }

    private void writeAuthors(BibItem item) throws IOException {
        if (item.getType() == Type.PROCEEDINGS && !anyPresent(item, "editor", "organization")) {
            Console.error("No editor or organization found for @proceedings entry \"%s\".", item.getId());
            return;
        }
        if (item.getType() != Type.PROCEEDINGS && !anyPresent(item, "author", "editor")) {
            Console.error("No author information found for entry \"%s\".", item.getId());
            return;
        }

        String authors;

        if (item.getType() == Type.PROCEEDINGS && isPresent(item, "organization")) {
            authors = get(item, "organization");
        } else {
            // Proceedings never prints the author
            boolean useEditorInsteadOfAuthor = item.getType() == Type.PROCEEDINGS || !isPresent(item, "author");

            // Don't add an authors line if it's just me and I just want to list co-authors
            List<Author> authorList = (useEditorInsteadOfAuthor ? item.getEditors() : item.getAuthors());
            if (!settings.getGeneralSettings().isListAllAuthors()
                    && authorList.size() == 1
                    && authorList.get(0).isMe(settings.getGeneralSettings())) {
                return;
            }

            authors = formatAuthors(item, useEditorInsteadOfAuthor, false);
        }

        String connective = (authors.endsWith(".") ? "" : "."); // Don't double up on periods when author names are abbreviated and reversed
        output("\\publyAuthors{", authors, connective + '}', true);
    }

    protected void writeArticle(BibItem item) throws IOException {
        output(get(item, "journal"), ", ");

        if (anyPresent(item, "volume", "number")) {
            output(get(item, "volume"));
            output("(", get(item, "number"), ")");
            output(":", formatPages(item, false), "");
            out.write(", ");
        } else {
            output(formatPages(item, true), ", ");
        }
    }

    protected void writeBook(BibItem item) throws IOException {
        writeVolume(item, true, ". ");
        writePublisherAndEdition(item);
    }

    protected void writeInBook(BibItem item) throws IOException {
        // Only add chapter and pages here if volume is present
        if (isPresent(item, "volume") && anyPresent(item, "chapter", "pages")) {
            writeVolume(item, true, "");

            if (isPresent(item, "chapter")) {
                out.write(", ");
                writeChapter(item, false);
            }

            output(", ", formatPages(item, true), "");
            out.write(". ");
        } else {
            writeVolume(item, true, ". ");
        }

        writePublisherAndEdition(item);
    }

    protected void writeBooklet(BibItem item) throws IOException {
        output(get(item, "howpublished"), ", ");
        output(get(item, "address"), ", ");
    }

    protected void writeInCollection(BibItem item) throws IOException {
        out.write("In ");

        if (isPresent(item, "editor")) {
            output(formatAuthors(item, true, false), ", ");
        }

        output(get(item, "booktitle"));

        if (anyPresent(item, "volume", "series", "number")) {
            out.write(", ");
            writeVolume(item, false, "");
        }

        if (isPresent(item, "chapter")) {
            out.write(", ");
            writeChapter(item, false);
        }

        output(", ", formatPages(item, true), "");
        out.write(". ");

        writePublisherAndEdition(item);
    }

    protected void writeManual(BibItem item) throws IOException {
        output(get(item, "organization"), ", ");
        output(get(item, "address"), ", ");

        String edition = get(item, "edition");

        if (edition != null && !edition.isEmpty()) {
            if (anyPresent(item, "organization", "address")) {
                output(toLowerCase(edition), " edition, ");
            } else {
                output(toTitleCase(edition), " edition, ");
            }
        }
    }

    protected void writeMisc(BibItem item) throws IOException {
        output(get(item, "howpublished"), ", ");
        output(get(item, "address"), ", ");
    }

    protected void writeOnline(BibItem item) throws IOException {
        // No publication information necessary
    }

    protected void writePatent(BibItem item) throws IOException {
        // TODO
    }

    protected void writeProceedings(BibItem item) throws IOException {
        if (!isPresent(item, "address") && (isPresent(item, "publisher") || (isPresent(item, "editor") && isPresent(item, "organization")))) {
            writeVolume(item, true, ". ");
        } else {
            writeVolume(item, true, ", ");
        }

        if (isPresent(item, "address")) {
            output(get(item, "address"), ", ");
            output(formatDate(item), ". ");

            if (isPresent(item, "editor")) {
                if (isPresent(item, "publisher")) {
                    output(get(item, "organization"), ", ");
                } else {
                    output(get(item, "organization"), ".");
                }
            }

            output(get(item, "publisher"), ".");
        } else {
            if (isPresent(item, "editor")) {
                output(get(item, "organization"), ", ");
            }

            output(get(item, "publisher"), ", ");
        }
    }

    protected void writeInProceedings(BibItem item) throws IOException {
        out.write("In ");

        if (isPresent(item, "editor")) {
            output(formatAuthors(item, true, false), ", ");
        }

        output(get(item, "booktitle"));

        if (anyPresent(item, "volume", "number", "series")) {
            out.write(", ");
            writeVolume(item, false, "");
        }

        output(", ", formatPages(item, true), "");

        if (!isPresent(item, "address") && anyPresent(item, "publisher", "organization")) {
            out.write(". ");
        } else {
            out.write(", ");
        }

        if (isPresent(item, "address")) {
            output(get(item, "address"), ", ");
            output(formatDate(item), ". ");

            if (isPresent(item, "publisher")) {
                output(get(item, "organization"), ", ");
                output(get(item, "publisher"), ".");
            } else {
                output(get(item, "organization"), ".");
            }
        } else {
            output(get(item, "organization"), ", ");
            output(get(item, "publisher"), ", ");
        }
    }

    protected void writeReport(BibItem item) throws IOException {
        output(toTitleCase(get(item, "type")));
        output(" ", get(item, "number"), "");
        out.write(", ");

        output(get(item, "institution"), ", ");
        output(get(item, "address"), ", ");
    }

    protected void writeThesis(BibItem item) throws IOException {
        output(get(item, "type"), ", ");
        output(get(item, "school"), ", ");
        output(get(item, "address"), ", ");
    }

    protected void writeUnpublished(BibItem item) throws IOException {
        output(get(item, "note"), ", ");
    }

    private void writeVolume(BibItem item, boolean capitalize, String connective) throws IOException {
        String volume = get(item, "volume");
        String series = get(item, "series");
        String number = get(item, "number");

        if (volume != null && !volume.isEmpty()) {
            output((capitalize ? "Volume " : "volume "), volume, "");
            output(" of ", series, "");
            out.write(connective);
        } else if (number != null && !number.isEmpty()) {
            output((capitalize ? "Number " : "number "), number, "");
            output(" in ", series, "");
            out.write(connective);
        } else {
            output(series, connective);
        }
    }

    protected void writePublisherAndEdition(BibItem item) throws IOException {
        output(get(item, "publisher"), ", ");
        output(get(item, "address"), ", ");

        if (anyPresent(item, "publisher", "address")) {
            output(toLowerCase(get(item, "edition")), " edition, ");
        } else {
            output(toTitleCase(get(item, "edition")), " edition, ");
        }
    }

    protected void writeChapter(BibItem item, boolean capitalize) throws IOException {
        String type = get(item, "type");
        String chapter = get(item, "chapter");

        if (chapter != null && !chapter.isEmpty()) {
            if (type != null && !type.isEmpty()) {
                output(capitalize ? toTitleCase(type) : toLowerCase(type));
            } else {
                out.write(capitalize ? "Chapter" : "chapter");
            }

            output(" ", chapter, "");
        }
    }

    private static final Map<String, String> NO_VENUE_STATUS = new HashMap<>();
    private static final Map<String, String> VENUE_STATUS = new HashMap<>();

    static {
        NO_VENUE_STATUS.put("inpreparation", "In preparation");
        NO_VENUE_STATUS.put("submitted", "Submitted for review");
        NO_VENUE_STATUS.put("acceptedrev", "Accepted for publication, pending minor revisions");
        NO_VENUE_STATUS.put("accepted", "Accepted for publication");
        NO_VENUE_STATUS.put("forthcoming", "Forthcoming");
        NO_VENUE_STATUS.put("inpress", "In press");
        NO_VENUE_STATUS.put("prepublished", "Pre-published");

        VENUE_STATUS.put("inpreparation", "In preparation for submission to ");
        VENUE_STATUS.put("submitted", "Submitted to ");
        VENUE_STATUS.put("acceptedrev", "Accepted, pending minor revisions, to ");
        VENUE_STATUS.put("accepted", "Accepted to ");
        VENUE_STATUS.put("forthcoming", "Forthcoming in ");
        VENUE_STATUS.put("inpress", "In press with ");
        VENUE_STATUS.put("prepublished", "Pre-published in ");
    }

    private void writeStatus(BibItem item) throws IOException {
        String pubstate = get(item, "pubstate");
        String venue = extractVenue(item);
        String status = (venue == null ? NO_VENUE_STATUS.get(pubstate) : VENUE_STATUS.get(pubstate));
        
        if (status == null) {
            Console.error("Item \"%s\" has an unrecognized pubstate: \"%s\".", item.getId(), pubstate);
            return;
        }
        
        if (venue != null) {
            status += venue;
        }
        
        output("\\publyInfo{", status, ".}", true);
    }

    private String extractVenue(BibItem item) {
        switch (item.getType()) {
            case ARTICLE:
                return get(item, "journal");
            case INPROCEEDINGS:
                String venue = get(item, "booktitle");

                if (venue.startsWith("Proceedings of ")) {
                    venue = venue.substring("Proceedings of ".length());
                }

                return venue;
            default:
                return null;
        }
    }

    protected String formatPages(BibItem item, boolean verbose) {
        String pages = (verbose ? super.formatPages(item) : get(item, "pages"));

        if (pages == null) {
            pages = "";
        }

        return pages.replaceAll("-+", "-");
    }

    @Override
    protected String processString(String string) {
        return string; // Since we want to output LaTeX, we don't need to do any special processing here
    }
}
