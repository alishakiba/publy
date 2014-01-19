/*
 * Copyright 2014 Sander.
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
package publy.algo;

import java.util.List;
import publy.data.bibitem.BibItem;

/**
 *
 * @author Sander
 */
public class PostProcessor {

    public static void postProcess(List<BibItem> items) {
        for (BibItem item : items) {
            detectArxiv(item);
        }
    }

    protected static void detectArxiv(BibItem item) {
        // If this entry has an arxiv and primaryclass field, it's done
        String arxiv = item.get("arxiv");
        String primaryClass = item.get("primaryclass");

        if (arxiv != null) {
            if (arxiv.startsWith("http://arxiv.org/abs/")) {
                arxiv = arxiv.substring("http://arxiv.org/abs/".length()).trim();
            }
        }

        if (arxiv == null || primaryClass == null) {
            if (arxiv == null) {
                // Other fields might specify the arxiv identifier
                String eprint = item.get("eprint");

                if (eprint == null) {
                    for (String field : item.getFields()) {
                        String value = item.get(field);

                        if (value != null && value.startsWith("http://arxiv.org/abs/")) {
                            arxiv = value.substring("http://arxiv.org/abs/".length()).trim();
                        }
                    }
                } else if (eprint.startsWith("http")) {
                    if (eprint.startsWith("http://arxiv.org/abs/")) {
                        arxiv = eprint.substring("http://arxiv.org/abs/".length()).trim();
                    }
                } else {
                    String prefix = item.get("archiveprefix");

                    if (prefix == null || prefix.equalsIgnoreCase("arXiv")) {
                        // eprint is most likely an old arXiv identifier of the form "class/arxivid"
                        if (eprint.contains("/")) {
                            // class/arxivid
                            int index = eprint.indexOf("/");
                            primaryClass = eprint.substring(0, index).trim();
                            arxiv = eprint.substring(index + 1).trim();
                        } else if (eprint.contains("[") && eprint.contains("]")) {
                            // arxivid [class]
                            int index1 = eprint.indexOf("[");
                            int index2 = eprint.indexOf("]");

                            primaryClass = eprint.substring(index1 + 1, index2).trim();
                            arxiv = eprint.substring(0, index1).trim();
                        } else {
                            arxiv = eprint;
                        }
                    }
                }
            } else {
                // Arxiv identifier, but no primary class yet
                if (arxiv.contains("/")) {
                    // class/arxivid
                    int index = arxiv.indexOf("/");
                    primaryClass = arxiv.substring(0, index).trim();
                    arxiv = arxiv.substring(index + 1).trim();
                } else if (arxiv.contains("[") && arxiv.contains("]")) {
                    // arxivid [class]
                    int index1 = arxiv.indexOf("[");
                    int index2 = arxiv.indexOf("]");

                    primaryClass = arxiv.substring(index1 + 1, index2).trim();
                    arxiv = arxiv.substring(0, index1).trim();
                }
            }
        }

        if (arxiv != null) {
            item.put("arxiv", arxiv);
        }

        if (primaryClass != null) {
            item.put("primaryclass", primaryClass);
        }
    }
}
