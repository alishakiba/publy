/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.tex;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import publistgenerator.bibitem.BibItem;
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
    protected void writePublicationList(List<BibItem> items, Map<String, String> categoryNotes, BufferedWriter out) throws IOException {
        itemWriter = new TeXBibItemWriter(out);
        
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
}
