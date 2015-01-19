/*
 * Copyright 2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.io.settings.legacy;

import java.io.IOException;
import java.nio.file.Path;
import publy.data.settings.Settings;
import publy.io.settings.SettingsReader;

public class SettingsReaderV0_8 implements SettingsReader {

    @Override
    public Settings parseSettings(Path inputFile) throws IOException {
        System.err.println("Version 0.8 settings parse!");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
