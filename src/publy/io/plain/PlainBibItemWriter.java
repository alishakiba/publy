/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publy.io.plain;

import java.io.BufferedWriter;
import java.io.IOException;
import publy.data.bibitem.Article;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.Book;
import publy.data.bibitem.InCollection;
import publy.data.bibitem.InProceedings;
import publy.data.bibitem.InvitedTalk;
import publy.data.bibitem.MastersThesis;
import publy.data.bibitem.PhDThesis;
import publy.data.bibitem.Unpublished;
import publy.data.settings.FormatSettings;
import publy.io.BibItemWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class PlainBibItemWriter extends BibItemWriter {

    public PlainBibItemWriter(BufferedWriter out, FormatSettings settings) {
        super(out, settings);
    }

    @Override
    protected void writeArticle(Article item, int number) throws IOException {
        writeNumber(number);
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

    @Override
    protected void writeBook(Book item, int number) throws IOException {
        writeNumber(number);
        writeTitleAndAuthors(item);

        output(item.get("publisher"), ", ");
        output(item.get("year"), ".", true);

        output(item.get("note"), ".", true);
    }

    @Override
    protected void writeInProceedings(InProceedings item, int number) throws IOException {
        writePart(item, number);
    }

    @Override
    protected void writeInCollection(InCollection item, int number) throws IOException {
        writePart(item, number);
    }

    private void writePart(BibItem item, int number) throws IOException {
        writeNumber(number);
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

    @Override
    protected void writeMastersThesis(MastersThesis item, int number) throws IOException {
        writeNumber(number);
        writeTitleAndAuthors(item);

        output("Master's thesis, ", item.get("school"), ", ");
        output(item.get("year"), ".", true);

        output(item.get("note"), ".", true);
    }

    @Override
    protected void writePhDThesis(PhDThesis item, int number) throws IOException {
        writeNumber(number);
        writeTitleAndAuthors(item);

        output("PhD thesis, ", item.get("school"), ", ");
        output(item.get("year"), ".", true);

        output(item.get("note"), ".", true);
    }

    @Override
    protected void writeInvitedTalk(InvitedTalk item, int number) throws IOException {
        writeNumber(number);
        output(formatTitle(item), ".", true);

        output(item.get("address"), ", ");
        output(formatDate(item), ".", true);

        output(item.get("note"), ".", true);
    }

    @Override
    protected void writeUnpublished(Unpublished item, int number) throws IOException {
        writeNumber(number);
        writeTitleAndAuthors(item);

        output(item.get("note"), ".", true);
    }

    private void writeNumber(int number) throws IOException {
        if (number >= 0) {
            out.write(Integer.toString(number));
            out.write(". ");
        }
    }

    private void writeTitleAndAuthors(BibItem item) throws IOException {
        if (settings.isTitleFirst()) {
            output(formatTitle(item), ".", true);
        }

        // Don't add an authors line if it's just me and I just want to list co-authors
        if (settings.isListAllAuthors() || item.getAuthors().size() > 1) {
            output(formatAuthors(item), ".", true);
        }

        if (!settings.isTitleFirst()) {
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
