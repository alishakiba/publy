/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.data.settings;

import java.io.File;
import publistgenerator.gui.MainFrame;
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
                    throw new InternalError("Unrecognized PublicationType: " + this);
            }
        }
    }
    
    private boolean linkToTextVersion = false;
    private PublicationType includeAbstract = PublicationType.ALL;
    private PublicationType includeBibtex = PublicationType.ARXIV; // ALL should not be an option here.
    private PublicationType includePDF = PublicationType.ARXIV;
    private File header = MainFrame.getFile(HTMLPublicationListWriter.DEFAULT_HEADER_LOCATION);
    private File footer = MainFrame.getFile(HTMLPublicationListWriter.DEFAULT_FOOTER_LOCATION);
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

    public PublicationType getIncludePDF() {
        return includePDF;
    }

    public File getHeader() {
        return header;
    }

    public File getFooter() {
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

    public void setIncludePDF(PublicationType includePDF) {
        this.includePDF = includePDF;
    }

    public void setHeader(File header) {
        this.header = header;
    }

    public void setFooter(File footer) {
        this.footer = footer;
    }

    public void setGoogleAnalyticsUser(String googleAnalyticsUser) {
        this.googleAnalyticsUser = googleAnalyticsUser;
    }
}
