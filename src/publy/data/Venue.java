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
package publy.data;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class Venue {

    /**
     * Whether this is a conference (true) or a journal (false).
     */
    private boolean conference;
    private String abbreviation;
    private String fullName;
    private String shortName;

    public Venue(boolean conference, String abbreviation, String fullName, String shortName) {
        this.conference = conference;
        this.abbreviation = abbreviation;
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public boolean isConference() {
        return conference;
    }
    
    public boolean isJournal() {
        return !conference;
    }

    public void setConference(boolean conference) {
        this.conference = conference;
    }

    /**
     * Get the value of shortName
     *
     * @return the value of shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Set the value of shortName
     *
     * @param shortName new value of shortName
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Get the value of fullName
     *
     * @return the value of fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Set the value of fullName
     *
     * @param fullName new value of fullName
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Get the value of abbreviation
     *
     * @return the value of abbreviation
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Set the value of abbreviation
     *
     * @param abbreviation new value of abbreviation
     */
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {
        return abbreviation;
    }

}
