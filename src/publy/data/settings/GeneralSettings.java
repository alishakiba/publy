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

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Sander
 */
public class GeneralSettings {

    public enum Numbering {

        NONE, LOCAL, GLOBAL;
    }

    public enum NameDisplay {

        FULL, ABBREVIATED, NONE;
    }
    // Identification
    private List<String> myNames = Arrays.asList("me");
    // Author info
    private NameDisplay nameDisplay = NameDisplay.ABBREVIATED;
    private boolean reverseNames = false;
    private boolean listAllAuthors = true;
    // Publication Structure
    private boolean titleFirst = true;
    // Numbering
    private Numbering numbering = Numbering.NONE;
    private boolean reverseNumbering = false;

    public List<String> getMyNames() {
        return myNames;
    }

    public void setMyNames(List<String> myNames) {
        this.myNames = myNames;
    }

    public boolean listAllAuthors() {
        return listAllAuthors;
    }

    public void setListAllAuthors(boolean listAllAuthors) {
        this.listAllAuthors = listAllAuthors;
    }

    public NameDisplay getNameDisplay() {
        return nameDisplay;
    }

    public void setNameDisplay(NameDisplay nameDisplay) {
        this.nameDisplay = nameDisplay;
    }

    public boolean reverseNames() {
        return reverseNames;
    }

    public void setReverseNames(boolean reverseNames) {
        this.reverseNames = reverseNames;
    }

    public boolean titleFirst() {
        return titleFirst;
    }

    public void setTitleFirst(boolean titleFirst) {
        this.titleFirst = titleFirst;
    }

    public Numbering getNumbering() {
        return numbering;
    }

    public void setNumbering(Numbering numbering) {
        this.numbering = numbering;
    }

    public boolean reverseNumbering() {
        return reverseNumbering;
    }

    public void setReverseNumbering(boolean reverseNumbering) {
        this.reverseNumbering = reverseNumbering;
    }
}
