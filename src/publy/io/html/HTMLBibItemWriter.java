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
package publy.io.html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import publy.Console;
import publy.data.PublicationType;
import publy.data.bibitem.Article;
import publy.data.Author;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.Book;
import publy.data.bibitem.InCollection;
import publy.data.bibitem.InProceedings;
import publy.data.bibitem.InvitedTalk;
import publy.data.bibitem.MastersThesis;
import publy.data.bibitem.PhDThesis;
import publy.data.bibitem.Unpublished;
import publy.data.settings.GeneralSettings;
import publy.data.settings.HTMLSettings;
import publy.data.settings.Settings;
import publy.io.BibItemWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLBibItemWriter extends BibItemWriter {

    private static final String indent = "          ";

    public HTMLBibItemWriter(BufferedWriter out, Settings settings) {
        super(out, settings);
    }

    @Override
    protected void writeArticle(Article item) throws IOException {
        writeTitleAndAuthorsHTML(item);

        // Handle submitted / accepted
        if (item.anyNonEmpty("status")) {
            writeStatus(item, item.get("journal"));
        } else {
            output(indent + "<span class=\"booktitle\">", item.get("journal"), "</span>, ");

            if (item.anyNonEmpty("volume", "number")) {
                output(item.get("volume"));
                output("(", item.get("number"), ")");
                output(":", formatPages(item, false), "");
                out.write(", ");
            } else {
                output(formatPages(item, false), ", ");
            }

            output(item.get("year"), ".<br>", true);
        }

        output(indent, item.get("note"), ".<br>", true);

        writeLinks(item);
    }

    @Override
    protected void writeBook(Book item) throws IOException {
        writeTitleAndAuthorsHTML(item);

        output(indent, item.get("publisher"), ", ");
        output(item.get("year"), ".<br>", true);

        output(indent, item.get("note"), ".<br>", true);
        writeLinks(item);
    }

    @Override
    protected void writeInProceedings(InProceedings item) throws IOException {
        writePart(item);
    }

    @Override
    protected void writeInCollection(InCollection item) throws IOException {
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
            output(indent + "In <span class=\"booktitle\">", item.get("booktitle"), "</span>, ");

            writeVolume(item, ", ");

            output(formatPages(item, true), ", ");
            output(item.get("year"), ".<br>", true);
        }

        output(indent, item.get("note"), ".<br>", true);

        writeLinks(item);
    }

    @Override
    protected void writeMastersThesis(MastersThesis item) throws IOException {
        writeTitleAndAuthorsHTML(item);

        output(indent + "Master's thesis, ", item.get("school"), ", ");
        output(item.get("year"), ".<br>", true);

        output(indent, item.get("note"), ".<br>", true);

        writeLinks(item);
    }

    @Override
    protected void writePhDThesis(PhDThesis item) throws IOException {
        writeTitleAndAuthorsHTML(item);

        output(indent + "PhD thesis, ", item.get("school"), ", ");
        output(item.get("year"), ".<br>", true);

        output(indent, item.get("note"), ".<br>", true);

        writeLinks(item);
    }

    @Override
    protected void writeInvitedTalk(InvitedTalk item) throws IOException {
        writeTitleAndAbstractHTML(item);

        output(indent, item.get("address"), ", ", false);
        output(formatDate(item), ".<br>", true);

        output(indent, item.get("note"), ".<br>", true);

        // links (no bibtex for talks)
        writeLinks(item, false, false);
    }

    @Override
    protected void writeUnpublished(Unpublished item) throws IOException {
        writeTitleAndAuthorsHTML(item);

        output(indent, item.get("note"), ".<br>", true);

        // links (bibtex only if it's on the arXiv)
        writeLinks(item, false, item.anyNonEmpty("arxiv") && includeBibtex(item));
    }

    protected void writeTitleAndAuthorsHTML(BibItem item) throws IOException {
        if (settings.getGeneralSettings().titleFirst()) {
            writeTitleAndAbstractHTML(item);
        }

        // Don't add an authors line if it's just me and I just want to list co-authors
        if (settings.getGeneralSettings().listAllAuthors() || item.getAuthors().size() > 1 || (item.getAuthors().size() == 1 && !item.getAuthors().get(0).isMe(settings.getGeneralSettings().getMyNames(), settings.getGeneralSettings().getNameDisplay(), settings.getGeneralSettings().reverseNames()))) {
            String authors = formatAuthors(item);

            if (authors.endsWith(".</span>") || authors.endsWith(".</a>")) {
                // Don't double up on periods (occurs when author names are abbreviated and reversed)
                output(indent, authors, "<br>", true);
            } else {
                output(indent, authors, ".<br>", true);
            }
        }

        if (!settings.getGeneralSettings().titleFirst()) {
            writeTitleAndAbstractHTML(item);
        }
    }

    protected void writeTitleAndAbstractHTML(BibItem item) throws IOException {
        out.write(indent);

        // Title
        if (settings.getHtmlSettings().getTitleTarget() == HTMLSettings.TitleLinkTarget.ABSTRACT && includeAbstract(item)) {
            output("<h3 class=\"title abstract-toggle\">", formatTitle(item), "</h3>");
        } else if (settings.getHtmlSettings().getTitleTarget() == HTMLSettings.TitleLinkTarget.PAPER && includePaper(item)) {
            try {
                String href = (new URI(null, null, item.get("paper"), null)).toString();

                out.write("<a href=\"" + href + "\">");
                output("<h3 class=\"title\">", formatTitle(item), "</h3>");
                out.write("</a>");
                checkExistance(item.get("paper"), "paper", item);
            } catch (URISyntaxException ex) {
                Console.except(ex, "Paper link for entry \"%s\" is not formatted properly:", item.getId());
                output("<h3 class=\"title\">", formatTitle(item), "</h3>");
            }
        } else {
            output("<h3 class=\"title\">", formatTitle(item), "</h3>");
        }

        // Add text if I presented this paper
        if ("yes".equals(item.get("presented"))) {
            output(" ", settings.getHtmlSettings().getPresentedText(), "");
        }

        // Abstract if included
        if (includeAbstract(item)) {
            // Show \ hide link for the abstract
            if (settings.getHtmlSettings().getTitleTarget() != HTMLSettings.TitleLinkTarget.ABSTRACT) {
                out.newLine();
                writeToggleLink("abstract", "Abstract");
            }

            out.write("<br>");
            out.newLine();

            // Actual abstract
            out.write(indent + "<div class=\"abstract-container\">");
            out.write("<div class=\"abstract\">");
            out.newLine();
            out.write(indent + "  <span class=\"abstractword\">Abstract: </span>");
            output(item.get("abstract"));
            out.newLine();
            out.write(indent + "</div></div>");
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
            Console.error("No authors found for entry \"%s\".", item.getId());
            return "";
        } else {
            List<String> authorLinks = new ArrayList<>(item.getAuthors().size());
            GeneralSettings gs = settings.getGeneralSettings();

            for (Author a : item.getAuthors()) {
                if (a == null) {
                    Console.error("Null author found for entry \"%s\".%n(Authors: \"%s\")", item.getId(), author);
                } else {
                    if (gs.listAllAuthors() || !a.isMe(gs.getMyNames(), gs.getNameDisplay(), gs.reverseNames())) {
                        authorLinks.add(a.getLinkedAndFormattedHtmlName(gs.getNameDisplay(), gs.reverseNames()));
                    }
                }
            }

            if (gs.listAllAuthors()) {
                return formatNames(authorLinks);
            } else {
                if (authorLinks.size() == item.getAuthors().size()) {
                    Console.warn(Console.WarningType.NOT_AUTHORED_BY_USER, "None of the authors of entry \"%s\" match your name.%n(Authors: \"%s\")", item.getId(), author);

                    return formatNames(authorLinks);
                } else {
                    return "With " + formatNames(authorLinks);
                }
            }
        }
    }

    @Override
    protected String processString(String string) {
        return changeQuotes(super.processString(string));
    }
    
    protected String changeQuotes(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        char lastChar = '\u0000'; // State. Either '\u0000' (regular), '<' (in an HTML tag), '\'' (after a straight quote), or '`' (after a grave)
        
        for (char c : string.toCharArray()) {
            if (lastChar == '`') {
                // Single or double quote?
                if (c == '`') {
                    // Double `` -> U+201C (left double quotation mark)
                    sb.append('\u201C');
                    // lastChar is reset at the end of the loop
                } else {
                    // Single ` -> U+2018 (left single quotation mark)
                    sb.append('\u2018');
                    lastChar = '\u0000'; // Reset lastChar here so current char gets processed regularly
                }
            } else if (lastChar == '\'') {
                // Single or double quote?
                if (c == '\'') {
                    // Double '' -> U+201D (right double quotation mark)
                    sb.append('\u201D');
                    // lastChar is reset at the end of the loop
                } else {
                    // Single ' -> U+2019 (right single quotation mark)
                    sb.append('\u2019');
                    lastChar = '\u0000'; // Reset lastChar here so current char gets processed regularly
                }
            }
            
            if (lastChar == '\u0000') {
                // Regular case
                switch (c) {
                    case '<': // HTML tag open
                        lastChar = c;
                        sb.append(c);
                        break;
                    case '"': // Single " -> U+201D (right double quotation mark)
                        sb.append('\u201D');
                        break;
                    case '`': // Single or double `
                        lastChar = c;
                        break;
                    case '\'': // Single or double '
                        lastChar = c;
                        break;
                    default:
                        sb.append(c);
                        break;
                }
            } else if (lastChar == '<') {
                // In an HTML tag
                if (c == '>') {
                    // Close the tag
                    lastChar = '\u0000';
                }
                
                sb.append(c);
            } else {
                lastChar = '\u0000';
            }
        }
        
        return sb.toString();
    }

    private void writeVolume(BibItem item, String connective) throws IOException {
        String volume = item.get("volume");
        String series = item.get("series");
        String number = item.get("number");

        if (volume != null && !volume.isEmpty()) {
            output("volume ", volume, "");
            output(" of <span class=\"series\">", series, "</span>");
            out.write(connective);
        } else if (number != null && !number.isEmpty()) {
            output("number ", number, "");
            output(" in <span class=\"series\">", series, "</span>");
            out.write(connective);
        } else {
            output("<span class=\"series\">", series, "</span>" + connective);
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
                output("Submitted to <span class=\"booktitle\">", title, "</span>.<br>", true);
                break;
            case "accepted":
                output("Accepted to <span class=\"booktitle\">", title, "</span>.<br>", true);
                break;
            case "acceptedrev":
                output("Accepted, pending minor revisions, to <span class=\"booktitle\">", title, "</span>.<br>", true);
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

        return pages.replaceAll("-+", "&ndash;");
    }

    private void writeLinks(BibItem item) throws IOException {
        if (includeBibtex(item)) {
            if (PublicationType.ACCEPTED.matches(item)) {
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
        // Make sure the div is not written if there are no links
        boolean divOpened = false;
        
        // Paper link
        if (includePaper(item) && settings.getHtmlSettings().getTitleTarget() != HTMLSettings.TitleLinkTarget.PAPER) {
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

                writeLink(divOpened, link, text);
                divOpened = true;

                checkExistance(item.get("paper"), "paper", item);
            } catch (URISyntaxException ex) {
                Console.except(ex, "Paper link for entry \"%s\" is not formatted properly:", item.getId());
            }
        }

        // arXiv link
        if (item.anyNonEmpty("arxiv")) {
            writeLink(divOpened, "http://arxiv.org/abs/" + item.get("arxiv"), "arXiv");
            divOpened = true;
        }

        // DOI link
        if (item.anyNonEmpty("doi")) {
            String link = item.get("doi");
            
            // Add the general DOI part if necessary 
            if (!link.startsWith("http://dx.doi.org/")) {
                link = "http://dx.doi.org/" + link;
            }
            
            writeLink(divOpened, link, "DOI");
            divOpened = true;
        }
        
        // ISBN link
        if (item.anyNonEmpty("isbn")) {
            String link = "http://www.worldcat.org/isbn/" + item.get("isbn");
            writeLink(divOpened, link, "ISBN");
            divOpened = true;
        }
        
        // URL link
        if (item.anyNonEmpty("url")) {
            String link = item.get("url");
            writeLink(divOpened, link, "URL");
            divOpened = true;
        }

        // Other user-specified links
        divOpened = writeCustomLink(divOpened, item, -1); // link

        for (int i = 0; i < 20; i++) {
            divOpened = writeCustomLink(divOpened, item, i); // link<i>
        }

        // Close links div
        if (divOpened) {
            out.write(indent + "</div>");
            out.newLine();
        }

        // BibTeX link
        if (includeBibtex || includeArxivBibtex) {
            // Show / hide link
            writeToggleLink("bibtex", "BibTeX");
            out.newLine();

            // Actual bibtex
            if (includeBibtex) {
                writeBibtexHTML(item);
            } else {
                writeArxivBibtexHTML(item);
            }
        }
    }

    private void writeLink(boolean divOpened, String link, String text) throws IOException {
        if (!divOpened) {
            out.write(indent + "<div class=\"links\">");
            out.newLine();
        }

        out.write(indent + "  <a href=\"" + link + "\">" + text + "</a>");
        out.newLine();
    }

    private boolean writeCustomLink(boolean divOpened, BibItem item, int i) throws IOException {
        String attribute = (i == -1 ? "link" : "link" + i);

        if (item.anyNonEmpty(attribute)) {
            String link = item.get(attribute);
            int divider = link.indexOf('|');

            if (divider == -1) {
                Console.error("No divider \"|\" found in %s of item \"%s\". Links should be formatted as%n  %s={<Link text>|<Link target>}", attribute, item.getId(), attribute);
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
                        checkExistance(target, attribute, item);
                        target = (new URI(null, null, target, null)).toString();
                    } catch (URISyntaxException ex) {
                        Console.except(ex, "Could not parse the target of %s of item \"%s\":", attribute, item.getId());
                    }
                }

                writeLink(divOpened, target, text);
                return true;
            }
        }
        
        return divOpened;
    }

    private void writeBibtexHTML(BibItem item) throws IOException {
        out.write(indent + "<div class=\"bibtex-container\">");
        out.newLine();
        out.write(indent + "  <pre class=\"bibtex\">");
        out.newLine();

        // Item type
        out.write("@" + item.getType() + "{" + item.getId() + ",");
        out.newLine();

        // The first field should omit the connecting ",".
        boolean first = true;

        // Get the proper format for authors
        if (item.anyNonEmpty("author")) {
            out.write("  author={");

            for (int i = 0; i < item.getAuthors().size(); i++) {
                out.write(item.getAuthors().get(i).getName());

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

                out.write("  " + field + "={" + item.get(field) + "}");
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

                out.write("  " + field + "={" + item.get(field) + "}");
            }
        }

        out.newLine(); // No comma after the last element
        out.write("}</pre>");
        out.newLine();

        out.write(indent + "</div>");
        out.newLine();
    }

    private void writeArxivBibtexHTML(BibItem item) throws IOException {
        out.write(indent + "<div class=\"bibtex-container\">");
        out.newLine();
        out.write(indent + "  <pre class=\"bibtex\">");
        out.newLine();

        // Item type
        out.write("@article{" + item.getId() + ",");
        out.newLine();

        // The first field should omit the connecting ",".
        boolean first = true;

        // Get the proper format for authors
        if (item.anyNonEmpty("author")) {
            out.write("  author={");

            for (int i = 0; i < item.getAuthors().size(); i++) {
                out.write(item.getAuthors().get(i).getName());

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

            out.write("  " + field + "={" + item.get(field) + "}");
        }

        out.write(",");
        out.newLine();
        out.write("  journal={CoRR},");
        out.newLine();
        out.write("  volume={abs/" + item.get("arxiv") + "},");
        out.newLine();
        out.write("  ee={http://arxiv.org/abs/" + item.get("arxiv") + "}");

        out.newLine(); // No comma after the last element
        out.write("}</pre>");
        out.newLine();

        out.write(indent + "</div>");
        out.newLine();
    }

    private void writeToggleLink(String type, String text) throws IOException {
        out.write(indent);
        out.write("<button class=\"" + type + "-toggle\">");
        out.write(text);
        out.write("</button>");
    }

    private boolean includeAbstract(BibItem item) {
        return item.anyNonEmpty("abstract") && settings.getHtmlSettings().getIncludeAbstract().matches(item);
    }

    private boolean includeBibtex(BibItem item) {
        return settings.getHtmlSettings().getIncludeBibtex().matches(item);
    }

    private boolean includePaper(BibItem item) {
        return item.anyNonEmpty("paper") && settings.getHtmlSettings().getIncludePaper().matches(item);
    }

    private void checkExistance(String path, String attr, BibItem item) {
        Path file = settings.getFileSettings().getTarget().resolveSibling(path);

        if (Files.notExists(file)) {
            Console.warn(Console.WarningType.MISSING_REFERENCE, "The file \"%s\" that is linked in attribute \"%s\" of publication \"%s\" cannot be found at \"%s\".", path, attr, item.getId(), file);
        }
    }
}
