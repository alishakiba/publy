package publistgenerator.bibitem;

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
