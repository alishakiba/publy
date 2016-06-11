/*
 * Copyright 2013-2016 Sander Verdonschot <sander.verdonschot at gmail.com>.
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

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import publy.Runner;
import publy.data.settings.FileSettings;
import publy.io.ResourceLocator;

/**
 *
 *
 */
public class FileSettingsPanel extends javax.swing.JPanel {

    private final FileSettings settings;
    private FileDialog pubFileChooserMac;
    private FileDialog htmlFileChooserMac; // OS X doesn't save the current directory per file chooser, so there's no point duplicating these.

    /**
     * Empty constructor, for use in the NetBeans GUI editor.
     */
    public FileSettingsPanel() {
        // Don't initialize settings, as NetBeans will error on ResourceLocator
        settings = null;
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

        // Set the correct file filters
        if (Runner.isMacOS()) { // The OS X Java guidelines reccommend using FileDialog instead of JFileChooser
            pubFileChooserMac = new FileDialog((Frame) null);
            htmlFileChooserMac = new FileDialog((Frame) null);

            FilenameFilter bibFilter = new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".bib");
                }
            };
            FilenameFilter htmlFilter = new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    String lowercaseName = name.toLowerCase();
                    return lowercaseName.endsWith(".html") || lowercaseName.endsWith(".htm");
                }
            };

            pubFileChooserMac.setFilenameFilter(bibFilter);
            htmlFileChooserMac.setFilenameFilter(htmlFilter);
        } else {
            FileFilter bibFilter = new FileNameExtensionFilter("BibTeX files (*.bib)", "bib");
            FileFilter htmlFilter = new FileNameExtensionFilter("HTML files (*.htm;*.html)", "htm", "html");

            pubFileChooser.setFileFilter(bibFilter);
            targetFileChooser.setFileFilter(htmlFilter);
            headerFileChooser.setFileFilter(htmlFilter);
            footerFileChooser.setFileFilter(htmlFilter);
        }
    }

    private void applyStyles() {
        UIStyles.applyHeaderStyle(pubLabel, targetLabel, headerLabel);
    }

    private void populateValues() {
        // Publications
        updateField(pubTextField, pubFileChooser, settings.getPublications(), true);

        // Target
        updateField(targetTextField, targetFileChooser, settings.getTarget(), true);
        openOutputCheckBox.setSelected(settings.isOpenOutput());

        // Header and Footer
        updateField(headerTextField, headerFileChooser, settings.getHeader(), false);
        updateField(footerTextField, footerFileChooser, settings.getFooter(), false);
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
                textField.setBackground(UIStyles.TEXTFIELD_ERROR_COLOR);
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
        openOutputCheckBox = new javax.swing.JCheckBox();

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

        openOutputCheckBox.setText("Open output file after generating");
        openOutputCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                openOutputCheckBoxItemStateChanged(evt);
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(targetLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(targetSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(pubLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pubSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(openOutputCheckBox)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(pubTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(pubBrowseButton))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(targetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(targetBrowseButton)))))
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(headerLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(headerSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(footerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(footerBrowseButton))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(headerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(headerBrowseButton)))))
                        .addContainerGap())))
        );
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
                    .addComponent(pubBrowseButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(targetLabel)
                    .addComponent(targetSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(targetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(targetBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(openOutputCheckBox)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(headerSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(headerLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(headerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(headerBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(footerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(footerBrowseButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pubBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pubBrowseButtonActionPerformed
        Path selected = selectFile(pubFileChooser, pubFileChooserMac);

        if (selected != null) {
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
            pubTextField.setBackground(UIStyles.TEXTFIELD_ERROR_COLOR);
        } else {
            pubTextField.setBackground(UIManager.getColor("TextField.background"));
        }
    }

    private void targetTextFieldTextChanged(javax.swing.event.DocumentEvent evt) {
        // Update the settings
        settings.setTarget(ResourceLocator.getFullPath(targetTextField.getText()));

        // Remove the error background?
        if (targetTextField.getText().isEmpty()) {
            targetTextField.setBackground(UIStyles.TEXTFIELD_ERROR_COLOR);
        } else {
            targetTextField.setBackground(UIManager.getColor("TextField.background"));
        }
    }

    private void headerTextFieldTextChanged(javax.swing.event.DocumentEvent evt) {
        // Update the settings
        settings.setHeader(ResourceLocator.getFullPath(headerTextField.getText()));
    }

    private void footerTextFieldTextChanged(javax.swing.event.DocumentEvent evt) {
        // Update the settings
        settings.setFooter(ResourceLocator.getFullPath(footerTextField.getText()));
    }

    private void targetBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_targetBrowseButtonActionPerformed
        Path selected = selectFile(targetFileChooser, htmlFileChooserMac);

        if (selected != null) {
            targetTextField.setText(ResourceLocator.getRelativePath(selected));
        }
    }//GEN-LAST:event_targetBrowseButtonActionPerformed

    private void footerBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_footerBrowseButtonActionPerformed
        Path selected = selectFile(footerFileChooser, htmlFileChooserMac);

        if (selected != null) {
            footerTextField.setText(ResourceLocator.getRelativePath(selected));
        }
    }//GEN-LAST:event_footerBrowseButtonActionPerformed

    private void headerBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_headerBrowseButtonActionPerformed
        Path selected = selectFile(headerFileChooser, htmlFileChooserMac);

        if (selected != null) {
            headerTextField.setText(ResourceLocator.getRelativePath(selected));
        }
    }//GEN-LAST:event_headerBrowseButtonActionPerformed

    private void openOutputCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_openOutputCheckBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
            settings.setOpenOutput(false);
        } else if (evt.getStateChange() == ItemEvent.SELECTED) {
            settings.setOpenOutput(true);
        }
    }//GEN-LAST:event_openOutputCheckBoxItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton footerBrowseButton;
    private javax.swing.JFileChooser footerFileChooser;
    private javax.swing.JTextField footerTextField;
    private javax.swing.JButton headerBrowseButton;
    private javax.swing.JFileChooser headerFileChooser;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JSeparator headerSeparator;
    private javax.swing.JTextField headerTextField;
    private javax.swing.JCheckBox openOutputCheckBox;
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
    // End of variables declaration//GEN-END:variables

    private Path selectFile(JFileChooser fileChooser, FileDialog fileChooserMac) {
        if (Runner.isMacOS()) {
            fileChooserMac.setVisible(true);
            File[] selectedFiles = fileChooserMac.getFiles();

            if (selectedFiles != null && selectedFiles.length > 0) {
                return selectedFiles[0].toPath();
            }
        } else {
            int opened = fileChooser.showOpenDialog(this);

            if (opened == JFileChooser.APPROVE_OPTION) {
                return fileChooser.getSelectedFile().toPath();
            }
        }

        return null;
    }
}
