/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publy.data.settings;

import java.nio.file.Path;
import publy.data.PublicationType;
import publy.io.ResourceLocator;
import publy.io.html.HTMLPublicationListWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLSettings {

    public enum TitleLinkTarget {

        NONE, ABSTRACT, PAPER;

        @Override
        public String toString() {
            switch (this) {
                case NONE:
                    return "Nothing";
                case ABSTRACT:
                    return "The abstract";
                case PAPER:
                    return "The paper";
                default:
                    throw new AssertionError("Unexpected TitleLinkTarget: " + this);
            }
        }
    }
    private boolean generateTextVersion = false;
    private boolean generateBibtexVersion = false;
    private boolean linkToAlternateVersions = true;
    private PublicationType includeAbstract = PublicationType.ALL;
    private PublicationType includeBibtex = PublicationType.ARXIV; // ALL should not be an option here.
    private PublicationType includePaper = PublicationType.ARXIV;
    private TitleLinkTarget titleTarget = TitleLinkTarget.NONE;
    private Path header = ResourceLocator.getFullPath(HTMLPublicationListWriter.DEFAULT_HEADER_LOCATION);
    private Path footer = ResourceLocator.getFullPath(HTMLPublicationListWriter.DEFAULT_FOOTER_LOCATION);
    private String googleAnalyticsUser = null;
    private String presentedText = null;

    public boolean generateBibtexVersion() {
        return generateBibtexVersion;
    }

    public void setGenerateBibtexVersion(boolean generateBibtexVersion) {
        this.generateBibtexVersion = generateBibtexVersion;
    }

    public boolean generateTextVersion() {
        return generateTextVersion;
    }
    
    public void setGenerateTextVersion(boolean generateTextVersion) {
        this.generateTextVersion = generateTextVersion;
    }

    public boolean linkToAlternateVersions() {
        return linkToAlternateVersions;
    }

    public void setLinkToAlternateVersions(boolean linkToAlternateVersions) {
        this.linkToAlternateVersions = linkToAlternateVersions;
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

    public TitleLinkTarget getTitleTarget() {
        return titleTarget;
    }

    public void setTitleTarget(TitleLinkTarget titleTarget) {
        this.titleTarget = titleTarget;
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

    public String getPresentedText() {
        return presentedText;
    }

    public void setPresentedText(String presentedText) {
        this.presentedText = presentedText;
    }
}
