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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import publy.Console;

/**
 * A JLabel that functions as a HTML hyperlink.
 *
 * This class was inspired by the answers at
 * http://stackoverflow.com/questions/527719/how-to-add-hyperlink-in-jlabel
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class LinkLabel extends JLabel {

    private String text;
    private URI uri;

    /**
     * For usage in the GUI editor
     */
    public LinkLabel() {
        super("link", new javax.swing.ImageIcon(LinkLabel.class.getResource("/publy/gui/resources/BlueGlobe-12.png")), SwingConstants.LEADING);
    }

    public LinkLabel(String text, String uri) {
        super();

        if (setUri(uri) && isBrowsingSupported()) {
            this.text = text;
            styleAsLink();
            makeClickable();
        } else {
            setText(text + " (" + uri.toString() + ")");
        }
    }

    private boolean setUri(String uri) {
        try {
            this.uri = new URI(uri);
            return true;
        } catch (URISyntaxException ex) {
            this.uri = null;
            return false;
        }
    }

    private static boolean isBrowsingSupported() {
        try {
            if (Desktop.isDesktopSupported()) {
                return Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void styleAsLink() {
        setIcon(new javax.swing.ImageIcon(getClass().getResource("/publy/gui/resources/BlueGlobe-12.png")));
        setIconTextGap(2);
        setText(text);
        setForeground(Color.blue);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText(uri.toString());
    }

    private void makeClickable() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                (new LinkRunner(uri)).execute();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setText("<html><a href=\"" + uri.toString() + "\">" + text + "</a></html>");

                /* By default, setting the text to HTML confuses getPreferredSize, 
                 * which screws up the layout. This works around that by fixing
                 * the size of the label to its current size, since we know that 
                 * we're just underlining what's already there. */
                setMaximumSize(getPreferredSize());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setText(text);
            }
        });
    }

    private static class LinkRunner extends SwingWorker<Void, Void> {

        private final URI uri;

        private LinkRunner(URI uri) {
            if (uri == null) {
                throw new NullPointerException();
            }

            this.uri = uri;
        }

        @Override
        protected Void doInBackground() throws Exception {
            Desktop desktop = java.awt.Desktop.getDesktop();
            desktop.browse(uri);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (ExecutionException | InterruptedException ex) {
                handleException(uri, ex);
            }
        }

        private static void handleException(URI u, Exception e) {
            Console.except(e, "An exception occurred while trying to open the link \"%s\" in the standard browser:", u.toString());
        }
    }
}
