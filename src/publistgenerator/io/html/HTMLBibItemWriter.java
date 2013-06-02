/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import publistgenerator.Console;
import publistgenerator.data.bibitem.Article;
import publistgenerator.data.bibitem.Author;
import publistgenerator.data.bibitem.BibItem;
import publistgenerator.data.bibitem.InCollection;
import publistgenerator.data.bibitem.InProceedings;
import publistgenerator.data.bibitem.InvitedTalk;
import publistgenerator.data.bibitem.MastersThesis;
import publistgenerator.data.bibitem.PhDThesis;
import publistgenerator.data.bibitem.Unpublished;
import publistgenerator.data.settings.HTMLSettings;
import publistgenerator.io.BibItemWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLBibItemWriter extends BibItemWriter {

    private HTMLSettings htmlSettings;
    private static final String indent = "        ";

    public HTMLBibItemWriter(BufferedWriter out, HTMLSettings settings) {
        super(out, settings);
        this.htmlSettings = settings;
    }

    @Override
    public void write(Article item, int number) throws IOException {
        writeTitleAndAuthorsHTML(item, number);

        // Handle submitted / accepted
        if (item.anyNonEmpty("status")) {
            writeStatus(item, item.get("journal"));
        } else {
            out.write(indent);
            out.write("<span class=\"booktitle\">");
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

        output(indent, item.get("note"), ".<br>", true);

        writeLinks(item);
    }

    @Override
    public void write(InProceedings item, int number) throws IOException {
        writePart(item, number);
    }

    @Override
    public void write(InCollection item, int number) throws IOException {
        writePart(item, number);
    }

    /**
     * Used for both InCollection, and InProceedings.
     *
     * @param item
     * @throws IOException
     */
    private void writePart(BibItem item, int number) throws IOException {
        writeTitleAndAuthorsHTML(item, number);

        if (item.anyNonEmpty("status")) {
            writeStatus(item, item.get("booktitle"));
        } else {
            out.write(indent);
            out.write("In <span class=\"booktitle\">");
            out.write(item.get("booktitle"));
            out.write("</span>, ");

            writeVolume(item, ", ");
            output(formatPages(item), ", ");

            out.write(item.get("year"));
            out.write(".<br>");
            out.newLine();
        }

        output(indent, item.get("note"), ".<br>", true);

        writeLinks(item);
    }

    @Override
    public void write(MastersThesis item, int number) throws IOException {
        writeTitleAndAuthorsHTML(item, number);

        out.write(indent);
        out.write("Master's thesis, ");
        out.write(item.get("school"));
        out.write(", ");
        out.write(item.get("year"));
        out.write(".<br>");
        out.newLine();

        output(indent, item.get("note"), ".<br>", true);

        writeLinks(item);
    }

    @Override
    public void write(PhDThesis item, int number) throws IOException {
        writeTitleAndAuthorsHTML(item, number);

        out.write(indent);
        out.write("PhD thesis, ");
        out.write(item.get("school"));
        out.write(", ");
        out.write(item.get("year"));
        out.write(".<br>");
        out.newLine();

        output(indent, item.get("note"), ".<br>", true);

        writeLinks(item);
    }

    @Override
    public void write(InvitedTalk item, int number) throws IOException {
        writeTitleAndAbstractHTML(item, number);

        output(indent, item.get("address"), ", ", false);
        output(formatDate(item), ".<br>", true);

        output(indent, item.get("note"), ".<br>", true);

        // links (no bibtex for talks)
        writeLinks(item, false, false);
    }

    @Override
    public void write(Unpublished item, int number) throws IOException {
        writeTitleAndAuthorsHTML(item, number);

        output(indent, item.get("note"), ".<br>", true);

        // links (only bibtex if it's on the arXiv)
        writeLinks(item, false, item.anyNonEmpty("arxiv") && includeBibtex(item));
    }

    protected void writeTitleAndAuthorsHTML(BibItem item, int number) throws IOException {
        writeTitleAndAbstractHTML(item, number);

        // Don't add an authors line if it's just me and I just want to list co-authors
        if (settings.isListAllAuthors() || item.getAuthors().size() > 1) {
            out.write(indent);
            out.write(formatAuthors(item));
            out.write(".<br>");
            out.newLine();
        }
    }

    protected void writeTitleAndAbstractHTML(BibItem item, int number) throws IOException {
        out.write(indent);

        // Number
        if (number >= 0) {
            out.write("<span class=\"number\">");
            out.write(Integer.toString(number));
            out.write(".<span> ");
        }

        // Title
        out.write("<a id=\"");
        out.write(item.getId());
        out.write("\"><h2 class=\"title\">");
        out.write(formatTitle(item));
        out.write("</h2></a>");

        // Add text if I presented this paper
        if ("yes".equals(item.get("presented")) && settings.getPresentedText() != null && !settings.getPresentedText().isEmpty()) {
            out.write(" ");
            out.write(settings.getPresentedText());
        }

        // Abstract if included
        String abstr = item.get("abstract");

        if (abstr != null && !abstr.isEmpty() && includeAbstract(item)) {
            out.newLine();

            // Show \ hide link for the abstract
            writeToggleLink(item.getId() + "_abstract", "Abstract");
            out.write("<br>");
            out.newLine();

            // Actual abstract
            out.write(indent);
            out.write("<div id=\"");
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
            Console.error("No authors found for %s.", item.getId());
            return "";
        } else {
            List<String> authorLinks = new ArrayList<>(item.getAuthors().size());

            for (Author a : item.getAuthors()) {
                if (a == null) {
                    Console.error("Null author found for %s.%n(Authors: %s)", item.getId(), item.getAuthors().toString());
                } else {
                    if (settings.isListAllAuthors() || !a.isMe()) {
                        authorLinks.add(a.getLinkedHtmlName());
                    }
                }
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

        out.write(indent);

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
                throw new AssertionError("Unrecognized status: \"" + item.get("status") + "\"");
        }
    }

    @Override
    protected String formatPages(BibItem item) {
        return super.formatPages(item).replaceAll("-+", "&ndash;");
    }

    private void writeLinks(BibItem item) throws IOException {
        if (includeBibtex(item)) {
            if (matches(HTMLSettings.PublicationType.ACCEPTED, item)) {
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
        // Paper link
        if (item.anyNonEmpty("paper") && includePaper(item)) {
            try {
                String link = (new URI(null, null, item.get("paper"), null)).toString();
                String text;

                // Use the extension as link text, or "Paper" if no extension is found
                int dot = link.lastIndexOf('.');

                if (dot != -1) {
                    text = link.substring(dot + 1);

                    // Convert *.ps.gz to ps
                    if ("gz".equals(text) && link.lastIndexOf('.', dot - 1) != -1) {
                        text = link.substring(link.lastIndexOf('.', dot - 1) + 1, dot);
                    }
                } else {
                    text = "Paper";
                }

                out.write(indent);
                out.write("[<a href=\"");
                out.write(link);
                out.write("\">");
                out.write(text);
                out.write("</a>]");
                out.newLine();

                checkExistance(item.get("paper"));
            } catch (URISyntaxException ex) {
                Console.except(ex, "Paper link for entry \"%s\" is not formatted properly:", item.getId());
            }
        }

        // arXiv link
        if (item.anyNonEmpty("arxiv")) {
            out.write(indent);
            out.write("[<a href=\"http://arxiv.org/abs/");
            out.write(item.get("arxiv"));
            out.write("\">arXiv</a>]");
            out.newLine();
        }

        // DOI link
        if (item.anyNonEmpty("doi")) {
            out.write(indent);
            out.write("[<a href=\"http://dx.doi.org/");
            out.write(item.get("doi"));
            out.write("\">DOI</a>]");
            out.newLine();
        }

        // Other user-specified links
        for (int i = 0; i < 20; i++) {
            if (item.anyNonEmpty("link" + i)) {
                String link = item.get("link" + i);
                int divider = link.indexOf('|');

                if (divider == -1) {
                    Console.error("No divider \"|\" found in link %d of item \"%s\". Links should be formatted as%n  link%d={<Link text>|<Link target>}", i, item.getId(), i);
                } else {
                    String text = link.substring(0, divider);
                    String target = link.substring(divider + 1);

                    if (target.startsWith("#")) {
                        // Link to another paper, good as-is
                    } else if (target.contains(":")) {
                        // Most file systems prohibit colons in file names, so
                        // it seems safe to assume that this indicates an
                        // absolute URI and as such, should be fine.
                    } else {
                        // Most likely link to a file on disk. Encode correctly.
                        try {
                            checkExistance(target);
                            target = (new URI(null, null, target, null)).toString();
                        } catch (URISyntaxException ex) {
                            Console.except(ex, "Could not parse the target of link%d of item \"%s\":", target, i, item.getId());
                        }
                    }

                    out.write(indent);
                    out.write("[<a href=\"");
                    out.write(target);
                    out.write("\">");
                    out.write(text);
                    out.write("</a>]");
                    out.newLine();
                }
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
        out.write(indent);
        out.write("<div id=\"");
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
                out.write(item.getAuthors().get(i).getLatexName());

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
        out.write(indent);
        out.write("<div id=\"");
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
                out.write(item.getAuthors().get(i).getLatexName());

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
        out.write(indent);
        out.write("<span class=\"interactive\">");

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

    private boolean includeAbstract(BibItem item) {
        return matches(htmlSettings.getIncludeAbstract(), item);
    }

    private boolean includeBibtex(BibItem item) {
        return matches(htmlSettings.getIncludeBibtex(), item);
    }

    private boolean includePaper(BibItem item) {
        return matches(htmlSettings.getIncludePaper(), item);
    }

    public static boolean matches(HTMLSettings.PublicationType type, BibItem item) {
        if (type == HTMLSettings.PublicationType.ALL) {
            return true;
        } else if (type == HTMLSettings.PublicationType.NONE) {
            return false;
        } else {
            if (item.anyNonEmpty("status")) {
                if (type == HTMLSettings.PublicationType.PUBLISHED) {
                    return false;
                } else {
                    if (item.get("status").startsWith("accepted")) {
                        return true;
                    } else {
                        if (type == HTMLSettings.PublicationType.ACCEPTED) {
                            return false;
                        } else {
                            // Type is ARXIV
                            return item.anyNonEmpty("arxiv");
                        }
                    }
                }
            } else {
                return true;
            }
        }
    }

    private void checkExistance(String path) {
        File file = new File(settings.getTarget().getParentFile(), path);

        if (!file.exists()) {
            Console.log("Warning: linked file \"%s\" cannot be found at \"%s\".", path, file.getPath());
        }
    }
}
