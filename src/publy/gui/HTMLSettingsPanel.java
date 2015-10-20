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

import java.awt.Component;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import publy.Console;
import publy.data.PublicationStatus;
import publy.data.settings.HTMLSettings;
import publy.io.ResourceLocator;

/**
 *
 *
 */
public class HTMLSettingsPanel extends javax.swing.JPanel {

    private final HTMLSettings settings;

    /**
     * Empty constructor, for use in the NetBeans GUI editor.
     */
    public HTMLSettingsPanel() {
        settings = new HTMLSettings();
        initComponents();
        applyStyles();
        populateValues();
    }

    /**
     * Creates new form GeneralSettingsPanel
     *
     * @param settings
     */
    public HTMLSettingsPanel(HTMLSettings settings) {
        this.settings = settings;
        initComponents();
        applyStyles();
        populateValues();
    }

    private void applyStyles() {
        UIStyles.applyHeaderStyle(themeLabel, linkToTextLabel, navigationLabel, linksLabel, titleLinkLabel, presentedLabel, analyticsLabel);
    }

    private void populateValues() {
        // Theme
        themeComboBox.setSelectedItem(settings.getTheme());

        // Links
        linkToTextCheckBox.setSelected(settings.isGenerateTextVersion());
        linkToBibtexCheckBox.setSelected(settings.isGenerateBibtexVersion());
        insertLinksCheckBox.setSelected(settings.isLinkToAlternateVersions());
        insertLinksCheckBox.setEnabled(settings.isGenerateTextVersion() || settings.isGenerateBibtexVersion());

        // Navigation
        navigationComboBox.setSelectedItem(settings.getNavPlacement());

        // Publication links
        abstractComboBox.setSelectedItem(settings.getIncludeAbstract());
        bibtexComboBox.setSelectedItem(settings.getIncludeBibtex());
        paperComboBox.setSelectedItem(settings.getIncludePaper());

        // Title link
        titleLinkComboBox.setSelectedItem(settings.getTitleTarget());

        // Google analytics
        String user = settings.getGoogleAnalyticsUser();
        analyticsUserTextField.setText(user);
        analyticsUserTextField.setEnabled(user != null && !user.isEmpty());
        analyticsCheckBox.setSelected(user != null && !user.isEmpty());

        // PresentedText
        if (settings.getPresentedText() == null) {
            presentedTextField.setText("");
        } else {
            presentedTextField.setText(settings.getPresentedText());
        }
    }

