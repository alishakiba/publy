/*
 * Copyright 2013-2014 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
import java.util.List;
import publy.Console;
import publy.data.PublicationType;
import publy.data.Author;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.Type;
import publy.data.category.OutputCategory;
import publy.data.settings.HTMLSettings;
import publy.data.settings.Settings;
import publy.io.BibItemWriter;
import publy.io.bibtex.BibtexBibItemWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLBibItemWriter extends BibItemWriter {

    private static final String indent = "          ";
    private final BibtexBibItemWriter bibtexWriter;
    private final List<OutputCategory> categories;

    public HTMLBibItemWriter(BufferedWriter out, List<OutputCategory> categories, Settings settings) {
        super(out, settings);
        bibtexWriter = new BibtexBibItemWriter(out, settings);
        this.categories = categories;
    }

    @Override
    public void write(BibItem item) throws IOException {
        writeTitleAndAuthorsHTML(item);

        if (isPresent(item, "status")) {
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
            output(indent + "<span class=\"note\">", get(item, "note"), ".</span>", true);
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
            output("<span class=\"editor\">", formatAuthors(item, true, Author.NameOutputType.LINKED_HTML), ", </span>");
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
            output("<span class=\"editor\">", formatAuthors(item, true, Author.NameOutputType.LINKED_HTML), ", </span>");
        }

        output("<span class=\"booktitle\">", get(item, "booktitle"), "</span>");

        if (anyPresent(item, "volume", "number", "series")) {
            out.write(", ");
            writeVolume(item, false, "");
        }

        output(", <span class=\"pages\">", formatPages(item, true), "</span>");

        if (isPresent(item, "address")) {
            output(", <span class=\"address\">", get(item, "address"), "</span>, ");
            output("<span class=\"date\">", formatDate(item), "</span>.", true);

            if (isPresent(item, "publisher")) {
                output("<span class=\"organization\">", get(item, "organization"), "</span>, ");
                output("<span class=\"publisher\">", get(item, "publisher"), "</span>. ");
            } else {
                output("<span class=\"organization\">", get(item, "organization"), "</span>. ");
            }
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
        out.write(indent);

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

        // Title
        if (settings.getHtmlSettings().getTitleTarget() == HTMLSettings.TitleLinkTarget.ABSTRACT && includeAbstract(item)) {
            output("<h3 class=\"title abstract-toggle\">", title, "</h3>");
        } else if (settings.getHtmlSettings().getTitleTarget() == HTMLSettings.TitleLinkTarget.PAPER && includePaper(item)) {
            try {
                String href = (new URI(null, null, get(item, "file"), null)).toString();

                out.write("<a href=\"" + href + "\">");
                output("<h3 class=\"title\">", title, "</h3>");
                out.write("</a>");
                checkExistance(get(item, "file"), "file", item);
            } catch (URISyntaxException ex) {
                Console.except(ex, "Paper link for entry \"%s\" is not formatted properly:", item.getId());
                output("<h3 class=\"title\">", title, "</h3>");
            }
        } else {
            output("<h3 class=\"title\">", title, "</h3>");
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
            out.write("<br>");
            out.newLine();
        } else {
            out.write('.');
        }
    }

    protected void writeAbstract(BibItem item) throws IOException {
        out.write(indent + "<div class=\"abstract-container\">");
        out.write("<div class=\"abstract\">");
        out.newLine();
        out.write(indent + "  <span class=\"abstractword\">Abstract: </span>");
        output(get(item, "abstract"));
        out.newLine();
        out.write(indent + "</div></div>");
        out.newLine();
    }

    protected void writeAuthors(BibItem item) throws IOException {
        boolean useEditor;

        if (item.getType() == Type.PROCEEDINGS) {
            // Proceedings never prints the author
            if (isPresent(item, "editor")) {
                useEditor = true;
            } else if (isPresent(item, "organization")) {
                output(indent + "<span class=\"organization\">", get(item, "organization"), "</span>.", true);
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
            String authors = formatAuthors(item, useEditor, Author.NameOutputType.LINKED_HTML);

            if (authors.endsWith(".</span>") || authors.endsWith(".</a>")) {
                // Don't double up on periods (occurs when author names are abbreviated and reversed)
                output(indent, authors, "", true);
            } else {
                output(indent, authors, ".", true);
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
        String title;

        switch (item.getType()) {
            case ARTICLE:
                title = get(item, "journal");
                break;
            case INPROCEEDINGS:
                title = get(item, "booktitle");

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
            switch (get(item, "status")) {
                case "submitted":
                    output("Submitted for review.", true);
                    break;
                case "accepted":
                    output("Accepted for publication.", true);
                    break;
                case "acceptedrev":
                    output("Accepted for publication, pending minor revisions.", true);
                    break;
                default:
                    throw new AssertionError("Item \"" + item.getId() + "\" has an unrecognized status: \"" + get(item, "status") + "\"");
            }
        } else {
            switch (get(item, "status")) {
                case "submitted":
                    output("Submitted to <span class=\"booktitle\">", title, "</span>.", true);
                    break;
                case "accepted":
                    output("Accepted to <span class=\"booktitle\">", title, "</span>.", true);
                    break;
                case "acceptedrev":
                    output("Accepted, pending minor revisions, to <span class=\"booktitle\">", title, "</span>.", true);
                    break;
                default:
                    throw new AssertionError("Item \"" + item.getId() + "\" has an unrecognized status: \"" + get(item, "status") + "\"");
            }
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
                    if (PublicationType.ACCEPTED.matches(item)) {
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

                checkExistance(get(item, "file"), "file", item);
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

            // Don't add this link if it points to the arxiv and the item already has an arxiv link
            if (!(link.startsWith("http://arxiv.org/abs/") && isPresent(item, "arxiv"))) {
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
            out.write(indent + "</div>");
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
            out.write(indent + "<div class=\"links\">");
            out.newLine();
        }

        out.write(indent + "  <a href=\"" + link + "\">" + text + "</a>");
        out.newLine();
    }

    private boolean writeCustomLink(boolean divOpened, BibItem item, int i) throws IOException {
        String attribute = (i == -1 ? "link" : "link" + i);

        if (isPresent(item, attribute)) {
            String link = get(item, attribute);
            int divider = link.indexOf('|');

            if (divider == -1) {
                Console.error("No divider \"|\" found in %s of item \"%s\". Links should be formatted as%n  %s={<Link text>|<Link target>}", attribute, item.getId(), attribute);
            } else {
                String text = link.substring(0, divider);
                String target = link.substring(divider + 1);

                if (target.startsWith("#")) {
                    // Link to another paper, good as-is
                    checkIdExistance(target.substring(1), attribute, item);
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

        bibtexWriter.write(item);

        out.write("</pre>");
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
        if (isPresent(item, "author")) {
            out.write("  author={");

            for (int i = 0; i < item.getAuthors().size(); i++) {
                out.write(item.getAuthors().get(i).getName(Author.NameOutputType.LATEX));

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

            out.write("  " + field + "={" + get(item, field) + "}");
        }

        out.write(",");
        out.newLine();
        out.write("  journal={ArXiv e-prints},");
        out.newLine();
        out.write("  archivePrefix={arXiv},");
        out.newLine();
        out.write("  eprint={" + get(item, "arxiv") + "},");
        out.newLine();

        if (isPresent(item, "primaryclass")) {
            out.write("  primaryClass={" + get(item, "primaryclass") + "},");
            out.newLine();
            out.write(String.format("  note={\\href{http://arxiv.org/abs/%s}{arXiv:%s} [%s]},", get(item, "arxiv"), get(item, "arxiv"), get(item, "primaryclass")));
            out.newLine();
        } else {
            out.write(String.format("  note={\\href{http://arxiv.org/abs/%s}{arXiv:%s}},", get(item, "arxiv"), get(item, "arxiv")));
            out.newLine();
        }

        out.write(String.format("  url={http://arxiv.org/abs/%s}", get(item, "arxiv")));

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
        return isPresent(item, "abstract") && settings.getHtmlSettings().getIncludeAbstract().matches(item);
    }

    private boolean includeBibtex(BibItem item) {
        return settings.getHtmlSettings().getIncludeBibtex().matches(item);
    }

    private boolean includePaper(BibItem item) {
        return isPresent(item, "file") && settings.getHtmlSettings().getIncludePaper().matches(item);
    }

    private void checkExistance(String path, String attr, BibItem item) {
        Path file = settings.getFileSettings().getTarget().resolveSibling(path);

        if (Files.notExists(file)) {
            Console.warn(Console.WarningType.MISSING_REFERENCE, "File \"%s\" (linked in attribute \"%s\" of publication \"%s\") cannot be found at \"%s\".", path, attr, item.getId(), file);
        }
    }

    /**
     * Checks whether a BibItem with the given id exists, gives a warning when
     * it doesn't.
     *
     * @param id
     */
    private void checkIdExistance(String id, String attr, BibItem item) {
        for (OutputCategory cat : categories) {
            for (BibItem i : cat.getItems()) {
                if (i.getId().equals(id)) {
                    return;
                }
            }
        }

        Console.warn(Console.WarningType.MISSING_REFERENCE, "Publication \"%s\" (linked in attribute \"%s\" of publication \"%s\") is not in the final list.", id, attr, item.getId());
    }

    @Override
    protected void newline() throws IOException {
        if (settings.getGeneralSettings().isUseNewLines()) {
            out.write("<br>"); // Add a new line in both the web page and source file
            out.newLine();
        } else {
            out.write(' ');
        }
    }
}
