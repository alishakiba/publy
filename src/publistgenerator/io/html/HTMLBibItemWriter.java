/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import publistgenerator.bibitem.*;
import publistgenerator.io.BibItemWriter;
import publistgenerator.settings.HTMLSettings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLBibItemWriter extends BibItemWriter {

    private HTMLSettings settings;

    public HTMLBibItemWriter(BufferedWriter out, HTMLSettings settings) {
        super(out);
        this.settings = settings;
    }

    @Override
    public void write(Article item) throws IOException {
        writeTitleAndAuthorsHTML(item);

        // Handle submitted / accepted
        if (item.anyNonEmpty("status")) {
            writeStatus(item, item.get("journal"));
        } else {
            out.write("   <span class=\"booktitle\">");
            out.write(item.get("journal"));
            out.write("</span>, ");

            output(item.get("volume"));

            if (item.anyNonEmpty("number")) {
                out.write("(");
                out.write(item.get("number"));
                out.write(")");
            }

            if (item.anyNonEmpty("pages")) {
                if (item.anyNonEmpty("volume", "number")) {
                    out.write(":");
                    out.write(item.get("pages").replaceAll("-+", "&ndash;"));
                    out.write(", ");
                } else {
                    output(formatPages(item), ", ");
                }
            }

            out.write(item.get("year"));
            out.write(".<br>");
            out.newLine();
        }

        output(item.get("note"), ".<br>", true);

        writeLinks(item);
    }

    @Override
    public void write(InProceedings item) throws IOException {
        writePart(item);
    }

    @Override
    public void write(InCollection item) throws IOException {
        writePart(item);
    }

    /**
     * Used for both InCollection, and InProceedings.
     *
     * @param item
     * @throws IOException
     */
    private void writePart(BibItem item) throws IOException {
        writeTitleAndAuthorsHTML(item);

        if (item.anyNonEmpty("status")) {
            writeStatus(item, item.get("booktitle"));
        } else {
            out.write("   In <span class=\"booktitle\">");
            out.write(item.get("booktitle"));
            out.write("</span>, ");

            writeVolume(item, ", ");
            output(formatPages(item), ", ");

            out.write(item.get("year"));
            out.write(".<br>");
            out.newLine();
        }

        output(item.get("note"), ".<br>", true);

        writeLinks(item);
    }

    @Override
    public void write(MastersThesis item) throws IOException {
        writeTitleAndAuthorsHTML(item);

        out.write("   Master's thesis, ");
        out.write(item.get("school"));
        out.write(", ");
        out.write(item.get("year"));
        out.write(".<br>");
        out.newLine();

        output(item.get("note"), ".<br>", true);

        writeLinks(item);
    }

    @Override
    public void write(PhDThesis item) throws IOException {
        writeTitleAndAuthorsHTML(item);

        out.write("   PhD thesis, ");
        out.write(item.get("school"));
        out.write(", ");
        out.write(item.get("year"));
        out.write(".<br>");
        out.newLine();

        output(item.get("note"), ".<br>", true);

        writeLinks(item);
    }

    @Override
    public void write(InvitedTalk item) throws IOException {
        writeTitleAndAbstractHTML(item);

        output(item.get("address"), ", ");
        output(formatDate(item), ".<br>", true);

        output(item.get("note"), ".<br>", true);

        // links (no bibtex for talks)
        writeLinks(item, false, false);
    }

    @Override
    public void write(Unpublished item) throws IOException {
        writeTitleAndAuthorsHTML(item);

        output(item.get("note"), ".<br>", true);

        // links (only bibtex if it's on the arXiv)
        writeLinks(item, false, item.anyNonEmpty("arxiv"));
    }

    protected void writeTitleAndAuthorsHTML(BibItem item) throws IOException {
        writeTitleAndAbstractHTML(item);
        out.write("   ");
        out.write(formatAuthors(item));
        out.write(".<br>");
        out.newLine();
    }

    protected void writeTitleAndAbstractHTML(BibItem item) throws IOException {
        // Title
        out.write("   <a id=\"");
        out.write(item.getId());
        out.write("\"><h2 class=\"title\">");
        out.write(formatTitle(item));
        out.write("</h2>.</a>");

        // Icon if I presented this paper at the conference
        if ("yes".equals(item.get("presented"))) {
            out.newLine();
            out.write("   <img src=\"images/presentation.png\" alt=\"(presented)\" class=\"presented\">");
        }

        // Abstract if included
        String abstr = item.get("abstract");

        if (abstr != null && !abstr.isEmpty() && settings.includeAbstract(item)) {
            out.newLine();

            // Show \ hide link for the abstract
            writeToggleLink(item.getId() + "_abstract", "Abstract");
            out.write("<br>");
            out.newLine();

            // Actual abstract
            out.write("   <div id=\"");
            out.write(item.getId());
            out.write("_abstract\" class=\"collapsible\"><div class=\"abstract\"><span class=\"abstractword\">Abstract: </span>");
            out.write(abstr);
            out.write("</div></div>");
            out.newLine();
        } else {
            out.write("<br>");
            out.newLine();
        }
    }

    @Override
    protected String formatAuthors(BibItem item) {
        String author = item.get("author");

        if (author == null) {
            return "";
        } else {
            List<String> authorLinks = new ArrayList<>(item.getAuthors().size());

            for (Author a : item.getAuthors()) {
                if (a == null) {
                    System.err.println("Author is null! List: " + item.getAuthors());
                }
                authorLinks.add(a.getLinkedHtmlName());
            }

            return formatNames(authorLinks);
        }
    }

    private void writeVolume(BibItem item, String connective) throws IOException {
        String volume = item.get("volume");
        String series = item.get("series");
        String number = item.get("number");

        if (volume != null && !volume.isEmpty()) {
            out.write("volume ");
            out.write(volume);

            if (series != null && !series.isEmpty()) {
                out.write(" of <span class=\"series\">");
                out.write(series);
                out.write("</span>");
            }

            out.write(connective);
        } else if (number != null && !number.isEmpty()) {
            out.write("number ");
            out.write(number);

            if (series != null && !series.isEmpty()) {
                out.write(" in <span class=\"series\">");
                out.write(series);
                out.write("</span>");
            }

            out.write(connective);
        } else if (series != null && !series.isEmpty()) {
            out.write("<span class=\"series\">");
            out.write(series);
            out.write("</span>");
            out.write(connective);
        }
    }

    protected void writeStatus(BibItem item, String booktitle) throws IOException {
        String title = booktitle;

        if (title.startsWith("Proceedings of ")) {
            title = title.substring("Proceedings of ".length());
        }

        switch (item.get("status")) {
            case "submitted":
                out.write("Submitted to <span class=\"booktitle\">");
                out.write(title);
                out.write("</span>.<br>");
                out.newLine();
                break;
            case "accepted":
                out.write("Accepted to <span class=\"booktitle\">");
                out.write(title);
                out.write("</span>.<br>");
                out.newLine();
                break;
            case "acceptedrev":
                out.write("Accepted, pending minor revisions, to <span class=\"booktitle\">");
                out.write(title);
                out.write("</span>.<br>");
                out.newLine();
                break;
            default:
                throw new InternalError("Unrecognized status: \"" + item.get("status") + "\"");
        }
    }

    @Override
    protected String formatPages(BibItem item) {
        return super.formatPages(item).replaceAll("-+", "&ndash;");
    }

    private void writeLinks(BibItem item) throws IOException {
        if (settings.includeBibtex(item)) {
            if (HTMLSettings.PublicationType.ACCEPTED.matches(item)) {
                writeLinks(item, true, false);
            } else if (item.anyNonEmpty("arxiv")) {
                writeLinks(item, false, true);
            } else {
                // Just in case.
                writeLinks(item, false, false);
            }
        } else {
            writeLinks(item, false, false);
        }
    }

    private void writeLinks(BibItem item, boolean includeBibtex, boolean includeArxivBibtex) throws IOException {
        // PDF link
        if (item.anyNonEmpty("pdf") && settings.includePDF(item)) {
            out.write("   [<a href=\"publications/papers/");
            out.write(item.get("year"));
            out.write("/");
            out.write(URLEncoder.encode(item.get("pdf"), "UTF-8").replaceAll("\\+", "%20"));
            out.write("\">pdf</a>]");
            out.newLine();
        }

        // arXiv link
        if (item.anyNonEmpty("arxiv")) {
            out.write("   [<a href=\"http://arxiv.org/abs/");
            out.write(item.get("arxiv"));
            out.write("\">arXiv</a>]");
            out.newLine();
        }

        // DOI link
        if (item.anyNonEmpty("doi")) {
            out.write("   [<a href=\"http://dx.doi.org/");
            out.write(item.get("doi"));
            out.write("\">DOI</a>]");
            out.newLine();
        }

        // Slides link
        if (item.anyNonEmpty("slides")) {
            String slides = item.get("slides");
            String extension = slides.substring(slides.lastIndexOf('.') + 1);

            out.write("   [<a href=\"publications/slides/");
            out.write(item.get("year"));
            out.write("/");
            out.write(URLEncoder.encode(slides, "UTF-8").replaceAll("\\+", "%20"));
            out.write("\">Slides (");
            out.write(extension);
            out.write(")</a>]");
            out.newLine();
        }

        // Conference version(s) link(s)
        if (item.anyNonEmpty("conf")) {
            String[] confPapers = item.get("conf").split(",");

            for (int i = 0; i < confPapers.length; i++) {
                out.write("   [<a href=\"#");
                out.write(confPapers[i].trim());
                out.write("\">Conference version");

                if (confPapers.length > 1) {
                    out.write(" " + i);
                }

                out.write("</a>]");
                out.newLine();
            }
        }

        // Journal version(s) link(s)
        if (item.anyNonEmpty("journ")) {
            String[] journPapers = item.get("journ").split(",");

            for (int i = 0; i < journPapers.length; i++) {
                out.write("   [<a href=\"#");
                out.write(journPapers[i].trim());
                out.write("\">Journal version");

                if (journPapers.length > 1) {
                    out.write(" " + i);
                }

                out.write("</a>]");
                out.newLine();
            }
        }

        // BibTeX link
        if (includeBibtex) {
            writeBibTeXHTML(item);
        } else if (includeArxivBibtex) {
            writeArXivBibTeXHTML(item);
        }
    }

    private void writeBibTeXHTML(BibItem item) throws IOException {
        // Show / hide links
        writeToggleLink(item.getId() + "_bibtex", "BibTeX");

        // Actual bibtex
        out.write("   <div id=\"");
        out.write(item.getId());
        out.write("_bibtex\" class=\"collapsible\"><pre class=\"bibtex\">");
        out.newLine();

        // Item type
        out.write("@");
        out.write(item.getType());
        out.write("{");
        out.write(item.getId());
        out.write(",");
        out.newLine();

        // The first field should omit the connecting ",".
        boolean first = true;

        // Get the proper format for authors
        if (item.anyNonEmpty("author")) {
            out.write("  author={");

            for (int i = 0; i < item.getAuthors().size(); i++) {
                out.write(item.getAuthors().get(i).getRawLatexName());

                if (i < item.getAuthors().size() - 1) {
                    out.write(" and ");
                }
            }

            out.write("}");

            first = false;
        }

        for (String field : item.getMandatoryFields()) {
            if (!field.equals("author")) {
                if (first) {
                    first = false;
                } else {
                    out.write(",");
                    out.newLine();
                }

                out.write("  ");
                out.write(field);
                out.write("={");
                out.write(latexify(item.get(field)));
                out.write("}");
            }
        }

        for (String field : item.getOptionalFields()) {
            String v = item.get(field);

            if (!field.equals("author") && v != null && !v.isEmpty()) {
                if (first) {
                    first = false;
                } else {
                    out.write(",");
                    out.newLine();
                }

                out.write("  ");
                out.write(field);
                out.write("={");
                out.write(latexify(item.get(field)));
                out.write("}");
            }
        }

        out.newLine(); // No comma after the last element
        out.write("}</pre></div>");
        out.newLine();
    }

    private void writeArXivBibTeXHTML(BibItem item) throws IOException {
        // Show / hide links
        writeToggleLink(item.getId() + "_bibtex", "BibTeX");

        // Actual bibtex
        out.write("   <div id=\"");
        out.write(item.getId());
        out.write("_bibtex\" class=\"collapsible\"><pre class=\"bibtex\">");
        out.newLine();

        // Item type
        out.write("@article{");
        out.write(item.getId());
        out.write(",");
        out.newLine();

        // The first field should omit the connecting ",".
        boolean first = true;

        // Get the proper format for authors
        if (item.anyNonEmpty("author")) {
            out.write("  author={");

            for (int i = 0; i < item.getAuthors().size(); i++) {
                out.write(item.getAuthors().get(i).getRawLatexName());

                if (i < item.getAuthors().size() - 1) {
                    out.write(" and ");
                }
            }

            out.write("}");

            first = false;
        }

        for (String field : new String[]{"title", "year"}) {
            if (first) {
                first = false;
            } else {
                out.write(",");
                out.newLine();
            }

            out.write("  ");
            out.write(field);
            out.write("={");
            out.write(latexify(item.get(field)));
            out.write("}");
        }

        out.write(",");
        out.newLine();
        out.write("  journal={CoRR},");
        out.newLine();
        out.write("  volume={abs/");
        out.write(item.get("arxiv"));
        out.write("},");
        out.newLine();
        out.write("  ee={http://arxiv.org/abs/");
        out.write(item.get("arxiv"));
        out.write("}");

        out.newLine(); // No comma after the last element
        out.write("}</pre></div>");
        out.newLine();
    }

    private void writeToggleLink(String id, String linkText) throws IOException {
        // Mark as interactive so users with JS disabled do not see links that do nothing
        out.write("   <span class=\"interactive\">");

        // Link to reveal
        out.write("[<a href=\"javascript:toggle('");
        out.write(id);
        out.write("');\" id=\"");
        out.write(id);
        out.write("_plus\" class=\"shown\">");
        out.write(linkText);
        out.write("</a>");

        // Link to hide
        out.write("<a href=\"javascript:toggle('");
        out.write(id);
        out.write("');\" id=\"");
        out.write(id);
        out.write("_minus\" class=\"hidden\">");
        out.write("Hide " + linkText);
        out.write("</a>]</span>");

        // Disabled link for users without JS
        out.write("<noscript><div>[");
        out.write(linkText);
        out.write("]</div></noscript>");
        out.newLine();
    }

    private String latexify(String string) {
        return string.replaceAll("&", "{\\\\&amp;}");
    }
}
