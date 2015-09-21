/*
 * Copyright 2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.io.settings.legacy;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import publy.data.PublicationStatus;
import publy.data.category.OutputCategory;
import publy.data.category.conditions.FieldEqualsCondition;
import publy.data.settings.ConsoleSettings;
import publy.data.settings.GeneralSettings;
import publy.data.settings.HTMLSettings;
import publy.data.settings.Settings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class SettingsReaderV0_8Test {

    private static final Path TEST_DIR = Paths.get("test/publy/io/settings/legacy/0_8");
    private static final String BASE_NAME = "PublySettingsV0_8-%d.xml";

    public SettingsReaderV0_8Test() {
    }

    @Test
    public void testReadFullSettings() throws IOException {
        System.out.println("SettingsReaderV0_8Test");
        Settings settings = (new SettingsReaderV0_8()).parseSettings(TEST_DIR.resolve(String.format(BASE_NAME, 1)));

        // File settings
        assertTrue(settings.getFileSettings().getPublications().endsWith("Data/Publications/publications.bib"));
        assertTrue(settings.getFileSettings().getTarget().endsWith("WWW/publications.html"));
        assertTrue(settings.getFileSettings().getHeader().endsWith("Data/Publications/PublicationsHeader.html"));
        assertTrue(settings.getFileSettings().getFooter().endsWith("Data/Publications/PublicationsFooter.html"));

        // Category Settings
        List<OutputCategory> categories = settings.getCategorySettings().getAllCategories();

        // Books
        OutputCategory books = categories.get(0);
        assertEquals("Books", books.getShortName());
        assertEquals("Books", books.getName());
        assertEquals("", books.getHtmlNote());
        assertFalse(books.getTypeCondition().isInverted());
        assertEquals(Arrays.asList("book"), books.getTypeCondition().getTypes());
        assertEquals(1, books.getFieldConditions().size());
        assertTrue(books.getFieldConditions().get(0).isInverted());
        assertEquals("pubstate", books.getFieldConditions().get(0).getField());
        assertTrue(books.getFieldConditions().get(0) instanceof FieldEqualsCondition);
        assertEquals(Arrays.asList("submitted"), ((FieldEqualsCondition) books.getFieldConditions().get(0)).getValues());
        assertEquals(Arrays.asList("address"), books.getIgnoredFields());

        // Chapters
        OutputCategory chapters = categories.get(1);
        assertEquals("Chapters", chapters.getShortName());
        assertEquals("Chapters in Books", chapters.getName());
        assertEquals("", chapters.getHtmlNote());
        assertFalse(chapters.getTypeCondition().isInverted());
        assertEquals(Arrays.asList("incollection"), chapters.getTypeCondition().getTypes());
        assertEquals(1, chapters.getFieldConditions().size());
        assertTrue(chapters.getFieldConditions().get(0).isInverted());
        assertEquals("pubstate", chapters.getFieldConditions().get(0).getField());
        assertTrue(chapters.getFieldConditions().get(0) instanceof FieldEqualsCondition);
        assertEquals(Arrays.asList("submitted"), ((FieldEqualsCondition) chapters.getFieldConditions().get(0)).getValues());
        assertEquals(Arrays.asList("address"), chapters.getIgnoredFields());

        // Conference
        OutputCategory conference = categories.get(2);
        assertEquals("Conference", conference.getShortName());
        assertEquals("Conference papers", conference.getName());
        assertEquals("Conference papers that I presented are marked with <img src=\"images/presentation.png\" alt=\"(presented)\" class=\"presented\">.<br><span class=\"attribution\">(Icon by <a href=\"http://www.doublejdesign.co.uk/\">Double-J Design</a>, used under a <a href=\"http://creativecommons.org/licenses/by/3.0/\">Creative Commons Attribution license</a>)</span>", conference.getHtmlNote());
        assertFalse(conference.getTypeCondition().isInverted());
        assertEquals(Arrays.asList("inproceedings", "conference"), conference.getTypeCondition().getTypes());
        assertEquals(1, conference.getFieldConditions().size());
        assertTrue(conference.getFieldConditions().get(0).isInverted());
        assertEquals("pubstate", conference.getFieldConditions().get(0).getField());
        assertTrue(conference.getFieldConditions().get(0) instanceof FieldEqualsCondition);
        assertEquals(Arrays.asList("submitted"), ((FieldEqualsCondition) conference.getFieldConditions().get(0)).getValues());
        assertEquals(Arrays.asList("address", "publisher", "editor", "volume", "number", "series"), conference.getIgnoredFields());

        // Journal
        OutputCategory journal = categories.get(3);
        assertEquals("Journal", journal.getShortName());
        assertEquals("Journal papers", journal.getName());
        assertEquals("", journal.getHtmlNote());
        assertFalse(journal.getTypeCondition().isInverted());
        assertEquals(Arrays.asList("article"), journal.getTypeCondition().getTypes());
        assertEquals(1, journal.getFieldConditions().size());
        assertTrue(journal.getFieldConditions().get(0).isInverted());
        assertEquals("pubstate", journal.getFieldConditions().get(0).getField());
        assertTrue(journal.getFieldConditions().get(0) instanceof FieldEqualsCondition);
        assertEquals(Arrays.asList("submitted"), ((FieldEqualsCondition) journal.getFieldConditions().get(0)).getValues());
        assertEquals(Arrays.asList(""), journal.getIgnoredFields());

        // Other
        OutputCategory other = categories.get(4);
        assertEquals("Other", other.getShortName());
        assertEquals("Other", other.getName());
        assertEquals("", other.getHtmlNote());
        assertFalse(other.getTypeCondition().isInverted());
        assertEquals(Arrays.asList("*"), other.getTypeCondition().getTypes());
        assertEquals(1, other.getFieldConditions().size());
        assertTrue(other.getFieldConditions().get(0).isInverted());
        assertEquals("pubstate", other.getFieldConditions().get(0).getField());
        assertTrue(other.getFieldConditions().get(0) instanceof FieldEqualsCondition);
        assertEquals(Arrays.asList("submitted"), ((FieldEqualsCondition) other.getFieldConditions().get(0)).getValues());
        assertEquals(Arrays.asList(""), other.getIgnoredFields());

        // Submitted
        OutputCategory submitted = categories.get(5);
        assertEquals("Submitted", submitted.getShortName());
        assertEquals("Currently under review", submitted.getName());
        assertEquals("", submitted.getHtmlNote());
        assertFalse(submitted.getTypeCondition().isInverted());
        assertEquals(Arrays.asList("*"), submitted.getTypeCondition().getTypes());
        assertEquals(1, submitted.getFieldConditions().size());
        assertFalse(submitted.getFieldConditions().get(0).isInverted());
        assertEquals("pubstate", submitted.getFieldConditions().get(0).getField());
        assertTrue(submitted.getFieldConditions().get(0) instanceof FieldEqualsCondition);
        assertEquals(Arrays.asList("submitted"), ((FieldEqualsCondition) submitted.getFieldConditions().get(0)).getValues());
        assertEquals(Arrays.asList(""), submitted.getIgnoredFields());

        // Talks
        OutputCategory talks = categories.get(6);
        assertEquals("Talks", talks.getShortName());
        assertEquals("Invited Talks", talks.getName());
        assertEquals("", talks.getHtmlNote());
        assertFalse(talks.getTypeCondition().isInverted());
        assertEquals(Arrays.asList("talk"), talks.getTypeCondition().getTypes());
        assertEquals(1, talks.getFieldConditions().size());
        assertTrue(talks.getFieldConditions().get(0).isInverted());
        assertEquals("pubstate", talks.getFieldConditions().get(0).getField());
        assertTrue(talks.getFieldConditions().get(0) instanceof FieldEqualsCondition);
        assertEquals(Arrays.asList("submitted"), ((FieldEqualsCondition) talks.getFieldConditions().get(0)).getValues());
        assertEquals(Arrays.asList(""), talks.getIgnoredFields());

        // Thesis
        OutputCategory thesis = categories.get(7);
        assertEquals("Thesis", thesis.getShortName());
        assertEquals("Thesis", thesis.getName());
        assertEquals("", thesis.getHtmlNote());
        assertFalse(thesis.getTypeCondition().isInverted());
        assertEquals(Arrays.asList("mastersthesis", "phdthesis"), thesis.getTypeCondition().getTypes());
        assertEquals(1, thesis.getFieldConditions().size());
        assertTrue(thesis.getFieldConditions().get(0).isInverted());
        assertEquals("pubstate", thesis.getFieldConditions().get(0).getField());
        assertTrue(thesis.getFieldConditions().get(0) instanceof FieldEqualsCondition);
        assertEquals(Arrays.asList("submitted"), ((FieldEqualsCondition) thesis.getFieldConditions().get(0)).getValues());
        assertEquals(Arrays.asList(""), thesis.getIgnoredFields());

        // Unpublished
        OutputCategory unpublished = categories.get(8);
        assertEquals("Unpublished", unpublished.getShortName());
        assertEquals("Unpublished manuscripts", unpublished.getName());
        assertEquals("", unpublished.getHtmlNote());
        assertFalse(unpublished.getTypeCondition().isInverted());
        assertEquals(Arrays.asList("unpublished"), unpublished.getTypeCondition().getTypes());
        assertEquals(0, unpublished.getFieldConditions().size());
        assertEquals(Arrays.asList(""), unpublished.getIgnoredFields());

        assertEquals(Arrays.asList(submitted, journal, conference, books, chapters, thesis, unpublished, talks, other), settings.getCategorySettings().getActiveCategories());

        // General Settings
        GeneralSettings gs = settings.getGeneralSettings();
        assertEquals(Arrays.asList("me"), gs.getMyNames());
        assertEquals(GeneralSettings.FirstNameDisplay.INITIAL, gs.getNameDisplay());
        assertFalse(gs.isReverseNames());
        assertFalse(gs.isListAllAuthors());
        assertTrue(gs.isTitleFirst());
        assertTrue(gs.isUseNewLines());
        assertEquals(GeneralSettings.Numbering.NO_NUMBERS, gs.getNumbering());
        assertFalse(gs.isReverseNumbering());

        // HTML settings
        HTMLSettings html = settings.getHtmlSettings();
        assertTrue(html.isGenerateTextVersion());
        assertTrue(html.isGenerateBibtexVersion());
        assertFalse(html.isLinkToAlternateVersions());
        assertEquals(HTMLSettings.NavigationPlacement.BEFORE_SECTION_AND_BOTTOM, html.getNavPlacement());
        assertEquals(PublicationStatus.ALL, html.getIncludeAbstract());
        assertEquals(PublicationStatus.ARXIV, html.getIncludeBibtex());
        assertEquals(PublicationStatus.ALL, html.getIncludePaper());
        assertEquals(HTMLSettings.TitleLinkTarget.NONE, html.getTitleTarget());
        assertEquals("<img src=\"images/presentation.png\" alt=\"(presented)\" class=\"presented\">", html.getPresentedText());
        assertEquals("UA-38946478-1", html.getGoogleAnalyticsUser());
        
        // Console settings
        ConsoleSettings cs = settings.getConsoleSettings();
        assertTrue(cs.isShowWarnings());
        assertTrue(cs.isWarnMissingReferences());
        assertTrue(cs.isWarnNotAuthor());
        assertTrue(cs.isWarnNoCategoryForItem());
        assertTrue(cs.isWarnMandatoryFieldIgnored());
        assertTrue(cs.isShowLogs());
        assertTrue(cs.isShowStackTraces());
    }

    @Test
    public void testReadAbbreviated() throws IOException {
        Settings settings = (new SettingsReaderV0_8()).parseSettings(TEST_DIR.resolve(String.format(BASE_NAME, 1)));
        assertEquals(GeneralSettings.FirstNameDisplay.INITIAL, settings.getGeneralSettings().getNameDisplay());
    }

    @Test
    public void testReadNoNumbers() throws IOException {
        Settings settings = (new SettingsReaderV0_8()).parseSettings(TEST_DIR.resolve(String.format(BASE_NAME, 1)));
        assertEquals(GeneralSettings.Numbering.NO_NUMBERS, settings.getGeneralSettings().getNumbering());
    }

    @Test
    public void testReadLocalNumbers() throws IOException {
        Settings settings = (new SettingsReaderV0_8()).parseSettings(TEST_DIR.resolve(String.format(BASE_NAME, 2)));
        assertEquals(GeneralSettings.Numbering.WITHIN_CATEGORIES, settings.getGeneralSettings().getNumbering());
    }
}
