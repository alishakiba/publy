/*
 * Copyright 2014-2016 Sander Verdonschot <sander.verdonschot at gmail.com>.
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

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * A custom label that is used as the title of a tab.
 */
public class TabTitle extends JLabel {

    /**
     * Creates a new TabTitle with the specified text and icon.
     *
     * @param title The text to be displayed by the label.
     * @param icon The image to be displayed by the label.
     */
    public TabTitle(String title, Icon icon) {
        super(title, icon, JLabel.LEADING);

        setPreferredSize(new Dimension(130, 40));

        setFont(getFont().deriveFont(getFont().getSize2D() + 2));
        UIStyles.applyHeaderStyle(this);

        setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
    }
}