    private Path[] discoverThemes() {
        final List<Path> paths = new ArrayList<>();

        try {
            Files.walkFileTree(ResourceLocator.getFullPath(HTMLSettings.THEME_DIRECTORY), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().endsWith(".css")) {
                        paths.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    // If we don't implement this, a failed visit terminates the search with an IOException
                    Console.warn(Console.WarningType.OTHER, "Could not examine potential theme \"%s\".", toRelativePath(file));
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException ex) {
            Console.except(ex, "Exception when searching for themes:");
            return new Path[0];
        }

        Collections.sort(paths, new Comparator<Path>() {

            @Override
            public int compare(Path o1, Path o2) {
                int diff = 0;

                // Find the first part of the path that differs
                while (diff < o1.getNameCount() && diff < o2.getNameCount() && o1.getName(diff).equals(o2.getName(diff))) {
                    diff++;
                }

                if (diff == o1.getNameCount()) {
                    return -1;
                }
                if (diff == o2.getNameCount()) {
                    return 1;
                }

                boolean isComposite1 = diff < o1.getNameCount() - 1;
                boolean isComposite2 = diff < o2.getNameCount() - 1;

                if (isComposite1 && !isComposite2) {
                    return 1;
                } else if (!isComposite1 && isComposite2) {
                    return -1;
                } else {
                    return o1.getName(diff).compareTo(o2.getName(diff));
                }
            }
        });

        return paths.toArray(new Path[paths.size()]);
    }

    private String toRelativePath(Path theme) {
        return ResourceLocator.getFullPath(HTMLSettings.THEME_DIRECTORY).relativize(theme).toString();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        linkToTextLabel = new javax.swing.JLabel();
        linkToTextSeparator = new javax.swing.JSeparator();
        linkToTextCheckBox = new javax.swing.JCheckBox();
        linkToBibtexCheckBox = new javax.swing.JCheckBox();
        linksLabel = new javax.swing.JLabel();
        linksSeparator = new javax.swing.JSeparator();
        abstractLabel = new javax.swing.JLabel();
        abstractComboBox = new javax.swing.JComboBox<>();
        bibtexLabel = new javax.swing.JLabel();
        paperLabel = new javax.swing.JLabel();
        bibtexComboBox = new javax.swing.JComboBox<>();
        paperComboBox = new javax.swing.JComboBox<>();
        analyticsLabel = new javax.swing.JLabel();
        analyticsSeparator = new javax.swing.JSeparator();
        analyticsCheckBox = new javax.swing.JCheckBox();
        analyticsUserLabel = new javax.swing.JLabel();
        analyticsUserTextField = new javax.swing.JTextField();
        presentedLabel = new javax.swing.JLabel();
        presentedSeparator = new javax.swing.JSeparator();
        presentedTextField = new javax.swing.JTextField();
        titleLinkLabel = new javax.swing.JLabel();
        titleLinksSeparator = new javax.swing.JSeparator();
        titleLinkComboText = new javax.swing.JLabel();
        titleLinkComboBox = new javax.swing.JComboBox<>();
        insertLinksCheckBox = new javax.swing.JCheckBox();
        navigationLabel = new javax.swing.JLabel();
        navigationSeparator = new javax.swing.JSeparator();
        navigationComboLabel = new javax.swing.JLabel();
        navigationComboBox = new javax.swing.JComboBox<>();
        themeLabel = new javax.swing.JLabel();
        themeSeparator = new javax.swing.JSeparator();
        themeComboLabel = new javax.swing.JLabel();
        themeComboBox = new javax.swing.JComboBox<>();

        linkToTextLabel.setText("Alternative versions");

        linkToTextCheckBox.setText("Generate a plain text version");
        linkToTextCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkToTextCheckBoxActionPerformed(evt);
            }
        });

        linkToBibtexCheckBox.setText("Generate a BibTeX version");
        linkToBibtexCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkToBibtexCheckBoxActionPerformed(evt);
            }
        });

        linksLabel.setText("Per publication links");

        abstractLabel.setText("Include the abstract for:");

        abstractComboBox.setModel(new DefaultComboBoxModel<>(publy.data.PublicationStatus.values()));
        abstractComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abstractComboBoxActionPerformed(evt);
            }
        });

        bibtexLabel.setText("Include the BibTeX for:");

        paperLabel.setText("Include the paper for:");

        bibtexComboBox.setModel(new DefaultComboBoxModel<>(Arrays.copyOfRange(publy.data.PublicationStatus.values(), 0, 4)));
        bibtexComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bibtexComboBoxActionPerformed(evt);
            }
        });

        paperComboBox.setModel(new DefaultComboBoxModel<>(publy.data.PublicationStatus.values()));
        paperComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paperComboBoxActionPerformed(evt);
            }
        });

        analyticsLabel.setText("Google analytics");

        analyticsCheckBox.setText("Include analytics code");
        analyticsCheckBox.setToolTipText("This allows you to monitor your site usage through Google Analytics. To use this feature, you need a valid Google Analytics account.");
        analyticsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analyticsCheckBoxActionPerformed(evt);
            }
        });

        analyticsUserLabel.setText("Account identifier:");

        analyticsUserTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                analyticsUserTextFieldTextChanged(e);
            }
            public void removeUpdate(DocumentEvent e) {
                analyticsUserTextFieldTextChanged(e);
            }
            public void changedUpdate(DocumentEvent e) {
                //Plain text components do not fire these events
            }
        });
        analyticsUserTextField.setColumns(25);
        analyticsUserTextField.setEnabled(false);

        presentedLabel.setText("Text added after presented papers");

        presentedTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                presentedTextFieldTextChanged(e);
            }
            public void removeUpdate(DocumentEvent e) {
                presentedTextFieldTextChanged(e);
            }
            public void changedUpdate(DocumentEvent e) {
                //Plain text components do not fire these events
            }
        });
        presentedTextField.setColumns(40);

        titleLinkLabel.setText("Title link");

        titleLinkComboText.setText("Use the title as link for:");

        titleLinkComboBox.setModel(new DefaultComboBoxModel<>(HTMLSettings.TitleLinkTarget.values()));
        titleLinkComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                titleLinkComboBoxActionPerformed(evt);
            }
        });

        insertLinksCheckBox.setText("Insert links to these versions");
        insertLinksCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertLinksCheckBoxActionPerformed(evt);
            }
        });

        navigationLabel.setText("Navigation links");

        navigationComboLabel.setText("Include navigation:");

        navigationComboBox.setModel(new DefaultComboBoxModel<>(HTMLSettings.NavigationPlacement.values()));
        navigationComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                navigationComboBoxActionPerformed(evt);
            }
        });

        themeLabel.setText("Theme");

        themeComboLabel.setText("Theme:");

        themeComboBox.setModel(new DefaultComboBoxModel<>(discoverThemes()));
        themeComboBox.setRenderer(new ThemeNameRenderer());
        themeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                themeComboBoxActionPerformed(evt);
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
                        .addComponent(themeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(themeSeparator))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(linkToTextLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(linkToTextSeparator))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(titleLinkLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(titleLinksSeparator))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(presentedLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(presentedSeparator))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(analyticsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(analyticsSeparator))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(linksLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(linksSeparator))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(navigationLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(navigationSeparator))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(themeComboLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(themeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(titleLinkComboText)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(titleLinkComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(presentedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(analyticsUserLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(analyticsUserTextField))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(bibtexLabel)
                                    .addComponent(paperLabel))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(bibtexComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(paperComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(insertLinksCheckBox)
                                    .addComponent(linkToTextCheckBox)
                                    .addComponent(linkToBibtexCheckBox)
                                    .addComponent(analyticsCheckBox))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(abstractLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(abstractComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(navigationComboLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(navigationComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(themeLabel)
                    .addComponent(themeSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(themeComboLabel)
                    .addComponent(themeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(linkToTextLabel)
                    .addComponent(linkToTextSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(linkToTextCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(linkToBibtexCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(insertLinksCheckBox)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(navigationLabel)
                            .addComponent(navigationSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(navigationComboLabel)
                            .addComponent(navigationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(linksLabel))
                    .addComponent(linksSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(abstractLabel)
                    .addComponent(abstractComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bibtexLabel)
                    .addComponent(bibtexComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(paperLabel)
                    .addComponent(paperComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(titleLinkLabel)
                    .addComponent(titleLinksSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titleLinkComboText)
                    .addComponent(titleLinkComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(presentedLabel)
                    .addComponent(presentedSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(presentedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(analyticsLabel)
                    .addComponent(analyticsSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(analyticsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(analyticsUserLabel)
                    .addComponent(analyticsUserTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void linkToTextCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkToTextCheckBoxActionPerformed
        settings.setGenerateTextVersion(linkToTextCheckBox.isSelected());
        insertLinksCheckBox.setEnabled(settings.isGenerateTextVersion() || settings.isGenerateBibtexVersion());
    }//GEN-LAST:event_linkToTextCheckBoxActionPerformed

    private void abstractComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abstractComboBoxActionPerformed
        settings.setIncludeAbstract((PublicationStatus) abstractComboBox.getSelectedItem());
    }//GEN-LAST:event_abstractComboBoxActionPerformed

    private void bibtexComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bibtexComboBoxActionPerformed
        settings.setIncludeBibtex((PublicationStatus) bibtexComboBox.getSelectedItem());
    }//GEN-LAST:event_bibtexComboBoxActionPerformed

    private void paperComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paperComboBoxActionPerformed
        settings.setIncludePaper((PublicationStatus) paperComboBox.getSelectedItem());
    }//GEN-LAST:event_paperComboBoxActionPerformed

    private void analyticsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analyticsCheckBoxActionPerformed
        if (analyticsCheckBox.isSelected()) {
            // Update settings
            settings.setGoogleAnalyticsUser(analyticsUserTextField.getText());

            // Update UI
            analyticsUserTextField.setEnabled(true);
        } else {
            // Update settings
            settings.setGoogleAnalyticsUser(null);

            // Update UI
            analyticsUserTextField.setEnabled(false);
        }
    }//GEN-LAST:event_analyticsCheckBoxActionPerformed

    private void linkToBibtexCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkToBibtexCheckBoxActionPerformed
        settings.setGenerateBibtexVersion(linkToBibtexCheckBox.isSelected());
        insertLinksCheckBox.setEnabled(settings.isGenerateTextVersion() || settings.isGenerateBibtexVersion());
    }//GEN-LAST:event_linkToBibtexCheckBoxActionPerformed

    private void titleLinkComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_titleLinkComboBoxActionPerformed
        settings.setTitleTarget((HTMLSettings.TitleLinkTarget) titleLinkComboBox.getSelectedItem());
    }//GEN-LAST:event_titleLinkComboBoxActionPerformed

    private void insertLinksCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertLinksCheckBoxActionPerformed
        settings.setLinkToAlternateVersions(insertLinksCheckBox.isSelected());
    }//GEN-LAST:event_insertLinksCheckBoxActionPerformed

    private void navigationComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_navigationComboBoxActionPerformed
        settings.setNavPlacement((HTMLSettings.NavigationPlacement) navigationComboBox.getSelectedItem());
    }//GEN-LAST:event_navigationComboBoxActionPerformed

    private void themeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_themeComboBoxActionPerformed
        settings.setTheme((Path) themeComboBox.getSelectedItem());
    }//GEN-LAST:event_themeComboBoxActionPerformed

    private void analyticsUserTextFieldTextChanged(javax.swing.event.DocumentEvent evt) {
        // Update the settings
        settings.setGoogleAnalyticsUser(analyticsUserTextField.getText());
    }

    private void presentedTextFieldTextChanged(javax.swing.event.DocumentEvent evt) {
        // Update the settings
        settings.setPresentedText(presentedTextField.getText());
    }

    private class ThemeNameRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); // Correctly sets the appearance of this instance (DefaultListCellRenderer extends JLabel)

            String relativePath = toRelativePath((Path) value);
            relativePath = relativePath.substring(0, relativePath.length() - ".css".length());
            setText(relativePath);

            return this;
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<publy.data.PublicationStatus> abstractComboBox;
    private javax.swing.JLabel abstractLabel;
    private javax.swing.JCheckBox analyticsCheckBox;
    private javax.swing.JLabel analyticsLabel;
    private javax.swing.JSeparator analyticsSeparator;
    private javax.swing.JLabel analyticsUserLabel;
    private javax.swing.JTextField analyticsUserTextField;
    private javax.swing.JComboBox<publy.data.PublicationStatus> bibtexComboBox;
    private javax.swing.JLabel bibtexLabel;
    private javax.swing.JCheckBox insertLinksCheckBox;
    private javax.swing.JCheckBox linkToBibtexCheckBox;
    private javax.swing.JCheckBox linkToTextCheckBox;
    private javax.swing.JLabel linkToTextLabel;
    private javax.swing.JSeparator linkToTextSeparator;
    private javax.swing.JLabel linksLabel;
    private javax.swing.JSeparator linksSeparator;
    private javax.swing.JComboBox<HTMLSettings.NavigationPlacement> navigationComboBox;
    private javax.swing.JLabel navigationComboLabel;
    private javax.swing.JLabel navigationLabel;
    private javax.swing.JSeparator navigationSeparator;
    private javax.swing.JComboBox<publy.data.PublicationStatus> paperComboBox;
    private javax.swing.JLabel paperLabel;
    private javax.swing.JLabel presentedLabel;
    private javax.swing.JSeparator presentedSeparator;
    private javax.swing.JTextField presentedTextField;
    private javax.swing.JComboBox<Path> themeComboBox;
    private javax.swing.JLabel themeComboLabel;
    private javax.swing.JLabel themeLabel;
    private javax.swing.JSeparator themeSeparator;
    private javax.swing.JComboBox<HTMLSettings.TitleLinkTarget> titleLinkComboBox;
    private javax.swing.JLabel titleLinkComboText;
    private javax.swing.JLabel titleLinkLabel;
    private javax.swing.JSeparator titleLinksSeparator;
    // End of variables declaration//GEN-END:variables
}
