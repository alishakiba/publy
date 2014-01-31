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
package publy.gui;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class UIConstants {

    public static final int MAJOR_VERSION = 0;
    public static final int MINOR_VERSION = 8;
    public static final List<Image> PUBLY_ICONS; // Initialized in the static block
    public static final Image PUBLY_LOGO; // Initialized in the static block
    public static final Color TEXTFIELD_ERROR_COLOR = new Color(255, 210, 210);

    static {
        // Prepare our icons
        String[] icons = {"resources/icon-16.png",
            "resources/icon-24.png",
            "resources/icon-32.png",
            "resources/icon-48.png",
            "resources/icon-64.png",
            "resources/icon-256.png"};

        PUBLY_ICONS = new ArrayList<>(icons.length);

        for (String fileName : icons) {
            URL iconUrl = UIConstants.class.getResource(fileName);
            UIConstants.PUBLY_ICONS.add(Toolkit.getDefaultToolkit().createImage(iconUrl));
        }

        // Import the logo
        URL logoUrl = UIConstants.class.getResource("resources/logo.png");
        PUBLY_LOGO = Toolkit.getDefaultToolkit().createImage(logoUrl);
    }
}
