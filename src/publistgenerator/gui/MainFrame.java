/*
 */
package publistgenerator.gui;

import publistgenerator.data.settings.Settings;

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
        populateValues();
    }

    private void populateValues() {
        if (settings.getPublications() == null) {
            pubTextField.setText("");
        } else {
            pubTextField.setText(settings.getPublications().getPath());
        }
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
        topPanel = new javax.swing.JPanel();
        publicationsPanel = new javax.swing.JPanel();
        pubLabel = new javax.swing.JLabel();
        pubTextField = new javax.swing.JTextField();
        pubBrowseButton = new javax.swing.JButton();
        settingsTabbedPane = new javax.swing.JTabbedPane();
        htmlPanel = new javax.swing.JPanel();
        plainPanel = new javax.swing.JPanel();
        bottomPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        generateButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        saveNQuitButton = new javax.swing.JButton();
        consoleScrollPane = new javax.swing.JScrollPane();
        consoleTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Publication List Generator");

        mainSplitPane.setDividerLocation(200);
        mainSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setResizeWeight(1.0);

        topPanel.setLayout(new java.awt.BorderLayout());

        pubLabel.setText("Publications file:");

        pubTextField.setEditable(false);

        pubBrowseButton.setText("Browse...");
        pubBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pubBrowseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout publicationsPanelLayout = new javax.swing.GroupLayout(publicationsPanel);
        publicationsPanel.setLayout(publicationsPanelLayout);
        publicationsPanelLayout.setHorizontalGroup(
            publicationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(publicationsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pubLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pubTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pubBrowseButton)
                .addContainerGap())
        );
        publicationsPanelLayout.setVerticalGroup(
            publicationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(publicationsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(publicationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pubLabel)
                    .addComponent(pubTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pubBrowseButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        topPanel.add(publicationsPanel, java.awt.BorderLayout.NORTH);

        javax.swing.GroupLayout htmlPanelLayout = new javax.swing.GroupLayout(htmlPanel);
        htmlPanel.setLayout(htmlPanelLayout);
        htmlPanelLayout.setHorizontalGroup(
            htmlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 678, Short.MAX_VALUE)
        );
        htmlPanelLayout.setVerticalGroup(
            htmlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 126, Short.MAX_VALUE)
        );

        settingsTabbedPane.addTab("HTML", null, htmlPanel, "");

        javax.swing.GroupLayout plainPanelLayout = new javax.swing.GroupLayout(plainPanel);
        plainPanel.setLayout(plainPanelLayout);
        plainPanelLayout.setHorizontalGroup(
            plainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 678, Short.MAX_VALUE)
        );
        plainPanelLayout.setVerticalGroup(
            plainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 126, Short.MAX_VALUE)
        );

        settingsTabbedPane.addTab("Text", plainPanel);

        topPanel.add(settingsTabbedPane, java.awt.BorderLayout.CENTER);

        mainSplitPane.setTopComponent(topPanel);

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
                .addContainerGap(229, Short.MAX_VALUE))
        );

        bottomPanel.add(buttonPanel, java.awt.BorderLayout.LINE_END);

        consoleTextArea.setEditable(false);
        consoleTextArea.setColumns(20);
        consoleTextArea.setLineWrap(true);
        consoleTextArea.setRows(5);
        consoleScrollPane.setViewportView(consoleTextArea);

        bottomPanel.add(consoleScrollPane, java.awt.BorderLayout.CENTER);

        mainSplitPane.setRightComponent(bottomPanel);

        getContentPane().add(mainSplitPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void generateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_generateButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void saveNQuitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveNQuitButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_saveNQuitButtonActionPerformed

    private void pubBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pubBrowseButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pubBrowseButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame(new Settings()).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollPane consoleScrollPane;
    private javax.swing.JTextArea consoleTextArea;
    private javax.swing.JButton generateButton;
    private javax.swing.JPanel htmlPanel;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JPanel plainPanel;
    private javax.swing.JButton pubBrowseButton;
    private javax.swing.JLabel pubLabel;
    private javax.swing.JTextField pubTextField;
    private javax.swing.JPanel publicationsPanel;
    private javax.swing.JButton saveNQuitButton;
    private javax.swing.JTabbedPane settingsTabbedPane;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
