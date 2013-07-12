/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publy.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import publy.Console;
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
import publy.data.settings.FormatSettings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public abstract class BibItemWriter {

    protected BufferedWriter out;
    protected FormatSettings settings;

    public BibItemWriter(BufferedWriter out, FormatSettings settings) {
        this.out = out;
        this.settings = settings;
    }

    public void write(BibItem item) throws IOException {
        switch (item.getType()) {
            case "article":
                writeArticle((Article) item);
                break;
            case "book":
                writeBook((Book) item);
                break;
            case "inproceedings":
                writeInProceedings((InProceedings) item);
                break;
            case "mastersthesis":
                writeMastersThesis((MastersThesis) item);
                break;
            case "phdthesis":
                writePhDThesis((PhDThesis) item);
                break;
            case "incollection":
                writeInCollection((InCollection) item);
                break;
            case "talk":
                writeInvitedTalk((InvitedTalk) item);
                break;
            case "unpublished":
                writeUnpublished((Unpublished) item);
                break;
            default:
                throw new AssertionError("Unrecognized BibItem type: " + item.getType());
        }
    }

    protected abstract void writeArticle(Article item) throws IOException;

    protected abstract void writeBook(Book item) throws IOException;

    protected abstract void writeInProceedings(InProceedings item) throws IOException;

    protected abstract void writeMastersThesis(MastersThesis item) throws IOException;

    protected abstract void writePhDThesis(PhDThesis item) throws IOException;

    protected abstract void writeInCollection(InCollection item) throws IOException;

    protected abstract void writeInvitedTalk(InvitedTalk item) throws IOException;

    protected abstract void writeUnpublished(Unpublished item) throws IOException;

    protected String formatTitle(BibItem item) {
        String title = item.get("title");

        if (title == null || title.isEmpty()) {
            return "";
        } else {
            return changeCaseT(title);
        }
    }

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
                        authorLinks.add(a.getFormattedHtmlName());
                    }
                }
            }

            return formatNames(authorLinks);
        }
    }

    protected String formatNames(List<String> names) {
        StringBuilder result = new StringBuilder();

        if (!settings.isListAllAuthors()) {
            result.append("With ");
        }

        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);

            if (i == 0) {
                // First author
                result.append(name);
            } else if (i < names.size() - 1) {
                // Middle author
                result.append(", ");
                result.append(name);
            } else {
                // Last author
                if (names.size() > 2) {
                    result.append(",");
                }

                if (name.equals("others")) {
                    result.append(" et~al.");
                } else {
                    result.append(" and ");
                    result.append(name);
                }
            }
        }

        return result.toString();
    }

    protected String formatPages(BibItem item) {
        String pages = item.get("pages");

        if (pages == null || pages.isEmpty()) {
            return "";
        } else {
            if (pages.contains("-") || pages.contains("+") || pages.contains(",")) {
                return "pages " + pages;
            } else {
                return "page " + pages;
            }
        }
    }

    protected String formatDate(BibItem item) {
        String year = item.get("year");
        String month = item.get("month");

        if (year == null || year.isEmpty()) {
            if (month == null || month.isEmpty()) {
                return "";
            } else {
                return month;
            }
        } else {
            if (month == null || month.isEmpty()) {
                return year;
            } else {
                return month + " " + year;
            }
        }
    }

    protected void output(String string) throws IOException {
        output("", string, "", false);
    }

    protected void output(String string, String connective) throws IOException {
        output("", string, connective, false);
    }

    protected void output(String prefix, String string, String connective) throws IOException {
        output(prefix, string, connective, false);
    }

    protected void output(String string, String connective, boolean newLine) throws IOException {
        output("", string, connective, newLine);
    }

    protected void output(String prefix, String string, String connective, boolean newLine) throws IOException {
        if (string != null && !string.isEmpty()) {
            out.write(prefix);
            out.write(removeBraces(LatexToUnicode.convertToUnicode(string)));
            out.write(connective);

            if (newLine) {
                out.newLine();
            }
        }
    }

    protected String changeCaseT(String s) {
        StringBuilder sb = new StringBuilder();
        int level = 0;
        boolean first = true;
        boolean escape = false;

        for (char c : s.toCharArray()) {
            if (escape) {
                sb.append(c);
                escape = false;
            } else {
                switch (c) {
                    case '{':
                        level++;
                        break;
                    case '}':
                        level--;
                        break;
                    case '\\':
                        if (level == 0) {
                            escape = true;
                        }
                        
                        sb.append(c);
                        break;
                    default:
                        if (level == 0) {
                            if (first) {
                                sb.append(Character.toUpperCase(c));
                                first = false;
                            } else {
                                sb.append(Character.toLowerCase(c));
                            }
                        } else {
                            sb.append(c);
                            first = false;
                        }

                        break;
                }
            }
        }

        return sb.toString();
    }

    protected String removeBraces(String field) {
        StringBuilder result = new StringBuilder(field.length());
        boolean escape = false;
        
        for (char c : field.toCharArray()) {
            if (!escape) {
                switch (c) {
                    case '{': // Remove
                        break;
                    case '}': // Remove
                        break;
                    case '\\':
                        escape = true;
                        break;
                    default:
                        result.append(c);
                        break;
                }
            } else {
                if (c != '{' && c != '}') {
                    // Only add the escaping slash for non-braces
                    result.append('\\');
                }
                
                result.append(c);
                escape = false;
            }
        }
        
        return result.toString();
    }
}
