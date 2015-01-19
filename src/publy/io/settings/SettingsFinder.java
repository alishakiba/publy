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
package publy.io.settings;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class SettingsFinder extends SimpleFileVisitor<Path> {

    private Path settingsFile;

    public static Path findSettingsFileInFileTree(Path root) throws IOException {
        SettingsFinder sf = new SettingsFinder();
        Files.walkFileTree(root, sf);
        return sf.settingsFile;
    }

    @Override
    public FileVisitResult visitFile(Path t, BasicFileAttributes bfa) throws IOException {
        if (Files.isRegularFile(t) && SettingsReader.DEFAULT_SETTINGS_FILE.equals(t.getFileName().toString())) {
            settingsFile = t;
            return FileVisitResult.TERMINATE;
        } else {
            return FileVisitResult.CONTINUE;
        }
    }
}
