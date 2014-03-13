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
package publy.data.category;

import org.junit.Test;
import static org.junit.Assert.*;
import publy.data.ExampleBibItems;
import publy.data.category.conditions.TypeCondition;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class OutputCategoryTest {
    
    public OutputCategoryTest() {
    }

    /**
     * Test of main method, of class OutputCategory.
     */
    @Test
    public void testClone() throws Exception {
        // Check that clone creates a new list containing the same items
        // This way, additions to the new list are not reflected in the old list
        OutputCategory main = new OutputCategory("main", "main", new TypeCondition(false, "article"));
        OutputCategory copy = (OutputCategory) main.clone();
        
        copy.populate(new ExampleBibItems());
        
        assertFalse(copy.getItems().isEmpty());
        assertTrue("Items: " + main.getItems(), main.getItems().isEmpty());
        
        OutputCategory copy2 = (OutputCategory) copy.clone();
        
        assertTrue(copy.getItems().equals(copy2.getItems()));
        
        copy2.populate(new ExampleBibItems());
        
        assertFalse(copy.getItems().equals(copy2.getItems()));
        assertTrue(copy2.getItems().size() == 2 * copy.getItems().size());
    }
}