/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.plain;

import java.io.BufferedWriter;
import java.io.IOException;
import publistgenerator.data.bibitem.Article;
import publistgenerator.data.bibitem.BibItem;
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
    public void write(Article item, int number) throws IOException {
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
    public void write(InProceedings item, int number) throws IOException {
        writePart(item, number);
    }

    @Override
    public void write(InCollection item, int number) throws IOException {
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
    public void write(MastersThesis item, int number) throws IOException {
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
    public void write(PhDThesis item, int number) throws IOException {
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
    public void write(InvitedTalk item, int number) throws IOException {
        writeNumber(number);
        out.write(formatTitle(item));
        out.write(".");
        out.newLine();

        output(item.get("address"), ", ");
        output(formatDate(item), ".", true);

        output(item.get("note"), ".", true);
    }
    
    @Override
    public void write(Unpublished item, int number) throws IOException {
        writeNumber(number);
        writeTitleAndAuthors(item);

        output(item.get("note"), ".", true);
    }
    
    private void writeNumber(int number) throws IOException {
        if (number >= 0) {
            out.write(number);
            out.write(". ");
        }
    }

    private void writeTitleAndAuthors(BibItem item) throws IOException {
        out.write(formatTitle(item));
        out.write(".");

        if ("yes".equals(item.get("presented"))) {
            out.write(" ");
            out.write(settings.getPresentedText());
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
                throw new InternalError("Unrecognized status: \"" + item.get("status") + "\"");
        }
    }
}
