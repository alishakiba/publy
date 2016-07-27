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

public class ParseException extends Exception {

    private static final long serialVersionUID = 1L;
    private int lineNumber = -1;
    private String item;
    private String type;

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorText() {
        StringBuilder result = new StringBuilder("Error while parsing ");
        
        if (type != null && !type.isEmpty()) {
            result.append(type);
        } else {
            result.append("item");
        }
        
        if (item != null && !item.isEmpty()) {
            result.append(" \"").append(item).append('"');
        }
        
        if (lineNumber >= 0) {
            result.append(", on line ").append(lineNumber);
        }
        
        result.append(": ").append(getMessage());
        
        return result.toString();
    }
    
    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
