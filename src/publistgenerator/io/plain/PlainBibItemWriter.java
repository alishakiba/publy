/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.plain;

import java.io.BufferedWriter;
import java.io.IOException;
import publistgenerator.bibitem.*;
import publistgenerator.io.BibItemWriter;
import publistgenerator.settings.FormatSettings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class PlainBibItemWriter extends BibItemWriter {

    public PlainBibItemWriter(BufferedWriter out, FormatSettings settings) {
        super(out, settings);
    }

    @Override
    public void write(Article item) throws IOException {
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
    public void write(InProceedings item) throws IOException {
        writePart(item);
    }

    @Override
    public void write(InCollection item) throws IOException {
        writePart(item);
    }

    private void writePart(BibItem item) throws IOException {
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
    public void write(MastersThesis item) throws IOException {
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
    public void write(PhDThesis item) throws IOException {
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
    public void write(InvitedTalk item) throws IOException {
        out.write(formatTitle(item));
        out.write(".");
        out.newLine();

        output(item.get("address"), ", ");
        output(formatDate(item), ".", true);

        output(item.get("note"), ".", true);
    }
    
    @Override
    public void write(Unpublished item) throws IOException {
        writeTitleAndAuthors(item);

        output(item.get("note"), ".", true);
    }

    private void writeTitleAndAuthors(BibItem item) throws IOException {
        out.write(formatTitle(item));
        out.write(".");

        if ("yes".equals(item.get("presented"))) {
            out.write(" (*)");
        }

        out.newLine();
        out.write(formatAuthors(item));
        out.write(".");
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
        if (booktitle.startsWith("Proceedings of ")) {
            booktitle = booktitle.substring("Proceedings of ".length());
        }

        switch (item.get("status")) {
            case "submitted":
                out.write("Submitted to ");
                out.write(booktitle);
                out.write(".");
                out.newLine();
                break;
            case "accepted":
                out.write("Accepted to ");
                out.write(booktitle);
                out.write(".");
                out.newLine();
                break;
            case "acceptedrev":
                out.write("Accepted, pending minor revisions, to ");
                out.write(booktitle);
                out.write(".");
                out.newLine();
                break;
            default:
                throw new InternalError("Unrecognized status: \"" + item.get("status") + "\"");
        }
    }
}
