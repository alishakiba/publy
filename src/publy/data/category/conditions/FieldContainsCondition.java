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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import publy.data.bibitem.BibItem;

/**
 * A field condition that tests whether a publication's field contains specific
 * text anywhere in its value.
 * <p>
 * For example, it could match all publications co-authored with a specific
 * researcher, or all papers published in conferences with IEEE in their name.
 */
public class FieldContainsCondition extends FieldCondition {

    private List<String> values;

    /**
     * Creates a new field condition that tests whether the given field of a
     * publication contains any of the specified strings.
     * <p>
     * If {@code inverted} is true, any item that would otherwise match this
     * condition no longer matches it and vice versa. In other words, the
     * condition tests that the field contains none of the specified strings.
     *
     * @param inverted whether to invert this condition
     * @param field the field this condition checks
     * @param values the strings to test for
     */
    public FieldContainsCondition(boolean inverted, String field, List<String> values) {
        super(inverted, field);
        this.values = values;
    }

    /**
     * Creates a new field condition that tests whether the given field of a
     * publication contains any of the specified strings.
     * <p>
     * If {@code inverted} is true, any item that would otherwise match this
     * condition no longer matches it and vice versa. In other words, the
     * condition tests that the field contains none of the specified strings.
     *
     * @param inverted whether to invert this condition
     * @param field the field this condition checks
     * @param values the strings to test for
     */
    public FieldContainsCondition(boolean inverted, String field, String... values) {
        super(inverted, field);
        this.values = Arrays.asList(values);
    }

    /**
     * Creates a new field condition with the same attributes as the given one.
     *
     * @param condition the condition to copy
     */
    public FieldContainsCondition(FieldContainsCondition condition) {
        super(condition);
        values = new ArrayList<>(condition.values);
    }

    /**
     * Gets the strings this condition looks for.
     *
     * @return the values
     */
    public List<String> getValues() {
        return values;
    }

    /**
     * Sets the strings this condition looks for.
     *
     * @param values the new values
     */
    public void setValues(List<String> values) {
        this.values = values;
    }

    /**
     * Sets the strings this condition looks for.
     *
     * @param values the new values
     */
    public void setValues(String... values) {
        this.values = Arrays.asList(values);
    }

    @Override
    public boolean internalMatches(BibItem item) {
        String value = item.get(getField());

        if (value != null) {
            for (String v : values) {
                if (value.contains(v)) {
                    return true;
                }
            }
        }

        return false;
    }
}
