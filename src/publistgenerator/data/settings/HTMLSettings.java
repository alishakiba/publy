/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.data.settings;

import java.nio.file.Path;
import publistgenerator.io.ResourceLocator;
import publistgenerator.io.html.HTMLPublicationListWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLSettings extends FormatSettings {

    public enum PublicationType {

        NONE, PUBLISHED, ACCEPTED, ARXIV, ALL;

        @Override
        public String toString() {
            switch (this) {
                case NONE:
                    return "No papers";
                case PUBLISHED:
                    return "Published papers";
                case ACCEPTED:
                    return "Accepted papers";
                case ARXIV:
                    return "Accepted or arXiv papers";
                case ALL:
                    return "All papers";
                default:
                    throw new AssertionError("Unrecognized PublicationType: " + this);
            }
        }
    }
    
    private boolean linkToTextVersion = false;
    private PublicationType includeAbstract = PublicationType.ALL;
    private PublicationType includeBibtex = PublicationType.ARXIV; // ALL should not be an option here.
    private PublicationType includePaper = PublicationType.ARXIV;
    private Path header = ResourceLocator.getFullPath(HTMLPublicationListWriter.DEFAULT_HEADER_LOCATION);
    private Path footer = ResourceLocator.getFullPath(HTMLPublicationListWriter.DEFAULT_FOOTER_LOCATION);
    private String googleAnalyticsUser = null;

    public HTMLSettings(Settings settings) {
        super(settings);
    }

    public boolean linkToTextVersion() {
        return linkToTextVersion;
    }

    public PublicationType getIncludeAbstract() {
        return includeAbstract;
    }

    public PublicationType getIncludeBibtex() {
        return includeBibtex;
    }

    public PublicationType getIncludePaper() {
        return includePaper;
    }

    public Path getHeader() {
        return header;
    }

    public Path getFooter() {
        return footer;
    }

    public String getGoogleAnalyticsUser() {
        return googleAnalyticsUser;
    }

    public void setLinkToTextVersion(boolean linkToTextVersion) {
        this.linkToTextVersion = linkToTextVersion;
    }

    public void setIncludeAbstract(PublicationType includeAbstract) {
        this.includeAbstract = includeAbstract;
    }

    public void setIncludeBibtex(PublicationType includeBibtex) {
        this.includeBibtex = includeBibtex;
    }

    public void setIncludePaper(PublicationType includePaper) {
        this.includePaper = includePaper;
    }

    public void setHeader(Path header) {
        this.header = header;
    }

    public void setFooter(Path footer) {
        this.footer = footer;
    }

    public void setGoogleAnalyticsUser(String googleAnalyticsUser) {
        this.googleAnalyticsUser = googleAnalyticsUser;
    }
}
