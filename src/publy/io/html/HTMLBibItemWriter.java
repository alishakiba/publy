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
import publy.data.Author;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.FieldData;
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
    public void write(BibItem item) throws IOException {
        writeTitleAndAbstractHTML(item);

        if (item.anyNonEmpty("status")) {
            writeStatus(item);
        } else {
            out.write(indent);

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
                    //writeInCollection(item);
                    break;
                case MANUAL:
                    //writeManual(item);
                    break;
                case MISC:
                    writeMisc(item);
                    break;
                case ONLINE:
                    //writeOnline(item);
                    break;
                case PATENT:
                    //writePatent(item);
                    break;
                case PROCEEDINGS:
                    //writeProceedings(item);
                    break;
                case INPROCEEDINGS:
                    //writeInProceedings(item);
                    break;
                case REPORT:
                    //writeReport(item);
                    break;
                case THESIS:
                    writeThesis(item);
                    break;
                case UNPUBLISHED:
                    //writeUnpublished(item);
                    break;
                default:
                    throw new AssertionError("Item \"" + item.getId() + "\" has an unexpected publication type: " + item.getType());
            }

            output("<span class=\"date\">", formatDate(item), "</span>.<br>", true);
        }

        output(indent + "<span class=\"note\">", item.get("note"), ".</span><br>", true);
        writeLinks(item);
    }

    protected void writeArticle(BibItem item) throws IOException {
        output("<span class=\"journal\">", item.get("journal"), "</span>, ");

        if (item.anyNonEmpty("volume", "number")) {
            output("<span class=\"volume\">", item.get("volume"), "</span>");
            output("<span class=\"number\">(", item.get("number"), ")</span>");
            output(":<span class=\"pages\">", formatPages(item, false), "</span>");
            out.write(", ");
        } else {
            output("<span class=\"pages\">", formatPages(item, true), "</span>, ");
        }
    }

    protected void writeBook(BibItem item) throws IOException {
        writeVolume(item, true, ". ");
        writePublisherAndEdition(item);
    }
    
    protected void writeInBook(BibItem item) throws IOException {
        writeVolume(item, true, ". ");
        writePublisherAndEdition(item);
    }
    
    protected void writeBooklet(BibItem item) throws IOException {
        output("<span class=\"howpublished\">", item.get("howpublished"), "</span>, ");
        output("<span class=\"address\">", item.get("address"), "</span>, ");
    }

    private void writePart(BibItem item) throws IOException {
        output("In <span class=\"booktitle\">", item.get("booktitle"), "</span>, ");
        writeVolume(item, false, ", ");
        output(formatPages(item, true), ", ");
    }

    protected void writeThesis(BibItem item) throws IOException {
        output(item.get("type"), ", ");
        output(item.get("school"), ", ");
    }

    protected void writeMisc(BibItem item) throws IOException {
        output(item.get("address"), ", ");
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

    private void writeVolume(BibItem item, boolean capitalize, String connective) throws IOException {
        String volume = item.get("volume");
        String series = item.get("series");
        String number = item.get("number");

        if (volume != null && !volume.isEmpty()) {
            output("<span class=\"volume\">" + (capitalize ? "Volume " : "volume "), volume, "</span>");
            output(" of <span class=\"series\">", series, "</span>");
            out.write(connective);
        } else if (number != null && !number.isEmpty()) {
            output("<span class=\"number\">" + (capitalize ? "Number " : "number "), number, "</number>");
            output(" in <span class=\"series\">", series, "</span>");
            out.write(connective);
        } else {
            output("<span class=\"series\">", series, "</span>" + connective);
        }
    }
    
    protected void writePublisherAndEdition(BibItem item) throws IOException {
        if (item.anyNonEmpty("publisher")) {
            output("<span class=\"publisher\">", item.get("publisher"), "</span>, ");

            if (item.anyNonEmpty("edition")) {
                output("<span class=\"edition\">", item.get("edition").toLowerCase(), " edition</span>, ");
            }
        } else if (item.anyNonEmpty("edition")) {
            output("<span class=\"edition\">", changeCaseT(item.get("edition")), " edition</span>, ");
        }
    }

    protected void writeStatus(BibItem item) throws IOException {
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

        out.write(indent);

        if (title == null) {
            switch (item.get("status")) {
                case "submitted":
                    output("Submitted for review.<br>", true);
                    break;
                case "accepted":
                    output("Accepted for publication.<br>", true);
                    break;
                case "acceptedrev":
                    output("Accepted for publication, pending minor revisions.<br>", true);
                    break;
                default:
                    throw new AssertionError("Item \"" + item.getId() + "\" has an unrecognized status: \"" + item.get("status") + "\"");
            }
        } else {
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
                    throw new AssertionError("Item \"" + item.getId() + "\" has an unrecognized status: \"" + item.get("status") + "\"");
            }
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
        if (!includeBibtex(item)) {
            writeLinks(item, false, false);
        } else {
            switch (item.getType()) {
                // Only include bibtex if there is an eprint reference
                case MISC:
                case ONLINE:
                case UNPUBLISHED:
                case PATENT:
                    if (item.anyNonEmpty("arxiv")) {
                        writeLinks(item, false, true);
                    } else {
                        writeLinks(item, false, false);
                    }
                    break;
                default:
                    if (PublicationType.ACCEPTED.matches(item)) {
                        writeLinks(item, true, false);
                    } else if (item.anyNonEmpty("arxiv")) {
                        writeLinks(item, false, true);
                    } else {
                        // Just in case.
                        writeLinks(item, false, false);
                    }
                    break;
            }
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
        out.write("@" + item.getOriginalType() + "{" + item.getId() + ",");
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

        for (String field : FieldData.getMandatoryFields(item.getType())) {
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

        for (String field : FieldData.getOptionalFields(item.getType())) {
            String v = item.get(field);

            if (!field.equals("author") && v != null && !v.isEmpty()) {
                if (first) {
                    first = false;
                } else {
                    out.write(",");
                    out.newLine();
                }

                out.write("  " + field + "={" + v + "}");
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
