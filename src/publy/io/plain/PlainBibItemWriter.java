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
package publy.io.plain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;
import publy.Console;
import publy.data.Author;
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
    public void write(BibItem item, Set<String> ignoredFields) throws IOException {
        writeTitleAndAuthors(item);

        if (item.anyNonEmpty("status")) {
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

            if ((item.getType() == Type.PROCEEDINGS || item.getType() == Type.INPROCEEDINGS) && item.anyNonEmpty("address")) {
                // The year has already been written
                out.newLine();
            } else {
                output(formatDate(item), ".", true);
            }
        }

        // Write note (unpublished uses note as the publication info)
        if (item.getType() != Type.UNPUBLISHED) {
            output(item.get("note"), ".", true);
        }
    }

    protected void writeArticle(BibItem item) throws IOException {
        output(item.get("journal"), ", ");

        if (item.anyNonEmpty("volume", "number")) {
            output(item.get("volume"));
            output("(", item.get("number"), ")");
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
        if (item.anyNonEmpty("volume") && item.anyNonEmpty("chapter", "pages")) {
            writeVolume(item, true, "");

            if (item.anyNonEmpty("chapter")) {
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
        output(item.get("howpublished"), ", ");
        output(item.get("address"), ", ");
    }

    protected void writeInCollection(BibItem item) throws IOException {
        out.write("In ");

        if (item.anyNonEmpty("editor")) {
            output(formatAuthors(item, true, Author.NameOutputType.PLAINTEXT), ", ");
        }

        output(item.get("booktitle"));

        if (item.anyNonEmpty("volume", "series", "number")) {
            out.write(", ");
            writeVolume(item, false, "");
        }

        if (item.anyNonEmpty("chapter")) {
            out.write(", ");
            writeChapter(item, false);
        }

        output(", ", formatPages(item, true), "");
        out.write(". ");

        writePublisherAndEdition(item);
    }

    protected void writeManual(BibItem item) throws IOException {
        output(item.get("organization"), ", ");
        output(item.get("address"), ", ");

        String edition = item.get("edition");

        if (edition != null && !edition.isEmpty()) {
            if (item.anyNonEmpty("organization", "address")) {
                output(toLowerCase(edition), " edition, ");
            } else {
                output(toTitleCase(edition), " edition, ");
            }
        }
    }

    protected void writeMisc(BibItem item) throws IOException {
        output(item.get("howpublished"), ", ");
        output(item.get("address"), ", ");
    }

    protected void writeOnline(BibItem item) throws IOException {
        // No publication information necessary
    }

    protected void writePatent(BibItem item) throws IOException {
        // TODO
    }

    protected void writeProceedings(BibItem item) throws IOException {
        if (!item.anyNonEmpty("address") && (item.anyNonEmpty("publisher") || (item.anyNonEmpty("editor") && item.anyNonEmpty("organization")))) {
            writeVolume(item, true, ". ");
        } else {
            writeVolume(item, true, ", ");
        }

        if (item.anyNonEmpty("address")) {
            output(item.get("address"), ", ");
            output(formatDate(item), ". ");

            if (item.anyNonEmpty("editor")) {
                if (item.anyNonEmpty("publisher")) {
                    output(item.get("organization"), ", ");
                } else {
                    output(item.get("organization"), ".");
                }
            }

            output(item.get("publisher"), ".");
        } else {
            if (item.anyNonEmpty("editor")) {
                output(item.get("organization"), ", ");
            }

            output(item.get("publisher"), ", ");
        }
    }

    protected void writeInProceedings(BibItem item) throws IOException {
        out.write("In ");

        output(formatAuthors(item, true, Author.NameOutputType.PLAINTEXT), ", ");

        output(item.get("booktitle"));

        if (item.anyNonEmpty("volume", "number", "series")) {
            out.write(", ");
            writeVolume(item, false, "");
        }

        output(", ", formatPages(item, true), "");

        if (!item.anyNonEmpty("address") && item.anyNonEmpty("publisher", "organization")) {
            out.write(". ");
        } else {
            out.write(", ");
        }

        if (item.anyNonEmpty("address")) {
            output(item.get("address"), ", ");
            output(formatDate(item), ". ");

            if (item.anyNonEmpty("publisher")) {
                output(item.get("organization"), ", ");
            } else {
                output(item.get("organization"), ".");
            }

            output(item.get("publisher"), ".");
        } else {
            output(item.get("organization"), ", ");
            output(item.get("publisher"), ", ");
        }
    }

    protected void writeReport(BibItem item) throws IOException {
        output(toTitleCase(item.get("type")));
        output(" ", item.get("number"), "");
        out.write(", ");

        output(item.get("institution"), ", ");
        output(item.get("address"), ", ");
    }

    protected void writeThesis(BibItem item) throws IOException {
        output(item.get("type"), ", ");
        output(item.get("school"), ", ");
        output(item.get("address"), ", ");
    }

    protected void writeUnpublished(BibItem item) throws IOException {
        output(item.get("note"), ", ");
    }

    private void writeTitleAndAuthors(BibItem item) throws IOException {
        if (settings.getGeneralSettings().titleFirst()) {
            writeTitle(item);
        }

        writeAuthors(item);

        if (!settings.getGeneralSettings().titleFirst()) {
            writeTitle(item);
        }
    }

    private void writeTitle(BibItem item) throws IOException {
        output(formatTitle(item));

        if (item.getType() == Type.INBOOK && !item.anyNonEmpty("volume")) {
            if (item.anyNonEmpty("chapter")) {
                out.write(", ");
                writeChapter(item, false);
            }

            output(", ", formatPages(item, true), "");
        }

        out.write(".");
        out.newLine();
    }

    private void writeAuthors(BibItem item) throws IOException {
        boolean useEditor;

        if (item.getType() == Type.PROCEEDINGS) {
            // Proceedings never prints the author
            if (item.anyNonEmpty("editor")) {
                useEditor = true;
            } else if (item.anyNonEmpty("organization")) {
                output(item.get("organization"), ".", true);
                return;
            } else {
                Console.error("No editor or organization found for entry \"%s\".", item.getId());
                return;
            }
        } else {
            if (item.anyNonEmpty("author")) {
                useEditor = false;
            } else if (item.anyNonEmpty("editor")) {
                useEditor = true;
            } else {
                Console.error("No author information found for entry \"%s\".", item.getId());
                return;
            }
        }

        // Don't add an authors line if it's just me and I just want to list co-authors
        if (settings.getGeneralSettings().listAllAuthors() || item.getAuthors().size() > 1) {
            String authors = formatAuthors(item, useEditor, Author.NameOutputType.PLAINTEXT);

            if (authors.endsWith(".")) {
                // Don't double up on periods when author names are abbreviated and reversed
                output(authors, "", true);
            } else {
                output(authors, ".", true);
            }
        }
    }

    private void writeVolume(BibItem item, boolean capitalize, String connective) throws IOException {
        String volume = item.get("volume");
        String series = item.get("series");
        String number = item.get("number");

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
        output(item.get("publisher"), ", ");
        output(item.get("address"), ", ");

        if (item.anyNonEmpty("publisher", "address")) {
            output(toLowerCase(item.get("edition")), " edition, ");
        } else {
            output(toTitleCase(item.get("edition")), " edition, ");
        }
    }

    protected void writeChapter(BibItem item, boolean capitalize) throws IOException {
        String type = item.get("type");
        String chapter = item.get("chapter");

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
        String title;

        switch (item.getType()) {
            case ARTICLE:
                title = item.get("journal");
                break;
            case INPROCEEDINGS:
                title = item.get("booktitle");

                if (title.startsWith("Proceedings of ")) {
                    title = title.substring("Proceedings of ".length());
                }

                break;
            default:
                title = null;
                break;
        }

        if (title == null) {
            switch (item.get("status")) {
                case "submitted":
                    output("Submitted for review.", true);
                    break;
                case "accepted":
                    output("Accepted for publication.", true);
                    break;
                case "acceptedrev":
                    output("Accepted for publication, pending minor revisions.", true);
                    break;
                default:
                    throw new AssertionError("Item \"" + item.getId() + "\" has an unrecognized status: \"" + item.get("status") + "\"");
            }
        } else {
            switch (item.get("status")) {
                case "submitted":
                    output("Submitted to ", title, ".", true);
                    break;
                case "accepted":
                    output("Accepted to ", title, ".", true);
                    break;
                case "acceptedrev":
                    output("Accepted, pending minor revisions, to ", title, ".", true);
                    break;
                default:
                    throw new AssertionError("Item \"" + item.getId() + "\" has an unrecognized status: \"" + item.get("status") + "\"");
            }
        }
    }

    protected String formatPages(BibItem item, boolean verbose) {
        String pages = (verbose ? super.formatPages(item) : item.get("pages"));

        if (pages == null) {
            pages = "";
        }

        return pages.replaceAll("-+", "-");
    }
}
