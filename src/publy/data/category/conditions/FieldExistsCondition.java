/*
 * Copyright 2013-2016 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
 * A field condition that tests whether a publication's field has a value
 * assigned.
 * <p>
 * For example, it could match all publications that have an e-print version or
 * a DOI.
 */
public class FieldExistsCondition extends FieldCondition {

    /**
     * Creates a new field condition that tests whether the given field of a
     * publication is set.
     * <p>
     * If {@code inverted} is true, any item that would otherwise match this
     * condition no longer matches it and vice versa. In other words, the
     * condition tests that the field is not set.
     *
     * @param inverted whether to invert this condition
     * @param field the field this condition checks
     */
    public FieldExistsCondition(boolean inverted, String field) {
        super(inverted, field);
    }

    /**
     * Creates a new field condition with the same attributes as the given one.
     *
     * @param condition the condition to copy
     */
    public FieldExistsCondition(FieldExistsCondition condition) {
        super(condition);
    }

    @Override
    public boolean internalMatches(BibItem item) {
        String value = item.get(getField());
        return (value != null && !value.isEmpty());
    }

}
