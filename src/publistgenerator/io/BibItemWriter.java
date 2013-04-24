/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import publistgenerator.bibitem.*;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public abstract class BibItemWriter {

    protected BufferedWriter out;
    
    public BibItemWriter(BufferedWriter out) {
        this.out = out;
    }
    
    public void write(BibItem item) throws IOException {
        if (item instanceof Article) {
            write((Article) item);
        } else if (item instanceof InProceedings) {
            write((InProceedings) item);
        } else if (item instanceof MastersThesis) {
            write((MastersThesis) item);
        } else if (item instanceof PhDThesis) {
            write((PhDThesis) item);
        } else if (item instanceof InCollection) {
            write((InCollection) item);
        } else if (item instanceof InvitedTalk) {
            write((InvitedTalk) item);
        } else if (item instanceof Unpublished) {
            write((Unpublished) item);
        } else {
            throw new InternalError("Unrecognized BibItem type: " + item.getType());
        }
    }

    public abstract void write(Article item) throws IOException;

    public abstract void write(InProceedings item) throws IOException;

    public abstract void write(MastersThesis item) throws IOException;

    public abstract void write(PhDThesis item) throws IOException;
    
    public abstract void write(InCollection item) throws IOException;
    
    public abstract void write(InvitedTalk item) throws IOException;
    
    public abstract void write(Unpublished item) throws IOException;

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
            return "";
        } else {
            List<String> authorLinks = new ArrayList<>(item.getAuthors().size());

            for (Author a : item.getAuthors()) {
                authorLinks.add(a.getHtmlName());
            }

            return formatNames(authorLinks);
        }
    }

    protected String formatNames(List<String> names) {
        StringBuilder result = new StringBuilder();

        int namePtr = 1;
        int numNames = names.size();
        int namesLeft = numNames;

        while (namesLeft > 0) {
            String name = names.get(namePtr - 1);

            if (namePtr > 1) {
                if (namesLeft > 1) {
                    result.append(", ");
                    result.append(name);
                } else {
                    if (numNames > 2) {
                        result.append(",");
                    }

                    if (name.equals("others")) {
                        result.append(" et~al.");
                    } else {
                        result.append(" and ");
                        result.append(name);
                    }
                }
            } else {
                result.append(name);
            }

            namePtr++;
            namesLeft--;
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
