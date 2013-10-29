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
package publy.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class UIStyles {
    private static final int HEADER_FONT_STYLE = Font.BOLD;
    private static final Color HEADER_TEXT_COLOR = new Color(51, 51, 102);
    
    /**
     * Applies the header style to all given labels. The font used will be derived from the font of the first label in the list.
     * @param labels 
     */
    static void applyHeaderStyle(JLabel... labels) {
        Font headerFont = null;
        
        if (labels.length > 0) {
            headerFont = labels[0].getFont().deriveFont(HEADER_FONT_STYLE);
        }
        
        for (JLabel label : labels) {
            label.setFont(headerFont);
            label.setForeground(HEADER_TEXT_COLOR);
        }
    }
    
    /**
     * Applies the header style to all given borders. The font used will be derived from the font of the first border in the list.
     * @param labels 
     */
    static void applyHeaderStyle(TitledBorder... borders) {
        Font headerFont = null;
        
        if (borders.length > 0) {
            Font current = borders[0].getTitleFont();
            
            if (current == null) {
                current = UIManager.getDefaults().getFont("TitledBorder.font");
            }
            
            headerFont = current.deriveFont(HEADER_FONT_STYLE);
        }
        
        for (TitledBorder border : borders) {
            border.setTitleFont(headerFont);
            border.setTitleColor(HEADER_TEXT_COLOR);
        }
    }
    
    /**
     * Converts the given list of strings to a single string that can be displayed in a text field.
     * @param values
     * @return 
     */
    static String convertToDisplayString(List<String> values) {
        StringBuilder text = new StringBuilder();
        boolean first = true;

        for (String val : values) {
            if (first) {
                first = false;
            } else {
                text.append(';');
            }

            text.append(val);
        }

        return text.toString();
    }
    
    /**
     * Parses the given display string and converts it to a list of values.
     * @param value
     * @return 
     */
    static List<String> parseDisplayString(String value) {
        return Arrays.asList(value.split(";"));
    }
}
