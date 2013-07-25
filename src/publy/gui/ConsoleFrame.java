/*
 */
package publy.gui;

import java.awt.Cursor;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import publy.Console;
import publy.GeneratorMain;
import publy.data.settings.Settings;
import publy.io.settings.SettingsReader;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class ConsoleFrame extends javax.swing.JFrame {

    /**
     * Creates new form ConsoleFrame
     */
    public ConsoleFrame() {
        initComponents();
        setLocationRelativeTo(null); // Center
        Console.setOutputTarget(consoleTextPane);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonPanel = new javax.swing.JPanel();
        generateButton = new javax.swing.JButton();
        settingsButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        consoleScrollPane = new javax.swing.JScrollPane();
        consoleTextPane = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Publy - Console");

        generateButton.setText("Generate");
        generateButton.setToolTipText("Run the generator with the current settings.");
        generateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateButtonActionPerformed(evt);
            }
        });

        settingsButton.setText("Edit Settings");
        settingsButton.setToolTipText("Opens the settings editor.");
        settingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsButtonActionPerformed(evt);
            }
        });

        closeButton.setText("Close");
        closeButton.setToolTipText("Closes the application.");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(generateButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(settingsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(closeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(generateButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(settingsButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(buttonPanel, java.awt.BorderLayout.LINE_END);

        consoleTextPane.setEditable(false);
        consoleTextPane.setPreferredSize(new java.awt.Dimension(300, 20));
        consoleScrollPane.setViewportView(consoleTextPane);

        getContentPane().add(consoleScrollPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void generateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateButtonActionPerformed
        // Change cursor to hourglass
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        // Clear the console before generating
        consoleTextPane.setText("");
        
        // Parse the settings again (the user might have changed them manually)
        Settings settings = null;

        try {
            settings = SettingsReader.parseSettings();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Console.except(ex, "Exception occurred while parsing the configuration:");
        }

        if (settings != null) {
            GeneratorMain.generatePublicationList(settings);
        }
        
        // Change cursor back
        setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_generateButtonActionPerformed

    private void settingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsButtonActionPerformed
        // Parse the settings again (the user might have changed them manually)
        Settings settings = null;

        try {
            settings = SettingsReader.parseSettings();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Console.except(ex, "Exception occurred while parsing the configuration:");
        }

        if (settings == null) {
            // Display an alert to the user
            JOptionPane.showMessageDialog(null, "The configuration could not be parsed. The default configuration will be shown.", "Publy - Launching Settings Window", JOptionPane.WARNING_MESSAGE);
        }
        
        // Create the settings GUI
        MainFrame mainFrame = new MainFrame(settings);
        // The current console text is copied automatically

        // Close this window and open the settings GUI
        dispose();
        mainFrame.setVisible(true);
    }//GEN-LAST:event_settingsButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JScrollPane consoleScrollPane;
    private javax.swing.JTextPane consoleTextPane;
    private javax.swing.JButton generateButton;
    private javax.swing.JButton settingsButton;
    // End of variables declaration//GEN-END:variables
}
