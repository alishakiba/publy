/*
 * Copyright 2014 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 *
 * @author Sander
 */
public class TempWriter extends BufferedWriter {

    private static final FileAttribute<Set<PosixFilePermission>> FILE_ATTRIBUTES;
    private final Path tempFile;
    private final Path actualFile;
    private boolean copyOnClose = false;

    static {
        /*
         * On Linux, temporary files can typically only be read by the owner. 
         * Since these files are intended to be read by the public, we need to 
         * explicitely specify more relaxed file permissions.
         */
        if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
            FILE_ATTRIBUTES = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-r--r--"));
        } else {
            FILE_ATTRIBUTES = null;
        }
    }

    /**
     * Creates a new TempWriter to write to the given file.
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static TempWriter newTempWriter(Path path) throws IOException {
        Path temp = (FILE_ATTRIBUTES == null ? Files.createTempFile("publy-", ".tmp") : Files.createTempFile("publy-", ".tmp", FILE_ATTRIBUTES));
        temp.toFile().deleteOnExit();
        return new TempWriter(Files.newBufferedWriter(temp, Charset.forName("UTF-8")), temp, path);
    }

    private TempWriter(BufferedWriter writer, Path tempFile, Path actualFile) {
        super(writer);
        this.tempFile = tempFile;
        this.actualFile = actualFile;
    }

    /**
     * Signifies that the writing was completed successfully, and that the
     * generated temporary file should overwrite the target file. If this is not
     * called, the target file remains unchanged.
     */
    public void copyWrittenFileOnClose() {
        copyOnClose = true;
    }

    @Override
    public void close() throws IOException {
        super.close();

        if (copyOnClose) {
            Files.copy(tempFile, actualFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
