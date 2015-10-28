/*
 * Copyright 2013-2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import publy.Console;
import publy.data.PublicationStatus;
import publy.data.Author;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.Type;
import publy.data.settings.HTMLSettings;
import publy.data.settings.Settings;
import publy.io.BibItemWriter;
import publy.io.bibtex.BibtexBibItemWriter;

/**
 *
 *
 */
public class HTMLBibItemWriter extends BibItemWriter {

    public HTMLBibItemWriter(BufferedWriter out, Settings settings) {
        super(out, settings);
    }

    @Override
    public void write(BibItem item) throws IOException {
        writeTitleAndAuthorsHTML(item);

        if (isPresent(item, "pubstate")) {
            writeStatus(item);
        } else {
            out.write(indentString);

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
                    writeInCollection(item);
                    break;
                case MANUAL:
                    writeManual(item);
                    break;
                case MISC:
                    writeMisc(item);
                    break;
                case ONLINE:
                    writeOnline(item);
                    break;
                case PATENT:
                    writePatent(item);
                    break;
                case PROCEEDINGS:
                    writeProceedings(item);
                    break;
                case INPROCEEDINGS:
                    writeInProceedings(item);
                    break;
                case REPORT:
                    writeReport(item);
                    break;
                case THESIS:
                    writeThesis(item);
                    break;
                case UNPUBLISHED:
                    writeUnpublished(item);
                    break;
                default:
                    throw new AssertionError("Item \"" + item.getId() + "\" has an unexpected publication type: " + item.getType());
            }

            if (!((item.getType() == Type.PROCEEDINGS || item.getType() == Type.INPROCEEDINGS) && isPresent(item, "address"))) {
                output("<span class=\"date\">", formatDate(item), "</span>.", true);
            }
        }

        // Write note (unpublished uses note as the publication info)
        if (item.getType() != Type.UNPUBLISHED) {
            output(indentString + "<span class=\"note\">", get(item, "note"), ".</span>", true);
        }

