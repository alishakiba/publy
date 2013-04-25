/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.tex;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import publistgenerator.bibitem.BibItem;
import publistgenerator.bibitem.Venue;
import publistgenerator.category.OutputCategory;
import publistgenerator.category.SubmittedCategory;
import publistgenerator.category.TalksCategory;
import publistgenerator.io.PublicationListWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class TeXPublicationListWriter extends PublicationListWriter {

    private TeXBibItemWriter itemWriter;
    private int count = 1;

    public TeXPublicationListWriter() {
        super("latex");
    }

    @Override
    public void writePublicationList(List<BibItem> items, Map<String, String> categoryNotes, BufferedWriter out) throws IOException {
        itemWriter = new TeXBibItemWriter(out);
        
        categorizePapers(items);
        setNotes(categoryNotes);
        
        int submittedIndex = -1, talksIndex = -1;

        for (int i = 0; i < categories.size(); i++) {
            OutputCategory c = categories.get(i);

            if (c instanceof SubmittedCategory) {
                submittedIndex = i;
            } else if (c instanceof TalksCategory) {
                talksIndex = i;
            } else {
                writeCategory(c, out);
            }
        }

        if (submittedIndex >= 0) {
            writeCategory(categories.get(submittedIndex), out);
        }

        if (talksIndex >= 0) {
            writeCategory(categories.get(talksIndex), out);
        }
    }

    private void writeCategory(OutputCategory c, BufferedWriter out) throws IOException {
        out.write("\\noindent {\\large \\textbf{" + c.getName() + "}}\\\\");
        out.newLine();
        out.newLine();

        if (c.getNote() != null && !c.getNote().isEmpty()) {
            out.write(c.getNote() + "\\\\");
            out.newLine();
            out.newLine();
        }

        for (BibItem item : c.getItems()) {
            out.write("\\noindent\\begin{tabular}{p{10pt}p{0.90\\linewidth}}");
            out.newLine();
            out.write("\\textbf{" + count + ".} ");

            itemWriter.write(item);

            out.write("\\end{tabular}");
            out.newLine();
            out.write("\\\\[0.4\\baselineskip]");
            out.newLine();

            count++;
        }

        out.newLine();
    }

    public void writeRefereeList(List<Venue> refereeList, BufferedWriter out) throws IOException {
        // Sort the conferences and journals by their full name
        Collections.sort(refereeList, new Comparator<Venue>() {

            @Override
            public int compare(Venue v1, Venue v2) {
                return v1.getFullName().compareTo(v2.getFullName());
            }
        });
        
        // Write the header
        out.write("\\noindent {\\large \\textbf{Refereed for}}\\\\[0.4\\baselineskip]");
        
        // Write all journals
        for (Venue v : refereeList) {
            if (v.isJournal()) {
                out.newLine();
                out.write("\\indent " + v.getFullName() + " (" + v.getAbbreviation() + ")\\\\");
            }
        }
        
        // Short spacing between journals and conferences
        out.write("[0.4\\baselineskip]");
        out.newLine();
        
        // Write all conferences
        for (Venue v : refereeList) {
            if (v.isConference()) {
                out.write("\\indent " + v.getFullName() + " (" + v.getAbbreviation() + ")\\\\");
                out.newLine();
            }
        }
    }
}
