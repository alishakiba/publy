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

/**
 * A boolean condition on the fields and field values of publications.
 */
public abstract class FieldCondition extends Condition {

    private String field;

    /**
     * Creates a new field condition for the specified field.
     * <p>
     * If {@code inverted} is true, any item that would otherwise match this
     * condition no longer matches it and vice versa.
     *
     * @param inverted whether to invert this condition
     * @param field the field this condition checks
     */
    public FieldCondition(boolean inverted, String field) {
        super(inverted);
        this.field = field;
    }

    /**
     * Creates a new field condition with the same attributes as the given one.
     *
     * @param condition the condition to copy
     */
    public FieldCondition(FieldCondition condition) {
        super(condition);
        this.field = condition.field;
    }

    /**
     * Gets the field that this condition checks.
     *
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * Sets the field that this condition checks.
     *
     * @param field the new field
     */
    public void setField(String field) {
        this.field = field;
    }
}
