/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import publistgenerator.data.settings.FormatSettings;

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

    public void write(BibItem item, int number) throws IOException {
        if (item instanceof Article) {
            write((Article) item, number);
        } else if (item instanceof InProceedings) {
            write((InProceedings) item, number);
        } else if (item instanceof MastersThesis) {
            write((MastersThesis) item, number);
        } else if (item instanceof PhDThesis) {
            write((PhDThesis) item, number);
        } else if (item instanceof InCollection) {
            write((InCollection) item, number);
        } else if (item instanceof InvitedTalk) {
            write((InvitedTalk) item, number);
        } else if (item instanceof Unpublished) {
            write((Unpublished) item, number);
        } else {
            throw new InternalError("Unrecognized BibItem type: " + item.getType());
        }
    }

    public abstract void write(Article item, int number) throws IOException;

    public abstract void write(InProceedings item, int number) throws IOException;

    public abstract void write(MastersThesis item, int number) throws IOException;

    public abstract void write(PhDThesis item, int number) throws IOException;

    public abstract void write(InCollection item, int number) throws IOException;

    public abstract void write(InvitedTalk item, int number) throws IOException;

    public abstract void write(Unpublished item, int number) throws IOException;

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
                        authorLinks.add(a.getHtmlName());
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

            if (i > 0) {
                if (i == names.size() - 1) { // Last name
                    if (names.size() > 2) {
                        result.append(",");
                    }

                    if (name.equals("others")) {
                        result.append(" et~al.");
                    } else {
                        result.append(" and ");
                        result.append(name);
                    }
                } else {
                    result.append(", ");
                    result.append(name);
                }
            } else {
                result.append(name);
            }
        }

        return result.toString();
    }

    protected String formatNumberSeries(BibItem item) {
        String volume = item.get("volume");

        if (volume == null || volume.isEmpty()) {

            String number = item.get("number");

            if (number == null || number.isEmpty()) {
                String series = item.get("series");

                if (series == null || series.isEmpty()) {
                    return "";
                } else {
                    return series;
                }
            } else {
                String result;

                // TODO: fix better
                /* if (outputState == OutputState.MID_SENTENCE) {
                 result = "number " + number;
                 } else {
                 result = "Number " + number;
                 }*/
                result = "number " + number;

                String series = item.get("series");

                if (series == null || series.isEmpty()) {
                    // bad
                    return result;
                } else {
                    return result + " in " + series;
                }
            }
        } else {
            return "";
        }
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

    protected String escapeMath(String s) {
        return s.replaceAll("([^\\\\])\\$", "$1\\\\\\$");
    }

    protected String outputOld(String string) {
        if (string == null) {
            return "";
        } else {
            return string;
        }
    }

    protected String outputOld(String string, String connective) {
        if (string == null || string.isEmpty()) {
            return "";
        } else {
            return string + connective;
        }
    }

    protected void output(String string) throws IOException {
        if (string != null) {
            out.write(string);
        }
    }

    protected void output(String string, String connective) throws IOException {
        output(string, connective, false);
    }

    protected void output(String string, String connective, boolean newLine) throws IOException {
        if (string != null && !string.isEmpty()) {
            out.write(string);
            out.write(connective);

            if (newLine) {
                out.newLine();
            }
        }
    }

    // TODO: remove? Seems unused
    protected String addPeriod(String string) {
        int i = string.length() - 1;

        while (string.charAt(i) == '}') {
            i--;
        }

        switch (string.charAt(i)) {
            case '.':
            case '!':
            case '?':
                return string;
            default:
                return string + "."; // TODO: does not work when string ends with braces
        }
    }

    protected String changeCaseT(String s) {
        // TODO: handle escaped brackets
        if (s.contains("{")) {
            StringBuilder sb = new StringBuilder();
            int level = 0;

            for (char c : s.toCharArray()) {
                switch (c) {
                    case '{':
                        level++;
                        break;
                    case '}':
                        level--;
                        break;
                    default:
                        if (level == 0) {
                            sb.append(Character.toLowerCase(c));
                        } else {
                            sb.append(c);
                        }
                        break;
                }
            }

            return sb.toString();
        } else {
            return Character.toUpperCase(s.charAt(0)) + s.toLowerCase().substring(1);
        }
    }
}
