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
package publy.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import publy.Console;
import publy.data.settings.FileSettings;
import publy.io.ResourceLocator;

/**
 *
 *
 */
public class FileSettingsPanel extends javax.swing.JPanel {

    private final FileSettings settings;
    private final Desktop desktop;

    /**
     * Empty constructor, for use in the NetBeans GUI editor.
     */
    public FileSettingsPanel() {
        // Don't initialize settings, as NetBeans will error on ResourceLocator
        settings = null;
        desktop = null;
        initComponents();
        applyStyles();
    }

    /**
     * Creates new form FileSettingsPanel
     *
     * @param settings
     */
    public FileSettingsPanel(FileSettings settings) {
        this.settings = settings;
        initComponents();
        applyStyles();
        populateValues();

        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            enableDesktopButtons();
        } else {
            desktop = null;
        }

        // Set the correct file filters
        FileFilter bibFilter = new FileNameExtensionFilter("BibTeX files (*.bib)", "bib");
        FileFilter htmlFilter = new FileNameExtensionFilter("HTML files (*.htm;*.html)", "htm", "html");

        pubFileChooser.setFileFilter(bibFilter);
        targetFileChooser.setFileFilter(htmlFilter);
        headerFileChooser.setFileFilter(htmlFilter);
        footerFileChooser.setFileFilter(htmlFilter);
    }

    private void applyStyles() {
        UIStyles.applyHeaderStyle(pubLabel, targetLabel, headerLabel);
    }

    private void populateValues() {
        // Publications
        updateField(pubTextField, pubFileChooser, settings.getPublications(), true);

        // Target
        updateField(targetTextField, targetFileChooser, settings.getTarget(), true);

        // Header and Footer
        updateField(headerTextField, headerFileChooser, settings.getHeader(), false);
        updateField(footerTextField, footerFileChooser, settings.getFooter(), false);
    }

    private void enableDesktopButtons() {
        if (desktop.isSupported(Desktop.Action.BROWSE) || desktop.isSupported(Desktop.Action.OPEN)) {
            viewTargetButton.setEnabled(!targetTextField.getText().isEmpty());
        } else {
            viewTargetButton.setToolTipText("This action is not supported by your operating system");
        }

        if (desktop.isSupported(Desktop.Action.EDIT) || desktop.isSupported(Desktop.Action.OPEN)) {
            editPubButton.setEnabled(!pubTextField.getText().isEmpty());
        } else {
            editPubButton.setToolTipText("This action is not supported by your operating system");
        }

        if (desktop.isSupported(Desktop.Action.EDIT)) {
            editHeaderButton.setEnabled(!headerTextField.getText().isEmpty());
            editFooterButton.setEnabled(!footerTextField.getText().isEmpty());
        } else {
            editHeaderButton.setToolTipText("This action is not supported by your operating system");
            editFooterButton.setToolTipText("This action is not supported by your operating system");
        }
    }

    /**
     * Updates the property with the associated text field and file chooser to
     * reflect the new value.
     */
    private void updateField(JTextField textField, JFileChooser fileChooser, Path newValue, boolean errorWhenEmpty) {
        if (newValue == null) {
            textField.setText("");
            fileChooser.setCurrentDirectory(ResourceLocator.getBaseDirectory().toFile());

            if (errorWhenEmpty) {
                textField.setBackground(UIConstants.TEXTFIELD_ERROR_COLOR);
            }
        } else {
            textField.setText(ResourceLocator.getRelativePath(newValue));
            fileChooser.setCurrentDirectory(newValue.getParent().toFile());
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

        pubFileChooser = new javax.swing.JFileChooser();
        targetFileChooser = new javax.swing.JFileChooser();
        headerFileChooser = new javax.swing.JFileChooser();
        footerFileChooser = new javax.swing.JFileChooser();
        pubLabel = new javax.swing.JLabel();
        pubTextField = new javax.swing.JTextField();
        pubBrowseButton = new javax.swing.JButton();
        pubSeparator = new javax.swing.JSeparator();
        targetSeparator = new javax.swing.JSeparator();
        targetLabel = new javax.swing.JLabel();
        targetTextField = new javax.swing.JTextField();
        targetBrowseButton = new javax.swing.JButton();
        headerLabel = new javax.swing.JLabel();
        headerSeparator = new javax.swing.JSeparator();
        headerTextField = new javax.swing.JTextField();
        footerTextField = new javax.swing.JTextField();
        footerBrowseButton = new javax.swing.JButton();
        headerBrowseButton = new javax.swing.JButton();
        viewTargetButton = new javax.swing.JButton();
        editPubButton = new javax.swing.JButton();
        editHeaderButton = new javax.swing.JButton();
        editFooterButton = new javax.swing.JButton();

        pubLabel.setText("List of publications");

        pubTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                pubTextFieldTextChanged(e);
            }
            public void removeUpdate(DocumentEvent e) {
                pubTextFieldTextChanged(e);
            }
            public void changedUpdate(DocumentEvent e) {
                //Plain text components do not fire these events
            }
        });
        pubTextField.setColumns(50);

        pubBrowseButton.setText("Browse...");
        pubBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pubBrowseButtonActionPerformed(evt);
            }
        });

        pubSeparator.setPreferredSize(new java.awt.Dimension(0, 5));

        targetSeparator.setPreferredSize(new java.awt.Dimension(0, 5));

        targetLabel.setText("Output file");

        targetTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                targetTextFieldTextChanged(e);
            }
            public void removeUpdate(DocumentEvent e) {
                targetTextFieldTextChanged(e);
            }
            public void changedUpdate(DocumentEvent e) {
                //Plain text components do not fire these events
            }
        });
        targetTextField.setColumns(30);
        targetTextField.setToolTipText("This file will be created or overridden with a list of your publications.");

        targetBrowseButton.setText("Browse...");
        targetBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetBrowseButtonActionPerformed(evt);
            }
        });

        headerLabel.setText("Header & Footer");

        headerSeparator.setPreferredSize(new java.awt.Dimension(0, 5));

        headerTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                headerTextFieldTextChanged(e);
            }
            public void removeUpdate(DocumentEvent e) {
                headerTextFieldTextChanged(e);
            }
            public void changedUpdate(DocumentEvent e) {
                //Plain text components do not fire these events
            }
        });
        headerTextField.setColumns(29);
        headerTextField.setToolTipText("The contents of this file will be copied to your publication list before the publications themselves are listed. See the provided default header for an example.");

        footerTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                footerTextFieldTextChanged(e);
            }
            public void removeUpdate(DocumentEvent e) {
                footerTextFieldTextChanged(e);
            }
            public void changedUpdate(DocumentEvent e) {
                //Plain text components do not fire these events
            }
        });
        footerTextField.setColumns(29);
        footerTextField.setToolTipText("The contents of this file will be copied to your publication list after the publications themselves are listed. See the provided default footer for an example.");

        footerBrowseButton.setText("Browse...");
        footerBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                footerBrowseButtonActionPerformed(evt);
            }
        });

        headerBrowseButton.setText("Browse...");
        headerBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                headerBrowseButtonActionPerformed(evt);
            }
        });

        viewTargetButton.setText("View");
        viewTargetButton.setEnabled(false);
        viewTargetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewTargetButtonActionPerformed(evt);
            }
        });

        editPubButton.setText("Edit");
        editPubButton.setEnabled(false);
        editPubButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPubButtonActionPerformed(evt);
            }
        });

        editHeaderButton.setText("Edit");
        editHeaderButton.setEnabled(false);
        editHeaderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editHeaderButtonActionPerformed(evt);
            }
        });

        editFooterButton.setText("Edit");
        editFooterButton.setEnabled(false);
        editFooterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editFooterButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(pubTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pubBrowseButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(targetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(targetBrowseButton))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(footerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(footerBrowseButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(headerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(headerBrowseButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(viewTargetButton)
                            .addComponent(editPubButton)
                            .addComponent(editHeaderButton)
                            .addComponent(editFooterButton)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pubLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pubSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(targetLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(targetSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(headerLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(headerSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {editFooterButton, editHeaderButton, editPubButton, viewTargetButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pubLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(pubSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pubTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pubBrowseButton)
                    .addComponent(editPubButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(targetLabel)
                    .addComponent(targetSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(targetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(targetBrowseButton)
                    .addComponent(viewTargetButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(headerSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(headerLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(headerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(headerBrowseButton)
                    .addComponent(editHeaderButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(footerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(footerBrowseButton)
                    .addComponent(editFooterButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pubBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pubBrowseButtonActionPerformed
        int opened = pubFileChooser.showOpenDialog(this);

        if (opened == JFileChooser.APPROVE_OPTION) {
            Path selected = pubFileChooser.getSelectedFile().toPath();
            pubTextField.setText(ResourceLocator.getRelativePath(selected));
            settings.setPublications(selected);

            // Auto-fill the target field
            if (settings.getTarget() == null) {
                // Extract the base name
                String baseName = pubFileChooser.getSelectedFile().getName();
                int extension = baseName.lastIndexOf('.');

                if (extension > -1) {
                    baseName = baseName.substring(0, extension);
                }

                // Set an initial target
                settings.setTarget(selected.resolveSibling(baseName + ".html"));

                // Update the GUI
                updateField(targetTextField, targetFileChooser, settings.getTarget(), true);
            }
        }
    }//GEN-LAST:event_pubBrowseButtonActionPerformed

    private void pubTextFieldTextChanged(javax.swing.event.DocumentEvent evt) {
        // Update the settings
        settings.setPublications(ResourceLocator.getFullPath(pubTextField.getText()));

        // Remove the error background?
        if (pubTextField.getText().isEmpty()) {
            pubTextField.setBackground(UIConstants.TEXTFIELD_ERROR_COLOR);
        } else {
            pubTextField.setBackground(UIManager.getColor("TextField.background"));
        }

        // Update edit button enabled state?
        if (desktop != null && (desktop.isSupported(Desktop.Action.EDIT) || desktop.isSupported(Desktop.Action.OPEN))) {
            editPubButton.setEnabled(!pubTextField.getText().isEmpty());
        }
    }

    private void targetTextFieldTextChanged(javax.swing.event.DocumentEvent evt) {
        // Update the settings
        settings.setTarget(ResourceLocator.getFullPath(targetTextField.getText()));

        // Remove the error background?
        if (targetTextField.getText().isEmpty()) {
            targetTextField.setBackground(UIConstants.TEXTFIELD_ERROR_COLOR);
        } else {
            targetTextField.setBackground(UIManager.getColor("TextField.background"));
        }

        // Update view button enabled state?
        if (desktop != null && (desktop.isSupported(Desktop.Action.BROWSE) || desktop.isSupported(Desktop.Action.OPEN))) {
            viewTargetButton.setEnabled(!targetTextField.getText().isEmpty());
        }
    }

    private void headerTextFieldTextChanged(javax.swing.event.DocumentEvent evt) {
        // Update the settings
        settings.setHeader(ResourceLocator.getFullPath(headerTextField.getText()));

        // Update edit button enabled state?
        if (desktop != null && desktop.isSupported(Desktop.Action.EDIT)) {
            editHeaderButton.setEnabled(!headerTextField.getText().isEmpty());
        }
    }

    private void footerTextFieldTextChanged(javax.swing.event.DocumentEvent evt) {
        // Update the settings
        settings.setFooter(ResourceLocator.getFullPath(footerTextField.getText()));

        // Update edit button enabled state?
        if (desktop != null && desktop.isSupported(Desktop.Action.EDIT)) {
            editFooterButton.setEnabled(!footerTextField.getText().isEmpty());
        }
    }

    private void targetBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_targetBrowseButtonActionPerformed
        int opened = targetFileChooser.showOpenDialog(this);

        if (opened == JFileChooser.APPROVE_OPTION) {
            targetTextField.setText(ResourceLocator.getRelativePath(targetFileChooser.getSelectedFile().toPath()));
        }
    }//GEN-LAST:event_targetBrowseButtonActionPerformed

    private void footerBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_footerBrowseButtonActionPerformed
        int opened = footerFileChooser.showOpenDialog(this);

        if (opened == JFileChooser.APPROVE_OPTION) {
            footerTextField.setText(ResourceLocator.getRelativePath(footerFileChooser.getSelectedFile().toPath()));
        }
    }//GEN-LAST:event_footerBrowseButtonActionPerformed

    private void headerBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_headerBrowseButtonActionPerformed
        int opened = headerFileChooser.showOpenDialog(this);

        if (opened == JFileChooser.APPROVE_OPTION) {
            headerTextField.setText(ResourceLocator.getRelativePath(headerFileChooser.getSelectedFile().toPath()));
        }
    }//GEN-LAST:event_headerBrowseButtonActionPerformed

    private void viewTargetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewTargetButtonActionPerformed
        if (desktop == null) {
            return;
        }

        if (!Files.exists(settings.getTarget())) {
            JOptionPane.showMessageDialog(this, "This file does not exist yet.", "Publy - Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(settings.getTarget().toUri());
            } else if (desktop.isSupported(Desktop.Action.OPEN)) {
                desktop.open(settings.getTarget().toFile());
            }
        } catch (IOException ex) {
            Console.except(ex, "The website could not be opened.");
        }
    }//GEN-LAST:event_viewTargetButtonActionPerformed

    private void editPubButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPubButtonActionPerformed
        edit(settings.getPublications(), "publications list");
    }//GEN-LAST:event_editPubButtonActionPerformed

    private void editHeaderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editHeaderButtonActionPerformed
        edit(settings.getHeader(), "header");
    }//GEN-LAST:event_editHeaderButtonActionPerformed

    private void editFooterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editFooterButtonActionPerformed
        edit(settings.getFooter(), "footer");
    }//GEN-LAST:event_editFooterButtonActionPerformed

    private void edit(Path file, String name) {
        if (desktop == null) {
            return;
        }

        if (!Files.exists(file)) {
            JOptionPane.showMessageDialog(this, "This file could not be found.", "Publy - Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (desktop.isSupported(Desktop.Action.EDIT)) {
                desktop.edit(file.toFile());
            } else if (desktop.isSupported(Desktop.Action.OPEN)) {
                desktop.open(file.toFile());
            }
        } catch (IOException ex) {
            Console.except(ex, "The %s could not be opened.", name);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton editFooterButton;
    private javax.swing.JButton editHeaderButton;
    private javax.swing.JButton editPubButton;
    private javax.swing.JButton footerBrowseButton;
    private javax.swing.JFileChooser footerFileChooser;
    private javax.swing.JTextField footerTextField;
    private javax.swing.JButton headerBrowseButton;
    private javax.swing.JFileChooser headerFileChooser;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JSeparator headerSeparator;
    private javax.swing.JTextField headerTextField;
    private javax.swing.JButton pubBrowseButton;
    private javax.swing.JFileChooser pubFileChooser;
    private javax.swing.JLabel pubLabel;
    private javax.swing.JSeparator pubSeparator;
    private javax.swing.JTextField pubTextField;
    private javax.swing.JButton targetBrowseButton;
    private javax.swing.JFileChooser targetFileChooser;
    private javax.swing.JLabel targetLabel;
    private javax.swing.JSeparator targetSeparator;
    private javax.swing.JTextField targetTextField;
    private javax.swing.JButton viewTargetButton;
    // End of variables declaration//GEN-END:variables
}
