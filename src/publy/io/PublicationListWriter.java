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
package publy.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import publy.data.category.OutputCategory;
import publy.data.settings.Settings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public abstract class PublicationListWriter {

    protected Settings settings;

    public PublicationListWriter(Settings settings) {
        this.settings = settings;
    }

    public void writePublicationList(List<OutputCategory> categories, Path target) throws IOException {
        Files.createDirectories(target.getParent());
        
        try (BufferedWriter out = Files.newBufferedWriter(target, Charset.forName("UTF-8"))) {
            writePublicationList(categories, out);
        }
    }

    protected abstract void writePublicationList(List<OutputCategory> categories, BufferedWriter out) throws IOException;
}
