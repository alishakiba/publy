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
package publy.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import publy.Console;
import publy.data.Author;
import publy.data.bibitem.BibItem;
import publy.data.settings.GeneralSettings;
import publy.data.settings.Settings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public abstract class BibItemWriter {

    protected BufferedWriter out;
    protected Settings settings;
    protected Set<String> ignoredFields;

    public BibItemWriter(BufferedWriter out, Settings settings) {
        this.out = out;
        this.settings = settings;
        ignoredFields = Collections.<String>emptySet();
    }

    public abstract void write(BibItem item) throws IOException;

    public Set<String> getIgnoredFields() {
        return ignoredFields;
    }

    public void setIgnoredFields(Set<String> ignoredFields) {
        this.ignoredFields = ignoredFields;
    }

    protected String formatTitle(BibItem item) {
        String title = item.get("title");

        if (title == null || title.isEmpty()) {
            return "";
        } else {
            return toTitleCase(title);
        }
    }

    protected String formatAuthors(BibItem item, boolean editors, Author.NameOutputType type) {
        List<Author> authorList = (editors ? item.getEditors() : item.getAuthors());
        List<String> authors = new ArrayList<>(authorList.size());
        GeneralSettings gs = settings.getGeneralSettings();

        // Collect the formatted names of all authors that need to be printed
        for (Author a : authorList) {
            if (a == null) {
                if (editors) {
                    Console.error("Null editor found for entry \"%s\".%n(Editors: \"%s\")", item.getId(), item.get("editor"));
                } else {
                    Console.error("Null author found for entry \"%s\".%n(Authors: \"%s\")", item.getId(), item.get("author"));
                }
            } else {
                if (gs.isListAllAuthors() || !a.isMe(gs)) {
                    authors.add(a.getFormattedName(gs.getNameDisplay(), gs.isReverseNames(), type));
                }
            }
        }

        // Connect these names in the proper way
        String result = formatNames(authors);

        // Add "With" if necessary
        if (!gs.isListAllAuthors()) {
            if (authors.size() == authorList.size()) {
                if (editors) {
                    Console.warn(Console.WarningType.NOT_AUTHORED_BY_USER, "None of the editors of entry \"%s\" match your name.%n(Editors: \"%s\")", item.getId(), item.get("editor"));
                } else {
                    Console.warn(Console.WarningType.NOT_AUTHORED_BY_USER, "None of the authors of entry \"%s\" match your name.%n(Authors: \"%s\")", item.getId(), item.get("author"));
                }
            } else {
                result = "With " + result;
            }
        }

        // Add the ", editors" postfix
        if (editors && !authors.isEmpty()) {
            if (authors.size() > 1) {
                result += ", editors";
            } else {
                result += ", editor";
            }
        }

        return result;
    }

    protected String formatNames(List<String> names) {
        StringBuilder result = new StringBuilder();

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
        if (!isPresent(item, "pages")) {
            return "";
        } else {
            String pages = item.get("pages");

            if (pages.contains("-") || pages.contains("+") || pages.contains(",")) {
                return "pages " + pages;
            } else {
                return "page " + pages;
            }
        }
    }

    protected String formatDate(BibItem item) {
        String year = item.get("year");

        if (year == null || year.isEmpty()) {
            if (!isPresent(item, "month")) {
                return "";
            } else {
                return formatMonth(item.get("month"));
            }
        } else {
            if (!isPresent(item, "month")) {
                return year;
            } else {
                return formatMonth(item.get("month")) + " " + year;
            }
        }
    }

    protected String formatMonth(String month) {
        switch (month) {
            case "jan":
            case "1":
                return "January";
            case "feb":
            case "2":
                return "February";
            case "mar":
            case "3":
                return "March";
            case "apr":
            case "4":
                return "April";
            case "may":
            case "5":
                return "May";
            case "jun":
            case "6":
                return "June";
            case "jul":
            case "7":
                return "July";
            case "aug":
            case "8":
                return "August";
            case "sep":
            case "9":
                return "September";
            case "oct":
            case "10":
                return "October";
            case "nov":
            case "11":
                return "November";
            case "dec":
            case "12":
                return "December";
            default:
                return month;
        }
    }

    protected void output(String string) throws IOException {
        output("", string, "", false);
    }

    protected void output(String string, boolean newLine) throws IOException {
        output("", string, "", newLine);
    }

    protected void output(String string, String connective) throws IOException {
        output("", string, connective, false);
    }

    protected void output(String string, String connective, boolean newLine) throws IOException {
        output("", string, connective, newLine);
    }

    protected void output(String prefix, String string, String connective) throws IOException {
        output(prefix, string, connective, false);
    }

    protected void output(String prefix, String string, String connective, boolean newLine) throws IOException {
        if (string != null && !string.isEmpty()) {
            out.write(prefix);
            out.write(processString(string));
            out.write(connective);

            if (newLine) {
                newline();
            }
        }
    }

    protected void newline() throws IOException {
        if (settings.getGeneralSettings().isUseNewLines()) {
            out.newLine();
        } else {
            out.write(' ');
        }
    }

    protected String processString(String string) {
        return changeQuotes(removeBraces(LatexToUnicode.convertToUnicode(string)));
    }

    /**
     * Converts the given string to title case. The first character is set in
     * upper case, while the remaining characters are set in lower case.
     * Characters between braces remain untouched.
     *
     * @param s
     * @return
     */
    protected String toTitleCase(String s) {
        return changeCase(s, true);
    }

    /**
     * Converts the given string to lower case. Characters between braces remain
     * untouched.
     *
     * @param s
     * @return
     */
    protected String toLowerCase(String s) {
        return changeCase(s, false);
    }

    private String changeCase(String s, boolean title) {
        if (s == null || s.isEmpty()) {
            return "";
        }

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
                            if (first && title) {
                                sb.append(Character.toUpperCase(c));
                            } else {
                                sb.append(Character.toLowerCase(c));
                            }
                        } else {
                            sb.append(c);
                        }

                        first = false;
                        break;
                }
            }
        }

        return sb.toString();
    }

    private enum RemoveBracesState {

        DEFAULT, ESCAPE, COMMAND_NAME, OPTIONAL_ARGUMENT, ARGUMENT, BEFORE_POSSIBLE_ARGUMENT;
    }

    protected String removeBraces(String field) {
        StringBuilder result = new StringBuilder(field.length());
        RemoveBracesState state = RemoveBracesState.DEFAULT;

        for (char c : field.toCharArray()) {
            if (state == RemoveBracesState.DEFAULT) {
                switch (c) {
                    case '{': // Remove
                        break;
                    case '}': // Remove
                        break;
                    case '\\':
                        state = RemoveBracesState.ESCAPE;
                        break;
                    default:
                        result.append(c);
                        break;
                }
            } else if (state == RemoveBracesState.ESCAPE) {
                if (Character.isLetter(c)) {
                    state = RemoveBracesState.COMMAND_NAME;
                } else {
                    state = RemoveBracesState.DEFAULT;
                }

                // Discard the slash before braces
                if (c != '{' && c != '}') {
                    result.append('\\');
                }

                result.append(c);
            } else if (state == RemoveBracesState.COMMAND_NAME) {
                if (c == '[') {
                    state = RemoveBracesState.OPTIONAL_ARGUMENT;
                } else if (c == '{') {
                    state = RemoveBracesState.ARGUMENT;
                } else if (!Character.isLetter(c)) {
                    state = RemoveBracesState.DEFAULT;
                }

                result.append(c);
            } else if (state == RemoveBracesState.OPTIONAL_ARGUMENT || state == RemoveBracesState.ARGUMENT) {
                if ((state == RemoveBracesState.OPTIONAL_ARGUMENT && c == ']') || (state == RemoveBracesState.ARGUMENT && c == '}')) {
                    state = RemoveBracesState.BEFORE_POSSIBLE_ARGUMENT;
                }

                result.append(c);
            } else if (state == RemoveBracesState.BEFORE_POSSIBLE_ARGUMENT) {
                if (c == '{') {
                    state = RemoveBracesState.ARGUMENT;
                } else {
                    state = RemoveBracesState.DEFAULT;
                }

                result.append(c);
            }
        }

        return result.toString();
    }

    private enum ChangeQuotesState {

        DEFAULT, IN_TAG, AFTER_QUOTE, AFTER_GRAVE;
    }

    protected String changeQuotes(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        ChangeQuotesState state = ChangeQuotesState.DEFAULT;

        for (char c : string.toCharArray()) {
            if (state == ChangeQuotesState.AFTER_GRAVE) {
                // Single or double quote?
                if (c == '`') {
                    // Double `` -> U+201C (left double quotation mark)
                    sb.append('\u201C');
                    // state is reset at the end of the loop
                } else {
                    // Single ` -> U+2018 (left single quotation mark)
                    sb.append('\u2018');
                    state = ChangeQuotesState.DEFAULT; // Reset state here so current char gets processed regularly
                }
            } else if (state == ChangeQuotesState.AFTER_QUOTE) {
                // Single or double quote?
                if (c == '\'') {
                    // Double '' -> U+201D (right double quotation mark)
                    sb.append('\u201D');
                    // state is reset at the end of the loop
                } else {
                    // Single ' -> U+2019 (right single quotation mark)
                    sb.append('\u2019');
                    state = ChangeQuotesState.DEFAULT; // Reset state here so current char gets processed regularly
                }
            }

            if (state == ChangeQuotesState.DEFAULT) {
                // Regular case
                switch (c) {
                    case '<': // HTML tag open
                        state = ChangeQuotesState.IN_TAG;
                        sb.append(c);
                        break;
                    case '"': // Single " -> U+201D (right double quotation mark)
                        sb.append('\u201D');
                        break;
                    case '`': // Single or double `
                        state = ChangeQuotesState.AFTER_GRAVE;
                        break;
                    case '\'': // Single or double '
                        state = ChangeQuotesState.AFTER_QUOTE;
                        break;
                    default:
                        sb.append(c);
                        break;
                }
            } else if (state == ChangeQuotesState.IN_TAG) {
                // In an HTML tag
                if (c == '>') {
                    // Close the tag
                    state = ChangeQuotesState.DEFAULT;
                }

                sb.append(c);
            } else {
                state = ChangeQuotesState.DEFAULT;
            }
        }

        return sb.toString();
    }

    protected String get(BibItem item, String field) {
        if (ignoredFields.contains(field)) {
            return "";
        } else {
            return item.get(field);
        }
    }

    protected boolean isPresent(BibItem item, String field) {
        return !ignoredFields.contains(field) && item.get(field) != null && !item.get(field).isEmpty();
    }

    protected boolean anyPresent(BibItem item, String... fields) {
        for (String field : fields) {
            if (!ignoredFields.contains(field) && item.get(field) != null && !item.get(field).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    protected boolean allPresent(BibItem item, String... fields) {
        for (String field : fields) {
            if (ignoredFields.contains(field) || item.get(field) == null || item.get(field).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
