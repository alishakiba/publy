/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator;

import publistgenerator.io.SitemapWriter;
import java.io.File;
import java.util.List;
import publistgenerator.bibitem.BibItem;
import publistgenerator.io.BibTeXParser;
import publistgenerator.io.HTMLPublicationListWriter;
import publistgenerator.io.PlainPublicationListWriter;
import publistgenerator.io.TeXCVWriter;

/**
 *
 * @author Sander
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        generatePublications();
    }

    private static void generatePublications() {
        BibTeXParser parser = new BibTeXParser();
        List<BibItem> items = parser.parseFile(new File("../../Website/publications/publications.bib"));

        HTMLPublicationListWriter writer = new HTMLPublicationListWriter(new File("../../Website/publications/PublicationsHeader.html"), new File("../../Website/publications/PublicationsFooter.html"));
        writer.writePublicationList(items, parser.getCategoryNotes(), new File("../../Website/publications.html"));

        PlainPublicationListWriter plainWriter = new PlainPublicationListWriter();
        plainWriter.writePublicationList(items, parser.getCategoryNotes(), new File("../../Website/publications.txt"));

        // Produce a sitemap, if one is specified
        File baseSites = new File("../../Website/sitemap.txt");
        if (baseSites.exists()) {
            SitemapWriter.writeSiteMap(items, baseSites, new File("../../Website/sitemap.xml"), new File("../../Website/"));
        }

        // Write my CV in TeX format, if the specification exists
        File cvContent = new File("../../Website/cv/cv.xml");
        if (cvContent.exists()) {
            TeXCVWriter.writeCV(cvContent, new File("../../Website/cv/cv.tex"), items, parser.getCategoryNotes(), parser.getRefereeList());
        }
    }
}
