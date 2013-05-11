/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io;

import publistgenerator.data.bibitem.Venue;
import publistgenerator.data.bibitem.InProceedings;
import publistgenerator.data.bibitem.InCollection;
import publistgenerator.data.bibitem.MastersThesis;
import publistgenerator.data.bibitem.PhDThesis;
import publistgenerator.data.bibitem.Unpublished;
import publistgenerator.data.bibitem.Article;
import publistgenerator.data.bibitem.InvitedTalk;
import publistgenerator.data.bibitem.Author;
import publistgenerator.data.bibitem.BibItem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Sander
 */
public class BibTeXParser {

    private BibTeXParser() {
    }

    public static List<BibItem> parseFile(File file) {
        HashMap<String, String> abbreviations = new HashMap<>();
        HashMap<String, Venue> venues = new HashMap<>();
        HashMap<String, Author> authors = new HashMap<>();

        parsePreliminaries(file, abbreviations, venues, authors);
        List<BibItem> items = parseItems(file);
        
        for (BibItem item : items) {
            setVenue(item, venues);
            expandAbbreviations(item, abbreviations, venues);
            replaceAuthors(item, authors);
        }

        return items;
    }

    private static List<BibItem> parseItems(File file) {
        HashSet<String> ids = new HashSet<>();
        ArrayList<BibItem> items = new ArrayList<>();

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));

            String line = in.readLine();

            while (line != null) {
                // Process this line
                line = line.trim();

                if (!line.isEmpty()) {
                    BibItem item = null;

                    if (line.startsWith("@inproceedings") || line.startsWith("@conference")) {
                        item = new InProceedings();
                    } else if (line.startsWith("@article")) {
                        item = new Article();
                    } else if (line.startsWith("@mastersthesis")) {
                        item = new MastersThesis();
                    } else if (line.startsWith("@phdthesis")) {
                        item = new PhDThesis();
                    } else if (line.startsWith("@incollection")) {
                        item = new InCollection();
                    } else if (line.startsWith("@unpublished")) {
                        item = new Unpublished();
                    } else if (line.startsWith("@talk")) {
                        item = new InvitedTalk();
                    }

                    if (item != null) {
                        parseItem(item, line, in);

                        if (!ids.contains(item.getId()) && item.checkMandatoryFields()) {
                            ids.add(item.getId());
                            items.add(item);
                        } else {
                            if (ids.contains(item.getId())) {
                                System.err.println("duplicate id: " + item.getId());
                            }
                        }
                    }
                }

                line = in.readLine();
            }
        } catch (IOException ex) {
            System.err.println("Exception occurred.");
            ex.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                System.err.println("Exception occurred.");
                ex.printStackTrace();
            }
        }

        return items;
    }

    private static void parseItem(BibItem item, String line, BufferedReader in) throws IOException {
        // First grab all contents of this bibitem into one long string to deal with different styles
        // Assumption: no two bibitems on any line
        StringBuilder content = new StringBuilder(line);

        int level = levelChange(line);

        while (level > 0) {
            line = in.readLine();

            if (line == null) {
                throw new IOException("Unexpected EoF.");
            } else {
                line = line.trim();
                content.append(line);
                level += levelChange(line);
            }
        }

        String body = content.toString();

        // Keep only the part between the first pair of braces
        body = body.substring(body.indexOf('{') + 1, body.lastIndexOf('}'));

        // Split the body into the comma-separated chunks
        // NOTE: this creates too many chunks, but we can detect when a value continues into the next chunk by looking at the braces
        String[] chunks = body.split(",");

        // Parse the id
        item.setId(chunks[0]);

        // Parse the attributes
        int i = 1;

        while (i < chunks.length) {
            String attr = chunks[i].substring(0, chunks[i].indexOf('=')).trim().toLowerCase();

            StringBuilder value = new StringBuilder(chunks[i].substring(chunks[i].indexOf('=') + 1));

            int chunkLevel = levelChange(chunks[i]);

            while (chunkLevel > 0) {
                i++;

                if (i == chunks.length) {
                    throw new IOException("Unexpected EoF.");
                } else {
                    value.append(",");
                    value.append(chunks[i]);
                    chunkLevel += levelChange(chunks[i]);
                }
            }

            String attrValue = value.toString();

            // Throw away the outer pair of braces
            attrValue = attrValue.substring(attrValue.indexOf('{') + 1, attrValue.lastIndexOf('}'));

            // Add this pair to the item
            item.put(attr, attrValue);

            i++;
        }
    }

    private static int levelChange(String line) {
        char[] chars = line.toCharArray();

        int change = 0;

        for (char c : chars) {
            switch (c) {
                case '{':
                    change++;
                    break;
                case '}':
                    change--;
                    break;
            }
        }

        return change;
    }

    private static void parsePreliminaries(File file, Map<String, String> abbreviations, Map<String, Venue> venues, Map<String, Author> authors) {
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                // Process this line
                line = line.trim();

                if (line.startsWith("<author")) {
                    parseAuthor(line, authors);
                } else if (line.startsWith("<abbr")) {
                    parseAbbreviation(line, abbreviations);
                } else if (line.startsWith("<conf")) {
                    parseVenue(line, true, venues);
                } else if (line.startsWith("<journal")) {
                    parseVenue(line, false, venues);
                }
            }
        } catch (IOException ex) {
            System.err.println("Exception occurred.");
            ex.printStackTrace();
        }
    }
    // Patterns for author and abbreviation parsing
    private static final Pattern shortPattern = Pattern.compile("short=\"([^\"]*)\"");
    private static final Pattern fullPattern = Pattern.compile("full=\"([^\"]*)\"");
    private static final Pattern abbPattern = Pattern.compile("abbr=\"([^\"]*)\"");
    private static final Pattern namePattern = Pattern.compile(" name=\"([^\"]*)\"");
    private static final Pattern htmlPattern = Pattern.compile("htmlname=\"([^\"]*)\"");
    private static final Pattern latexPattern = Pattern.compile("latexname=\"([^\"]*)\"");
    private static final Pattern urlPattern = Pattern.compile("url=\"([^\"]*)\"");
    private static final Pattern categoryPattern = Pattern.compile("category=\"([^\"]*)\"");
    private static final Pattern textPattern = Pattern.compile(" text=\"(.*?[^\\\\])\"");
    private static final Pattern htmltextPattern = Pattern.compile("htmltext=\"(.*?[^\\\\])\"");
    private static final Pattern plaintextPattern = Pattern.compile("plaintext=\"(.*?[^\\\\])\"");
    private static final Pattern latextextPattern = Pattern.compile("latextext=\"(.*?[^\\\\])\"");

    private static void parseAuthor(String line, Map<String, Author> authors) {
        String shortName = null, latexName = null, htmlName = null, url = null;

        Matcher matcher = shortPattern.matcher(line);

        if (matcher.find()) {
            shortName = matcher.group(1);
        }

        matcher = namePattern.matcher(line);

        if (matcher.find()) {
            latexName = matcher.group(1);
            htmlName = matcher.group(1);
        }

        matcher = htmlPattern.matcher(line);

        if (matcher.find()) {
            htmlName = matcher.group(1);
        }

        matcher = latexPattern.matcher(line);

        if (matcher.find()) {
            latexName = matcher.group(1);
        }

        matcher = urlPattern.matcher(line);

        if (matcher.find()) {
            url = matcher.group(1);
        }

        if (shortName != null && htmlName != null && latexName != null) {
            Author author = new Author(shortName, latexName, htmlName);
            author.setUrl(url);

            authors.put(shortName, author);
        } else {
            System.err.println("Author tag detected, but no full author information found: " + line);
        }
    }

    private static void parseAbbreviation(String line, Map<String, String> abbreviations) {
        String abbr = null, full = null;
        Matcher matcher = shortPattern.matcher(line);

        if (matcher.find()) {
            abbr = matcher.group(1);
        }

        matcher = fullPattern.matcher(line);

        if (matcher.find()) {
            full = matcher.group(1);
        }

        if (abbr != null && full != null) {
            abbreviations.put(abbr, full);
        } else {
            System.err.println("Abbreviation tag detected, but no full information found: " + line);
        }
    }

    private static void parseVenue(String line, boolean conference, Map<String, Venue> venues) {
        String shortName = null, fullName = null, abbreviation = null;
        Matcher matcher = shortPattern.matcher(line);

        if (matcher.find()) {
            shortName = matcher.group(1);
        }

        matcher = fullPattern.matcher(line);

        if (matcher.find()) {
            fullName = matcher.group(1);
        }

        matcher = abbPattern.matcher(line);

        if (matcher.find()) {
            abbreviation = matcher.group(1);
        }

        if (shortName != null && fullName != null && abbreviation != null) {
            venues.put(shortName, new Venue(conference, abbreviation, fullName, shortName));
        } else {
            System.err.println((conference ? "Conference" : "Journal") + " tag detected, but no full information found: " + line);
        }
    }

    // Pattern for detecting an author link
    private static final Pattern authorPattern = Pattern.compile("<([^<>]*)>");

    private static void replaceAuthors(BibItem item, Map<String, Author> authors) {
        // Replace authors
        String author = item.get("author");

        if (author != null && !author.isEmpty()) {
            Matcher matcher = authorPattern.matcher(author);

            while (matcher.find()) {
                Author a = authors.get(matcher.group(1));

                if (a == null) {
                    System.err.println("No author found for \"" + matcher.group(1) + "\"!");
                } else {
                    item.getAuthors().add(a);
                }
            }
        }
    }
    private static final Pattern abbrPattern = Pattern.compile("<<([^>]*)>>");

    private static void expandAbbreviations(BibItem item, Map<String, String> abbreviations, Map<String, Venue> venues) {
        for (String field : item.getFields()) {
            String val = item.get(field);

            if (val != null && !val.isEmpty()) {
                StringBuilder finalValue = new StringBuilder();
                Matcher matcher = abbrPattern.matcher(val);
                int prevEnd = 0;

                while (matcher.find()) {
                    String abbr = matcher.group(1);
                    int start = matcher.start();
                    int end = matcher.end();

                    finalValue.append(val.substring(prevEnd, start));

                    if (abbreviations.containsKey(abbr)) {
                        finalValue.append(abbreviations.get(abbr));
                    } else if (venues.containsKey(abbr)) {
                        finalValue.append(venues.get(abbr).getFullName());
                    } else {
                        System.err.println("Abbreviation \"" + matcher.group(1) + "\" not found!");
                    }

                    prevEnd = end;
                }

                finalValue.append(val.substring(prevEnd, val.length()));

                item.put(field, finalValue.toString());
            }
        }
    }

    private static void setVenue(BibItem item, Map<String, Venue> venues) {
        String venue = null;

        if (item.anyNonEmpty("booktitle")) {
            venue = item.get("booktitle");
        } else if (item.anyNonEmpty("journal")) {
            venue = item.get("journal");
        }

        if (venue != null) {
            Matcher matcher = abbrPattern.matcher(venue);

            while (matcher.find()) {
                String abbr = matcher.group(1);

                if (venues.containsKey(abbr)) {
                    item.setVenue(venues.get(abbr));
                    break;
                }
            }
        }
    }
}
