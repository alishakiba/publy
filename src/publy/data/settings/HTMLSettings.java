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

import publy.data.PublicationStatus;

/**
 * All settings specific to the HTML version of the publication list.
 */
public class HTMLSettings {

    /**
     * Where the paper title should link to.
     */
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

    /**
     * Where the between-section navigation should be placed.
     */
    public enum NavigationPlacement {

        NO_NAVIGATION, TOP, TOP_AND_BOTTOM, AFTER_SECTION_TITLE, BEFORE_SECTION_TITLE, BEFORE_SECTION_AND_BOTTOM;

        @Override
        public String toString() {
            switch (this) {
                case NO_NAVIGATION:
                    return "No navigation";
                case TOP:
                    return "At the top of the page";
                case TOP_AND_BOTTOM:
                    return "At the top and bottom";
                case AFTER_SECTION_TITLE:
                    return "After each section title";
                case BEFORE_SECTION_TITLE:
                    return "Before each section title";
                case BEFORE_SECTION_AND_BOTTOM:
                    return "Before each section and at the bottom";
                default:
                    throw new AssertionError("Unexpected NavigationPlacement: " + this);
            }
        }
    }
    // Alternate versions
    private boolean generateTextVersion = false;
    private boolean generateBibtexVersion = false;
    private boolean linkToAlternateVersions = true;
    // Navigation
    private NavigationPlacement navPlacement = NavigationPlacement.TOP;
    // Additional publication info
    private PublicationStatus includeAbstract = PublicationStatus.ALL;
    private PublicationStatus includeBibtex = PublicationStatus.ARXIV; // ALL should not be an option here.
    private PublicationStatus includePaper = PublicationStatus.ARXIV;
    // Title link
    private TitleLinkTarget titleTarget = TitleLinkTarget.NONE;
    // Presented
    private String presentedText = null;
    // Google analytics
    private String googleAnalyticsUser = null;

    /**
     * Gets whether a separate BibTeX version of the publication list should be
     * generated.
     *
     * @return whether to generate a BibTeX version
     */
    public boolean isGenerateBibtexVersion() {
        return generateBibtexVersion;
    }

    /**
     * Sets whether a separate BibTeX version of the publication list should be
     * generated.
     *
     * @param generateBibtexVersion whether to generate a BibTeX version
     */
    public void setGenerateBibtexVersion(boolean generateBibtexVersion) {
        this.generateBibtexVersion = generateBibtexVersion;
    }

    /**
     * Gets whether a separate plain-text version of the publication list should
     * be generated.
     *
     * @return whether to generate a plain-text version
     */
    public boolean isGenerateTextVersion() {
        return generateTextVersion;
    }

    /**
     * Sets whether a separate plain-text version of the publication list should
     * be generated.
     *
     * @param generateTextVersion whether to generate a plain-text version
     */
    public void setGenerateTextVersion(boolean generateTextVersion) {
        this.generateTextVersion = generateTextVersion;
    }

    /**
     * Gets whether links to alternate versions should be added to the HTML
     * version.
     *
     * @return whether to link to alternate versions
     */
    public boolean isLinkToAlternateVersions() {
        return linkToAlternateVersions;
    }

    /**
     * Sets whether links to alternate versions should be added to the HTML
     * version.
     *
     * @param linkToAlternateVersions whether to link to alternate versions
     */
    public void setLinkToAlternateVersions(boolean linkToAlternateVersions) {
        this.linkToAlternateVersions = linkToAlternateVersions;
    }

    /**
     * Gets where on the page to place the section navigation.
     *
     * @return where to place the section navigation
     */
    public NavigationPlacement getNavPlacement() {
        return navPlacement;
    }

    /**
     * Sets where on the page to place the section navigation.
     *
     * @param navPlacement where to place the section navigation
     */
    public void setNavPlacement(NavigationPlacement navPlacement) {
        this.navPlacement = navPlacement;
    }

    public PublicationStatus getIncludeAbstract() {
        return includeAbstract;
    }

    public PublicationStatus getIncludeBibtex() {
        return includeBibtex;
    }

    public PublicationStatus getIncludePaper() {
        return includePaper;
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

    public void setIncludeAbstract(PublicationStatus includeAbstract) {
        this.includeAbstract = includeAbstract;
    }

    public void setIncludeBibtex(PublicationStatus includeBibtex) {
        this.includeBibtex = includeBibtex;
    }

    public void setIncludePaper(PublicationStatus includePaper) {
        this.includePaper = includePaper;
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
