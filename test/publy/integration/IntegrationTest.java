/*
 * Copyright 2014-2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import publy.algo.PublicationListGenerator;
import publy.io.settings.SettingsReaderCurrent;
import static org.junit.Assert.*;
import publy.Console;
import publy.data.settings.Settings;

public class IntegrationTest {

    private static final Path HEADER = Paths.get("test/publy/integration/EmptyFileForTests.txt");
    private static final Path FOOTER = Paths.get("test/publy/integration/EmptyFileForTests.txt");
    private static final Path TEST_ARCHIVE = Paths.get("test/publy/integration/integration.zip");
    private static final Path WORKING_DIR = Paths.get("testOutput");

    /**
     * Run all integration tests.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testIntegration() throws IOException {
        Files.createDirectories(WORKING_DIR);
        Console.setHeadless(true);

        FileSystem zipFs = FileSystems.newFileSystem(TEST_ARCHIVE, null);

        int count = 1;

        while (testExists(count, zipFs)) {
            cleanUp();
            System.out.printf("Test %03d%n", count);
            runTest(count, zipFs);
            count++;
        }

        cleanUp();
    }

    private boolean testExists(int count, FileSystem zipFs) {
        return Files.exists(zipFs.getPath(String.format("test%03d", count)));
    }

    private void runTest(int count, FileSystem zipFs) throws IOException {
        Path inputDir = zipFs.getPath(String.format("test%03d", count));
        Path workDir = WORKING_DIR.resolve(String.format("test%03d", count));

        Files.createDirectories(workDir);

        // Copy the input file
        Path publications = workDir.resolve("publications.bib");
        Files.copy(inputDir.resolve("publications.bib"), publications);

        // Set the correct fields
        Settings.setSettingsPath(inputDir.resolve("TestSettings.xml"));

        try {
            Settings settings = (new SettingsReaderCurrent()).parseSettings();

            // Make it quiet
            settings.getConsoleSettings().setShowLogs(false);
            settings.getConsoleSettings().setShowWarnings(false);
            Console.setSettings(settings.getConsoleSettings());

            settings.getFileSettings().setHeader(HEADER);
            settings.getFileSettings().setFooter(FOOTER);

            PublicationListGenerator.generatePublicationList(settings);
        } catch (IOException ex) {
            fail("Exception while parsing: " + ex);
            ex.printStackTrace();
        }

        // Test all the things
        checkFileEquality("TestPublications.html", inputDir, workDir);
        checkFileEquality("TestPublications.utf8.txt", inputDir, workDir);
        checkFileEquality("TestPublications-generated.bib", inputDir, workDir);
    }

    private void cleanUp() throws IOException {
        for (Path path : Files.newDirectoryStream(WORKING_DIR)) {
            if (Files.isDirectory(path)) {
                for (Path path2 : Files.newDirectoryStream(path)) {
                    if (Files.isRegularFile(path2)) {
                        Files.delete(path2);
                    }
                }

                Files.delete(path);
            } else if (Files.isRegularFile(path)) {
                Files.delete(path);
            }
        }
    }

    private void checkFileEquality(String fileName, Path inputDir, Path workDir) throws IOException {
        Path inputFile = inputDir.resolve(fileName);

        if (Files.exists(inputFile)) {
            Path workFile = workDir.resolve(fileName);

            if (!Files.exists(workFile)) {
                fail("Expected output file does not exist: " + fileName);
            }

            try (BufferedReader input = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
                    BufferedReader work = Files.newBufferedReader(workFile, StandardCharsets.UTF_8)) {
                // Compare both files
                String inputLine = input.readLine();
                String workLine = work.readLine();
                int lineNo = 1;

                while (inputLine != null && workLine != null) {
                    if (inputLine.contains("Last modified on ")) {
                        if (!workLine.contains("Last modified on ")) {
                            fail(String.format("Generated file %s differs from expected output at line %d.%nExpected:  %s%nGenerated: %s%n", fileName, lineNo, inputLine, workLine));
                        } else {
                            // Verify only the part before the time stamp
                            String inputBeforeDate = inputLine.substring(0, inputLine.indexOf("Last modified on "));
                            String workBeforeDate = workLine.substring(0, workLine.indexOf("Last modified on "));
                            assertEquals(String.format("Generated file %s differs from expected output at line %d.%nExpected:  %s%nGenerated: %s%n", fileName, lineNo, inputLine, workLine), inputBeforeDate, workBeforeDate);
                        }
                    } else {
                        assertEquals(String.format("Generated file %s differs from expected output at line %d.%nExpected:  %s%nGenerated: %s%n", fileName, lineNo, inputLine, workLine), inputLine, workLine);
                    }

                    inputLine = input.readLine();
                    workLine = work.readLine();
                    lineNo++;
                }

                if (inputLine == null && workLine != null) {
                    fail(String.format("Generated file %s continues after expected output ends.%nNext line: %s", fileName, workLine));
                } else if (inputLine != null && workLine == null) {
                    fail(String.format("Generated file %s ends before expected output ends.%nNext input line: %s", fileName, inputLine));
                }
            }
        }
    }
}
