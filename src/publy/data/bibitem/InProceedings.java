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
package publy.data.bibitem;

/**
 *
 * @author Sander
 */
public class InProceedings extends BibItem {

    public InProceedings() {
        setMandatoryFields("author", "title", "booktitle", "year");
        setOptionalFields("editor", "volume", "number", "series", "pages", "organization", "publisher", "address", "month", "note", "key", "doi");
    }

    @Override
    public String getType() {
        return "inproceedings";
    }

}
