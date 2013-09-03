/*
 * Copyright 2013 Sander Verdonschot <sander.verdonschot at gmail.com>.
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

import publy.data.PublicationType;

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
    private PublicationType includeAbstract = PublicationType.ALL;
    private PublicationType includeBibtex = PublicationType.ARXIV; // ALL should not be an option here.
    private PublicationType includePaper = PublicationType.ARXIV;
    // Title link
    private TitleLinkTarget titleTarget = TitleLinkTarget.NONE;
    // Presented
    private String presentedText = null;
    // Google analytics
    private String googleAnalyticsUser = null;

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

    public NavigationPlacement getNavPlacement() {
        return navPlacement;
    }

    public void setNavPlacement(NavigationPlacement navPlacement) {
        this.navPlacement = navPlacement;
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
