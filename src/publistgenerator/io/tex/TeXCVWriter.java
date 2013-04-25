/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.tex;

import publistgenerator.io.tex.TeXPublicationListWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import publistgenerator.bibitem.BibItem;
import publistgenerator.bibitem.Venue;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class TeXCVWriter extends DefaultHandler {

    private BufferedWriter out;
    private TeXPublicationListWriter pubWriter;
    private List<BibItem> items;
    private Map<String, String> categoryNotes;
    private List<Venue> refereeList;
    // Objects to hold information during the parsing process
    private StringBuilder textBuffer; // Contains the characters that are read between start and end elements (e.g. <item>Text</item>)

    public TeXCVWriter(File outputFile, List<BibItem> items, Map<String, String> categoryNotes, List<Venue> refereeList) {
        try {
            out = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException ex) {
            Logger.getLogger(TeXCVWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.items = items;
        this.categoryNotes = categoryNotes;
        this.refereeList = refereeList;
        pubWriter = new TeXPublicationListWriter();
    }

    public void close() throws IOException {
        out.close();
    }

    public static void writeCV(File cvContent, File cvOutput, List<BibItem> items, Map<String, String> categoryNotes, List<Venue> refereeList) {
        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // Create a new instance of this class as handler
        TeXCVWriter handler = new TeXCVWriter(cvOutput, items, categoryNotes, refereeList);

        try {
            // Parse the input
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(cvContent, handler);
            handler.close();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(TeXCVWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        try {
            switch (qName) {
                case "cv":
                    writeHeader();
                    break;
                case "section":
                    boolean newpage = Boolean.parseBoolean(attrs.getValue("newpage"));
                    
                    if (newpage) {
                        out.write("\\newpage");
                        out.newLine();
                    }
                    
                    String name = attrs.getValue("name");

                    out.write("\\cvsection{" + name + "}");
                    out.newLine();
                    out.newLine();

                    break;
                case "entry":
                    String start = attrs.getValue("start");
                    String end = attrs.getValue("end");
                    name = attrs.getValue("name");
                    String place = attrs.getValue("place");

                    out.write("\\noindent\\begin{tabular}{@{}p{80pt}l}");
                    out.newLine();
                    out.write("{\\large \\textbf{" + start + " - " + end + "}} & {\\large \\textbf{" + name + "}}\\\\");
                    out.newLine();
                    out.write("& {\\large \\textbf{" + place + "}}");
                    out.newLine();
                    out.write("\\end{tabular}");
                    out.newLine();
                    out.write("\\\\[0.2\\baselineskip]");
                    out.newLine();
                    out.write("\\noindent ");

                    break;
                case "awardentry":
                    String year = attrs.getValue("year");
                    name = attrs.getValue("name");
                    place = attrs.getValue("place");
                    String amount = attrs.getValue("amount");

                    out.write("\\noindent\\begin{tabular*}{\\textwidth}{@{}p{40pt}l@{\\extracolsep{\\fill}}r}");
                    out.newLine();
                    out.write(year + " & \\textbf{" + name + "} & " + amount + "\\\\");
                    out.newLine();
                    out.write("& " + place + " &");
                    out.newLine();
                    out.write("\\end{tabular*}");
                    out.newLine();
                    out.write("\\\\[0.2\\baselineskip]");
                    out.newLine();

                    break;
                case "table":
                    out.write("\\noindent\\begin{tabular}{@{}ll}");
                    out.newLine();
                    break;
                case "item":
                    name = attrs.getValue("name");
                    out.write(name + " & ");
                    break;
                case "papers":
                    pubWriter.writePublicationList(items, categoryNotes, out);
                    break;
                case "referee":
                    pubWriter.writeRefereeList(refereeList, out);
            }
        } catch (IOException ex) {
            Logger.getLogger(TeXCVWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void endElement(String namespaceURI,
            String sName, // simple name
            String qName // qualified name
            ) throws SAXException {
        try {
            switch (qName) {
                case "cv":
                    out.write("\\end{document}");
                    out.newLine();
                    break;
                case "table":
                    out.write("\\end{tabular}");
                    out.newLine();
                    out.write("\\\\[0.6\\baselineskip]");
                    out.newLine();
                    out.newLine();
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(TeXCVWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Handle the characters that were recorded between the tags
        if (textBuffer != null) {
            String text = textBuffer.toString().trim().replaceAll("\\*AMP\\*", "&");

            try {
                switch (qName) {
                    case "name":
                        out.write("\\noindent{\\Huge \\textbf{" + text + "}}\\\\[0.6\\baselineskip]");
                        out.newLine();
                        out.write("{\\huge Curriculum Vitae}\\\\[0.6\\baselineskip]");
                        out.newLine();
                        out.newLine();
                        break;
                    case "entry":
                        out.write(text);
                        out.newLine();
                        out.write("\\\\[0.4\\baselineskip]");
                        out.newLine();
                        out.newLine();
                        break;
                    case "item":
                        String[] lines = text.split("\\\\");

                        for (int i = 0; i < lines.length; i++) {
                            String line = lines[i].trim();

                            if (!line.isEmpty()) {
                                if (i > 0) {
                                    out.write("& ");
                                }

                                out.write(line + "\\\\");
                                out.newLine();
                            }
                        }

                        break;
                }
            } catch (IOException ex) {
                Logger.getLogger(TeXCVWriter.class.getName()).log(Level.SEVERE, null, ex);
            }

            textBuffer = null;
        }
    }

    @Override
    public void characters(char buf[], int offset, int len) throws SAXException {
        String s = new String(buf, offset, len);

        if (textBuffer == null) {
            textBuffer = new StringBuilder(s);
        } else {
            textBuffer.append(s);
        }
    }

    private void writeHeader() throws IOException {
        out.write("\\documentclass[11pt]{article}");
        out.newLine();
        out.newLine();
        out.write("\\usepackage[T1]{fontenc}");
        out.newLine();
        out.write("\\usepackage[scaled]{helvet}");
        out.newLine();
        out.write("\\usepackage{fullpage}");
        out.newLine();
        out.write("\\renewcommand*\\familydefault{\\sfdefault}");
        out.newLine();
        out.newLine();
        out.write("% commands");
        out.newLine();
        out.write("\\renewcommand{\\hrule}[0]{\\rule{\\linewidth}{0.5pt}}");
        out.newLine();
        out.write("\\newcommand{\\cvsection}[1]{\\hfill {\\Large \\textbf{#1}}\\\\[-0.8\\baselineskip]\\hrule\\\\}");
        out.newLine();
        out.newLine();
        out.write("\\begin{document}");
        out.newLine();
    }
}
