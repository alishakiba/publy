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
package publy.data.category.conditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import publy.data.bibitem.BibItem;

/**
 *
 *
 */
public class TypeCondition extends Condition {

    private List<String> types;

    public TypeCondition(boolean inverted, List<String> types) {
        super(inverted);
        this.types = types;
    }

    public TypeCondition(boolean inverted, String... types) {
        super(inverted);
        this.types = Arrays.asList(types);
    }

    public TypeCondition(TypeCondition condition) {
        super(condition);
        types = new ArrayList<>(condition.types);
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public void setTypes(String... types) {
        this.types = Arrays.asList(types);
    }

    @Override
    public boolean internalMatches(BibItem item) {
        return types.contains(item.getOriginalType()) || types.contains("*");
    }
}
