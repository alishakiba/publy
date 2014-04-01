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

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import publy.data.ExampleBibItems;
import publy.data.bibitem.BibItem;

/**
 *
 *
 */
public class TypeConditionTest {

    public TypeConditionTest() {
    }

    /**
     * Test of matches method, of class TypeCondition.
     */
    @Test
    public void testMatches() {
        TypeCondition allTypes = new TypeCondition(false, "*");
        TypeCondition books = new TypeCondition(false, "book");
        TypeCondition conference = new TypeCondition(false, "inproceedings", "conference");
        TypeCondition journal = new TypeCondition(false, "article");
        TypeCondition talks = new TypeCondition(false, "talk");
        
        List<TypeCondition> conditions = Arrays.asList(allTypes, books, conference, journal, talks);

        List<BibItem> items = new ExampleBibItems();

        boolean[][] expected = new boolean[][] {
            new boolean[] {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true}, // allTypes
            new boolean[] {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false}, // books
            new boolean[] {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  false, false, false, true,  false, true,  true,  true,  true,  false}, // conference
            new boolean[] {false, false, false, false, false, false, false, false, false, false, false, true,  false, true,  false, false, false, false, false, false, false}, // journal
            new boolean[] {false, false, false, false, false, false, false, false, false, false, false, false, true,  false, false, false, false, false, false, false, false}  // talks
        };
        
        for (int i = 0; i < conditions.size(); i++) {
            for (int j = 0; j < items.size(); j++) {
                boolean ex = expected[i][j];
                boolean result = conditions.get(i).matches(items.get(j));
                
                assertEquals("Cond: " + conditions.get(i).getTypes() + " with " + items.get(j).getType(), ex, result);
                
                conditions.get(i).setInverted(!conditions.get(i).isInverted());
                
                result = conditions.get(i).matches(items.get(j));
                
                assertEquals("Inverted cond: " + conditions.get(i).getTypes() + " with " + items.get(j).getType(), !ex, result);
                
                conditions.get(i).setInverted(!conditions.get(i).isInverted());
            }
        }
    }
}