/*
 */
package publy.io.html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    public void testWriteInCollectionNoTags() {
        System.out.println("writeInCollection");

        HashMap<BibItem, String> expected = new LinkedHashMap<>();

        BibItem test1 = new BibItem("incollection", "test1");
        test1.put("author", "An Author");
        test1.put("title", "Meaningful Title");
        test1.put("booktitle", "Proceedings");
        test1.put("year", "2010");
        expected.put(test1, "In Proceedings.");

        BibItem test2 = new BibItem("incollection", "test2");
        test2.put("author", "An Author");
        test2.put("title", "Meaningful Title");
        test2.put("booktitle", "Proceedings");
        test2.put("year", "2010");
        test2.put("series", "Notes");
        expected.put(test2, "In Proceedings, Notes.");

        BibItem test3 = new BibItem("incollection", "test3");
        test3.put("author", "An Author");
        test3.put("title", "Meaningful Title");
        test3.put("booktitle", "Proceedings");
        test3.put("year", "2010");
        test3.put("pages", "1--13");
        expected.put(test3, "In Proceedings, pages 1&ndash;13.");

        BibItem test4 = new BibItem("incollection", "test4");
        test4.put("author", "An Author");
        test4.put("title", "Meaningful Title");
        test4.put("booktitle", "Proceedings");
        test4.put("year", "2010");
        test4.put("pages", "1--13");
        test4.put("series", "Notes");
        expected.put(test4, "In Proceedings, Notes, pages 1&ndash;13.");

        BibItem test5 = new BibItem("incollection", "test5");
        test5.put("author", "An Author");
        test5.put("title", "Meaningful Title");
        test5.put("booktitle", "Proceedings");
        test5.put("year", "2010");
        test5.put("type", "Typo");
        expected.put(test5, "In Proceedings.");

        BibItem test6 = new BibItem("incollection", "test6");
        test6.put("author", "An Author");
        test6.put("title", "Meaningful Title");
        test6.put("booktitle", "Proceedings");
        test6.put("year", "2010");
        test6.put("type", "Typo");
        test6.put("series", "Notes");
        expected.put(test6, "In Proceedings, Notes.");

        BibItem test7 = new BibItem("incollection", "test7");
        test7.put("author", "An Author");
        test7.put("title", "Meaningful Title");
        test7.put("booktitle", "Proceedings");
        test7.put("year", "2010");
        test7.put("type", "Typo");
        test7.put("pages", "1--13");
        expected.put(test7, "In Proceedings, pages 1&ndash;13.");

        BibItem test8 = new BibItem("incollection", "test8");
        test8.put("author", "An Author");
        test8.put("title", "Meaningful Title");
        test8.put("booktitle", "Proceedings");
        test8.put("year", "2010");
        test8.put("type", "Typo");
        test8.put("pages", "1--13");
        test8.put("series", "Notes");
        expected.put(test8, "In Proceedings, Notes, pages 1&ndash;13.");

        BibItem test9 = new BibItem("incollection", "test9");
        test9.put("author", "An Author");
        test9.put("title", "Meaningful Title");
        test9.put("booktitle", "Proceedings");
        test9.put("year", "2010");
        test9.put("chapter", "5");
        expected.put(test9, "In Proceedings, chapter 5.");

        BibItem test10 = new BibItem("incollection", "test10");
        test10.put("author", "An Author");
        test10.put("title", "Meaningful Title");
        test10.put("booktitle", "Proceedings");
        test10.put("year", "2010");
        test10.put("chapter", "5");
        test10.put("series", "Notes");
        expected.put(test10, "In Proceedings, Notes, chapter 5.");

        BibItem test11 = new BibItem("incollection", "test11");
        test11.put("author", "An Author");
        test11.put("title", "Meaningful Title");
        test11.put("booktitle", "Proceedings");
        test11.put("year", "2010");
        test11.put("chapter", "5");
        test11.put("pages", "1--13");
        expected.put(test11, "In Proceedings, chapter 5, pages 1&ndash;13.");

        BibItem test12 = new BibItem("incollection", "test12");
        test12.put("author", "An Author");
        test12.put("title", "Meaningful Title");
        test12.put("booktitle", "Proceedings");
        test12.put("year", "2010");
        test12.put("chapter", "5");
        test12.put("pages", "1--13");
        test12.put("series", "Notes");
        expected.put(test12, "In Proceedings, Notes, chapter 5, pages 1&ndash;13.");

        BibItem test13 = new BibItem("incollection", "test13");
        test13.put("author", "An Author");
        test13.put("title", "Meaningful Title");
        test13.put("booktitle", "Proceedings");
        test13.put("year", "2010");
        test13.put("chapter", "5");
        test13.put("type", "Typo");
        expected.put(test13, "In Proceedings, typo 5.");

        BibItem test14 = new BibItem("incollection", "test14");
        test14.put("author", "An Author");
        test14.put("title", "Meaningful Title");
        test14.put("booktitle", "Proceedings");
        test14.put("year", "2010");
        test14.put("chapter", "5");
        test14.put("type", "Typo");
        test14.put("series", "Notes");
        expected.put(test14, "In Proceedings, Notes, typo 5.");

        BibItem test15 = new BibItem("incollection", "test15");
        test15.put("author", "An Author");
        test15.put("title", "Meaningful Title");
        test15.put("booktitle", "Proceedings");
        test15.put("year", "2010");
        test15.put("chapter", "5");
        test15.put("type", "Typo");
        test15.put("pages", "1--13");
        expected.put(test15, "In Proceedings, typo 5, pages 1&ndash;13.");

        BibItem test16 = new BibItem("incollection", "test16");
        test16.put("author", "An Author");
        test16.put("title", "Meaningful Title");
        test16.put("booktitle", "Proceedings");
        test16.put("year", "2010");
        test16.put("chapter", "5");
        test16.put("type", "Typo");
        test16.put("pages", "1--13");
        test16.put("series", "Notes");
        expected.put(test16, "In Proceedings, Notes, typo 5, pages 1&ndash;13.");

        BibItem test17 = new BibItem("incollection", "test17");
        test17.put("author", "An Author");
        test17.put("title", "Meaningful Title");
        test17.put("booktitle", "Proceedings");
        test17.put("year", "2010");
        test17.put("editor", "Ed Editor");
        expected.put(test17, "In Ed Editor, editor, Proceedings.");

        BibItem test18 = new BibItem("incollection", "test18");
        test18.put("author", "An Author");
        test18.put("title", "Meaningful Title");
        test18.put("booktitle", "Proceedings");
        test18.put("year", "2010");
        test18.put("editor", "Ed Editor");
        test18.put("series", "Notes");
        expected.put(test18, "In Ed Editor, editor, Proceedings, Notes.");

        BibItem test19 = new BibItem("incollection", "test19");
        test19.put("author", "An Author");
        test19.put("title", "Meaningful Title");
        test19.put("booktitle", "Proceedings");
        test19.put("year", "2010");
        test19.put("editor", "Ed Editor");
        test19.put("pages", "1--13");
        expected.put(test19, "In Ed Editor, editor, Proceedings, pages 1&ndash;13.");

        BibItem test20 = new BibItem("incollection", "test20");
        test20.put("author", "An Author");
        test20.put("title", "Meaningful Title");
        test20.put("booktitle", "Proceedings");
        test20.put("year", "2010");
        test20.put("editor", "Ed Editor");
        test20.put("pages", "1--13");
        test20.put("series", "Notes");
        expected.put(test20, "In Ed Editor, editor, Proceedings, Notes, pages 1&ndash;13.");

        BibItem test21 = new BibItem("incollection", "test21");
        test21.put("author", "An Author");
        test21.put("title", "Meaningful Title");
        test21.put("booktitle", "Proceedings");
        test21.put("year", "2010");
        test21.put("editor", "Ed Editor");
        test21.put("type", "Typo");
        expected.put(test21, "In Ed Editor, editor, Proceedings.");

        BibItem test22 = new BibItem("incollection", "test22");
        test22.put("author", "An Author");
        test22.put("title", "Meaningful Title");
        test22.put("booktitle", "Proceedings");
        test22.put("year", "2010");
        test22.put("editor", "Ed Editor");
        test22.put("type", "Typo");
        test22.put("series", "Notes");
        expected.put(test22, "In Ed Editor, editor, Proceedings, Notes.");

        BibItem test23 = new BibItem("incollection", "test23");
        test23.put("author", "An Author");
        test23.put("title", "Meaningful Title");
        test23.put("booktitle", "Proceedings");
        test23.put("year", "2010");
        test23.put("editor", "Ed Editor");
        test23.put("type", "Typo");
        test23.put("pages", "1--13");
        expected.put(test23, "In Ed Editor, editor, Proceedings, pages 1&ndash;13.");

        BibItem test24 = new BibItem("incollection", "test24");
        test24.put("author", "An Author");
        test24.put("title", "Meaningful Title");
        test24.put("booktitle", "Proceedings");
        test24.put("year", "2010");
        test24.put("editor", "Ed Editor");
        test24.put("type", "Typo");
        test24.put("pages", "1--13");
        test24.put("series", "Notes");
        expected.put(test24, "In Ed Editor, editor, Proceedings, Notes, pages 1&ndash;13.");

        BibItem test25 = new BibItem("incollection", "test25");
        test25.put("author", "An Author");
        test25.put("title", "Meaningful Title");
        test25.put("booktitle", "Proceedings");
        test25.put("year", "2010");
        test25.put("editor", "Ed Editor");
        test25.put("chapter", "5");
        expected.put(test25, "In Ed Editor, editor, Proceedings, chapter 5.");

        BibItem test26 = new BibItem("incollection", "test26");
        test26.put("author", "An Author");
        test26.put("title", "Meaningful Title");
        test26.put("booktitle", "Proceedings");
        test26.put("year", "2010");
        test26.put("editor", "Ed Editor");
        test26.put("chapter", "5");
        test26.put("series", "Notes");
        expected.put(test26, "In Ed Editor, editor, Proceedings, Notes, chapter 5.");

        BibItem test27 = new BibItem("incollection", "test27");
        test27.put("author", "An Author");
        test27.put("title", "Meaningful Title");
        test27.put("booktitle", "Proceedings");
        test27.put("year", "2010");
        test27.put("editor", "Ed Editor");
        test27.put("chapter", "5");
        test27.put("pages", "1--13");
        expected.put(test27, "In Ed Editor, editor, Proceedings, chapter 5, pages 1&ndash;13.");

        BibItem test28 = new BibItem("incollection", "test28");
        test28.put("author", "An Author");
        test28.put("title", "Meaningful Title");
        test28.put("booktitle", "Proceedings");
        test28.put("year", "2010");
        test28.put("editor", "Ed Editor");
        test28.put("chapter", "5");
        test28.put("pages", "1--13");
        test28.put("series", "Notes");
        expected.put(test28, "In Ed Editor, editor, Proceedings, Notes, chapter 5, pages 1&ndash;13.");

        BibItem test29 = new BibItem("incollection", "test29");
        test29.put("author", "An Author");
        test29.put("title", "Meaningful Title");
        test29.put("booktitle", "Proceedings");
        test29.put("year", "2010");
        test29.put("editor", "Ed Editor");
        test29.put("chapter", "5");
        test29.put("type", "Typo");
        expected.put(test29, "In Ed Editor, editor, Proceedings, typo 5.");

        BibItem test30 = new BibItem("incollection", "test30");
        test30.put("author", "An Author");
        test30.put("title", "Meaningful Title");
        test30.put("booktitle", "Proceedings");
        test30.put("year", "2010");
        test30.put("editor", "Ed Editor");
        test30.put("chapter", "5");
        test30.put("type", "Typo");
        test30.put("series", "Notes");
        expected.put(test30, "In Ed Editor, editor, Proceedings, Notes, typo 5.");

        BibItem test31 = new BibItem("incollection", "test31");
        test31.put("author", "An Author");
        test31.put("title", "Meaningful Title");
        test31.put("booktitle", "Proceedings");
        test31.put("year", "2010");
        test31.put("editor", "Ed Editor");
        test31.put("chapter", "5");
        test31.put("type", "Typo");
        test31.put("pages", "1--13");
        expected.put(test31, "In Ed Editor, editor, Proceedings, typo 5, pages 1&ndash;13.");

        BibItem test32 = new BibItem("incollection", "test32");
        test32.put("author", "An Author");
        test32.put("title", "Meaningful Title");
        test32.put("booktitle", "Proceedings");
        test32.put("year", "2010");
        test32.put("editor", "Ed Editor");
        test32.put("chapter", "5");
        test32.put("type", "Typo");
        test32.put("pages", "1--13");
        test32.put("series", "Notes");
        expected.put(test32, "In Ed Editor, editor, Proceedings, Notes, typo 5, pages 1&ndash;13.");

        BibItem test33 = new BibItem("incollection", "test33");
        test33.put("author", "An Author");
        test33.put("title", "Meaningful Title");
        test33.put("booktitle", "Proceedings");
        test33.put("year", "2010");
        test33.put("volume", "1337");
        expected.put(test33, "In Proceedings, volume 1337.");

        BibItem test34 = new BibItem("incollection", "test34");
        test34.put("author", "An Author");
        test34.put("title", "Meaningful Title");
        test34.put("booktitle", "Proceedings");
        test34.put("year", "2010");
        test34.put("volume", "1337");
        test34.put("series", "Notes");
        expected.put(test34, "In Proceedings, volume 1337 of Notes.");

        BibItem test35 = new BibItem("incollection", "test35");
        test35.put("author", "An Author");
        test35.put("title", "Meaningful Title");
        test35.put("booktitle", "Proceedings");
        test35.put("year", "2010");
        test35.put("volume", "1337");
        test35.put("pages", "1--13");
        expected.put(test35, "In Proceedings, volume 1337, pages 1&ndash;13.");

        BibItem test36 = new BibItem("incollection", "test36");
        test36.put("author", "An Author");
        test36.put("title", "Meaningful Title");
        test36.put("booktitle", "Proceedings");
        test36.put("year", "2010");
        test36.put("volume", "1337");
        test36.put("pages", "1--13");
        test36.put("series", "Notes");
        expected.put(test36, "In Proceedings, volume 1337 of Notes, pages 1&ndash;13.");

        BibItem test37 = new BibItem("incollection", "test37");
        test37.put("author", "An Author");
        test37.put("title", "Meaningful Title");
        test37.put("booktitle", "Proceedings");
        test37.put("year", "2010");
        test37.put("volume", "1337");
        test37.put("type", "Typo");
        expected.put(test37, "In Proceedings, volume 1337.");

        BibItem test38 = new BibItem("incollection", "test38");
        test38.put("author", "An Author");
        test38.put("title", "Meaningful Title");
        test38.put("booktitle", "Proceedings");
        test38.put("year", "2010");
        test38.put("volume", "1337");
        test38.put("type", "Typo");
        test38.put("series", "Notes");
        expected.put(test38, "In Proceedings, volume 1337 of Notes.");

        BibItem test39 = new BibItem("incollection", "test39");
        test39.put("author", "An Author");
        test39.put("title", "Meaningful Title");
        test39.put("booktitle", "Proceedings");
        test39.put("year", "2010");
        test39.put("volume", "1337");
        test39.put("type", "Typo");
        test39.put("pages", "1--13");
        expected.put(test39, "In Proceedings, volume 1337, pages 1&ndash;13.");

        BibItem test40 = new BibItem("incollection", "test40");
        test40.put("author", "An Author");
        test40.put("title", "Meaningful Title");
        test40.put("booktitle", "Proceedings");
        test40.put("year", "2010");
        test40.put("volume", "1337");
        test40.put("type", "Typo");
        test40.put("pages", "1--13");
        test40.put("series", "Notes");
        expected.put(test40, "In Proceedings, volume 1337 of Notes, pages 1&ndash;13.");

        BibItem test41 = new BibItem("incollection", "test41");
        test41.put("author", "An Author");
        test41.put("title", "Meaningful Title");
        test41.put("booktitle", "Proceedings");
        test41.put("year", "2010");
        test41.put("volume", "1337");
        test41.put("chapter", "5");
        expected.put(test41, "In Proceedings, volume 1337, chapter 5.");

        BibItem test42 = new BibItem("incollection", "test42");
        test42.put("author", "An Author");
        test42.put("title", "Meaningful Title");
        test42.put("booktitle", "Proceedings");
        test42.put("year", "2010");
        test42.put("volume", "1337");
        test42.put("chapter", "5");
        test42.put("series", "Notes");
        expected.put(test42, "In Proceedings, volume 1337 of Notes, chapter 5.");

        BibItem test43 = new BibItem("incollection", "test43");
        test43.put("author", "An Author");
        test43.put("title", "Meaningful Title");
        test43.put("booktitle", "Proceedings");
        test43.put("year", "2010");
        test43.put("volume", "1337");
        test43.put("chapter", "5");
        test43.put("pages", "1--13");
        expected.put(test43, "In Proceedings, volume 1337, chapter 5, pages 1&ndash;13.");

        BibItem test44 = new BibItem("incollection", "test44");
        test44.put("author", "An Author");
        test44.put("title", "Meaningful Title");
        test44.put("booktitle", "Proceedings");
        test44.put("year", "2010");
        test44.put("volume", "1337");
        test44.put("chapter", "5");
        test44.put("pages", "1--13");
        test44.put("series", "Notes");
        expected.put(test44, "In Proceedings, volume 1337 of Notes, chapter 5, pages 1&ndash;13.");

        BibItem test45 = new BibItem("incollection", "test45");
        test45.put("author", "An Author");
        test45.put("title", "Meaningful Title");
        test45.put("booktitle", "Proceedings");
        test45.put("year", "2010");
        test45.put("volume", "1337");
        test45.put("chapter", "5");
        test45.put("type", "Typo");
        expected.put(test45, "In Proceedings, volume 1337, typo 5.");

        BibItem test46 = new BibItem("incollection", "test46");
        test46.put("author", "An Author");
        test46.put("title", "Meaningful Title");
        test46.put("booktitle", "Proceedings");
        test46.put("year", "2010");
        test46.put("volume", "1337");
        test46.put("chapter", "5");
        test46.put("type", "Typo");
        test46.put("series", "Notes");
        expected.put(test46, "In Proceedings, volume 1337 of Notes, typo 5.");

        BibItem test47 = new BibItem("incollection", "test47");
        test47.put("author", "An Author");
        test47.put("title", "Meaningful Title");
        test47.put("booktitle", "Proceedings");
        test47.put("year", "2010");
        test47.put("volume", "1337");
        test47.put("chapter", "5");
        test47.put("type", "Typo");
        test47.put("pages", "1--13");
        expected.put(test47, "In Proceedings, volume 1337, typo 5, pages 1&ndash;13.");

        BibItem test48 = new BibItem("incollection", "test48");
        test48.put("author", "An Author");
        test48.put("title", "Meaningful Title");
        test48.put("booktitle", "Proceedings");
        test48.put("year", "2010");
        test48.put("volume", "1337");
        test48.put("chapter", "5");
        test48.put("type", "Typo");
        test48.put("pages", "1--13");
        test48.put("series", "Notes");
        expected.put(test48, "In Proceedings, volume 1337 of Notes, typo 5, pages 1&ndash;13.");

        BibItem test49 = new BibItem("incollection", "test49");
        test49.put("author", "An Author");
        test49.put("title", "Meaningful Title");
        test49.put("booktitle", "Proceedings");
        test49.put("year", "2010");
        test49.put("volume", "1337");
        test49.put("editor", "Ed Editor");
        expected.put(test49, "In Ed Editor, editor, Proceedings, volume 1337.");

        BibItem test50 = new BibItem("incollection", "test50");
        test50.put("author", "An Author");
        test50.put("title", "Meaningful Title");
        test50.put("booktitle", "Proceedings");
        test50.put("year", "2010");
        test50.put("volume", "1337");
        test50.put("editor", "Ed Editor");
        test50.put("series", "Notes");
        expected.put(test50, "In Ed Editor, editor, Proceedings, volume 1337 of Notes.");

        BibItem test51 = new BibItem("incollection", "test51");
        test51.put("author", "An Author");
        test51.put("title", "Meaningful Title");
        test51.put("booktitle", "Proceedings");
        test51.put("year", "2010");
        test51.put("volume", "1337");
        test51.put("editor", "Ed Editor");
        test51.put("pages", "1--13");
        expected.put(test51, "In Ed Editor, editor, Proceedings, volume 1337, pages 1&ndash;13.");

        BibItem test52 = new BibItem("incollection", "test52");
        test52.put("author", "An Author");
        test52.put("title", "Meaningful Title");
        test52.put("booktitle", "Proceedings");
        test52.put("year", "2010");
        test52.put("volume", "1337");
        test52.put("editor", "Ed Editor");
        test52.put("pages", "1--13");
        test52.put("series", "Notes");
        expected.put(test52, "In Ed Editor, editor, Proceedings, volume 1337 of Notes, pages 1&ndash;13.");

        BibItem test53 = new BibItem("incollection", "test53");
        test53.put("author", "An Author");
        test53.put("title", "Meaningful Title");
        test53.put("booktitle", "Proceedings");
        test53.put("year", "2010");
        test53.put("volume", "1337");
        test53.put("editor", "Ed Editor");
        test53.put("type", "Typo");
        expected.put(test53, "In Ed Editor, editor, Proceedings, volume 1337.");

        BibItem test54 = new BibItem("incollection", "test54");
        test54.put("author", "An Author");
        test54.put("title", "Meaningful Title");
        test54.put("booktitle", "Proceedings");
        test54.put("year", "2010");
        test54.put("volume", "1337");
        test54.put("editor", "Ed Editor");
        test54.put("type", "Typo");
        test54.put("series", "Notes");
        expected.put(test54, "In Ed Editor, editor, Proceedings, volume 1337 of Notes.");

        BibItem test55 = new BibItem("incollection", "test55");
        test55.put("author", "An Author");
        test55.put("title", "Meaningful Title");
        test55.put("booktitle", "Proceedings");
        test55.put("year", "2010");
        test55.put("volume", "1337");
        test55.put("editor", "Ed Editor");
        test55.put("type", "Typo");
        test55.put("pages", "1--13");
        expected.put(test55, "In Ed Editor, editor, Proceedings, volume 1337, pages 1&ndash;13.");

        BibItem test56 = new BibItem("incollection", "test56");
        test56.put("author", "An Author");
        test56.put("title", "Meaningful Title");
        test56.put("booktitle", "Proceedings");
        test56.put("year", "2010");
        test56.put("volume", "1337");
        test56.put("editor", "Ed Editor");
        test56.put("type", "Typo");
        test56.put("pages", "1--13");
        test56.put("series", "Notes");
        expected.put(test56, "In Ed Editor, editor, Proceedings, volume 1337 of Notes, pages 1&ndash;13.");

        BibItem test57 = new BibItem("incollection", "test57");
        test57.put("author", "An Author");
        test57.put("title", "Meaningful Title");
        test57.put("booktitle", "Proceedings");
        test57.put("year", "2010");
        test57.put("volume", "1337");
        test57.put("editor", "Ed Editor");
        test57.put("chapter", "5");
        expected.put(test57, "In Ed Editor, editor, Proceedings, volume 1337, chapter 5.");

        BibItem test58 = new BibItem("incollection", "test58");
        test58.put("author", "An Author");
        test58.put("title", "Meaningful Title");
        test58.put("booktitle", "Proceedings");
        test58.put("year", "2010");
        test58.put("volume", "1337");
        test58.put("editor", "Ed Editor");
        test58.put("chapter", "5");
        test58.put("series", "Notes");
        expected.put(test58, "In Ed Editor, editor, Proceedings, volume 1337 of Notes, chapter 5.");

        BibItem test59 = new BibItem("incollection", "test59");
        test59.put("author", "An Author");
        test59.put("title", "Meaningful Title");
        test59.put("booktitle", "Proceedings");
        test59.put("year", "2010");
        test59.put("volume", "1337");
        test59.put("editor", "Ed Editor");
        test59.put("chapter", "5");
        test59.put("pages", "1--13");
        expected.put(test59, "In Ed Editor, editor, Proceedings, volume 1337, chapter 5, pages 1&ndash;13.");

        BibItem test60 = new BibItem("incollection", "test60");
        test60.put("author", "An Author");
        test60.put("title", "Meaningful Title");
        test60.put("booktitle", "Proceedings");
        test60.put("year", "2010");
        test60.put("volume", "1337");
        test60.put("editor", "Ed Editor");
        test60.put("chapter", "5");
        test60.put("pages", "1--13");
        test60.put("series", "Notes");
        expected.put(test60, "In Ed Editor, editor, Proceedings, volume 1337 of Notes, chapter 5, pages 1&ndash;13.");

        BibItem test61 = new BibItem("incollection", "test61");
        test61.put("author", "An Author");
        test61.put("title", "Meaningful Title");
        test61.put("booktitle", "Proceedings");
        test61.put("year", "2010");
        test61.put("volume", "1337");
        test61.put("editor", "Ed Editor");
        test61.put("chapter", "5");
        test61.put("type", "Typo");
        expected.put(test61, "In Ed Editor, editor, Proceedings, volume 1337, typo 5.");

        BibItem test62 = new BibItem("incollection", "test62");
        test62.put("author", "An Author");
        test62.put("title", "Meaningful Title");
        test62.put("booktitle", "Proceedings");
        test62.put("year", "2010");
        test62.put("volume", "1337");
        test62.put("editor", "Ed Editor");
        test62.put("chapter", "5");
        test62.put("type", "Typo");
        test62.put("series", "Notes");
        expected.put(test62, "In Ed Editor, editor, Proceedings, volume 1337 of Notes, typo 5.");

        BibItem test63 = new BibItem("incollection", "test63");
        test63.put("author", "An Author");
        test63.put("title", "Meaningful Title");
        test63.put("booktitle", "Proceedings");
        test63.put("year", "2010");
        test63.put("volume", "1337");
        test63.put("editor", "Ed Editor");
        test63.put("chapter", "5");
        test63.put("type", "Typo");
        test63.put("pages", "1--13");
        expected.put(test63, "In Ed Editor, editor, Proceedings, volume 1337, typo 5, pages 1&ndash;13.");

        BibItem test64 = new BibItem("incollection", "test64");
        test64.put("author", "An Author");
        test64.put("title", "Meaningful Title");
        test64.put("booktitle", "Proceedings");
        test64.put("year", "2010");
        test64.put("volume", "1337");
        test64.put("editor", "Ed Editor");
        test64.put("chapter", "5");
        test64.put("type", "Typo");
        test64.put("pages", "1--13");
        test64.put("series", "Notes");
        expected.put(test64, "In Ed Editor, editor, Proceedings, volume 1337 of Notes, typo 5, pages 1&ndash;13.");

        BibItem test65 = new BibItem("incollection", "test65");
        test65.put("series", "Notes");
        test65.put("edition", "Third");
        test65.put("howpublished", "Message in a bottle");
        test65.put("pages", "1--13");
        test65.put("booktitle", "Proceedings");
        test65.put("number", "42");
        test65.put("type", "Typo");
        test65.put("publisher", "Bottomless Pit");
        test65.put("journal", "Journal");
        test65.put("author", "An Author");
        test65.put("title", "Meaningful Title");
        test65.put("organization", "Test Group");
        test65.put("chapter", "5");
        test65.put("editor", "Ed Editor");
        test65.put("school", "School");
        test65.put("address", "Nederweert");
        test65.put("volume", "1337");
        test65.put("month", "January");
        test65.put("year", "2010");
        test65.put("note", "Note to self");
        test65.put("institution", "University");
        expected.put(test65, "In Ed Editor, editor, Proceedings, volume 1337 of Notes, typo 5, pages 1&ndash;13. Bottomless Pit, third edition,");

        StringWriter output = new StringWriter();
        BufferedWriter buffer = new BufferedWriter(output);
        HTMLBibItemWriter testInstance = new HTMLBibItemWriter(buffer, null);

        for (BibItem input : expected.keySet()) {
            String expectedResult = expected.get(input);

            try {
                testInstance.writeInCollection(input);
                buffer.flush();
                String result = removeTags(output.getBuffer().toString()).trim();

                assertEquals(expectedResult, result);
            } catch (IOException ex) {
                fail("writeInCollection threw IOException on input:\n" + input + "\nException:\n" + ex);
            }

            // Clear the output
            output.getBuffer().delete(0, output.getBuffer().length());
        }
    }

    @Test
    public void testWriteManualNoTags() {
        System.out.println("writeManual");

        HashMap<BibItem, String> expected = new LinkedHashMap<>();

        BibItem test1 = new BibItem("manual", "test1");
        test1.put("author", "An Author");
        test1.put("title", "Meaningful Title");
        test1.put("year", "2010");
        expected.put(test1, "");

        BibItem test2 = new BibItem("manual", "test2");
        test2.put("author", "An Author");
        test2.put("title", "Meaningful Title");
        test2.put("year", "2010");
        test2.put("edition", "Third");
        expected.put(test2, "Third edition,");

        BibItem test3 = new BibItem("manual", "test3");
        test3.put("author", "An Author");
        test3.put("title", "Meaningful Title");
        test3.put("year", "2010");
        test3.put("organization", "Test Group");
        expected.put(test3, "Test Group,");

        BibItem test4 = new BibItem("manual", "test4");
        test4.put("author", "An Author");
        test4.put("title", "Meaningful Title");
        test4.put("year", "2010");
        test4.put("organization", "Test Group");
        test4.put("edition", "Third");
        expected.put(test4, "Test Group, third edition,");

        BibItem test5 = new BibItem("manual", "test5");
        test5.put("series", "Notes");
        test5.put("edition", "Third");
        test5.put("howpublished", "Message in a bottle");
        test5.put("pages", "1--13");
        test5.put("booktitle", "Proceedings");
        test5.put("number", "42");
        test5.put("type", "Typo");
        test5.put("publisher", "Bottomless Pit");
        test5.put("journal", "Journal");
        test5.put("author", "An Author");
        test5.put("title", "Meaningful Title");
        test5.put("organization", "Test Group");
        test5.put("chapter", "5");
        test5.put("editor", "Ed Editor");
        test5.put("school", "School");
        test5.put("address", "Nederweert");
        test5.put("volume", "1337");
        test5.put("month", "January");
        test5.put("year", "2010");
        test5.put("note", "Note to self");
        test5.put("institution", "University");
        expected.put(test5, "Test Group, third edition,");

        StringWriter output = new StringWriter();
        BufferedWriter buffer = new BufferedWriter(output);
        HTMLBibItemWriter testInstance = new HTMLBibItemWriter(buffer, null);

        for (BibItem input : expected.keySet()) {
            String expectedResult = expected.get(input);

            try {
                testInstance.writeManual(input);
                buffer.flush();
                String result = removeTags(output.getBuffer().toString()).trim();

                assertEquals(expectedResult, result);
            } catch (IOException ex) {
                fail("writeManual threw IOException on input:\n" + input + "\nException:\n" + ex);
            }

            // Clear the output
            output.getBuffer().delete(0, output.getBuffer().length());
        }
    }

    @Test
    public void testWriteMiscNoTags() {
        System.out.println("writeMisc");

        HashMap<BibItem, String> expected = new LinkedHashMap<>();

        BibItem test1 = new BibItem("misc", "test1");
        test1.put("author", "An Author");
        test1.put("title", "Meaningful Title");
        test1.put("year", "2010");
        expected.put(test1, "");

        BibItem test2 = new BibItem("misc", "test2");
        test2.put("author", "An Author");
        test2.put("title", "Meaningful Title");
        test2.put("year", "2010");
        test2.put("howpublished", "Message in a bottle");
        expected.put(test2, "Message in a bottle,");

        BibItem test3 = new BibItem("misc", "test3");
        test3.put("series", "Notes");
        test3.put("edition", "Third");
        test3.put("howpublished", "Message in a bottle");
        test3.put("pages", "1--13");
        test3.put("booktitle", "Proceedings");
        test3.put("number", "42");
        test3.put("type", "Typo");
        test3.put("publisher", "Bottomless Pit");
        test3.put("journal", "Journal");
        test3.put("author", "An Author");
        test3.put("title", "Meaningful Title");
        test3.put("organization", "Test Group");
        test3.put("chapter", "5");
        test3.put("editor", "Ed Editor");
        test3.put("school", "School");
        test3.put("address", "Nederweert");
        test3.put("volume", "1337");
        test3.put("month", "January");
        test3.put("year", "2010");
        test3.put("note", "Note to self");
        test3.put("institution", "University");
        expected.put(test3, "Message in a bottle, Nederweert,");

        StringWriter output = new StringWriter();
        BufferedWriter buffer = new BufferedWriter(output);
        HTMLBibItemWriter testInstance = new HTMLBibItemWriter(buffer, null);

        for (BibItem input : expected.keySet()) {
            String expectedResult = expected.get(input);

            try {
                testInstance.writeMisc(input);
                buffer.flush();
                String result = removeTags(output.getBuffer().toString()).trim();

                assertEquals(expectedResult, result);
            } catch (IOException ex) {
                fail("writeMisc threw IOException on input:\n" + input + "\nException:\n" + ex);
            }

            // Clear the output
            output.getBuffer().delete(0, output.getBuffer().length());
        }
    }

    @Test
    public void testWriteOnlineNoTags() {
        System.out.println("writeOnline");

        HashMap<BibItem, String> expected = new LinkedHashMap<>();

        BibItem test1 = new BibItem("online", "test1");
        test1.put("series", "Notes");
        test1.put("edition", "Third");
        test1.put("howpublished", "Message in a bottle");
        test1.put("pages", "1--13");
        test1.put("booktitle", "Proceedings");
        test1.put("number", "42");
        test1.put("type", "Typo");
        test1.put("publisher", "Bottomless Pit");
        test1.put("journal", "Journal");
        test1.put("author", "An Author");
        test1.put("title", "Meaningful Title");
        test1.put("organization", "Test Group");
        test1.put("chapter", "5");
        test1.put("editor", "Ed Editor");
        test1.put("school", "School");
        test1.put("address", "Nederweert");
        test1.put("volume", "1337");
        test1.put("month", "January");
        test1.put("year", "2010");
        test1.put("note", "Note to self");
        test1.put("institution", "University");
        test1.put("url", "http://www.awesome.com");
        expected.put(test1, "");

        StringWriter output = new StringWriter();
        BufferedWriter buffer = new BufferedWriter(output);
        HTMLBibItemWriter testInstance = new HTMLBibItemWriter(buffer, null);

        for (BibItem input : expected.keySet()) {
            String expectedResult = expected.get(input);

            try {
                testInstance.writeOnline(input);
                buffer.flush();
                String result = removeTags(output.getBuffer().toString()).trim();

                assertEquals(expectedResult, result);
            } catch (IOException ex) {
                fail("writeOnline threw IOException on input:\n" + input + "\nException:\n" + ex);
            }

            // Clear the output
            output.getBuffer().delete(0, output.getBuffer().length());
        }
    }

    @Test
    public void testWritePatentNoTags() {
        System.out.println("writePatent");

        HashMap<BibItem, String> expected = new LinkedHashMap<>();

        // TODO

        StringWriter output = new StringWriter();
        BufferedWriter buffer = new BufferedWriter(output);
        HTMLBibItemWriter testInstance = new HTMLBibItemWriter(buffer, null);

        for (BibItem input : expected.keySet()) {
            String expectedResult = expected.get(input);

            try {
                testInstance.writePatent(input);
                buffer.flush();
                String result = removeTags(output.getBuffer().toString()).trim();

                assertEquals(expectedResult, result);
            } catch (IOException ex) {
                fail("writePatent threw IOException on input:\n" + input + "\nException:\n" + ex);
            }

            // Clear the output
            output.getBuffer().delete(0, output.getBuffer().length());
        }
    }

    @Test
    public void testWriteProceedingsNoTags() {
        System.out.println("writeProceedings");

        HashMap<BibItem, String> expected = new LinkedHashMap<>();

        BibItem test1 = new BibItem("proceedings", "test1");
        test1.put("author", "An Author");
        test1.put("title", "Meaningful Title");
        test1.put("year", "2010");
        expected.put(test1, "");

        BibItem test2 = new BibItem("proceedings", "test2");
        test2.put("author", "An Author");
        test2.put("title", "Meaningful Title");
        test2.put("year", "2010");
        test2.put("series", "Notes");
        expected.put(test2, "Notes,");

        BibItem test3 = new BibItem("proceedings", "test3");
        test3.put("author", "An Author");
        test3.put("title", "Meaningful Title");
        test3.put("year", "2010");
        test3.put("address", "Nederweert");
        expected.put(test3, "Nederweert,");

        BibItem test4 = new BibItem("proceedings", "test4");
        test4.put("author", "An Author");
        test4.put("title", "Meaningful Title");
        test4.put("year", "2010");
        test4.put("address", "Nederweert");
        test4.put("series", "Notes");
        expected.put(test4, "Notes, Nederweert,");

        BibItem test5 = new BibItem("proceedings", "test5");
        test5.put("author", "An Author");
        test5.put("title", "Meaningful Title");
        test5.put("year", "2010");
        test5.put("volume", "1337");
        expected.put(test5, "Volume 1337,");

        BibItem test6 = new BibItem("proceedings", "test6");
        test6.put("author", "An Author");
        test6.put("title", "Meaningful Title");
        test6.put("year", "2010");
        test6.put("volume", "1337");
        test6.put("series", "Notes");
        expected.put(test6, "Volume 1337 of Notes,");

        BibItem test7 = new BibItem("proceedings", "test7");
        test7.put("author", "An Author");
        test7.put("title", "Meaningful Title");
        test7.put("year", "2010");
        test7.put("volume", "1337");
        test7.put("address", "Nederweert");
        expected.put(test7, "Volume 1337, Nederweert,");

        BibItem test8 = new BibItem("proceedings", "test8");
        test8.put("author", "An Author");
        test8.put("title", "Meaningful Title");
        test8.put("year", "2010");
        test8.put("volume", "1337");
        test8.put("address", "Nederweert");
        test8.put("series", "Notes");
        expected.put(test8, "Volume 1337 of Notes, Nederweert,");

        BibItem test9 = new BibItem("proceedings", "test9");
        test9.put("series", "Notes");
        test9.put("edition", "Third");
        test9.put("howpublished", "Message in a bottle");
        test9.put("pages", "1--13");
        test9.put("booktitle", "Proceedings");
        test9.put("number", "42");
        test9.put("type", "Typo");
        test9.put("publisher", "Bottomless Pit");
        test9.put("journal", "Journal");
        test9.put("author", "An Author");
        test9.put("title", "Meaningful Title");
        test9.put("organization", "Test Group");
        test9.put("chapter", "5");
        test9.put("editor", "Ed Editor");
        test9.put("school", "School");
        test9.put("address", "Nederweert");
        test9.put("volume", "1337");
        test9.put("month", "January");
        test9.put("year", "2010");
        test9.put("note", "Note to self");
        test9.put("institution", "University");
        expected.put(test9, "Volume 1337 of Notes, Nederweert,");

        StringWriter output = new StringWriter();
        BufferedWriter buffer = new BufferedWriter(output);
        HTMLBibItemWriter testInstance = new HTMLBibItemWriter(buffer, null);

        for (BibItem input : expected.keySet()) {
            String expectedResult = expected.get(input);

            try {
                testInstance.writeProceedings(input);
                buffer.flush();
                String result = removeTags(output.getBuffer().toString()).trim();

                assertEquals(expectedResult, result);
            } catch (IOException ex) {
                fail("writeProceedings threw IOException on input:\n" + input + "\nException:\n" + ex);
            }

            // Clear the output
            output.getBuffer().delete(0, output.getBuffer().length());
        }
    }

    @Test
    public void testWriteInProceedingsNoTags() {
        System.out.println("writeInProceedings");

        HashMap<BibItem, String> expected = new LinkedHashMap<>();

        BibItem test1 = new BibItem("inproceedings", "test1");
        test1.put("author", "An Author");
        test1.put("title", "Meaningful Title");
        test1.put("booktitle", "Proceedings");
        test1.put("year", "2010");
        expected.put(test1, "In Proceedings,");

        BibItem test2 = new BibItem("inproceedings", "test2");
        test2.put("author", "An Author");
        test2.put("title", "Meaningful Title");
        test2.put("booktitle", "Proceedings");
        test2.put("year", "2010");
        test2.put("series", "Notes");
        expected.put(test2, "In Proceedings, Notes,");

        BibItem test3 = new BibItem("inproceedings", "test3");
        test3.put("author", "An Author");
        test3.put("title", "Meaningful Title");
        test3.put("booktitle", "Proceedings");
        test3.put("year", "2010");
        test3.put("pages", "1--13");
        expected.put(test3, "In Proceedings, pages 1&ndash;13,");

        BibItem test4 = new BibItem("inproceedings", "test4");
        test4.put("author", "An Author");
        test4.put("title", "Meaningful Title");
        test4.put("booktitle", "Proceedings");
        test4.put("year", "2010");
        test4.put("pages", "1--13");
        test4.put("series", "Notes");
        expected.put(test4, "In Proceedings, Notes, pages 1&ndash;13,");

        BibItem test5 = new BibItem("inproceedings", "test5");
        test5.put("author", "An Author");
        test5.put("title", "Meaningful Title");
        test5.put("booktitle", "Proceedings");
        test5.put("year", "2010");
        test5.put("number", "42");
        expected.put(test5, "In Proceedings, number 42,");

        BibItem test6 = new BibItem("inproceedings", "test6");
        test6.put("author", "An Author");
        test6.put("title", "Meaningful Title");
        test6.put("booktitle", "Proceedings");
        test6.put("year", "2010");
        test6.put("number", "42");
        test6.put("series", "Notes");
        expected.put(test6, "In Proceedings, number 42 in Notes,");

        BibItem test7 = new BibItem("inproceedings", "test7");
        test7.put("author", "An Author");
        test7.put("title", "Meaningful Title");
        test7.put("booktitle", "Proceedings");
        test7.put("year", "2010");
        test7.put("number", "42");
        test7.put("pages", "1--13");
        expected.put(test7, "In Proceedings, number 42, pages 1&ndash;13,");

        BibItem test8 = new BibItem("inproceedings", "test8");
        test8.put("author", "An Author");
        test8.put("title", "Meaningful Title");
        test8.put("booktitle", "Proceedings");
        test8.put("year", "2010");
        test8.put("number", "42");
        test8.put("pages", "1--13");
        test8.put("series", "Notes");
        expected.put(test8, "In Proceedings, number 42 in Notes, pages 1&ndash;13,");

        BibItem test9 = new BibItem("inproceedings", "test9");
        test9.put("author", "An Author");
        test9.put("title", "Meaningful Title");
        test9.put("booktitle", "Proceedings");
        test9.put("year", "2010");
        test9.put("address", "Nederweert");
        expected.put(test9, "In Proceedings, Nederweert,");

        BibItem test10 = new BibItem("inproceedings", "test10");
        test10.put("author", "An Author");
        test10.put("title", "Meaningful Title");
        test10.put("booktitle", "Proceedings");
        test10.put("year", "2010");
        test10.put("address", "Nederweert");
        test10.put("series", "Notes");
        expected.put(test10, "In Proceedings, Notes, Nederweert,");

        BibItem test11 = new BibItem("inproceedings", "test11");
        test11.put("author", "An Author");
        test11.put("title", "Meaningful Title");
        test11.put("booktitle", "Proceedings");
        test11.put("year", "2010");
        test11.put("address", "Nederweert");
        test11.put("pages", "1--13");
        expected.put(test11, "In Proceedings, pages 1&ndash;13, Nederweert,");

        BibItem test12 = new BibItem("inproceedings", "test12");
        test12.put("author", "An Author");
        test12.put("title", "Meaningful Title");
        test12.put("booktitle", "Proceedings");
        test12.put("year", "2010");
        test12.put("address", "Nederweert");
        test12.put("pages", "1--13");
        test12.put("series", "Notes");
        expected.put(test12, "In Proceedings, Notes, pages 1&ndash;13, Nederweert,");

        BibItem test13 = new BibItem("inproceedings", "test13");
        test13.put("author", "An Author");
        test13.put("title", "Meaningful Title");
        test13.put("booktitle", "Proceedings");
        test13.put("year", "2010");
        test13.put("address", "Nederweert");
        test13.put("number", "42");
        expected.put(test13, "In Proceedings, number 42, Nederweert,");

        BibItem test14 = new BibItem("inproceedings", "test14");
        test14.put("author", "An Author");
        test14.put("title", "Meaningful Title");
        test14.put("booktitle", "Proceedings");
        test14.put("year", "2010");
        test14.put("address", "Nederweert");
        test14.put("number", "42");
        test14.put("series", "Notes");
        expected.put(test14, "In Proceedings, number 42 in Notes, Nederweert,");

        BibItem test15 = new BibItem("inproceedings", "test15");
        test15.put("author", "An Author");
        test15.put("title", "Meaningful Title");
        test15.put("booktitle", "Proceedings");
        test15.put("year", "2010");
        test15.put("address", "Nederweert");
        test15.put("number", "42");
        test15.put("pages", "1--13");
        expected.put(test15, "In Proceedings, number 42, pages 1&ndash;13, Nederweert,");

        BibItem test16 = new BibItem("inproceedings", "test16");
        test16.put("author", "An Author");
        test16.put("title", "Meaningful Title");
        test16.put("booktitle", "Proceedings");
        test16.put("year", "2010");
        test16.put("address", "Nederweert");
        test16.put("number", "42");
        test16.put("pages", "1--13");
        test16.put("series", "Notes");
        expected.put(test16, "In Proceedings, number 42 in Notes, pages 1&ndash;13, Nederweert,");

        BibItem test17 = new BibItem("inproceedings", "test17");
        test17.put("author", "An Author");
        test17.put("title", "Meaningful Title");
        test17.put("booktitle", "Proceedings");
        test17.put("year", "2010");
        test17.put("volume", "1337");
        expected.put(test17, "In Proceedings, volume 1337,");

        BibItem test18 = new BibItem("inproceedings", "test18");
        test18.put("author", "An Author");
        test18.put("title", "Meaningful Title");
        test18.put("booktitle", "Proceedings");
        test18.put("year", "2010");
        test18.put("volume", "1337");
        test18.put("series", "Notes");
        expected.put(test18, "In Proceedings, volume 1337 of Notes,");

        BibItem test19 = new BibItem("inproceedings", "test19");
        test19.put("author", "An Author");
        test19.put("title", "Meaningful Title");
        test19.put("booktitle", "Proceedings");
        test19.put("year", "2010");
        test19.put("volume", "1337");
        test19.put("pages", "1--13");
        expected.put(test19, "In Proceedings, volume 1337, pages 1&ndash;13,");

        BibItem test20 = new BibItem("inproceedings", "test20");
        test20.put("author", "An Author");
        test20.put("title", "Meaningful Title");
        test20.put("booktitle", "Proceedings");
        test20.put("year", "2010");
        test20.put("volume", "1337");
        test20.put("pages", "1--13");
        test20.put("series", "Notes");
        expected.put(test20, "In Proceedings, volume 1337 of Notes, pages 1&ndash;13,");

        BibItem test21 = new BibItem("inproceedings", "test21");
        test21.put("author", "An Author");
        test21.put("title", "Meaningful Title");
        test21.put("booktitle", "Proceedings");
        test21.put("year", "2010");
        test21.put("volume", "1337");
        test21.put("address", "Nederweert");
        expected.put(test21, "In Proceedings, volume 1337, Nederweert,");

        BibItem test22 = new BibItem("inproceedings", "test22");
        test22.put("author", "An Author");
        test22.put("title", "Meaningful Title");
        test22.put("booktitle", "Proceedings");
        test22.put("year", "2010");
        test22.put("volume", "1337");
        test22.put("address", "Nederweert");
        test22.put("series", "Notes");
        expected.put(test22, "In Proceedings, volume 1337 of Notes, Nederweert,");

        BibItem test23 = new BibItem("inproceedings", "test23");
        test23.put("author", "An Author");
        test23.put("title", "Meaningful Title");
        test23.put("booktitle", "Proceedings");
        test23.put("year", "2010");
        test23.put("volume", "1337");
        test23.put("address", "Nederweert");
        test23.put("pages", "1--13");
        expected.put(test23, "In Proceedings, volume 1337, pages 1&ndash;13, Nederweert,");

        BibItem test24 = new BibItem("inproceedings", "test24");
        test24.put("author", "An Author");
        test24.put("title", "Meaningful Title");
        test24.put("booktitle", "Proceedings");
        test24.put("year", "2010");
        test24.put("volume", "1337");
        test24.put("address", "Nederweert");
        test24.put("pages", "1--13");
        test24.put("series", "Notes");
        expected.put(test24, "In Proceedings, volume 1337 of Notes, pages 1&ndash;13, Nederweert,");

        BibItem test25 = new BibItem("inproceedings", "test25");
        test25.put("series", "Notes");
        test25.put("edition", "Third");
        test25.put("howpublished", "Message in a bottle");
        test25.put("pages", "1--13");
        test25.put("booktitle", "Proceedings");
        test25.put("number", "42");
        test25.put("type", "Typo");
        test25.put("publisher", "Bottomless Pit");
        test25.put("journal", "Journal");
        test25.put("author", "An Author");
        test25.put("title", "Meaningful Title");
        test25.put("organization", "Test Group");
        test25.put("chapter", "5");
        test25.put("editor", "Ed Editor");
        test25.put("school", "School");
        test25.put("address", "Nederweert");
        test25.put("volume", "1337");
        test25.put("month", "January");
        test25.put("year", "2010");
        test25.put("note", "Note to self");
        test25.put("institution", "University");
        expected.put(test25, "In Proceedings, volume 1337 of Notes, pages 1&ndash;13, Nederweert,");

        StringWriter output = new StringWriter();
        BufferedWriter buffer = new BufferedWriter(output);
        HTMLBibItemWriter testInstance = new HTMLBibItemWriter(buffer, null);

        for (BibItem input : expected.keySet()) {
            String expectedResult = expected.get(input);

            try {
                testInstance.writeInProceedings(input);
                buffer.flush();
                String result = removeTags(output.getBuffer().toString()).trim();

                assertEquals(expectedResult, result);
            } catch (IOException ex) {
                fail("writeInProceedings threw IOException on input:\n" + input + "\nException:\n" + ex);
            }

            // Clear the output
            output.getBuffer().delete(0, output.getBuffer().length());
        }
    }

    @Test
    public void testWriteReportNoTags() {
        System.out.println("writeReport");

        HashMap<BibItem, String> expected = new LinkedHashMap<>();

        BibItem test1 = new BibItem("report", "test1");
        test1.put("author", "An Author");
        test1.put("title", "Meaningful Title");
        test1.put("type", "Typo");
        test1.put("institution", "University");
        test1.put("year", "2010");
        expected.put(test1, "Typo, University,");

        BibItem test2 = new BibItem("report", "test2");
        test2.put("author", "An Author");
        test2.put("title", "Meaningful Title");
        test2.put("type", "Typo");
        test2.put("institution", "University");
        test2.put("year", "2010");
        test2.put("number", "42");
        expected.put(test2, "Typo 42, University,");

        BibItem test3 = new BibItem("report", "test3");
        test3.put("author", "An Author");
        test3.put("title", "Meaningful Title");
        test3.put("type", "Typo");
        test3.put("institution", "University");
        test3.put("year", "2010");
        test3.put("address", "Nederweert");
        expected.put(test3, "Typo, University, Nederweert,");

        BibItem test4 = new BibItem("report", "test4");
        test4.put("author", "An Author");
        test4.put("title", "Meaningful Title");
        test4.put("type", "Typo");
        test4.put("institution", "University");
        test4.put("year", "2010");
        test4.put("address", "Nederweert");
        test4.put("number", "42");
        expected.put(test4, "Typo 42, University, Nederweert,");

        BibItem test5 = new BibItem("report", "test5");
        test5.put("series", "Notes");
        test5.put("edition", "Third");
        test5.put("howpublished", "Message in a bottle");
        test5.put("pages", "1--13");
        test5.put("booktitle", "Proceedings");
        test5.put("number", "42");
        test5.put("type", "Typo");
        test5.put("publisher", "Bottomless Pit");
        test5.put("journal", "Journal");
        test5.put("author", "An Author");
        test5.put("title", "Meaningful Title");
        test5.put("organization", "Test Group");
        test5.put("chapter", "5");
        test5.put("editor", "Ed Editor");
        test5.put("school", "School");
        test5.put("address", "Nederweert");
        test5.put("volume", "1337");
        test5.put("month", "January");
        test5.put("year", "2010");
        test5.put("note", "Note to self");
        test5.put("institution", "University");
        expected.put(test5, "Typo 42, University, Nederweert,");

        StringWriter output = new StringWriter();
        BufferedWriter buffer = new BufferedWriter(output);
        HTMLBibItemWriter testInstance = new HTMLBibItemWriter(buffer, null);

        for (BibItem input : expected.keySet()) {
            String expectedResult = expected.get(input);

            try {
                testInstance.writeReport(input);
                buffer.flush();
                String result = removeTags(output.getBuffer().toString()).trim();

                assertEquals(expectedResult, result);
            } catch (IOException ex) {
                fail("writeReport threw IOException on input:\n" + input + "\nException:\n" + ex);
            }

            // Clear the output
            output.getBuffer().delete(0, output.getBuffer().length());
        }
    }

    @Test
    public void testWriteThesisNoTags() {
        System.out.println("writeThesis");

        HashMap<BibItem, String> expected = new LinkedHashMap<>();

        BibItem test1 = new BibItem("thesis", "test1");
        test1.put("author", "An Author");
        test1.put("title", "Meaningful Title");
        test1.put("type", "Typo");
        test1.put("school", "School");
        test1.put("year", "2010");
        expected.put(test1, "Typo, School,");

        BibItem test2 = new BibItem("thesis", "test2");
        test2.put("author", "An Author");
        test2.put("title", "Meaningful Title");
        test2.put("type", "Typo");
        test2.put("school", "School");
        test2.put("year", "2010");
        test2.put("address", "Nederweert");
        expected.put(test2, "Typo, School, Nederweert,");

        BibItem test3 = new BibItem("thesis", "test3");
        test3.put("series", "Notes");
        test3.put("edition", "Third");
        test3.put("howpublished", "Message in a bottle");
        test3.put("pages", "1--13");
        test3.put("booktitle", "Proceedings");
        test3.put("number", "42");
        test3.put("type", "Typo");
        test3.put("publisher", "Bottomless Pit");
        test3.put("journal", "Journal");
        test3.put("author", "An Author");
        test3.put("title", "Meaningful Title");
        test3.put("organization", "Test Group");
        test3.put("chapter", "5");
        test3.put("editor", "Ed Editor");
        test3.put("school", "School");
        test3.put("address", "Nederweert");
        test3.put("volume", "1337");
        test3.put("month", "January");
        test3.put("year", "2010");
        test3.put("note", "Note to self");
        test3.put("institution", "University");
        expected.put(test3, "Typo, School, Nederweert,");

        BibItem test4 = new BibItem("phdthesis", "test4");
        test4.put("author", "An Author");
        test4.put("title", "Meaningful Title");
        test4.put("school", "School");
        test4.put("year", "2010");
        expected.put(test4, "PhD thesis, School,");

        BibItem test5 = new BibItem("mastersthesis", "test5");
        test5.put("author", "An Author");
        test5.put("title", "Meaningful Title");
        test5.put("school", "School");
        test5.put("year", "2010");
        expected.put(test5, "Master’s thesis, School,");

        StringWriter output = new StringWriter();
        BufferedWriter buffer = new BufferedWriter(output);
        HTMLBibItemWriter testInstance = new HTMLBibItemWriter(buffer, null);

        for (BibItem input : expected.keySet()) {
            String expectedResult = expected.get(input);

            try {
                testInstance.writeThesis(input);
                buffer.flush();
                String result = removeTags(output.getBuffer().toString()).trim();

                assertEquals(expectedResult, result);
            } catch (IOException ex) {
                fail("writeThesis threw IOException on input:\n" + input + "\nException:\n" + ex);
            }

            // Clear the output
            output.getBuffer().delete(0, output.getBuffer().length());
        }
    }

    @Test
    public void testWriteUnpublishedNoTags() {
        System.out.println("writeUnpublished");

        HashMap<BibItem, String> expected = new LinkedHashMap<>();

        BibItem test1 = new BibItem("unpublished", "test1");
        test1.put("author", "An Author");
        test1.put("title", "Meaningful Title");
        test1.put("year", "2010");
        expected.put(test1, "");

        BibItem test2 = new BibItem("unpublished", "test2");
        test2.put("author", "An Author");
        test2.put("title", "Meaningful Title");
        test2.put("year", "2010");
        test2.put("howpublished", "Message in a bottle");
        expected.put(test2, "Message in a bottle,");

        BibItem test3 = new BibItem("unpublished", "test3");
        test3.put("author", "An Author");
        test3.put("title", "Meaningful Title");
        test3.put("year", "2010");
        test3.put("note", "Note to self");
        expected.put(test3, "Note to self,");

        BibItem test4 = new BibItem("unpublished", "test4");
        test4.put("author", "An Author");
        test4.put("title", "Meaningful Title");
        test4.put("year", "2010");
        test4.put("note", "Note to self");
        test4.put("howpublished", "Message in a bottle");
        expected.put(test4, "Message in a bottle, Note to self,");

        BibItem test5 = new BibItem("unpublished", "test5");
        test5.put("series", "Notes");
        test5.put("edition", "Third");
        test5.put("howpublished", "Message in a bottle");
        test5.put("pages", "1--13");
        test5.put("booktitle", "Proceedings");
        test5.put("number", "42");
        test5.put("type", "Typo");
        test5.put("publisher", "Bottomless Pit");
        test5.put("journal", "Journal");
        test5.put("author", "An Author");
        test5.put("title", "Meaningful Title");
        test5.put("organization", "Test Group");
        test5.put("chapter", "5");
        test5.put("editor", "Ed Editor");
        test5.put("school", "School");
        test5.put("address", "Nederweert");
        test5.put("volume", "1337");
        test5.put("month", "January");
        test5.put("year", "2010");
        test5.put("note", "Note to self");
        test5.put("institution", "University");
        expected.put(test5, "Message in a bottle, Note to self,");

        StringWriter output = new StringWriter();
        BufferedWriter buffer = new BufferedWriter(output);
        HTMLBibItemWriter testInstance = new HTMLBibItemWriter(buffer, null);

        for (BibItem input : expected.keySet()) {
            String expectedResult = expected.get(input);

            try {
                testInstance.writeUnpublished(input);
                buffer.flush();
                String result = removeTags(output.getBuffer().toString()).trim();

                assertEquals(expectedResult, result);
            } catch (IOException ex) {
                fail("writeUnpublished threw IOException on input:\n" + input + "\nException:\n" + ex);
            }

            // Clear the output
            output.getBuffer().delete(0, output.getBuffer().length());
        }
    }

    private String removeTags(String html) {
        return html.replaceAll("<[^>]*>", "");
    }
}