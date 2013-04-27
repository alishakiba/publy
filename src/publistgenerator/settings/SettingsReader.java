/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class SettingsReader {

    public static Settings parseSettings(String file) {
        File settingsFile = new File(file);
        Settings settings = null;

        if (settingsFile.exists()) {
            try (BufferedReader in = new BufferedReader(new FileReader(settingsFile))) {
                parseSettings(settings, in);
            } catch (IOException ex) {
                Logger.getLogger(SettingsReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return settings;
    }

    private static void parseSettings(Settings settings, BufferedReader in) throws IOException {
        // TODO
    }
}
