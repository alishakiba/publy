/*
 */
package publy.gui;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import publy.Console;
import publy.GeneratorMain;
import publy.data.settings.Settings;
import publy.io.settings.SettingsWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class MainFrame extends javax.swing.JFrame {

    private Settings settings;

    /**
     * Creates new form MainFrame
     */
    public MainFrame(Settings settings) {
        this.settings = settings;
        initComponents();
        setLocationRelativeTo(null); // Center

        // Make sure all console output from the generation is redirected to the text area.
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

        mainSplitPane = new javax.swing.JSplitPane();
        settingsTabbedPane = new javax.swing.JTabbedPane();
        fileSettingsPanel = new publy.gui.FileSettingsPanel(settings);
        categorySettingsPanel = new publy.gui.CategorySettingsPanel(settings.getGeneralSettings());
        generalSettingsPanel = new publy.gui.GeneralSettingsPanel(settings.getGeneralSettings());
        htmlSettingsPanel = new publy.gui.HTMLSettingsPanel(settings.getHtmlSettings());
        bottomPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        generateButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        saveNQuitButton = new javax.swing.JButton();
        consoleScrollPane = new javax.swing.JScrollPane();
        consoleTextPane = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Publy " + GeneratorMain.MAJOR_VERSION + "." + GeneratorMain.MINOR_VERSION);
        setIconImage(GeneratorMain.PUBLY_ICON);

        mainSplitPane.setDividerLocation(getHeight() - 150);
        mainSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setResizeWeight(1.0);

        settingsTabbedPane.addTab("Files", fileSettingsPanel);
        settingsTabbedPane.addTab("Categories", categorySettingsPanel);
        settingsTabbedPane.addTab("General", generalSettingsPanel);
        settingsTabbedPane.addTab("HTML", htmlSettingsPanel);

        mainSplitPane.setTopComponent(settingsTabbedPane);

        bottomPanel.setLayout(new java.awt.BorderLayout());

        generateButton.setText("Generate");
        generateButton.setToolTipText("Run the generator with the current settings.");
        generateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Closes the application without saving any changes to the settings.");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        saveNQuitButton.setText("Save & Quit");
        saveNQuitButton.setToolTipText("Closes the application and saves all changes to the settings.");
        saveNQuitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveNQuitButtonActionPerformed(evt);
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
                    .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(saveNQuitButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(generateButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveNQuitButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bottomPanel.add(buttonPanel, java.awt.BorderLayout.LINE_END);

        consoleScrollPane.setViewportView(consoleTextPane);

        bottomPanel.add(consoleScrollPane, java.awt.BorderLayout.CENTER);

        mainSplitPane.setBottomComponent(bottomPanel);

        getContentPane().add(mainSplitPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void generateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateButtonActionPerformed
        // Change cursor to hourglass
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        // Clear the console before generating
        consoleTextPane.setText("");

        GeneratorMain.generatePublicationList(settings);
        
        // Change cursor back
        setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_generateButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void saveNQuitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveNQuitButtonActionPerformed
        try {
            SettingsWriter.writeSettings(settings);
            dispose();
        } catch (Exception | AssertionError ex) {
            Console.except(ex, "Exception when saving settings:");
        }
    }//GEN-LAST:event_saveNQuitButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame(Settings.defaultSettings()).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private publy.gui.CategorySettingsPanel categorySettingsPanel;
    private javax.swing.JScrollPane consoleScrollPane;
    private javax.swing.JTextPane consoleTextPane;
    private publy.gui.FileSettingsPanel fileSettingsPanel;
    private publy.gui.GeneralSettingsPanel generalSettingsPanel;
    private javax.swing.JButton generateButton;
    private publy.gui.HTMLSettingsPanel htmlSettingsPanel;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JButton saveNQuitButton;
    private javax.swing.JTabbedPane settingsTabbedPane;
    // End of variables declaration//GEN-END:variables
}