        writeLinks(item);
    }

    protected void writeArticle(BibItem item) throws IOException {
        output("<span class=\"journal\">", get(item, "journal"), "</span>, ");

        if (anyPresent(item, "volume", "number")) {
            output("<span class=\"volume\">", get(item, "volume"), "</span>");
            output("<span class=\"number\">(", get(item, "number"), ")</span>");
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
        if (isPresent(item, "volume") && anyPresent(item, "chapter", "pages")) {
            writeVolume(item, true, "");

            if (isPresent(item, "chapter")) {
                out.write(", ");
                writeChapter(item, false);
            }

            output(", <span class=\"pages\">", formatPages(item, true), "</span>");
            out.write(". ");
        } else {
            writeVolume(item, true, ". ");
        }

        writePublisherAndEdition(item);
    }

    protected void writeBooklet(BibItem item) throws IOException {
        output("<span class=\"howpublished\">", get(item, "howpublished"), "</span>, ");
        output("<span class=\"address\">", get(item, "address"), "</span>, ");
    }

    protected void writeInCollection(BibItem item) throws IOException {
        out.write("In ");

        if (isPresent(item, "editor")) {
            output("<span class=\"editor\">", formatAuthors(item, true, true), ", </span>");
        }

        output("<span class=\"booktitle\">", get(item, "booktitle"), "</span>");

        if (anyPresent(item, "volume", "series", "number")) {
            out.write(", ");
            writeVolume(item, false, "");
        }

        if (isPresent(item, "chapter")) {
            out.write(", ");
            writeChapter(item, false);
        }

        output(", <span class=\"pages\">", formatPages(item, true), "</span>");
        out.write(". ");

        if (anyPresent(item, "publisher", "address", "edition")) {
            writePublisherAndEdition(item);
        }
    }

    protected void writeManual(BibItem item) throws IOException {
        output("<span class=\"organization\">", get(item, "organization"), "</span>, ");
        output("<span class=\"address\">", get(item, "address"), "</span>, ");

        String edition = get(item, "edition");

        if (edition != null && !edition.isEmpty()) {
            if (anyPresent(item, "organization", "address")) {
                output("<span class=\"edition\">", toLowerCase(edition), " edition</span>, ");
            } else {
                output("<span class=\"edition\">", toTitleCase(edition), " edition</span>, ");
            }
        }
    }

    protected void writeMisc(BibItem item) throws IOException {
        output(get(item, "howpublished"), ", ");
        output(get(item, "address"), ", ");
    }

    protected void writeOnline(BibItem item) throws IOException {
        // No publication information necessary
    }

    protected void writePatent(BibItem item) throws IOException {
        // TODO
    }

    protected void writeProceedings(BibItem item) throws IOException {
        String notesConnective;

        if (isPresent(item, "address")) {
            notesConnective = ", ";
        } else {
            // There should be a period if anything follows notes
            if (isPresent(item, "publisher") || (isPresent(item, "organization") && isPresent(item, "editor"))) {
                notesConnective = ". ";
            } else {
                notesConnective = ", ";
            }
        }

        writeVolume(item, true, notesConnective);

        if (isPresent(item, "address")) {
            output("<span class=\"address\">", get(item, "address"), "</span>, ");
            output("<span class=\"date\">", formatDate(item), "</span>.", true);

            if (isPresent(item, "publisher")) {
                if (isPresent(item, "editor")) {
                    output("<span class=\"organization\">", get(item, "organization"), "</span>, ");
                }
                output("<span class=\"publisher\">", get(item, "publisher"), "</span>. ");
            } else if (isPresent(item, "editor")) {
                output("<span class=\"organization\">", get(item, "organization"), "</span>. ");
            }
        } else {
            if (isPresent(item, "editor")) {
                output("<span class=\"organization\">", get(item, "organization"), "</span>, ");
            }
            output("<span class=\"publisher\">", get(item, "publisher"), "</span>, ");
        }
    }

    protected void writeInProceedings(BibItem item) throws IOException {
        out.write("In ");

        if (isPresent(item, "editor")) {
            output("<span class=\"editor\">", formatAuthors(item, true, true), ", </span>");
        }

        output("<span class=\"booktitle\">", get(item, "booktitle"), "</span>");

        if (anyPresent(item, "volume", "number", "series")) {
            out.write(", ");
            writeVolume(item, false, "");
        }

        output(", <span class=\"pages\">", formatPages(item, true), "</span>");

        if (isPresent(item, "address")) {
            output(", <span class=\"address\">", get(item, "address"), "</span>, ");
            output("<span class=\"date\">", formatDate(item), "</span>.");

            if (isPresent(item, "publisher")) {
                output(" <span class=\"organization\">", get(item, "organization"), "</span>,");
                output(" <span class=\"publisher\">", get(item, "publisher"), "</span>.");
            } else {
                output(" <span class=\"organization\">", get(item, "organization"), "</span>.");
            }

            newline();
        } else if (anyPresent(item, "publisher", "organization")) {
            out.write(". ");
            output("<span class=\"organization\">", get(item, "organization"), "</span>, ");
            output("<span class=\"publisher\">", get(item, "publisher"), "</span>, ");
        } else {
            out.write(", ");
        }
    }

    protected void writeReport(BibItem item) throws IOException {
        output("<span class=\"type\">", toTitleCase(get(item, "type")), "</span>");
        output(" <span class=\"number\">", get(item, "number"), "</span>");
        out.write(", ");

        output("<span class=\"institution\">", get(item, "institution"), "</span>, ");
        output("<span class=\"address\">", get(item, "address"), "</span>, ");
    }

    protected void writeThesis(BibItem item) throws IOException {
        output("<span class=\"type\">", get(item, "type"), "</span>, ");
        output("<span class=\"school\">", get(item, "school"), "</span>, ");
        output("<span class=\"address\">", get(item, "address"), "</span>, ");
    }

    protected void writeUnpublished(BibItem item) throws IOException {
        output("<span class=\"note\">", get(item, "note"), "</span>, ");
    }

    protected void writeTitleAndAuthorsHTML(BibItem item) throws IOException {
        if (settings.getGeneralSettings().isTitleFirst()) {
            writeTitle(item);
        }

        writeAuthors(item);

        if (!settings.getGeneralSettings().isTitleFirst()) {
            writeTitle(item);
        }
    }

    protected void writeTitle(BibItem item) throws IOException {
        String title = formatTitle(item);

        if (item.getType() == Type.INBOOK && !isPresent(item, "volume")) {
            if (isPresent(item, "chapter")) {
                title += ", <span class=\"chapter\">";

                if (isPresent(item, "type")) {
                    title += toLowerCase(get(item, "type"));
                } else {
                    title += "chapter";
                }

                title += " " + get(item, "chapter") + "</span>";
            }

            if (isPresent(item, "pages")) {
                title += ", <span class=\"pages\">" + formatPages(item, true) + "</span>";
            }
        }

        out.write(indentString);

        // Title
        if (settings.getHtmlSettings().getTitleTarget() == HTMLSettings.TitleLinkTarget.ABSTRACT && includeAbstract(item)) {
            output("<h4 class=\"title abstract-toggle\">", title, "</h4>");
        } else if (settings.getHtmlSettings().getTitleTarget() == HTMLSettings.TitleLinkTarget.PAPER && includePaper(item)) {
            try {
                String href = (new URI(null, null, get(item, "file"), null)).toString();

                out.write("<a href=\"" + href + "\">");
                output("<h4 class=\"title\">", title, "</h4>");
                out.write("</a>");
            } catch (URISyntaxException ex) {
                Console.except(ex, "Paper link for entry \"%s\" is not formatted properly:", item.getId());
                output("<h4 class=\"title\">", title, "</h4>");
            }
        } else {
            output("<h4 class=\"title\">", title, "</h4>");
        }

        // Add text if I presented this paper
        if ("yes".equals(get(item, "presented"))) {
            output(" ", settings.getHtmlSettings().getPresentedText(), "");
        }

        // Abstract if necessary
        if (includeAbstract(item) && settings.getGeneralSettings().isUseNewLines()) {
            // Show \ hide link for the abstract
            if (settings.getHtmlSettings().getTitleTarget() != HTMLSettings.TitleLinkTarget.ABSTRACT) {
                out.newLine();
                writeToggleLink("abstract", "Abstract");
            }

            out.write("<br>");
            out.newLine();

            writeAbstract(item);
        } else if (settings.getGeneralSettings().isUseNewLines()) {
            newline();
        } else {
            out.write('.');
            newline(); // Still add a newline in the source
        }
    }

    protected void writeAbstract(BibItem item) throws IOException {
        out.write(indentString + "<div class=\"abstract-container tex2jax_ignore\">");
        out.write("<div class=\"abstract\">");
        out.newLine();
        out.write(indentString + "  <span class=\"abstractword\">Abstract: </span>");
        output(get(item, "abstract"));
        out.newLine();
        out.write(indentString + "</div></div>");
        out.newLine();
    }

    protected void writeAuthors(BibItem item) throws IOException {
        boolean useEditor;

        if (item.getType() == Type.PROCEEDINGS) {
            // Proceedings never prints the author
            if (isPresent(item, "editor")) {
                useEditor = true;
            } else if (isPresent(item, "organization")) {
                output(indentString + "<span class=\"organization\">", get(item, "organization"), "</span>.", true);
                return;
            } else {
                Console.error("No editor or organization found for entry \"%s\".", item.getId());
                return;
            }
        } else {
            if (isPresent(item, "author")) {
                useEditor = false;
            } else if (isPresent(item, "editor")) {
                useEditor = true;
            } else {
                Console.error("No author information found for entry \"%s\".", item.getId());
                return;
            }
        }

        List<Author> authorList = (useEditor ? item.getEditors() : item.getAuthors());

        // Don't add an authors line if it's just me and I just want to list co-authors
        if (settings.getGeneralSettings().isListAllAuthors() || authorList.size() > 1 || (authorList.size() == 1 && !authorList.get(0).isMe(settings.getGeneralSettings()))) {
            String authors = formatAuthors(item, useEditor, true);

            if (authors.endsWith(".</span>") || authors.endsWith(".</a>")) {
                // Don't double up on periods (occurs when author names are abbreviated and reversed)
                output(indentString, authors, "", true);
            } else {
                output(indentString, authors, ".", true);
            }
        }
    }

    private void writeVolume(BibItem item, boolean capitalize, String connective) throws IOException {
        String volume = get(item, "volume");
        String series = get(item, "series");
        String number = get(item, "number");

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
        output("<span class=\"publisher\">", get(item, "publisher"), "</span>, ");
        output("<span class=\"address\">", get(item, "address"), "</span>, ");

        String edition = get(item, "edition");

        if (edition != null && !edition.isEmpty()) {
            if (anyPresent(item, "publisher", "address")) {
                output("<span class=\"edition\">", toLowerCase(edition), " edition</span>, ");
            } else {
                output("<span class=\"edition\">", toTitleCase(edition), " edition</span>, ");
            }
        }
    }

    protected void writeChapter(BibItem item, boolean capitalize) throws IOException {
        String type = get(item, "type");
        String chapter = get(item, "chapter");

        if (chapter != null && !chapter.isEmpty()) {
            out.write("<span class=\"chapter\">");

            if (type != null && !type.isEmpty()) {
                output(capitalize ? toTitleCase(type) : toLowerCase(type));
            } else {
                out.write(capitalize ? "Chapter" : "chapter");
            }

            output(" ", chapter, "");

            out.write("</span>");
        }
    }

    protected void writeStatus(BibItem item) throws IOException {
        String venue;
        String venueType;

        switch (item.getType()) {
            case ARTICLE:
                venue = get(item, "journal");
                venueType = "article";
                break;
            case INPROCEEDINGS:
                venue = get(item, "booktitle");
                venueType = "booktitle";

                if (venue.startsWith("Proceedings of ")) {
                    venue = venue.substring("Proceedings of ".length());
                }

                break;
            default:
                venue = null;
                venueType = null;
                break;
        }

        out.write(indentString);

        if (venue == null) {
            switch (get(item, "pubstate")) {
                case "inpreparation":
                    output("In preparation.", true);
                    break;
                case "submitted":
                    output("Submitted for review.", true);
                    break;
                case "acceptedrev":
                    output("Accepted for publication, pending minor revisions.", true);
                    break;
                case "accepted":
                    output("Accepted for publication.", true);
                    break;
                case "forthcoming":
                    output("Forthcoming.", true);
                    break;
                case "inpress":
                    output("In press.", true);
                    break;
                case "prepublished":
                    output("Pre-published.", true);
                    break;
                default:
                    throw new AssertionError("Item \"" + item.getId() + "\" has an unrecognized pubstate: \"" + get(item, "pubstate") + "\"");
            }
        } else {
            switch (get(item, "pubstate")) {
                case "inpreparation":
                    output("In preparation for submission to ", false);
                    break;
                case "submitted":
                    output("Submitted to ", false);
                    break;
                case "acceptedrev":
                    output("Accepted, pending minor revisions, to ", false);
                    break;
                case "accepted":
                    output("Accepted to ", false);
                    break;
                case "forthcoming":
                    output("Forthcoming in ", false);
                    break;
                case "inpress":
                    output("In press with ", false);
                    break;
                case "prepublished":
                    output("Pre-published in ", false);
                    break;
                default:
                    throw new AssertionError("Item \"" + item.getId() + "\" has an unrecognized pubstate: \"" + get(item, "pubstate") + "\"");
            }

            output("<span class=\"" + venueType + "\">", venue, "</span>.", true);
        }
    }

    protected String formatPages(BibItem item, boolean verbose) {
        String pages = (verbose ? super.formatPages(item) : get(item, "pages"));

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
                    if (isPresent(item, "arxiv")) {
                        writeLinks(item, false, true);
                    } else {
                        writeLinks(item, false, false);
                    }
                    break;
                default:
                    if (PublicationStatus.ACCEPTED.matches(item)) {
                        writeLinks(item, true, false);
                    } else if (isPresent(item, "arxiv")) {
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
                String link = (new URI(null, null, get(item, "file"), null)).toString();
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
            } catch (URISyntaxException ex) {
                Console.except(ex, "Paper link for entry \"%s\" is not formatted properly:", item.getId());
            }
        }

        // arXiv link
        if (isPresent(item, "arxiv")) {
            writeLink(divOpened, "http://arxiv.org/abs/" + get(item, "arxiv"), "arXiv");
            divOpened = true;
        }

        // DOI link
        if (isPresent(item, "doi")) {
            String link = get(item, "doi");

            // Add the general DOI part if necessary 
            if (!link.startsWith("http://dx.doi.org/")) {
                link = "http://dx.doi.org/" + link;
            }

            writeLink(divOpened, link, "DOI");
            divOpened = true;
        }

        // ISSN link
        if (isPresent(item, "issn")) {
            String link = "http://www.worldcat.org/issn/" + get(item, "issn");
            writeLink(divOpened, link, "ISSN");
            divOpened = true;
        }

        // ISBN link
        if (isPresent(item, "isbn")) {
            String link = "http://www.worldcat.org/isbn/" + get(item, "isbn");
            writeLink(divOpened, link, "ISBN");
            divOpened = true;
        }

        // URL link
        if (isPresent(item, "url")) {
            String link = get(item, "url");
            boolean addUrl = true;

            // Don't add this link if it points to the arxiv and the item already has an arxiv link
            if (link.startsWith("http://arxiv.org/abs/") && isPresent(item, "arxiv")) {
                addUrl = false;
            }

            // Don't add it if it points to the DOI and the item already has a DOI link
            if (link.startsWith("http://dx.doi.org/") && isPresent(item, "doi")) {
                addUrl = false;
            }

            if (addUrl) {
                writeLink(divOpened, link, "URL");
                divOpened = true;
            }
        }

        // Other user-specified links
        divOpened = writeCustomLink(divOpened, item, -1); // link

        for (int i = 0; i < 20; i++) {
            divOpened = writeCustomLink(divOpened, item, i); // link<i>
        }

        // Close links div
        if (divOpened) {
            out.write(indentString + "</div>");
            out.newLine();
        }

        // Abstract link 
        if (includeAbstract(item)
                && !settings.getGeneralSettings().isUseNewLines()
                && settings.getHtmlSettings().getTitleTarget() != HTMLSettings.TitleLinkTarget.ABSTRACT) {
            writeToggleLink("abstract", "Abstract");
            out.newLine();
        }

        // BibTeX link
        if (includeBibtex || includeArxivBibtex) {
            // Show / hide link
            writeToggleLink("bibtex", "BibTeX");
            out.newLine();
        }

        // Actual abstract
        if (includeAbstract(item) && !settings.getGeneralSettings().isUseNewLines()) {
            writeAbstract(item);
        }

        // Actual BibTeX
        if (includeBibtex) {
            writeBibtexHTML(item);
        } else if (includeArxivBibtex) {
            writeArxivBibtexHTML(item);
        }
    }

    private void writeLink(boolean divOpened, String link, String text) throws IOException {
        if (!divOpened) {
            out.write(indentString + "<div class=\"links\">");
            out.newLine();
        }

        out.write(indentString + "  <a href=\"" + link + "\">" + text + "</a>");
        out.newLine();
    }

    private boolean writeCustomLink(boolean divOpened, BibItem item, int i) throws IOException {
        String attribute = (i == -1 ? "link" : "link" + i);

        if (isPresent(item, attribute)) {
            String link = get(item, attribute);
            int divider = link.indexOf('|');

            if (divider == -1) {
                Console.error("No divider \"|\" found in %s of item \"%s\". Links should be formatted as%n  %s={Link text|Link target}", attribute, item.getId(), attribute);
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
        out.write(indentString + "<div class=\"bibtex-container\">");
        out.newLine();
        out.write(indentString + "  <pre class=\"bibtex\">");
        out.newLine();

        String bibtex = htmlEscape(getBibtex(item));
        out.write(bibtex);

        out.write("</pre>"); // No indent, as this would end up as part of the BibTeX
        out.newLine();

        out.write(indentString + "</div>");
        out.newLine();
    }

    private String getBibtex(BibItem item) throws IOException {
        StringWriter bibtex = new StringWriter();
        BufferedWriter buffer = new BufferedWriter(bibtex);
        BibItemWriter bibtexWriter = new BibtexBibItemWriter(buffer, settings);
        
        bibtexWriter.write(item);
        buffer.flush();

        return bibtex.toString();
    }

    private void writeArxivBibtexHTML(BibItem item) throws IOException {
        out.write(indentString + "<div class=\"bibtex-container\">");
        out.newLine();
        out.write(indentString + "  <pre class=\"bibtex\">");
        out.newLine();

        // Item type
        out.write("@article{" + htmlEscape(item.getId()) + ",");
        out.newLine();

        // The first field should omit the connecting ",".
        boolean first = true;

        // Get the proper format for authors
        if (isPresent(item, "author")) {
            out.write("  author={");

            for (int i = 0; i < item.getAuthors().size(); i++) {
                out.write(htmlEscape(item.getAuthors().get(i).getName()));

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

            out.write(htmlEscape("  " + field + "={" + get(item, field) + "}"));
        }

        out.write(",");
        out.newLine();
        out.write("  journal={ArXiv e-prints},");
        out.newLine();
        out.write("  archivePrefix={arXiv},");
        out.newLine();
        out.write("  eprint={" + htmlEscape(get(item, "arxiv")) + "},");
        out.newLine();

        if (isPresent(item, "primaryclass")) {
            out.write("  primaryClass={" + htmlEscape(get(item, "primaryclass")) + "},");
            out.newLine();
            out.write(String.format("  note={\\href{http://arxiv.org/abs/%s}{arXiv:%s} [%s]},", htmlEscape(get(item, "arxiv")), htmlEscape(get(item, "arxiv")), htmlEscape(get(item, "primaryclass"))));
            out.newLine();
        } else {
            out.write(String.format("  note={\\href{http://arxiv.org/abs/%s}{arXiv:%s}},", htmlEscape(get(item, "arxiv")), htmlEscape(get(item, "arxiv"))));
            out.newLine();
        }

        out.write(String.format("  url={http://arxiv.org/abs/%s}", htmlEscape(get(item, "arxiv"))));

        out.newLine(); // No comma after the last element
        out.write("}</pre>");
        out.newLine();

        out.write(indentString + "</div>");
        out.newLine();
    }

    private void writeToggleLink(String type, String text) throws IOException {
        out.write(indentString + "<button class=\"" + type + "-toggle\">");
        out.write(text);
        out.write("</button>");
    }

    private boolean includeAbstract(BibItem item) {
        return isPresent(item, "abstract") && settings.getHtmlSettings().getIncludeAbstract().matches(item);
    }

    private boolean includeBibtex(BibItem item) {
        return settings.getHtmlSettings().getIncludeBibtex().matches(item);
    }

    private boolean includePaper(BibItem item) {
        return isPresent(item, "file") && settings.getHtmlSettings().getIncludePaper().matches(item);
    }

    @Override
    protected void newline() throws IOException {
        if (settings.getGeneralSettings().isUseNewLines()) {
            out.write("<br>"); // Also add the new line to the web page
        }

        out.newLine();
    }
    
    private String htmlEscape(String text) {
        return text.replaceAll("&", "&amp;")
                .replaceAll("\u00A0", "&nbsp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }
}
