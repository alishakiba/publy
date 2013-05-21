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
        // Convert a name in format <Last name(s)>, <First name(s)> to <First letter of first name(s)> <Last name(s)>
        if (!name.contains(",")) {
            System.err.println("No comma! Name: " + name);
        }

        String last = name.substring(0, name.indexOf(", "));
        String first = name.substring(name.indexOf(", ") + 2);

        return Character.toUpperCase(first.charAt(0)) + ". " + last;
    }
    
    @Override
    public String toString() {
        return "Author{" + "abbreviation=\"" + abbreviation + "\", latexName=\"" + latexName + "\", htmlName=\"" + htmlName + "\", url=\"" + url + "\"}";
    }
}
