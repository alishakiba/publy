/*
 */
package publy.io.html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;
import publy.data.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLBibItemWriterTest {

    public HTMLBibItemWriterTest() {
    }

    @Test
    public void testWriteArticleNoTags() {
        System.out.println("writeArticle");

        HashMap<BibItem, String> expected = new LinkedHashMap<>();

        BibItem test1 = new BibItem("article", "test1");
        test1.put("author", "Andrew Author");
        test1.put("title", "On the Importance of Meaningful Titles");
        test1.put("journal", "International Journal of Publications");
        test1.put("year", "2010");
        expected.put(test1, "International Journal of Publications,");

        BibItem test2 = new BibItem("article", "test2");
        test2.put("author", "Andrew Author");
        test2.put("title", "On the Importance of Meaningful Titles");
        test2.put("journal", "International Journal of Publications");
        test2.put("year", "2010");
        test2.put("pages", "1--13");
        expected.put(test2, "International Journal of Publications, pages 1&ndash;13,");

        BibItem test3 = new BibItem("article", "test3");
        test3.put("author", "Andrew Author");
        test3.put("title", "On the Importance of Meaningful Titles");
        test3.put("journal", "International Journal of Publications");
        test3.put("year", "2010");
        test3.put("number", "42");
        expected.put(test3, "International Journal of Publications, (42),");

        BibItem test4 = new BibItem("article", "test4");
        test4.put("author", "Andrew Author");
        test4.put("title", "On the Importance of Meaningful Titles");
        test4.put("journal", "International Journal of Publications");
        test4.put("year", "2010");
        test4.put("number", "42");
        test4.put("pages", "1--13");
        expected.put(test4, "International Journal of Publications, (42):1&ndash;13,");

        BibItem test5 = new BibItem("article", "test5");
        test5.put("author", "Andrew Author");
        test5.put("title", "On the Importance of Meaningful Titles");
        test5.put("journal", "International Journal of Publications");
        test5.put("year", "2010");
        test5.put("volume", "1337");
        expected.put(test5, "International Journal of Publications, 1337,");

        BibItem test6 = new BibItem("article", "test6");
        test6.put("author", "Andrew Author");
        test6.put("title", "On the Importance of Meaningful Titles");
        test6.put("journal", "International Journal of Publications");
        test6.put("year", "2010");
        test6.put("volume", "1337");
        test6.put("pages", "1--13");
        expected.put(test6, "International Journal of Publications, 1337:1&ndash;13,");

        BibItem test7 = new BibItem("article", "test7");
        test7.put("author", "Andrew Author");
        test7.put("title", "On the Importance of Meaningful Titles");
        test7.put("journal", "International Journal of Publications");
        test7.put("year", "2010");
        test7.put("volume", "1337");
        test7.put("number", "42");
        expected.put(test7, "International Journal of Publications, 1337(42),");

        BibItem test8 = new BibItem("article", "test8");
        test8.put("author", "Andrew Author");
        test8.put("title", "On the Importance of Meaningful Titles");
        test8.put("journal", "International Journal of Publications");
        test8.put("year", "2010");
        test8.put("volume", "1337");
        test8.put("number", "42");
        test8.put("pages", "1--13");
        expected.put(test8, "International Journal of Publications, 1337(42):1&ndash;13,");

        StringWriter output = new StringWriter();
        BufferedWriter buffer = new BufferedWriter(output);
        HTMLBibItemWriter testInstance = new HTMLBibItemWriter(buffer, null);

        for (BibItem input : expected.keySet()) {
            String expectedResult = expected.get(input);

            try {
                testInstance.writeArticle(input);
                buffer.flush();
                String result = removeTags(output.getBuffer().toString()).trim();

                assertEquals(expectedResult, result);
            } catch (IOException ex) {
                fail("writeArticle threw IOException on input:\n" + input + "\nException:\n" + ex);
            }

            // Clear the output
            output.getBuffer().delete(0, output.getBuffer().length());
        }
    }

    @Test
    public void testWriteBookNoTags() {
        System.out.println("writeBook");

        HashMap<BibItem, String> expected = new LinkedHashMap<>();

        BibItem test1 = new BibItem("book", "test1");
        test1.put("title", "On the Importance of Meaningful Titles");
        test1.put("year", "2010");
        test1.put("author", "Andrew Author");
        expected.put(test1, "");

        BibItem test2 = new BibItem("book", "test2");
        test2.put("title", "On the Importance of Meaningful Titles");
        test2.put("year", "2010");
        test2.put("author", "Andrew Author");
        test2.put("series", "Lecture Notes in Lecturing");
        expected.put(test2, "Lecture Notes in Lecturing.");

        BibItem test3 = new BibItem("book", "test3");
        test3.put("title", "On the Importance of Meaningful Titles");
        test3.put("year", "2010");
        test3.put("author", "Andrew Author");
        test3.put("edition", "Third");
        expected.put(test3, "Third edition,");

        BibItem test4 = new BibItem("book", "test4");
        test4.put("title", "On the Importance of Meaningful Titles");
        test4.put("year", "2010");
        test4.put("author", "Andrew Author");
        test4.put("edition", "Third");
        test4.put("series", "Lecture Notes in Lecturing");
        expected.put(test4, "Lecture Notes in Lecturing. Third edition,");

        BibItem test5 = new BibItem("book", "test5");
        test5.put("title", "On the Importance of Meaningful Titles");
        test5.put("year", "2010");
        test5.put("author", "Andrew Author");
        test5.put("number", "42");
        expected.put(test5, "Number 42.");

        BibItem test6 = new BibItem("book", "test6");
        test6.put("title", "On the Importance of Meaningful Titles");
        test6.put("year", "2010");
        test6.put("author", "Andrew Author");
        test6.put("number", "42");
        test6.put("series", "Lecture Notes in Lecturing");
        expected.put(test6, "Number 42 in Lecture Notes in Lecturing.");

        BibItem test7 = new BibItem("book", "test7");
        test7.put("title", "On the Importance of Meaningful Titles");
        test7.put("year", "2010");
        test7.put("author", "Andrew Author");
        test7.put("number", "42");
        test7.put("edition", "Third");
        expected.put(test7, "Number 42. Third edition,");

        BibItem test8 = new BibItem("book", "test8");
        test8.put("title", "On the Importance of Meaningful Titles");
        test8.put("year", "2010");
        test8.put("author", "Andrew Author");
        test8.put("number", "42");
        test8.put("edition", "Third");
        test8.put("series", "Lecture Notes in Lecturing");
        expected.put(test8, "Number 42 in Lecture Notes in Lecturing. Third edition,");

        BibItem test9 = new BibItem("book", "test9");
        test9.put("title", "On the Importance of Meaningful Titles");
        test9.put("year", "2010");
        test9.put("author", "Andrew Author");
        test9.put("publisher", "Bottomless Pit Publishing");
        expected.put(test9, "Bottomless Pit Publishing,");

        BibItem test10 = new BibItem("book", "test10");
        test10.put("title", "On the Importance of Meaningful Titles");
        test10.put("year", "2010");
        test10.put("author", "Andrew Author");
        test10.put("publisher", "Bottomless Pit Publishing");
        test10.put("series", "Lecture Notes in Lecturing");
        expected.put(test10, "Lecture Notes in Lecturing. Bottomless Pit Publishing,");

        BibItem test11 = new BibItem("book", "test11");
        test11.put("title", "On the Importance of Meaningful Titles");
        test11.put("year", "2010");
        test11.put("author", "Andrew Author");
        test11.put("publisher", "Bottomless Pit Publishing");
        test11.put("edition", "Third");
        expected.put(test11, "Bottomless Pit Publishing, third edition,");

        BibItem test12 = new BibItem("book", "test12");
        test12.put("title", "On the Importance of Meaningful Titles");
        test12.put("year", "2010");
        test12.put("author", "Andrew Author");
        test12.put("publisher", "Bottomless Pit Publishing");
        test12.put("edition", "Third");
        test12.put("series", "Lecture Notes in Lecturing");
        expected.put(test12, "Lecture Notes in Lecturing. Bottomless Pit Publishing, third edition,");

        BibItem test13 = new BibItem("book", "test13");
        test13.put("title", "On the Importance of Meaningful Titles");
        test13.put("year", "2010");
        test13.put("author", "Andrew Author");
        test13.put("publisher", "Bottomless Pit Publishing");
        test13.put("number", "42");
        expected.put(test13, "Number 42. Bottomless Pit Publishing,");

        BibItem test14 = new BibItem("book", "test14");
        test14.put("title", "On the Importance of Meaningful Titles");
        test14.put("year", "2010");
        test14.put("author", "Andrew Author");
        test14.put("publisher", "Bottomless Pit Publishing");
        test14.put("number", "42");
        test14.put("series", "Lecture Notes in Lecturing");
        expected.put(test14, "Number 42 in Lecture Notes in Lecturing. Bottomless Pit Publishing,");

        BibItem test15 = new BibItem("book", "test15");
        test15.put("title", "On the Importance of Meaningful Titles");
        test15.put("year", "2010");
        test15.put("author", "Andrew Author");
        test15.put("publisher", "Bottomless Pit Publishing");
        test15.put("number", "42");
        test15.put("edition", "Third");
        expected.put(test15, "Number 42. Bottomless Pit Publishing, third edition,");

        BibItem test16 = new BibItem("book", "test16");
        test16.put("title", "On the Importance of Meaningful Titles");
        test16.put("year", "2010");
        test16.put("author", "Andrew Author");
        test16.put("publisher", "Bottomless Pit Publishing");
        test16.put("number", "42");
        test16.put("edition", "Third");
        test16.put("series", "Lecture Notes in Lecturing");
        expected.put(test16, "Number 42 in Lecture Notes in Lecturing. Bottomless Pit Publishing, third edition,");

        BibItem test17 = new BibItem("book", "test17");
        test17.put("title", "On the Importance of Meaningful Titles");
        test17.put("year", "2010");
        test17.put("author", "Andrew Author");
        test17.put("volume", "1337");
        expected.put(test17, "Volume 1337.");

        BibItem test18 = new BibItem("book", "test18");
        test18.put("title", "On the Importance of Meaningful Titles");
        test18.put("year", "2010");
        test18.put("author", "Andrew Author");
        test18.put("volume", "1337");
        test18.put("series", "Lecture Notes in Lecturing");
        expected.put(test18, "Volume 1337 of Lecture Notes in Lecturing.");

        BibItem test19 = new BibItem("book", "test19");
        test19.put("title", "On the Importance of Meaningful Titles");
        test19.put("year", "2010");
        test19.put("author", "Andrew Author");
        test19.put("volume", "1337");
        test19.put("edition", "Third");
        expected.put(test19, "Volume 1337. Third edition,");

        BibItem test20 = new BibItem("book", "test20");
        test20.put("title", "On the Importance of Meaningful Titles");
        test20.put("year", "2010");
        test20.put("author", "Andrew Author");
        test20.put("volume", "1337");
        test20.put("edition", "Third");
        test20.put("series", "Lecture Notes in Lecturing");
        expected.put(test20, "Volume 1337 of Lecture Notes in Lecturing. Third edition,");

        BibItem test21 = new BibItem("book", "test21");
        test21.put("title", "On the Importance of Meaningful Titles");
        test21.put("year", "2010");
        test21.put("author", "Andrew Author");
        test21.put("volume", "1337");
        test21.put("publisher", "Bottomless Pit Publishing");
        expected.put(test21, "Volume 1337. Bottomless Pit Publishing,");

        BibItem test22 = new BibItem("book", "test22");
        test22.put("title", "On the Importance of Meaningful Titles");
        test22.put("year", "2010");
        test22.put("author", "Andrew Author");
        test22.put("volume", "1337");
        test22.put("publisher", "Bottomless Pit Publishing");
        test22.put("series", "Lecture Notes in Lecturing");
        expected.put(test22, "Volume 1337 of Lecture Notes in Lecturing. Bottomless Pit Publishing,");

        BibItem test23 = new BibItem("book", "test23");
        test23.put("title", "On the Importance of Meaningful Titles");
        test23.put("year", "2010");
        test23.put("author", "Andrew Author");
        test23.put("volume", "1337");
        test23.put("publisher", "Bottomless Pit Publishing");
        test23.put("edition", "Third");
        expected.put(test23, "Volume 1337. Bottomless Pit Publishing, third edition,");

        BibItem test24 = new BibItem("book", "test24");
        test24.put("title", "On the Importance of Meaningful Titles");
        test24.put("year", "2010");
        test24.put("author", "Andrew Author");
        test24.put("volume", "1337");
        test24.put("publisher", "Bottomless Pit Publishing");
        test24.put("edition", "Third");
        test24.put("series", "Lecture Notes in Lecturing");
        expected.put(test24, "Volume 1337 of Lecture Notes in Lecturing. Bottomless Pit Publishing, third edition,");

        BibItem test49 = new BibItem("book", "test49");
        test49.put("series", "Lecture Notes in Lecturing");
        test49.put("edition", "Third");
        test49.put("howpublished", "Published by throwing each page into the ocean in a separate bottle");
        test49.put("pages", "1--13");
        test49.put("booktitle", "Proceedings of the 42nd Symposium Conference");
        test49.put("number", "42");
        test49.put("type", "Typical Publication");
        test49.put("publisher", "Bottomless Pit Publishing");
        test49.put("journal", "International Journal of Publications");
        test49.put("author", "Andrew Author");
        test49.put("title", "On the Importance of Meaningful Titles");
        test49.put("organization", "Test Organization");
        test49.put("chapter", "5");
        test49.put("editor", "Edward Editor");
        test49.put("school", "School of Schooling");
        test49.put("address", "Nederweert, The Netherlands");
        test49.put("volume", "1337");
        test49.put("month", "January");
        test49.put("year", "2010");
        test49.put("note", "Note to self: don't use the note field");
        test49.put("institution", "University of Learning");
        expected.put(test49, "Volume 1337 of Lecture Notes in Lecturing. Bottomless Pit Publishing, third edition,");

        StringWriter output = new StringWriter();
        BufferedWriter buffer = new BufferedWriter(output);
        HTMLBibItemWriter testInstance = new HTMLBibItemWriter(buffer, null);

        for (BibItem input : expected.keySet()) {
            String expectedResult = expected.get(input);

            try {
                testInstance.writeBook(input);
                buffer.flush();
                String result = removeTags(output.getBuffer().toString()).trim();

                assertEquals(expectedResult, result);
            } catch (IOException ex) {
                fail("writeBook threw IOException on input:\n" + input + "\nException:\n" + ex);
            }

            // Clear the output
            output.getBuffer().delete(0, output.getBuffer().length());
        }
    }

    @Test
    public void testWriteInBookNoTags() {
        System.out.println("writeInBook");

        HashMap<BibItem, String> expected = new LinkedHashMap<>();

        BibItem test1 = new BibItem("inbook", "test1");
        test1.put("author", "Andrew Author");
        test1.put("title", "On the Importance of Meaningful Titles");
        test1.put("year", "2010");
        expected.put(test1, "");

        BibItem test2 = new BibItem("inbook", "test2");
        test2.put("author", "Andrew Author");
        test2.put("title", "On the Importance of Meaningful Titles");
        test2.put("year", "2010");
        test2.put("series", "Lecture Notes in Lecturing");
        expected.put(test2, "Lecture Notes in Lecturing.");

        BibItem test3 = new BibItem("inbook", "test3");
        test3.put("author", "Andrew Author");
        test3.put("title", "On the Importance of Meaningful Titles");
        test3.put("year", "2010");
        test3.put("edition", "Third");
        expected.put(test3, "Third edition,");

        BibItem test4 = new BibItem("inbook", "test4");
        test4.put("author", "Andrew Author");
        test4.put("title", "On the Importance of Meaningful Titles");
        test4.put("year", "2010");
        test4.put("edition", "Third");
        test4.put("series", "Lecture Notes in Lecturing");
        expected.put(test4, "Lecture Notes in Lecturing. Third edition,");

        BibItem test5 = new BibItem("inbook", "test5");
        test5.put("author", "Andrew Author");
        test5.put("title", "On the Importance of Meaningful Titles");
        test5.put("year", "2010");
        test5.put("publisher", "Bottomless Pit Publishing");
        expected.put(test5, "Bottomless Pit Publishing,");

        BibItem test6 = new BibItem("inbook", "test6");
        test6.put("author", "Andrew Author");
        test6.put("title", "On the Importance of Meaningful Titles");
        test6.put("year", "2010");
        test6.put("publisher", "Bottomless Pit Publishing");
        test6.put("series", "Lecture Notes in Lecturing");
        expected.put(test6, "Lecture Notes in Lecturing. Bottomless Pit Publishing,");

        BibItem test7 = new BibItem("inbook", "test7");
        test7.put("author", "Andrew Author");
        test7.put("title", "On the Importance of Meaningful Titles");
        test7.put("year", "2010");
        test7.put("publisher", "Bottomless Pit Publishing");
        test7.put("edition", "Third");
        expected.put(test7, "Bottomless Pit Publishing, third edition,");

        BibItem test8 = new BibItem("inbook", "test8");
        test8.put("author", "Andrew Author");
        test8.put("title", "On the Importance of Meaningful Titles");
        test8.put("year", "2010");
        test8.put("publisher", "Bottomless Pit Publishing");
        test8.put("edition", "Third");
        test8.put("series", "Lecture Notes in Lecturing");
        expected.put(test8, "Lecture Notes in Lecturing. Bottomless Pit Publishing, third edition,");

        BibItem test9 = new BibItem("inbook", "test9");
        test9.put("author", "Andrew Author");
        test9.put("title", "On the Importance of Meaningful Titles");
        test9.put("year", "2010");
        test9.put("volume", "1337");
        expected.put(test9, "Volume 1337.");

        BibItem test10 = new BibItem("inbook", "test10");
        test10.put("author", "Andrew Author");
        test10.put("title", "On the Importance of Meaningful Titles");
        test10.put("year", "2010");
        test10.put("volume", "1337");
        test10.put("series", "Lecture Notes in Lecturing");
        expected.put(test10, "Volume 1337 of Lecture Notes in Lecturing.");

        BibItem test11 = new BibItem("inbook", "test11");
        test11.put("author", "Andrew Author");
        test11.put("title", "On the Importance of Meaningful Titles");
        test11.put("year", "2010");
        test11.put("volume", "1337");
        test11.put("edition", "Third");
        expected.put(test11, "Volume 1337. Third edition,");

        BibItem test12 = new BibItem("inbook", "test12");
        test12.put("author", "Andrew Author");
        test12.put("title", "On the Importance of Meaningful Titles");
        test12.put("year", "2010");
        test12.put("volume", "1337");
        test12.put("edition", "Third");
        test12.put("series", "Lecture Notes in Lecturing");
        expected.put(test12, "Volume 1337 of Lecture Notes in Lecturing. Third edition,");

        BibItem test13 = new BibItem("inbook", "test13");
        test13.put("author", "Andrew Author");
        test13.put("title", "On the Importance of Meaningful Titles");
        test13.put("year", "2010");
        test13.put("volume", "1337");
        test13.put("publisher", "Bottomless Pit Publishing");
        expected.put(test13, "Volume 1337. Bottomless Pit Publishing,");

        BibItem test14 = new BibItem("inbook", "test14");
        test14.put("author", "Andrew Author");
        test14.put("title", "On the Importance of Meaningful Titles");
        test14.put("year", "2010");
        test14.put("volume", "1337");
        test14.put("publisher", "Bottomless Pit Publishing");
        test14.put("series", "Lecture Notes in Lecturing");
        expected.put(test14, "Volume 1337 of Lecture Notes in Lecturing. Bottomless Pit Publishing,");

        BibItem test15 = new BibItem("inbook", "test15");
        test15.put("author", "Andrew Author");
        test15.put("title", "On the Importance of Meaningful Titles");
        test15.put("year", "2010");
        test15.put("volume", "1337");
        test15.put("publisher", "Bottomless Pit Publishing");
        test15.put("edition", "Third");
        expected.put(test15, "Volume 1337. Bottomless Pit Publishing, third edition,");

        BibItem test16 = new BibItem("inbook", "test16");
        test16.put("author", "Andrew Author");
        test16.put("title", "On the Importance of Meaningful Titles");
        test16.put("year", "2010");
        test16.put("volume", "1337");
        test16.put("publisher", "Bottomless Pit Publishing");
        test16.put("edition", "Third");
        test16.put("series", "Lecture Notes in Lecturing");
        expected.put(test16, "Volume 1337 of Lecture Notes in Lecturing. Bottomless Pit Publishing, third edition,");

        BibItem test17 = new BibItem("inbook", "test17");
        test17.put("series", "Lecture Notes in Lecturing");
        test17.put("edition", "Third");
        test17.put("howpublished", "Published by throwing each page into the ocean in a separate bottle");
        test17.put("pages", "1--13");
        test17.put("booktitle", "Proceedings of the 42nd Symposium Conference");
        test17.put("number", "42");
        test17.put("type", "Typical Publication");
        test17.put("publisher", "Bottomless Pit Publishing");
        test17.put("journal", "International Journal of Publications");
        test17.put("author", "Andrew Author");
        test17.put("title", "On the Importance of Meaningful Titles");
        test17.put("organization", "Test Organization");
        test17.put("chapter", "5");
        test17.put("editor", "Edward Editor");
        test17.put("school", "School of Schooling");
        test17.put("address", "Nederweert, The Netherlands");
        test17.put("volume", "1337");
        test17.put("month", "January");
        test17.put("year", "2010");
        test17.put("note", "Note to self: don't use the note field");
        test17.put("institution", "University of Learning");
        expected.put(test17, "Volume 1337 of Lecture Notes in Lecturing. Bottomless Pit Publishing, third edition,");

        StringWriter output = new StringWriter();
        BufferedWriter buffer = new BufferedWriter(output);
        HTMLBibItemWriter testInstance = new HTMLBibItemWriter(buffer, null);

        for (BibItem input : expected.keySet()) {
            String expectedResult = expected.get(input);

            try {
                testInstance.writeInBook(input);
                buffer.flush();
                String result = removeTags(output.getBuffer().toString()).trim();

                assertEquals(expectedResult, result);
            } catch (IOException ex) {
                fail("writeInBook threw IOException on input:\n" + input + "\nException:\n" + ex);
            }

            // Clear the output
            output.getBuffer().delete(0, output.getBuffer().length());
        }
    }

    @Test
    public void testWriteBookletNoTags() {
        System.out.println("writeBooklet");

        HashMap<BibItem, String> expected = new LinkedHashMap<>();

        BibItem test1 = new BibItem("booklet", "test1");
        test1.put("author", "Andrew Author");
        test1.put("title", "On the Importance of Meaningful Titles");
        test1.put("year", "2010");
        expected.put(test1, "");

        BibItem test2 = new BibItem("booklet", "test2");
        test2.put("author", "Andrew Author");
        test2.put("title", "On the Importance of Meaningful Titles");
        test2.put("year", "2010");
        test2.put("howpublished", "Published by throwing each page into the ocean in a separate bottle");
        expected.put(test2, "Published by throwing each page into the ocean in a separate bottle,");

        BibItem test3 = new BibItem("booklet", "test3");
        test3.put("author", "Andrew Author");
        test3.put("title", "On the Importance of Meaningful Titles");
        test3.put("year", "2010");
        test3.put("address", "Nederweert, The Netherlands");
        expected.put(test3, "Nederweert, The Netherlands,");

        BibItem test4 = new BibItem("booklet", "test4");
        test4.put("author", "Andrew Author");
        test4.put("title", "On the Importance of Meaningful Titles");
        test4.put("year", "2010");
        test4.put("address", "Nederweert, The Netherlands");
        test4.put("howpublished", "Published by throwing each page into the ocean in a separate bottle");
        expected.put(test4, "Published by throwing each page into the ocean in a separate bottle, Nederweert, The Netherlands,");

        BibItem test5 = new BibItem("booklet", "test5");
        test5.put("series", "Lecture Notes in Lecturing");
        test5.put("edition", "Third");
        test5.put("howpublished", "Published by throwing each page into the ocean in a separate bottle");
        test5.put("pages", "1--13");
        test5.put("booktitle", "Proceedings of the 42nd Symposium Conference");
        test5.put("number", "42");
        test5.put("type", "Typical Publication");
        test5.put("publisher", "Bottomless Pit Publishing");
        test5.put("journal", "International Journal of Publications");
        test5.put("author", "Andrew Author");
        test5.put("title", "On the Importance of Meaningful Titles");
        test5.put("organization", "Test Organization");
        test5.put("chapter", "5");
        test5.put("editor", "Edward Editor");
        test5.put("school", "School of Schooling");
        test5.put("address", "Nederweert, The Netherlands");
        test5.put("volume", "1337");
        test5.put("month", "January");
        test5.put("year", "2010");
        test5.put("note", "Note to self: don't use the note field");
        test5.put("institution", "University of Learning");
        expected.put(test5, "Published by throwing each page into the ocean in a separate bottle, Nederweert, The Netherlands,");

        StringWriter output = new StringWriter();
        BufferedWriter buffer = new BufferedWriter(output);
        HTMLBibItemWriter testInstance = new HTMLBibItemWriter(buffer, null);

        for (BibItem input : expected.keySet()) {
            String expectedResult = expected.get(input);

            try {
                testInstance.writeBooklet(input);
                buffer.flush();
                String result = removeTags(output.getBuffer().toString()).trim();

                assertEquals(expectedResult, result);
            } catch (IOException ex) {
                fail("writeBooklet threw IOException on input:\n" + input + "\nException:\n" + ex);
            }

            // Clear the output
            output.getBuffer().delete(0, output.getBuffer().length());
        }
    }

    @Test
    public void testChangeQuotes() {
        System.out.println("changeQuotes");

        HashMap<String, String> expected = new LinkedHashMap<>();

        // Simple tests
        expected.put("O'Rourke", "O’Rourke");
        expected.put("This is `simple'.", "This is ‘simple’.");
        expected.put("``This is also simple.''", "“This is also simple.”");
        expected.put("As is ``this\".", "As is “this”.");

        // Ignore quotes in HTML tags
        expected.put("<span class=\"author\">O'Rourke</span>", "<span class=\"author\">O’Rourke</span>");
        expected.put("<a href=\"http://www.google.com\">O'Rourke</a>", "<a href=\"http://www.google.com\">O’Rourke</a>");
        expected.put("<span class=\"title\">On ``simple'' graphs</span>", "<span class=\"title\">On “simple” graphs</span>");
        expected.put("<span class=\"title\">On ``simple\" graphs</span>", "<span class=\"title\">On “simple” graphs</span>");

        HTMLBibItemWriter testInstance = new HTMLBibItemWriter(null, null);

        for (String input : expected.keySet()) {
            String expectedResult = expected.get(input);
            String result = testInstance.changeQuotes(input);

            assertEquals(expectedResult, result);
        }
    }

    private String removeTags(String html) {
        return html.replaceAll("<[^>]*>", "");
    }
}