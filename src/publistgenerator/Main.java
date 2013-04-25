/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator;

import publistgenerator.io.html.SitemapWriter;
import java.io.File;
import java.util.List;
import publistgenerator.bibitem.BibItem;
import publistgenerator.io.BibTeXParser;
import publistgenerator.io.html.HTMLPublicationListWriter;
import publistgenerator.io.plain.PlainPublicationListWriter;
import publistgenerator.io.tex.TeXPublicationListWriter;

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

    private static File webDir = new File("../../../My Dropbox/Website/");
    
    private static void generatePublications() {
        BibTeXParser parser = new BibTeXParser();
        List<BibItem> items = parser.parseFile(new File(webDir, "publications/publications.bib"));

        HTMLPublicationListWriter writer = new HTMLPublicationListWriter(new File(webDir, "publications/PublicationsHeader.html"), new File(webDir, "publications/PublicationsFooter.html"));
        writer.writePublicationList(items, parser.getCategoryNotes(), new File(webDir, "publications.html"));

        PlainPublicationListWriter plainWriter = new PlainPublicationListWriter();
        plainWriter.writePublicationList(items, parser.getCategoryNotes(), new File(webDir, "publications.txt"));

        // Produce a sitemap, if one is specified
        File baseSites = new File(webDir, "sitemap.txt");
        if (baseSites.exists()) {
            SitemapWriter.writeSiteMap(items, baseSites, new File(webDir, "sitemap.xml"), webDir);
        }

        // Write my CV in TeX format, if the specification exists
        File cvDir = new File(webDir, "cv/");
        if (cvDir.exists() && cvDir.isDirectory()) {
            TeXPublicationListWriter texWriter = new TeXPublicationListWriter();
            texWriter.writePublicationList(items, parser.getCategoryNotes(), new File(cvDir, "publications.tex"));
        }
    }
}
