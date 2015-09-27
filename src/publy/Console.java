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
 * A logging console for Publy.
 *
 * This class is responsible for handling communication with the user. All other
 * classes simply pass messages to this class. When a message comes in, this
 * class decides whether it should be shown to the user, based on their
 * preferences, and if so, how it should be shown.
 * <p>
 * There are three kinds of messages:
 * <p>
 * <ul>
 * <li> Log messages - typically progress updates,
 * <li> Warnings - for suspicious patterns that could indicate user errors but
 * can be processed normally, and
 * <li> Errors - problems that cause Publy to stop processing a certain unit (a
 * publication entry, or even an entire file).
 * </ul><p>
 * Warnings are further subdivided into different types, and some can be
 * individually enabled or disabled by users. The different types are given by
 * {@link WarningType}.
 */
public class Console {

    public enum WarningType {

        MISSING_REFERENCE, NOT_AUTHORED_BY_USER, ITEM_DOES_NOT_FIT_ANY_CATEGORY, MANDATORY_FIELD_IGNORED, OTHER;
    }
    private static final SimpleAttributeSet logAttributes;
    private static final SimpleAttributeSet warnAttributes;
    private static final SimpleAttributeSet errorAttributes;
    private static final SimpleAttributeSet debugAttributes;
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
        
        // The same font, but gray for debug messages
        debugAttributes = new SimpleAttributeSet(logAttributes);
        StyleConstants.setForeground(debugAttributes, Color.gray);
    }

    /**
     * Shows a log message to the user.
     * <p>
     * Log messages should be non-critical. They are typically used as progress
     * indicators.
     * <p>
     * The message is shown on the command-line, or in a GUI component,
     * depending on the mode the program was run in. If the user does not want
     * to display log messages, this method has no effect.
     *
     * @param format the message, in the form of a format string, as per
     * {@link String#format(java.lang.String, java.lang.Object[])}
     * @param args arguments referenced by the format string
     */
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

    /**
     * Shows a warning message to the user.
     * <p>
     * Warnings should be shown for situations that are indicative of a mistake
     * by the user, but do not prevent proper processing. A typical example is
     * linking to a file that does not exist.
     * <p>
     * The warning is shown on the command-line, or in a GUI component,
     * depending on the mode the program was run in. If the user does not want
     * to display warnings (or this particular kind of warning), this method has
     * no effect.
     *
     * @param type the kind of warning
     * @param format the warning, in the form of a format string, as per
     * {@link String#format(java.lang.String, java.lang.Object[])}
     * @param args arguments referenced by the format string
     */
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

    /**
     * Shows an error message to the user.
     * <p>
     * Error messages should be shown for extraordinary situations that prevent
     * Publy from processing the current unit of work (a publication entry, or
     * even an entire file).
     * <p>
     * The error is shown on the command-line, or in a GUI component, depending
     * on the mode the program was run in. Errors can not be disabled by the
     * user.
     *
     * @param format the error, in the form of a format string, as per
     * {@link String#format(java.lang.String, java.lang.Object[])}
     * @param args arguments referenced by the format string
     */
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

    /**
     * Shows an error to the user, based on the given Exception.
     * <p>
     * This method should be used as a last resort to inform the user of rare
     * exceptions that prevent Publy from processing the current unit of work (a
     * publication entry, or even an entire file). In general, it is preferable
     * to report a tailored error message with a helpful suggestion how to
     * resolve the error.
     * <p>
     * The error is shown on the command-line, or in a GUI component, depending
     * on the mode the program was run in. Errors can not be disabled by the
     * user. If the user has enabled debug information, the exception will be
     * accompanied by a stack trace.
     *
     * @param exception the exception that caused this error
     * @param format the error, in the form of a format string, as per
     * {@link String#format(java.lang.String, java.lang.Object[])}
     * @param args arguments referenced by the format string
     */
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

    /**
     * Shows a debug message to the user.
     * <p>
     * Debug messages are disabled by default and are probably not very
     * interesting to most users, but can be critical to diagnose strange bugs.
     *
     * @param format the message, in the form of a format string, as per
     * {@link String#format(java.lang.String, java.lang.Object[])}
     * @param args arguments referenced by the format string
     */
    public static void debug(String format, Object... args) {
        if (settings.isShowDebugLog()) {
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
                    textPane.getDocument().insertString(textPane.getDocument().getLength(), String.format(" " + format + "%n", args), debugAttributes);
                } catch (BadLocationException ex) {
                    // This should never happen
                    throw new AssertionError(ex);
                }
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

    /**
     * Changes the output target to the specified text pane.
     * <p>
     * If the text pane is not null, all new messages will appear there. If it
     * is null, new messages will be written to standard output.
     * <p>
     * If there is a current output target, its content will be copied to the
     * new one.
     *
     * @param textPane the place any new messages will appear
     */
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

    /**
     * Gets the configuration for this Console.
     *
     * @return the configuration
     */
    public static ConsoleSettings getSettings() {
        return settings;
    }

    /**
     * Sets the configuration for this Console.
     *
     * @param settings the new configuration
     */
    public static void setSettings(ConsoleSettings settings) {
        Console.settings = settings;
    }
}
