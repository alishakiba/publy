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
package publy.io.plain;

import java.io.BufferedWriter;
import java.io.IOException;
import publy.Console;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.Type;
import publy.data.settings.Settings;
import publy.io.BibItemWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class PlainBibItemWriter extends BibItemWriter {

    public PlainBibItemWriter(BufferedWriter out, Settings settings) {
        super(out, settings);
    }

    @Override
    public void write(BibItem item) throws IOException {
        writeTitleAndAuthors(item);

        if (isPresent(item, "pubstate")) {
            writeStatus(item);
        } else {
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
                newline();
            } else {
                output(formatDate(item), ".", true);
            }
        }

        // Write note (unpublished uses note as the publication info)
        if (item.getType() != Type.UNPUBLISHED) {
            output(get(item, "note"), ".", true);
        }
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

    private void writeTitleAndAuthors(BibItem item) throws IOException {
        if (settings.getGeneralSettings().isTitleFirst()) {
            writeTitle(item);
        }

        writeAuthors(item);

        if (!settings.getGeneralSettings().isTitleFirst()) {
            writeTitle(item);
        }
    }

    private void writeTitle(BibItem item) throws IOException {
        output(formatTitle(item));

        if (item.getType() == Type.INBOOK && !isPresent(item, "volume")) {
            if (isPresent(item, "chapter")) {
                out.write(", ");
                writeChapter(item, false);
            }

            output(", ", formatPages(item, true), "");
        }

        output(".", true);
    }

    private void writeAuthors(BibItem item) throws IOException {
        boolean useEditor;

        if (item.getType() == Type.PROCEEDINGS) {
            // Proceedings never prints the author
            if (isPresent(item, "editor")) {
                useEditor = true;
            } else if (isPresent(item, "organization")) {
                output(get(item, "organization"), ".", true);
                return;
            } else {
                Console.error("No editor or organization found for entry \"%s\".", item.getId());
                return;
            }
        } else {
            if (isPresent(item, "author")) {
                useEditor = false;
            } else if (isPresent(item, "editor")) {
                useEditor = true;
            } else {
                Console.error("No author information found for entry \"%s\".", item.getId());
                return;
            }
        }

        // Don't add an authors line if it's just me and I just want to list co-authors
        if (settings.getGeneralSettings().isListAllAuthors()
                || item.getAuthors().size() > 1
                || (item.getAuthors().size() == 1 && !item.getAuthors().get(0).isMe(settings.getGeneralSettings()))) {
            String authors = formatAuthors(item, useEditor, false);

            if (authors.endsWith(".")) {
                // Don't double up on periods when author names are abbreviated and reversed
                output(authors, "", true);
            } else {
                output(authors, ".", true);
            }
        }
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

    private void writeStatus(BibItem item) throws IOException {
        String venue;

        switch (item.getType()) {
            case ARTICLE:
                venue = get(item, "journal");
                break;
            case INPROCEEDINGS:
                venue = get(item, "booktitle");

                if (venue.startsWith("Proceedings of ")) {
                    venue = venue.substring("Proceedings of ".length());
                }

                break;
            default:
                venue = null;
                break;
        }

        if (venue == null) {
            switch (get(item, "pubstate")) {
                case "inpreparation":
                    output("In preparation.", true);
                    break;
                case "submitted":
                    output("Submitted for review.", true);
                    break;
                case "acceptedrev":
                    output("Accepted for publication, pending minor revisions.", true);
                    break;
                case "accepted":
                    output("Accepted for publication.", true);
                    break;
                case "forthcoming":
                    output("Forthcoming.", true);
                    break;
                case "inpress":
                    output("In press.", true);
                    break;
                case "prepublished":
                    output("Pre-published.", true);
                    break;
                default:
                    throw new AssertionError("Item \"" + item.getId() + "\" has an unrecognized pubstate: \"" + get(item, "pubstate") + "\"");
            }
        } else {
            switch (get(item, "pubstate")) {
                case "inpreparation":
                    output("In preparation for submission to ", false);
                    break;
                case "submitted":
                    output("Submitted to ", false);
                    break;
                case "acceptedrev":
                    output("Accepted, pending minor revisions, to ", false);
                    break;
                case "accepted":
                    output("Accepted to ", false);
                    break;
                case "forthcoming":
                    output("Forthcoming in ", false);
                    break;
                case "inpress":
                    output("In press with ", false);
                    break;
                case "prepublished":
                    output("Pre-published in ", false);
                    break;
                default:
                    throw new AssertionError("Item \"" + item.getId() + "\" has an unrecognized pubstate: \"" + get(item, "pubstate") + "\"");
            }
            
            output(venue, ".", true);
        }
    }

    protected String formatPages(BibItem item, boolean verbose) {
        String pages = (verbose ? super.formatPages(item) : get(item, "pages"));

        if (pages == null) {
            pages = "";
        }

        return pages.replaceAll("-+", "-");
    }
}
