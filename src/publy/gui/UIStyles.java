/*
 */
package publy.gui;

import java.awt.Color;
import java.awt.Font;
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
                current = UIManager.getDefaults().getFont("TitledBorder.font");;
            }
            
            headerFont = current.deriveFont(HEADER_FONT_STYLE);
        }
        
        for (TitledBorder border : borders) {
            border.setTitleFont(headerFont);
            border.setTitleColor(HEADER_TEXT_COLOR);
        }
    }
}
