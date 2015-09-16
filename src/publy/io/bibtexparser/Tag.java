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
package publy.io.bibtexparser;

import java.util.Map;
import publy.data.Author;

public class Tag {
    public enum Type {
        AUTHOR, ABBREVIATION;
    }
    
    public Type type;
    public Map<String, String> values;
    
    public Author toAuthor() {
        if (type != Type.AUTHOR) {
            throw new InternalError("This should only be called for Author tags!");
        }
        
        Author author = new Author(values.get("short"), values.get("name"));
        author.setUrl(values.get("url"));
        author.setGroup(values.get("group"));
        
        return author;
    }

    @Override
    public String toString() {
        return "Tag{" + "type=" + type + ", values=" + values + '}';
    }
}
