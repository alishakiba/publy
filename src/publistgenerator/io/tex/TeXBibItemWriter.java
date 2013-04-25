/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.tex;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import publistgenerator.bibitem.*;
import publistgenerator.io.BibItemWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class TeXBibItemWriter extends BibItemWriter {

    public TeXBibItemWriter(BufferedWriter out) {
        super(out);
    }

    @Override
    public void write(Article item) throws IOException {
        writeTitleAndAuthors(item);

        // Handle submitted / accepted
        if (item.anyNonEmpty("status")) {
            writeStatus(item, item.get("journal"));
        } else {
            out.write(latexify(item.get("journal")));
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
                    out.write(item.get("pages"));
                    out.write(", ");
                } else {
                    output(formatPages(item), ", ");
                }
            }

            out.write(item.get("year"));
            out.write(".");
            
            if (item.anyNonEmpty("note")) {
                out.write("\\\\");
            }

            out.newLine();
        }

        if (item.anyNonEmpty("note")) {
            out.write(" & ");
            output(latexify(item.get("note")), ".", true);
        }
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
            out.write(latexify(item.get("booktitle")));
            out.write(", ");

            writeVolume(item, ", ");
            output(formatPages(item), ", ");

            out.write(item.get("year"));
            out.write(".");

            if (item.anyNonEmpty("note")) {
                out.write("\\\\");
            }

            out.newLine();
        }

        if (item.anyNonEmpty("note")) {
            out.write(" & ");
            output(latexify(item.get("note")), ".", true);
        }
    }

    @Override
    public void write(MastersThesis item) throws IOException {
        writeTitleAndAuthors(item);

        out.write("Master's thesis, ");
        out.write(latexify(item.get("school")));
        out.write(", ");
        out.write(item.get("year"));
        out.write(".");

        if (item.anyNonEmpty("note")) {
            out.write("\\\\");
            out.newLine();
            out.write(" & ");
            output(latexify(item.get("note")), ".", true);
        } else {
            out.newLine();
        }
    }

    @Override
    public void write(PhDThesis item) throws IOException {
        writeTitleAndAuthors(item);

        out.write("PhD thesis, ");
        out.write(latexify(item.get("school")));
        out.write(", ");
        out.write(item.get("year"));
        out.write(".");

        if (item.anyNonEmpty("note")) {
            out.write("\\\\");
            out.newLine();
            out.write(" & ");
            output(latexify(item.get("note")), ".", true);
        } else {
            out.newLine();
        }
    }

    @Override
    public void write(InvitedTalk item) throws IOException {
        out.write("& \\textbf{");
        out.write(latexify(formatTitle(item)));
        out.write("}.\\\\");
        out.newLine();

        out.write(" & ");
        output(item.get("address"), ", ");

        if (item.anyNonEmpty("note")) {
            output(formatDate(item), ".\\\\", true);
            out.write(" & ");
            output(latexify(item.get("note")), ".", true);
        } else {
            output(formatDate(item), ".", true);
        }
    }
    
    @Override
    public void write(Unpublished item) throws IOException {
        writeTitleAndAuthors(item);
        
        if (item.anyNonEmpty("note")) {
            output(latexify(item.get("note")), ".", true);
        } else {
            out.write("\\\\");
            out.newLine();
        }
    }

    private void writeTitleAndAuthors(BibItem item) throws IOException {
        out.write("& \\textbf{");
        out.write(latexify(formatTitle(item)));
        out.write("}.\\\\");
        out.newLine();

        out.write(" & ");
        out.write(latexify(formatAuthors(item)));
        out.write(".\\\\");
        out.newLine();

        out.write(" & ");
    }

    @Override
    protected String formatAuthors(BibItem item) {
        String author = item.get("author");

        if (author == null) {
            return "";
        } else {
            List<String> authorLinks = new ArrayList<>(item.getAuthors().size());

            for (Author a : item.getAuthors()) {
                if (a.isMe() && "yes".equals(item.get("presented"))) {
                    // Underline my name
                    authorLinks.add("\\underline{" + a.getLatexName() + "}");
                } else {
                    authorLinks.add(a.getLatexName());
                }
            }

            return formatNames(authorLinks);
        }
    }

    private void writeVolume(BibItem item, String connective) throws IOException {
        String volume = item.get("volume");
        String series = latexify(item.get("series"));
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
        String title;

        if (booktitle.startsWith("Proceedings of ")) {
            title = latexify(booktitle.substring("Proceedings of ".length()));
        } else {
            title = latexify(booktitle);
        }

        switch (item.get("status")) {
            case "submitted":
                out.write("Submitted to ");
                out.write(title);
                out.write(".\\\\");
                out.newLine();
                break;
            case "accepted":
                out.write("Accepted to ");
                out.write(title);
                out.write(".\\\\");
                out.newLine();
                break;
            case "acceptedrev":
                out.write("Accepted, pending minor revisions, to ");
                out.write(title);
                out.write(".\\\\");
                out.newLine();
                break;
            default:
                throw new InternalError("Unrecognized status: \"" + item.get("status") + "\"");
        }
    }

    private String latexify(String s) {
        if (s == null) {
            return "";
        } else {
            return s.replaceAll("&", "\\\\&");
        }
    }
}
