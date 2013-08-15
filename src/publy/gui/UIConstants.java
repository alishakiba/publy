/*
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
    public static final int MINOR_VERSION = 5;
    public static final List<Image> PUBLY_ICONS;

    static {
        // Prepare our icons
        String[] icons = {"resources/Icon1-16.png",
            "resources/Icon1-24.png",
            "resources/Icon1-32.png",
            "resources/Icon1-48.png",
            "resources/Icon1-64.png",
            "resources/Icon1-256.png"};

        PUBLY_ICONS = new ArrayList<>(icons.length);

        for (String fileName : icons) {
            URL iconUrl = UIConstants.class.getResource(fileName);
            UIConstants.PUBLY_ICONS.add(Toolkit.getDefaultToolkit().createImage(iconUrl));
        }
    }
    
    public static final Color TEXTFIELD_ERROR_COLOR = new Color(255, 210, 210);
}
