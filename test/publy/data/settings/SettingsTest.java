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
package publy.data.settings;

import org.junit.Test;

/**
 *
 *
 */
public class SettingsTest {
    
    public SettingsTest() {
    }

    @Test
    public void testFileSettings() {
        System.out.println("test FileSettings");
        BeanTestUtils.testBean(new FileSettings());
    }
    
    @Test
    public void testCategorySettings() {
        System.out.println("test CategorySettings");
        BeanTestUtils.testBean(new CategorySettings());
    }
    
    @Test
    public void testGeneralSettings() {
        System.out.println("test GeneralSettings");
        BeanTestUtils.testBean(new GeneralSettings());
    }
    
    @Test
    public void testHTMLSettings() {
        System.out.println("test HTMLSettings");
        BeanTestUtils.testBean(new HTMLSettings());
    }
    
    @Test
    public void testConsoleSettings() {
        System.out.println("test ConsoleSettings");
        BeanTestUtils.testBean(new ConsoleSettings());
    }
}