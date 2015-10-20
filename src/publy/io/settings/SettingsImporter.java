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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import publy.data.Pair;
import publy.data.settings.FileSettings;
import publy.data.settings.Settings;
import publy.Constants;
import publy.io.ResourceLocator;
import publy.io.settings.legacy.SettingsReaderV0_8;

public class SettingsImporter {

    private static final Pattern majorVersionPattern = Pattern.compile("majorversion=\"?([0-9]+)");
    private static final Pattern minorVersionPattern = Pattern.compile("minorversion=\"?([0-9]+)");

    public static Settings importSettings(Path settingsFile) throws IOException {
        Pair<Integer, Integer> version = detectVersion(settingsFile);
        SettingsReader reader = selectSettingsReader(version);
        Settings settings = reader.parseSettings(settingsFile);
        correctFilePaths(settingsFile, settings);

        return settings;
    }

    private static Pair<Integer, Integer> detectVersion(Path settingsFile) throws IOException {
        int majorVersion = -1, minorVersion = -1;

        try (BufferedReader in = Files.newBufferedReader(settingsFile, Charset.forName("UTF-8"))) {
            for (String line = in.readLine(); line != null && (majorVersion == -1 || minorVersion == -1); line = in.readLine()) {
                Matcher matcher = majorVersionPattern.matcher(line);

                if (matcher.find()) {
                    majorVersion = Integer.parseInt(matcher.group(1));
                }

                matcher = minorVersionPattern.matcher(line);

                if (matcher.find()) {
                    minorVersion = Integer.parseInt(matcher.group(1));
                }
            }
        }

        if (majorVersion == -1 || minorVersion == -1) {
            throw new IOException("Configuration file does not specify version information.");
        } else {
            return new Pair<>(majorVersion, minorVersion);
        }
    }

    private static SettingsReader selectSettingsReader(Pair<Integer, Integer> version) {
        int majorVersion = version.getFirst();
        int minorVersion = version.getSecond();

        if (majorVersion > Constants.MAJOR_VERSION || (majorVersion == Constants.MAJOR_VERSION && minorVersion > Constants.MINOR_VERSION)) {
            JOptionPane.showMessageDialog(null,
                    "<html>The detected configuration file was created by a newer version of Publy.<br>We'll try to import it anyway, but this might not work.</html>",
                    "Publy - Import warning",
                    JOptionPane.WARNING_MESSAGE);
            return new SettingsReaderCurrent();
        }

        if (majorVersion <= 0 && minorVersion <= 8) {
            return new SettingsReaderV0_8();
        } else {
            return new SettingsReaderCurrent();
        }
    }

    private static void correctFilePaths(Path settingsFile, Settings settings) {
        FileSettings fileSettings = settings.getFileSettings();
        fileSettings.setPublications(correctPath(settingsFile, fileSettings.getPublications()));
        fileSettings.setTarget(correctPath(settingsFile, fileSettings.getTarget()));
        fileSettings.setHeader(correctPath(settingsFile, fileSettings.getHeader()));
        fileSettings.setFooter(correctPath(settingsFile, fileSettings.getFooter()));

        settings.getHtmlSettings().setTheme(correctPath(settingsFile, settings.getHtmlSettings().getTheme()));
    }

    private static Path correctPath(Path settingsFile, Path path) {
        // The path we get is resolved from the current base directory, but it should be resolved from the old base directory
        Path corrected = settingsFile.getParent().resolveSibling(ResourceLocator.getRelativePath(path)).normalize();

        if (inPublyDirectory(settingsFile, corrected)) {
            // If the file is in the settings file's Publy directory, copy it to ours if it doesn't already exist
            try {
                if (!Files.exists(path)) {
                    Files.copy(corrected, path);
                }
                return path;
            } catch (IOException ioe) {
                // Just refer to the old file
                return corrected;
            }
        } else {
            // If the path is to a file outside the Publy directory, just return it
            return corrected;
        }
    }

    private static boolean inPublyDirectory(Path settingsFile, Path path) {
        // Check whether the settings file is in a Publy directory
        Path potentialPublyDir = settingsFile.normalize().getParent().getParent();
        Path publyJar = potentialPublyDir.resolve("Publy.jar");

        if (Files.notExists(publyJar)) {
            return false;
        }

        // Check whether the given path is in the same Publy directory
        return path.normalize().startsWith(potentialPublyDir);
    }

    private SettingsImporter() {
    }
}
