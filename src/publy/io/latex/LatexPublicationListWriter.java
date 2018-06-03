/*
 * Copyright 2018 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.io.latex;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import publy.data.Section;
import publy.data.bibitem.BibItem;
import publy.data.settings.GeneralSettings;
import publy.data.settings.Settings;
import publy.io.PublicationListWriter;

public class LatexPublicationListWriter extends PublicationListWriter {

    private LatexBibItemWriter itemWriter;
    private int count;

    public LatexPublicationListWriter(Settings settings) {
        super(settings);
    }

    @Override
    protected void writePublicationList(List<Section> sections, BufferedWriter out) throws IOException {
        itemWriter = new LatexBibItemWriter(out, settings);
        count = getInitialCount(sections);

        writeCommands(out);
        
        // Write the body
        for (Section s : sections) {
            writeSection(s, out, 0);
        }
    }

    private void writeCommands(BufferedWriter out) throws IOException {
        out.write("%%% Publy commands %%%");
        out.newLine();
        out.write("% To change Publy's typesetting, just define");
        out.newLine();
        out.write("% any of Publy's commands from the list below");
        out.newLine();
        out.write("% before including this file.");
        out.newLine();
        out.newLine();
        // TODO: Insert commands
        // use \providecommand so that users can override simply by defining commands before including
        //
        // publySection
        // publySubsection
        // publyCount
        // publyTitle
        // publyAuthors
        // publyInfo
        //   per-field commands?
        // publyNote
    }
    
    private void writeSection(Section section, BufferedWriter out, int nestingLevel) throws IOException {
        // Reset the count if necessary
        if (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.WITHIN_CATEGORIES) {
            count = (settings.getGeneralSettings().isReverseNumbering() ? section.getItems().size() : 1);
        }

        // Write the title
        String command = (nestingLevel == 0 ? "publySection" : "publySubsection");
        out.write(String.format("\\%s{%s}", command, section.getName()));
        out.newLine();
        out.newLine();

        // Write the publications
        itemWriter.setIgnoredFields(new HashSet<>(section.getIgnoredFields()));

        for (BibItem item : section.getItems()) {
            // Write the appropriate number
            if (settings.getGeneralSettings().getNumbering() != GeneralSettings.Numbering.NO_NUMBERS) {
                out.write(String.format("\\%s{%s} ", "publyCount", count));
                count += (settings.getGeneralSettings().isReverseNumbering() ? -1 : 1);
            }

            itemWriter.write(item);
            out.newLine();
        }

        // Write the sub-sections
        for (Section subsection : section.getSubsections()) {
            writeSection(subsection, out, nestingLevel + 1);
        }

        out.newLine();
    }
}
