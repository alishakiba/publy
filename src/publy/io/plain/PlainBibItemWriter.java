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
import publy.data.bibitem.BibItem;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected void writeArticle(BibItem item) throws IOException {
        writeTitleAndAuthors(item);

        // Handle submitted / accepted
        if (item.anyNonEmpty("status")) {
            writeStatus(item, item.get("journal"));
        } else {
            output(item.get("journal"), ", ");

            if (item.anyNonEmpty("volume", "number")) {
                output(item.get("volume"));
                output("(", item.get("number"), ")");
                output(":", formatPages(item, false), "");
                out.write(", ");
            } else {
                output(formatPages(item, false), ", ");
            }

            output(item.get("year"), ".", true);
        }

        output(item.get("note"), ".", true);
    }

    protected void writeBook(BibItem item) throws IOException {
        writeTitleAndAuthors(item);

        output(item.get("publisher"), ", ");
        output(item.get("year"), ".", true);

        output(item.get("note"), ".", true);
    }

    protected void writeInProceedings(BibItem item) throws IOException {
        writePart(item);
    }

    protected void writeInCollection(BibItem item) throws IOException {
        writePart(item);
    }

    private void writePart(BibItem item) throws IOException {
        writeTitleAndAuthors(item);

        if (item.anyNonEmpty("status")) {
            writeStatus(item, item.get("booktitle"));
        } else {
            output("In ", item.get("booktitle"), ", ");

            writeVolume(item, ", ");
            output(formatPages(item, true), ", ");

            output(item.get("year"), ".", true);
        }

        output(item.get("note"), ".", true);
    }

    protected void writeMastersThesis(BibItem item) throws IOException {
        writeTitleAndAuthors(item);

        output("Master's thesis, ", item.get("school"), ", ");
        output(item.get("year"), ".", true);

        output(item.get("note"), ".", true);
    }

    protected void writePhDThesis(BibItem item) throws IOException {
        writeTitleAndAuthors(item);

        output("PhD thesis, ", item.get("school"), ", ");
        output(item.get("year"), ".", true);

        output(item.get("note"), ".", true);
    }

    protected void writeInvitedTalk(BibItem item) throws IOException {
        output(formatTitle(item), ".", true);

        output(item.get("address"), ", ");
        output(formatDate(item), ".", true);

        output(item.get("note"), ".", true);
    }

    protected void writeUnpublished(BibItem item) throws IOException {
        writeTitleAndAuthors(item);

        output(item.get("note"), ".", true);
    }

    private void writeTitleAndAuthors(BibItem item) throws IOException {
        if (settings.getGeneralSettings().titleFirst()) {
            output(formatTitle(item), ".", true);
        }

        // Don't add an authors line if it's just me and I just want to list co-authors
        if (settings.getGeneralSettings().listAllAuthors() || item.getAuthors().size() > 1) {
            String authors = formatAuthors(item);

            if (authors.endsWith(".")) {
                // Don't double up on periods when author names are abbreviated and reversed
                output(authors, "", true);
            } else {
                output(authors, ".", true);
            }
        }

        if (!settings.getGeneralSettings().titleFirst()) {
            output(formatTitle(item), ".", true);
        }
    }

    private void writeVolume(BibItem item, String connective) throws IOException {
        String volume = item.get("volume");
        String series = item.get("series");
        String number = item.get("number");

        if (volume != null && !volume.isEmpty()) {
            output("volume ", volume, "");
            output(" of ", series, "");
            out.write(connective);
        } else if (number != null && !number.isEmpty()) {
            output("number ", number, "");
            output(" in ", series, "");
            out.write(connective);
        } else {
            output(series, connective);
        }
    }

    private void writeStatus(BibItem item, String booktitle) throws IOException {
        String title = booktitle;

        if (title.startsWith("Proceedings of ")) {
            title = title.substring("Proceedings of ".length());
        }

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
                throw new AssertionError("Unrecognized status: \"" + item.get("status") + "\"");
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
