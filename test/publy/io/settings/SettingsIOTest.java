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
package publy.io.settings;

import org.junit.Test;
import publy.data.settings.BeanTestUtils;
import publy.data.settings.Settings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class SettingsIOTest {
    
    public SettingsIOTest() {
    }

    @Test
    public void testFileSettings() throws Exception {
        System.out.println("test FileSettings IO");
        Settings settings = new Settings();
        BeanTestUtils.testSettingsIO(settings, settings.getFileSettings());
    }
    
    @Test
    public void testCategorySettings() throws Exception {
        System.out.println("test CategorySettings IO");
        Settings settings = new Settings();
        BeanTestUtils.testSettingsIO(settings, settings.getCategorySettings());
    }
    
    @Test
    public void testGeneralSettings() throws Exception {
        System.out.println("test GeneralSettings IO");
        Settings settings = new Settings();
        BeanTestUtils.testSettingsIO(settings, settings.getGeneralSettings());
    }
    
    @Test
    public void testHTMLSettings() throws Exception {
        System.out.println("test HTMLSettings IO");
        Settings settings = new Settings();
        BeanTestUtils.testSettingsIO(settings, settings.getHtmlSettings());
    }
    
    @Test
    public void testConsoleSettings() throws Exception {
        System.out.println("test ConsoleSettings IO");
        Settings settings = new Settings();
        BeanTestUtils.testSettingsIO(settings, settings.getConsoleSettings());
    }
}