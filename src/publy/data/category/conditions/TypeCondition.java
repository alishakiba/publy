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
 * A boolean condition on the type of a publication.
 * <p>
 * This can test whether a publication is a journal paper, or if it is of a
 * well-defined type, i.e. not miscellaneous.
 */
public class TypeCondition extends Condition {

    private List<String> types;

    /**
     * Creates a new type condition that tests whether the type of a publication
     * is among the given types.
     * <p>
     * If {@code inverted} is true, any item that would otherwise match this
     * condition no longer matches it and vice versa. In other words, the
     * condition tests that the type is not among the given types.
     * <p>
     * The types are tested against the publication's
     * {@link BibItem#getOriginalType() original type}.
     *
     * @param inverted whether to invert this condition
     * @param types the types to test for
     */
    public TypeCondition(boolean inverted, List<String> types) {
        super(inverted);
        this.types = types;
    }

    /**
     * Creates a new type condition that tests whether the type of a publication
     * is among the given types.
     * <p>
     * If {@code inverted} is true, any item that would otherwise match this
     * condition no longer matches it and vice versa. In other words, the
     * condition tests that the type is not among the given types.
     * <p>
     * The types are tested against the publication's
     * {@link BibItem#getOriginalType() original type}.
     *
     * @param inverted whether to invert this condition
     * @param types the types to test for
     */
    public TypeCondition(boolean inverted, String... types) {
        super(inverted);
        this.types = Arrays.asList(types);
    }

    /**
     * Creates a new type condition with the same attributes as the given one.
     *
     * @param condition the condition to copy
     */
    public TypeCondition(TypeCondition condition) {
        super(condition);
        types = new ArrayList<>(condition.types);
    }

    /**
     * Gets the types this condition tests for.
     *
     * @return the types
     */
    public List<String> getTypes() {
        return types;
    }

    /**
     * Sets the types this condition tests for.
     * 
     * @param types the new types
     */
    public void setTypes(List<String> types) {
        this.types = types;
    }

    /**
     * Sets the types this condition tests for.
     * 
     * @param types the new types
     */
    public void setTypes(String... types) {
        this.types = Arrays.asList(types);
    }

    @Override
    public boolean internalMatches(BibItem item) {
        return types.contains(item.getOriginalType()) || types.contains("*");
    }
}
