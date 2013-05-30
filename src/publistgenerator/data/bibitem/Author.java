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

    private String abbreviation, plaintextName, latexName, htmlName, url;

    public Author(String abbreviation, String plaintextName, String latexName, String htmlName) {
        this.abbreviation = abbreviation;
        this.plaintextName = plaintextName;
        this.latexName = latexName;
        this.htmlName = htmlName;
    }

    public Author(String latexName) {
        this(latexName, latexName, latexName, latexName);
    }
    
    public Author(String abbreviation, String latexName) {
        this(abbreviation, latexName, latexName, latexName);
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getPlaintextName() {
        return plaintextName;
    }
    
    public String getFormattedPlaintextName() {
        return formatName(plaintextName);
    }

    public void setPlaintextName(String plaintextName) {
        this.plaintextName = plaintextName;
    }

    public String getHtmlName() {
        return htmlName;
    }
    
    public String getFormattedHtmlName() {
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
        return latexName;
    }
    
    public String getFormattedLatexName() {
        return formatName(latexName);
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
        int space = name.lastIndexOf(' ');
        
        if (comma != -1) {
            // Convert a name in format <Last name(s)>, <First name(s)> to <First letter of first name(s)> <Last name(s)>
            last = name.substring(0, comma).trim();
            first = name.substring(comma + 1).trim();
        } else if (space != -1) {
            // Assume the format is "<First name(s)> <Last name>"
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
        return "Author{" + "abbreviation=\"" + abbreviation + "\", latexName=\"" + latexName + "\", plaintextname=\"" + plaintextName + "\", htmlName=\"" + htmlName + "\", url=\"" + url + "\"}";
    }
}
