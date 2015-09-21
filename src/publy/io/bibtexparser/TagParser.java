/*
 * Copyright 2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.io.bibtexparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import publy.Console;

public class TagParser {
    
    // Patterns for author and abbreviation parsing
    private static final Pattern nameValuePattern = Pattern.compile("([a-zA-Z]+)\\s?=\\s?\"([^\"]*)\"");
    
    public static Tag parseTag(String text) {
        String type = text.substring(1, Math.min(text.indexOf(' ', 2), text.indexOf('>'))).trim().toLowerCase();

        switch (type) {
            case "author":
                return parseAuthor(text);
            case "abbr":
                return parseAbbreviation(text);
            // Ignore valid HTML tags
            case "a":
            case "acronym":
            case "address":
            case "area":
            case "b":
            case "base":
            case "bdo":
            case "big":
            case "blockquote":
            case "body":
            case "br":
            case "button":
            case "caption":
            case "cite":
            case "code":
            case "col":
            case "colgroup":
            case "dd":
            case "del":
            case "dfn":
            case "div":
            case "dl":
            case "DOCTYPE":
            case "dt":
            case "em":
            case "fieldset":
            case "form":
            case "h1":
            case "h2":
            case "h3":
            case "h4":
            case "h5":
            case "h6":
            case "head":
            case "html":
            case "hr":
            case "i":
            case "img":
            case "input":
            case "ins":
            case "kbd":
            case "label":
            case "legend":
            case "li":
            case "link":
            case "map":
            case "meta":
            case "noscript":
            case "object":
            case "ol":
            case "optgroup":
            case "option":
            case "p":
            case "param":
            case "pre":
            case "q":
            case "samp":
            case "script":
            case "select":
            case "small":
            case "span":
            case "strong":
            case "style":
            case "sub":
            case "sup":
            case "table":
            case "tbody":
            case "td":
            case "textarea":
            case "tfoot":
            case "th":
            case "thead":
            case "title":
            case "tr":
            case "tt":
            case "ul":
            case "var":
                Console.warn(Console.WarningType.OTHER, "Ignored HTML tag \"%s\" out of publication context.", type);
                break;
            default:
                Console.error("Unrecognized tag \"%s\" at line \"%s\".", type, text);
                break;
        }
        
        return null;
    }

    private static Tag parseAuthor(String text) {
        Tag result = new Tag(Tag.Type.AUTHOR);
        
        parseFields(text, result);

        if (!result.values.containsKey("short")) {
            Console.error("Author tag is missing mandatory field \"short\":%n%s", text);
            return null;
        } else if (!result.values.containsKey("name")) {
            Console.error("Author tag is missing mandatory field \"name\":%n%s", text);
            return null;
        } else {
            return result;
        }
    }

    private static Tag parseAbbreviation(String text) {
        Tag result = new Tag(Tag.Type.ABBREVIATION);
        
        parseFields(text, result);

        if (!result.values.containsKey("short")) {
            Console.error("Abbreviation tag is missing mandatory field \"short\":%n%s", text);
            return null;
        } else if (!result.values.containsKey("full")) {
            Console.error("Abbreviation tag is missing mandatory field \"full\":%n%s", text);
            return null;
        } else {
            return result;
        }
    }
    
    private static void parseFields(String text, Tag result) {
        Matcher matcher = nameValuePattern.matcher(text);
        
        while (matcher.find()) {
            result.values.put(matcher.group(1), matcher.group(2));
        }
    }
}
