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
package publy.data;

import publy.data.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
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
                throw new AssertionError("Unrecognized PublicationType: " + this);
        }
    }
    
    public boolean matches(BibItem item) {
        return matches(this, item);
    }
    
    public static boolean matches(PublicationType type, BibItem item) {
        if (type == ALL) {
            return true;
        } else if (type == NONE) {
            return false;
        } else {
            if (item.get("status") != null && !item.get("status").isEmpty()) {
                if (type == PUBLISHED) {
                    return false;
                } else {
                    if (item.get("status").startsWith("accepted")) {
                        return true;
                    } else {
                        if (type == ACCEPTED) {
                            return false;
                        } else {
                            // Type is ARXIV
                            return item.get("arxiv") != null && !item.get("arxiv").isEmpty();
                        }
                    }
                }
            } else {
                return true;
            }
        }
    }
}
