/*
 * Copyright 2013-2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import publy.gui.UIConstants;

/**
 * The entrypoint to Publy. This class interprets the command-line arguments and
 * starts the correct initialization procedure of {@link Runner}.
 *
 */
public class Publy {

    /**
     * Interprets the command-line arguments and starts the correct
     * initialization procedure of {@link Runner}.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        setLookAndFeel();

        // Parse the command line arguments
        CommandLineArguments arguments = new CommandLineArguments();
        JCommander jc;

        try {
            jc = new JCommander(arguments, args);
        } catch (ParameterException ex) {
            Console.error(ex.getMessage());
            return;
        }

        if (arguments.isHelp()) {
            jc.setProgramName("java -jar Publy.jar");
            jc.usage();
        } else if (arguments.isVersion()) {
            printVersionInfo();
        } else {
            if (arguments.isGui()) {
                Runner.runWithGUI(arguments);
            } else if (System.console() == null) {
                Runner.runInMixedMode(arguments);
            } else {
                Runner.runOnCommandLine(arguments);
            }
        }
    }

    /**
     * Changes the look and feel to the system default look and feel.
     * <p>
     * The only exception is Linux, where we use the nicer Metal look and feel
     * instead.
     */
    private static void setLookAndFeel() {
        try {
            if (UIManager.getSystemLookAndFeelClassName().contains("GTK") || UIManager.getSystemLookAndFeelClassName().contains("Motif")) {
                // Running on Linux. The system L&F has issues with font rendering, so reverting to the (nice) cross-platform "Metal" L&F
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            // Unavailable: use default L&F
        }
    }

    /**
     * Prints Publy's version information when the corresponding command-line
     * argument is used.
     */
    private static void printVersionInfo() {
        System.out.printf("Publy %d.%d%n"
                + "Copyright (c) 2013-2015 Sander Verdonschot%n"
                + "License Apache v2%n"
                + "This is free software. You are free to change and redistribute it.%n",
                UIConstants.MAJOR_VERSION, UIConstants.MINOR_VERSION);
    }
}
