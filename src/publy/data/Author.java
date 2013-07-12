/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publy.data;

import java.util.ArrayList;
import java.util.List;
import publy.Console;

/**
 *
 * @author Sander
 */
public class Author {

    private String abbreviation; // The abbreviation associated with this author in the bibtex file
    private String firstName = "", lastName = "", vonPart = "", juniorPart = ""; // The four parts of this name
    private String plaintextName, latexName, htmlName; // Possible name overrides from the bibtex file
    private String url; // The url associated with this author

    public Author(String name) {
        this(name, name);
    }

    public Author(String abbreviation, String name) {
        this.abbreviation = abbreviation;

        splitName(name);
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getVonPart() {
        return vonPart;
    }

    public String getJuniorPart() {
        return juniorPart;
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

    private void splitName(String name) {
        // Sanitize
        String cleanName = name.replaceAll("\\s+", " ").trim();

        // Separate into parts
        List<String> commaParts = getParts(cleanName, ',');

        if (commaParts.size() == 1) {
            // First von Last format
            List<String> spaceParts = getParts(cleanName, ' ');

            // Collect the first name
            // Check all except the last part, as the last name can never be empty
            int firstNameIndex = 0; // First part that does not belong to the first name

            while (firstNameIndex < spaceParts.size() - 1 && !startsWithLowercaseLetter(spaceParts.get(firstNameIndex))) {
                if (!spaceParts.get(firstNameIndex).isEmpty()) {
                    firstName = (firstName.isEmpty() ? spaceParts.get(firstNameIndex) : firstName + " " + spaceParts.get(firstNameIndex));
                }

                firstNameIndex++;
            }

            // Collect the remaining von Last part
            parseVonLast(spaceParts.subList(firstNameIndex, spaceParts.size()));
        } else if (commaParts.size() == 2) {
            // von Last, First format
            parseVonLast(getParts(commaParts.get(0).trim(), ' '));
            firstName = commaParts.get(1).trim();
        } else if (commaParts.size() == 3) {
            // von Last, Jr, First format
            parseVonLast(getParts(commaParts.get(0).trim(), ' '));
            juniorPart = commaParts.get(1).trim();
            firstName = commaParts.get(2).trim();
        } else {
            Console.error("Name has too many comma-separated parts: %s%nCommas that are part of a name should be contained in braces \"{}\".", name);
        }
    }

    private void parseVonLast(List<String> parts) {
        // Collect the last name
        int lastNameIndex = parts.size() - 2; // Last part that does not belong to the last name
        lastName = parts.get(parts.size() - 1); // The last part always belongs to the last name

        while (lastNameIndex >= 0 && !startsWithLowercaseLetter(parts.get(lastNameIndex))) {
            if (!parts.get(lastNameIndex).isEmpty()) {
                lastName = parts.get(lastNameIndex) + " " + lastName;
            }
            lastNameIndex--;
        }

        // Collect the von part (the rest)
        for (int i = 0; i <= lastNameIndex; i++) {
            if (!parts.get(i).isEmpty()) {
                vonPart = (vonPart.isEmpty() ? parts.get(i) : vonPart + " " + parts.get(i));
            }
        }
    }

    private List<String> getParts(String name, char separator) {
        List<String> parts = new ArrayList<>(4);
        StringBuilder sb = new StringBuilder(name.length());
        boolean escape = false;
        int braceLevel = 0;

        for (char c : name.toCharArray()) {
            if (escape) {
                sb.append(c);
                escape = false;
            } else {
                switch (c) {
                    case '{':
                        braceLevel++;
                        sb.append('{');
                        break;
                    case '}':
                        braceLevel--;
                        sb.append('}');
                        break;
                    case '\\':
                        sb.append('\\');
                        escape = true;
                        break;
                    default:
                        if (braceLevel == 0 && c == separator) {
                            parts.add(sb.toString());
                            sb.delete(0, sb.length());
                        } else {
                            sb.append(c);
                        }
                }
            }
        }

        parts.add(sb.toString());

        return parts;
    }

    private boolean startsWithLowercaseLetter(String word) {
        boolean escape = false;
        int braceLevel = 0;

        for (char c : word.toCharArray()) {
            if (escape) {
                if (Character.isLetter(c) && Character.isLowerCase(c)) {
                    return true;
                } else if (Character.isLetter(c) && Character.isUpperCase(c)) {
                    return false;
                }

                escape = false;
            } else {
                switch (c) {
                    case '{':
                        braceLevel++;
                        break;
                    case '}':
                        braceLevel--;
                        break;
                    case '\\':
                        escape = true;
                        break;
                    default:
                        if (braceLevel == 0 && Character.isLetter(c)) {
                            if (Character.isLowerCase(c)) {
                                return true;
                            } else if (Character.isUpperCase(c)) {
                                return false;
                            }
                        }
                }
            }
        }

        return false; // caseless
    }

    @Override
    public String toString() {
        return "Author{" + "abbreviation=\"" + abbreviation + "\", latexName=\"" + latexName + "\", plaintextname=\"" + plaintextName + "\", htmlName=\"" + htmlName + "\", url=\"" + url + "\"}";
    }
}
