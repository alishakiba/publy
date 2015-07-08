/*
 * Copyright 2013-2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
 * The location of all relevant user-configurable files.
 */
public class FileSettings {

    // Input file
    private Path publications;
    // Output file
    private Path target;
    // Header and Footer
    private Path header = ResourceLocator.getFullPath(HTMLPublicationListWriter.DEFAULT_HEADER_LOCATION);
    private Path footer = ResourceLocator.getFullPath(HTMLPublicationListWriter.DEFAULT_FOOTER_LOCATION);

    /**
     * Gets the location of the user's publication list.
     *
     * @return the path to the publication list
     */
    public Path getPublications() {
        return publications;
    }

    /**
     * Sets the location of the user's publication list.
     *
     * @param publications the path to the new publication list
     */
    public void setPublications(Path publications) {
        this.publications = publications;
    }

    /**
     * Gets the location of the output (HTML) file.
     *
     * @return the path to the output file
     */
    public Path getTarget() {
        return target;
    }

    /**
     * Sets the location of the output (HTML) file.
     *
     * @param target the path to the new output file
     */
    public void setTarget(Path target) {
        this.target = target;
    }

    /**
     * Gets the location where the BibTeX version of the publication list is to
     * be written, if any.
     * <p>
     * This path is generated from the HTML target, and guaranteed to be in the
     * same directory.
     *
     * @return the path to the BibTeX output file
     */
    public Path getBibtexTarget() {
        String baseName = target.getFileName().toString();
        int extension = baseName.lastIndexOf('.');

        if (extension > -1) {
            baseName = baseName.substring(0, extension);
        }

        return target.resolveSibling(baseName + "-generated.bib");
    }

    /**
     * Gets the location where the plain text version of the publication list is
     * to be written, if any.
     * <p>
     * This path is generated from the HTML target, and guaranteed to be in the
     * same directory.
     *
     * @return the path to the plain text output file
     */
    public Path getPlainTextTarget() {
        String baseName = target.getFileName().toString();
        int extension = baseName.lastIndexOf('.');

        if (extension > -1) {
            baseName = baseName.substring(0, extension);
        }

        return target.resolveSibling(baseName + ".utf8.txt");
    }

    /**
     * Gets the location of the HTML header file.
     * <p>
     * The contents of this file will be written to the HTML output file before
     * the publication list.
     *
     * @return the path to the HTML header file
     */
    public Path getHeader() {
        return header;
    }

    /**
     * Sets the location of the HTML header file.
     * <p>
     * The contents of this file will be written to the HTML output file before
     * the publication list.
     *
     * @param header the path to the new HTML header file
     */
    public void setHeader(Path header) {
        this.header = header;
    }

    /**
     * Gets the location of the HTML footer file.
     * <p>
     * The contents of this file will be written to the HTML output file after
     * the publication list.
     *
     * @return the path to the HTML footer file
     */
    public Path getFooter() {
        return footer;
    }

    /**
     * Sets the location of the HTML footer file.
     * <p>
     * The contents of this file will be written to the HTML output file after
     * the publication list.
     *
     * @param footer the path to the new HTML footer file
     */
    public void setFooter(Path footer) {
        this.footer = footer;
    }
}
