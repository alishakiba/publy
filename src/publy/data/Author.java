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
package publy.data;

import java.util.ArrayList;
import java.util.List;
import publy.Console;
import publy.data.settings.GeneralSettings;

/**
 *
 *
 */
public class Author {

    public enum NameOutputType {

        LATEX, PLAINTEXT, HTML, LINKED_HTML;
    }
    private String abbreviation; // The abbreviation associated with this author in the bibtex file
    private String firstName = "", lastName = "", vonPart = "", juniorPart = ""; // The four parts of this name
    private String url; // The url associated with this author
    private String group; // The group associated with this author
    private String name; // The name in LaTeX format, as given in the input

    public Author(String name) {
        this(name, name);
    }

    public Author(String abbreviation, String name) {
        this.abbreviation = abbreviation;
        this.name = name;
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

    /**
     * Returns the name in LaTeX format, exactly as specified in the input file.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkedHTMLName(GeneralSettings.NameDisplay display, boolean reversed) {
        String result = getFormattedName(display, reversed);

        String classes = "author";

        if (group != null && !group.isEmpty()) {
            classes += " " + group;
        }

        if (url != null && !url.isEmpty()) {
            result = "<a href=\"" + url + "\" class=\"" + classes + "\">" + result + "</a>";
        } else {
            result = "<span class=\"" + classes + "\">" + result + "</span>";
        }

        return result;
    }

    public String getFormattedName(GeneralSettings.NameDisplay display, boolean reversed) {
        // First von Last, Jr OR von Last, First, Jr
        String result = "";

        if (!reversed && display != GeneralSettings.NameDisplay.NONE && !firstName.isEmpty()) {
            result = formatFirstName(display) + " ";
        }

        // von Last
        if (!vonPart.isEmpty()) {
            result += vonPart + " ";
        }
        result += lastName;

        // Junior
        if (!juniorPart.isEmpty()) {
            result += ", " + juniorPart;
        }

        if (reversed && display != GeneralSettings.NameDisplay.NONE && !firstName.isEmpty()) {
            result += ", " + formatFirstName(display);
        }

        return result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isMe(GeneralSettings gs) {
        for (String myName : gs.getMyNames()) {
            if (name.equals(myName) || abbreviation.equals(myName) || getFormattedName(gs.getNameDisplay(), gs.isReverseNames()).equals(myName)) {
                return true;
            }
        }

        return false;
    }

    private String formatFirstName(GeneralSettings.NameDisplay display) {
        switch (display) {
            case NONE:
                return "";
            case FULL:
                return firstName;
            case ABBREVIATED:
                return abbreviate(firstName);
            default:
                throw new AssertionError("Unexpected NameDisplay value: " + display);
        }
    }

    private String abbreviate(String name) {
        List<String> parts = getParts(name, ' ');
        StringBuilder sb = new StringBuilder(parts.size() * 3);

        for (int i = 0; i < parts.size(); i++) {
            if (i > 0) {
                sb.append(' ');
            }

            List<String> dashParts = getParts(parts.get(i), '-');

            for (int j = 0; j < dashParts.size(); j++) {
                if (j > 0) {
                    sb.append('-');
                }

                sb.append(getFirstLetter(dashParts.get(j)));
                sb.append('.');
            }
        }

        return sb.toString();
    }

    private String getFirstLetter(String part) {
        StringBuilder sb = new StringBuilder();
        int braceLevel = 0;

        for (char c : part.toCharArray()) {
            switch (c) {
                case '{':
                    braceLevel++;
                    sb.append('{');
                    break;
                case '}':
                    braceLevel--;
                    sb.append('}');
                    break;
                default:
                    if (braceLevel > 0 || Character.isLetter(c)) {
                        sb.append(c);
                    }

                    if (Character.isLetter(c)) {
                        // We're done
                        for (int i = 0; i < braceLevel; i++) {
                            sb.append('}');
                        }

                        return sb.toString();
                    }
                    break;
            }
        }

        return sb.toString();
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

    private List<String> getParts(String name, char... separator) {
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
                        if (braceLevel == 0 && contained(c, separator)) {
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
        boolean command = false;
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
                        command = false;
                        break;
                    case '\\':
                        escape = true;
                        command = true;
                        break;
                    default:
                        if ((braceLevel == 0 || command) && Character.isLetter(c)) {
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

    private boolean contained(char c, char[] separator) {
        for (char s : separator) {
            if (c == s) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return name;
    }
}
