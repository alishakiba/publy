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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Settings that control the formatting of the publication list.
 */
public class GeneralSettings {

    /**
     * Possible options for the numbering of publications.
     */
    public enum Numbering {

        NO_NUMBERS, WITHIN_CATEGORIES, GLOBAL;
    }

    /**
     * Options for the display of the first name.
     */
    public enum FirstNameDisplay {

        FULL, INITIAL, NONE;
    }
    // Identification
    private final List<String> myNames = new ArrayList<>(Arrays.asList("me"));
    // Author info
    private FirstNameDisplay nameDisplay = FirstNameDisplay.FULL;
    private boolean reverseNames = false;
    private boolean listAllAuthors = true;
    // Publication Structure
    private boolean titleFirst = true;
    private boolean useNewLines = true;
    // Numbering
    private Numbering numbering = Numbering.NO_NUMBERS;
    private boolean reverseNumbering = false;

    public List<String> getMyNames() {
        return myNames;
    }

    public void setMyNames(List<String> myNames) {
        this.myNames.clear();
        this.myNames.addAll(myNames);
    }

    public boolean isListAllAuthors() {
        return listAllAuthors;
    }

    public void setListAllAuthors(boolean listAllAuthors) {
        this.listAllAuthors = listAllAuthors;
    }

    public FirstNameDisplay getNameDisplay() {
        return nameDisplay;
    }

    public void setNameDisplay(FirstNameDisplay nameDisplay) {
        this.nameDisplay = nameDisplay;
    }

    public boolean isReverseNames() {
        return reverseNames;
    }

    public void setReverseNames(boolean reverseNames) {
        this.reverseNames = reverseNames;
    }

    public boolean isTitleFirst() {
        return titleFirst;
    }

    public void setTitleFirst(boolean titleFirst) {
        this.titleFirst = titleFirst;
    }

    public boolean isUseNewLines() {
        return useNewLines;
    }

    public void setUseNewLines(boolean useNewLines) {
        this.useNewLines = useNewLines;
    }

    public Numbering getNumbering() {
        return numbering;
    }

    public void setNumbering(Numbering numbering) {
        this.numbering = numbering;
    }

    public boolean isReverseNumbering() {
        return reverseNumbering;
    }

    public void setReverseNumbering(boolean reverseNumbering) {
        this.reverseNumbering = reverseNumbering;
    }
}
