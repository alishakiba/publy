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
package publy.data.category.conditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import publy.data.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class FieldContainsCondition extends FieldCondition {

    private List<String> values;

    public FieldContainsCondition(boolean inverted, String field, List<String> values) {
        super(inverted, field);
        this.values = values;
    }

    public FieldContainsCondition(boolean inverted, String field, String... values) {
        super(inverted, field);
        this.values = Arrays.asList(values);
    }

    public FieldContainsCondition(FieldContainsCondition condition) {
        super(condition);
        values = new ArrayList<>(condition.values);
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

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
