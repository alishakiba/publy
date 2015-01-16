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
package publy.data.category.conditions;

import publy.data.bibitem.BibItem;

/**
 * A boolean condition on publications. A publication either matches this
 * condition, or it does not. The actual test is implemented in child classes.
 */
public abstract class Condition {

    private boolean inverted;

    /**
     * Creates a new condition.
     * <p>
     * If {@code inverted} is true, any item that would otherwise match this
     * condition no longer matches it and vice versa.
     *
     * @param inverted whether to invert this condition
     */
    public Condition(boolean inverted) {
        this.inverted = inverted;
    }

    /**
     * Creates a new condition with the same attributes as the given one.
     *
     * @param condition the condition to copy
     */
    public Condition(Condition condition) {
        this.inverted = condition.inverted;
    }

    /**
     * Checks whether the given publication matches this condition.
     *
     * @param item the publication
     * @return true if the given item matches this condition, false otherwise
     */
    public boolean matches(BibItem item) {
        boolean match = internalMatches(item);
        return (inverted ? !match : match);
    }

    protected abstract boolean internalMatches(BibItem item);

    /**
     * Gets whether this condition is inverted.
     * <p>
     * If this is true, any item that would otherwise match this condition no
     * longer matches it and vice versa.
     *
     * @return whether this condition is inverted
     */
    public boolean isInverted() {
        return inverted;
    }

    /**
     * Sets whether this condition is inverted.
     * <p>
     * Setting this to true makes any item that would otherwise match this
     * condition no longer match it and vice versa.
     *
     * @param inverted the new inverted value
     */
    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }
}
