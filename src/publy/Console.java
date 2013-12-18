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
package publy;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import publy.data.settings.ConsoleSettings;
import publy.gui.ConsoleFrame;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class Console {

    public enum WarningType {
        MISSING_REFERENCE, NOT_AUTHORED_BY_USER, ITEM_DOES_NOT_FIT_ANY_CATEGORY, MANDATORY_FIELD_IGNORED, OTHER;
    }
    
    private static final SimpleAttributeSet logAttributes;
    private static final SimpleAttributeSet warnAttributes;
    private static final SimpleAttributeSet errorAttributes;
    private static ConsoleSettings settings = new ConsoleSettings();
    private static JTextPane textPane = null; // A styled text area to log to, if the program was invoked without an attached console

    // Static font initialization
    static {
        // Simple black mono-spaced log font
        logAttributes = new SimpleAttributeSet();
        StyleConstants.setFontFamily(logAttributes, "Monospaced");
        StyleConstants.setFontSize(logAttributes, 12);

        // The same font, but yellow/orange for warnings
        warnAttributes = new SimpleAttributeSet(logAttributes);
        StyleConstants.setForeground(warnAttributes, new Color(230, 138, 0));

        // The same font, but red for errors
        errorAttributes = new SimpleAttributeSet(logAttributes);
        StyleConstants.setForeground(errorAttributes, Color.red);
    }

    public static void log(String format, Object... args) {
        if (settings.isShowLogs()) {
            if (textPane == null) {
                if (System.console() == null) {
                    createConsoleFrame();
                } else {
                    System.console().format(" " + format + "%n", args);
                    System.console().flush();
                }
            }

            // Re-check, because textPane might have been set by createConsoleFrame()
            if (textPane != null) {
                try {
                    textPane.getDocument().insertString(textPane.getDocument().getLength(), String.format(" " + format + "%n", args), logAttributes);
                } catch (BadLocationException ex) {
                    // This should never happen
                    throw new AssertionError(ex);
                }
            }
        }
    }

    public static void warn(WarningType type, String format, Object... args) {
        if (showWarnings(type)) {
            if (textPane == null) {
                if (System.console() == null) {
                    createConsoleFrame();
                } else {
                    System.console().format(" WARNING: " + format + "%n", args);
                    System.console().flush();
                }
            }

            // Re-check, because textPane might have been set by createConsoleFrame()
            if (textPane != null) {
                try {
                    textPane.getDocument().insertString(textPane.getDocument().getLength(), String.format(" Warning: " + format + "%n", args), warnAttributes);
                } catch (BadLocationException ex) {
                    // This should never happen
                    throw new AssertionError(ex);
                }
            }
        }
    }

    public static void error(String format, Object... args) {
        if (textPane == null) {
            if (System.console() == null) {
                createConsoleFrame();
            } else {
                System.console().format(" ERROR: " + format + "%n", args);
                System.console().flush();
            }
        }

        // Re-check, because textPane might have been set by createConsoleFrame()
        if (textPane != null) {
            try {
                textPane.getDocument().insertString(textPane.getDocument().getLength(), String.format(" " + format + "%n", args), errorAttributes);
            } catch (BadLocationException ex) {
                // This should never happen
                throw new AssertionError(ex);
            }
        }
    }

    public static void except(Throwable exception, String format, Object... args) {
        String exceptionText;

        if (settings.isShowStackTraces()) {
            StringWriter stackTrace = new StringWriter();
            exception.printStackTrace(new PrintWriter(stackTrace));
            exceptionText = stackTrace.toString();
        } else {
            exceptionText = exception.toString();
        }

        if (textPane == null) {
            if (System.console() == null) {
                createConsoleFrame();
            } else {
                System.console().format(" ERROR: " + format + "%n", args);
                System.console().format("%s%n", exceptionText);
                System.console().flush();
            }
        }

        // Re-check, because textPane might have been set by createConsoleFrame()
        if (textPane != null) {
            try {
                textPane.getDocument().insertString(textPane.getDocument().getLength(), String.format(" " + format + "%n", args), errorAttributes);
                textPane.getDocument().insertString(textPane.getDocument().getLength(), String.format("%s%n", exceptionText), errorAttributes);
            } catch (BadLocationException ex) {
                // This should never happen
                throw new AssertionError(ex);
            }
        }
    }
    
    private static boolean showWarnings(WarningType type) {
        if (settings.isShowWarnings()) {
            switch (type) {
                case MISSING_REFERENCE:
                    return settings.isWarnMissingReferences();
                case NOT_AUTHORED_BY_USER:
                    return settings.isWarnNotAuthor();
                case ITEM_DOES_NOT_FIT_ANY_CATEGORY:
                    return settings.isWarnNoCategoryForItem();
                case MANDATORY_FIELD_IGNORED:
                    return settings.isWarnMandatoryFieldIgnored();
                case OTHER:
                    return true;
                default:
                    throw new AssertionError("Unexpected warning type: " + type);
            }
        } else {
            return false;
        }
    }

    public static void setOutputTarget(JTextPane textPane) {
        if (Console.textPane != null) {
            // Copy the current text over
            textPane.setStyledDocument(Console.textPane.getStyledDocument());
        }

        Console.textPane = textPane;
    }

    private static void createConsoleFrame() {
        ConsoleFrame consoleFrame = new ConsoleFrame();
        // The constructor of ConsoleFrame calls setOutputTarget
        consoleFrame.setVisible(true);
    }

    public static ConsoleSettings getSettings() {
        return settings;
    }

    public static void setSettings(ConsoleSettings settings) {
        Console.settings = settings;
    }
}
