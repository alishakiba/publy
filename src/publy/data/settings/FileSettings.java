/*
 * Copyright 2013-2014 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.data.settings;

import java.nio.file.Path;
import publy.io.ResourceLocator;
import publy.io.html.HTMLPublicationListWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class FileSettings {

    // Input file
    private Path publications;
    // Output file
    private Path target;
    // Header and Footer
    private Path header = ResourceLocator.getFullPath(HTMLPublicationListWriter.DEFAULT_HEADER_LOCATION);
    private Path footer = ResourceLocator.getFullPath(HTMLPublicationListWriter.DEFAULT_FOOTER_LOCATION);

    public Path getPublications() {
        return publications;
    }

    public void setPublications(Path publications) {
        this.publications = publications;
    }

    public Path getTarget() {
        return target;
    }

    public void setTarget(Path target) {
        this.target = target;
    }

    /**
     * Returns the path to the location where the bibtex version of the
     * publication list will be written to, if any. Returns
     * <code>null</code> if no such version will be written. The target is
     * guaranteed to be in the same directory as the HTML target.
     *
     * @return
     */
    public Path getBibtexTarget() {
        String baseName = target.getFileName().toString();
        int extension = baseName.lastIndexOf('.');
        if (extension > -1) {
            baseName = baseName.substring(0, extension);
        }
        return target.resolveSibling(baseName + ".bib");
    }

    /**
     * Returns the path to the location where the plain text version of the
     * publication list will be written to, if any. Returns
     * <code>null</code> if no such version will be written. The target is
     * guaranteed to be in the same directory as the HTML target.
     *
     * @return
     */
    public Path getPlainTextTarget() {
        String baseName = target.getFileName().toString();
        int extension = baseName.lastIndexOf('.');
        if (extension > -1) {
            baseName = baseName.substring(0, extension);
        }
        return target.resolveSibling(baseName + ".utf8.txt");
    }

    public Path getHeader() {
        return header;
    }

    public void setHeader(Path header) {
        this.header = header;
    }

    public Path getFooter() {
        return footer;
    }

    public void setFooter(Path footer) {
        this.footer = footer;
    }
}
