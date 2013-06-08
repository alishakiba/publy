/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.plain;

import java.io.BufferedWriter;
import java.io.IOException;
import publistgenerator.data.bibitem.Article;
import publistgenerator.data.bibitem.BibItem;
import publistgenerator.data.bibitem.Book;
import publistgenerator.data.bibitem.InCollection;
import publistgenerator.data.bibitem.InProceedings;
import publistgenerator.data.bibitem.InvitedTalk;
import publistgenerator.data.bibitem.MastersThesis;
import publistgenerator.data.bibitem.PhDThesis;
import publistgenerator.data.bibitem.Unpublished;
import publistgenerator.data.settings.FormatSettings;
import publistgenerator.io.BibItemWriter;

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
            out.write(item.get("journal"));
            out.write(", ");

            output(item.get("volume"));

            if (item.anyNonEmpty("number")) {
                out.write("(");
                out.write(item.get("number"));
                out.write(")");
            }

            if (item.anyNonEmpty("pages")) {
                if (item.anyNonEmpty("volume", "number")) {
                    out.write(":");
                    out.write(item.get("pages").replaceAll("-+", "-"));
                    out.write(", ");
                } else {
                    output(formatPages(item).replaceAll("-+", "-"), ", ");
                }
            }

            out.write(item.get("year"));
            out.write(".");
            out.newLine();
        }

        output(item.get("note"), ".", true);
    }

    @Override
    protected void writeBook(Book item, int number) throws IOException {
        writeTitleAndAuthors(item);
        
        out.write(item.get("publisher"));
        out.write(", ");
        out.write(item.get("year"));
        out.write(".");
        out.newLine();
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
            out.write("In ");
            out.write(item.get("booktitle"));
            out.write(", ");

            writeVolume(item, ", ");
            output(formatPages(item).replaceAll("-+", "-"), ", ");

            out.write(item.get("year"));
            out.write(".");
            out.newLine();
        }

        output(item.get("note"), ".", true);
    }

    @Override
    protected void writeMastersThesis(MastersThesis item, int number) throws IOException {
        writeNumber(number);
        writeTitleAndAuthors(item);

        out.write("Master's thesis, ");
        out.write(item.get("school"));
        out.write(", ");
        out.write(item.get("year"));
        out.write(".");
        out.newLine();

        output(item.get("note"), ".", true);
    }

    @Override
    protected void writePhDThesis(PhDThesis item, int number) throws IOException {
        writeNumber(number);
        writeTitleAndAuthors(item);

        out.write("PhD thesis, ");
        out.write(item.get("school"));
        out.write(", ");
        out.write(item.get("year"));
        out.write(".");
        out.newLine();

        output(item.get("note"), ".", true);
    }

    @Override
    protected void writeInvitedTalk(InvitedTalk item, int number) throws IOException {
        writeNumber(number);
        out.write(formatTitle(item));
        out.write(".");
        out.newLine();

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
        out.write(formatTitle(item));
        out.write(".");

        if ("yes".equals(item.get("presented")) && settings.getPresentedText() != null && !settings.getPresentedText().isEmpty()) {
            out.write(" ");
            out.write(settings.getPresentedText());
        }

        // Don't add an authors line if it's just me and I just want to list co-authors
        if (settings.isListAllAuthors() || item.getAuthors().size() > 1) {
            out.newLine();
            out.write(formatAuthors(item));
            out.write(".");
        }
        
        out.newLine();
    }

    private void writeVolume(BibItem item, String connective) throws IOException {
        String volume = item.get("volume");
        String series = item.get("series");
        String number = item.get("number");

        if (volume != null && !volume.isEmpty()) {
            out.write("volume ");
            out.write(volume);

            if (series != null && !series.isEmpty()) {
                out.write(" of ");
                out.write(series);
            }

            out.write(connective);
        } else if (number != null && !number.isEmpty()) {
            out.write("number ");
            out.write(number);

            if (series != null && !series.isEmpty()) {
                out.write(" in ");
                out.write(series);
            }

            out.write(connective);
        } else if (series != null && !series.isEmpty()) {
            out.write(series);
            out.write(connective);
        }
    }

    private void writeStatus(BibItem item, String booktitle) throws IOException {
        String title = booktitle;

        if (title.startsWith("Proceedings of ")) {
            title = title.substring("Proceedings of ".length());
        }

        switch (item.get("status")) {
            case "submitted":
                out.write("Submitted to ");
                out.write(title);
                out.write(".");
                out.newLine();
                break;
            case "accepted":
                out.write("Accepted to ");
                out.write(title);
                out.write(".");
                out.newLine();
                break;
            case "acceptedrev":
                out.write("Accepted, pending minor revisions, to ");
                out.write(title);
                out.write(".");
                out.newLine();
                break;
            default:
                throw new AssertionError("Unrecognized status: \"" + item.get("status") + "\"");
        }
    }
}
