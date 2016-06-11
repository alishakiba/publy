/*
 * Copyright 2013-2016 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Constants that are used throughout the UI.
 */
public class Constants {

    /**
     * Publy's major version number.
     */
    public static final int MAJOR_VERSION = 1;
    /**
     * Publy's minor version number.
     */
    public static final int MINOR_VERSION = 2;
    /**
     * Publy's application icons.
     */
    public static final List<Image> PUBLY_ICONS; // Initialized in the static block
    /**
     * Publy's logo in all its glory.
     */
    public static final Image PUBLY_LOGO; // Initialized in the static block

    static {
        // Prepare our icons
        String[] icons = {"gui/resources/icon-16.png",
            "gui/resources/icon-24.png",
            "gui/resources/icon-32.png",
            "gui/resources/icon-48.png",
            "gui/resources/icon-64.png",
            "gui/resources/icon-256.png"};

        PUBLY_ICONS = new ArrayList<>(icons.length);

        for (String fileName : icons) {
            URL iconUrl = Constants.class.getResource(fileName);
            Constants.PUBLY_ICONS.add(Toolkit.getDefaultToolkit().createImage(iconUrl));
        }

        // Import the logo
        URL logoUrl = Constants.class.getResource("gui/resources/logo.png");
        PUBLY_LOGO = Toolkit.getDefaultToolkit().createImage(logoUrl);
    }
}
