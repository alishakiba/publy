/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.data.bibitem;

/**
 *
 * @author Sander
 */
public class Author {

    private String abbreviation, latexName, htmlName, url;

    public Author(String abbreviation, String latexName, String htmlName) {
        this.abbreviation = abbreviation;
        this.latexName = latexName;
        this.htmlName = htmlName;
    }

    public Author(String author) {
        this(author, author, author);
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getHtmlName() {
        return formatName(htmlName);
    }

    public String getLinkedHtmlName() {
        if (url != null && !url.isEmpty()) {
            return "<a href=\"" + url + "\" class=\"author\">" + formatName(htmlName) + "</a>";
        } else {
            return "<span class=\"author\">" + formatName(htmlName) + "</span>";
        }
    }

    public void setHtmlName(String htmlName) {
        this.htmlName = htmlName;
    }

    public String getLatexName() {
        return formatName(latexName);
    }

    public String getRawLatexName() {
        return latexName;
    }

    public void setLatexName(String latexName) {
        this.latexName = latexName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isMe() {
        return "me".equals(abbreviation);
    }

    private String formatName(String name) {
        String first, last;
        int comma = name.indexOf(',');
        int space = name.indexOf(' ');
        
        if (comma != -1) {
            // Convert a name in format <Last name(s)>, <First name(s)> to <First letter of first name(s)> <Last name(s)>
            last = name.substring(0, comma).trim();
            first = name.substring(comma + 1).trim();
        } else if (space != -1) {
            // Assume the format is "<First name> <Last name(s)>"
            first = name.substring(0, space).trim();
            last = name.substring(space + 1).trim();
        } else {
            // Unknown format, or just the last name
            return name;
        }
        
        return Character.toUpperCase(first.charAt(0)) + ". " + last;
    }

    @Override
    public String toString() {
        return "Author{" + "abbreviation=\"" + abbreviation + "\", latexName=\"" + latexName + "\", htmlName=\"" + htmlName + "\", url=\"" + url + "\"}";
    }
}
