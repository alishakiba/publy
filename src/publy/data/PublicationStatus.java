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
package publy.data;

import java.util.HashSet;
import java.util.Set;
import publy.data.bibitem.BibItem;

/**
 * A classification of publications by progress in the publication pipeline.
 * <p>
 * The classification increases monotonically from most exclusive to most
 * inclusive:
 * <p>
 * <ul>
 * <li> NONE - No publications match this type.
 * <li> PUBLISHED - Papers that have been published, i.e. whose {@code pubstate}
 * field is not set.
 * <li> ACCEPTED - Papers that have been published or accepted for publications,
 * i.e. whose {@code pubstate} field is either not set, or one of
 * {@code accepted}, {@code acceptedrev}, {@code forthcoming}, {@code inpress},
 * {@code prepublished}.
 * <li> ARXIV - Papers that have been published, accepted for publication, or
 * placed on the arXiv, i.e. whose {@code arxiv} field is set.
 * <li> ALL - All publications match this type.
 * </ul>
 */
public enum PublicationStatus {

    NONE, PUBLISHED, ACCEPTED, ARXIV, ALL;

    /**
     * All {@code pubstate} values which are classified as ACCEPTED.
     */
    private static final Set<String> ACCEPTED_STATES;

    static {
        ACCEPTED_STATES = new HashSet<>();

        ACCEPTED_STATES.add("accepted");
        ACCEPTED_STATES.add("acceptedrev");
        ACCEPTED_STATES.add("forthcoming");
        ACCEPTED_STATES.add("inpress");
        ACCEPTED_STATES.add("prepublished");
    }

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
                return "Accepted or preprint papers";
            case ALL:
                return "All papers";
            default:
                throw new AssertionError("Unrecognized PublicationType: " + this);
        }
    }

    /**
     * Tests whether the given paper matches this type.
     *
     * @param item the paper to test
     * @return true if {@code item} matches this type, false otherwise
     */
    public boolean matches(BibItem item) {
        return matches(this, item);
    }

    /**
     * Tests whether the given paper matches the specified type.
     *
     * @param type the publication type to match
     * @param item the paper to test
     * @return true if {@code item} matches {@code type}, false otherwise
     */
    public static boolean matches(PublicationStatus type, BibItem item) {
        if (type == ALL) {
            return true;
        }

        if (type == NONE) {
            return false;
        }

        // The type is either PUBLISHED, ACCEPTED, or ARXIV
        String pubstate = item.get("pubstate");

        if (pubstate == null || pubstate.isEmpty()) {
            // The paper has been published, matching all remaining types
            return true;
        }

        // The paper has not been published
        if (type == PUBLISHED) {
            return false;
        }

        // The type is either ACCEPTED or ARXIV
        if (ACCEPTED_STATES.contains(pubstate)) {
            // The paper has been accepted, matching all remaining types
            return true;
        }

        // The paper has not been accepted
        if (type == ACCEPTED) {
            return false;
        }

        // The type is ARXIV
        String arxiv = item.get("arxiv");
        return arxiv != null && !arxiv.isEmpty();
    }
}
