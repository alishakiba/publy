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

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import publy.data.ExampleBibItems;
import publy.data.bibitem.BibItem;

/**
 *
 *
 */
public class FieldContainsConditionTest {
    
    public FieldContainsConditionTest() {
    }

    /**
     * Test of matches method, of class FieldContainsCondition.
     */
    @Test
    public void testMatches() {
        FieldCondition year = new FieldContainsCondition(false, "year", "201");
        FieldCondition pages = new FieldContainsCondition(false, "pages", "--1");
        FieldCondition booktitle = new FieldContainsCondition(false, "booktitle", "CCCG");
        FieldCondition pubstate = new FieldContainsCondition(false, "pubstate", "submitted");
        
        List<FieldCondition> conditions = Arrays.asList(year, pages, booktitle, pubstate);

        List<BibItem> items = new ExampleBibItems();

        boolean[][] expected = new boolean[][] {
            new boolean[] {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true}, // year
            new boolean[] {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true,  false, false, true,  false}, // pages
            new boolean[] {false, false, false, false, true,  false, false, false, true,  true,  false, false, false, false, false, false, false, false, true,  false, false}, // booktitle
            new boolean[] {true,  true,  true,  true,  false, false, false, false, false, false, false, true,  false, false, false, false, false, false, false, false, false}  // pubstate
        };
        
        for (int i = 0; i < conditions.size(); i++) {
            for (int j = 0; j < items.size(); j++) {
                boolean ex = expected[i][j];
                boolean result = conditions.get(i).matches(items.get(j));
                
                assertEquals("Cond: " + conditions.get(i).getField()+ " with " + items.get(j), ex, result);
                
                conditions.get(i).setInverted(!conditions.get(i).isInverted());
                
                result = conditions.get(i).matches(items.get(j));
                
                assertEquals("Inverted cond: " + conditions.get(i).getField()+ " with " + items.get(j), !ex, result);
                
                conditions.get(i).setInverted(!conditions.get(i).isInverted());
            }
        }
    }
}