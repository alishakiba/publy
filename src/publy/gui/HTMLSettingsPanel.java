/*
 */
package publy.gui;

import java.awt.Component;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import publy.data.PublicationType;
import publy.data.settings.HTMLSettings;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLSettingsPanel extends javax.swing.JPanel {

    private HTMLSettings settings;

    /**
     * Empty constructor, for use in the NetBeans GUI editor.
     */
    public HTMLSettingsPanel() {
        initComponents();
    }
    
    /**
     * Creates new form GeneralSettingsPanel
     */
    public HTMLSettingsPanel(HTMLSettings settings) {
        this.settings = settings;
        initComponents();
        populateValues();
    }
    
    private void populateValues() {
        // Links
        linkToTextCheckBox.setSelected(settings.linkToTextVersion());
        linkToBibtexCheckBox.setSelected(settings.linkToBibtexVersion());

        // Publication links
        abstractComboBox.setSelectedItem(settings.getIncludeAbstract());
        bibtexComboBox.setSelectedItem(settings.getIncludeBibtex());
        paperComboBox.setSelectedItem(settings.getIncludePaper());
        
        // Title link
        titleLinkComboBox.setSelectedItem(settings.getTitleTarget());

        // Google analytics
        String user = settings.getGoogleAnalyticsUser();
        analyticsUserTextField.setText(user);
        analyticsCheckBox.setSelected(user != null && !user.isEmpty());
        
        // PresentedText
        if (settings.getPresentedText() == null) {
            presentedTextField.setText("");
        } else {
            presentedTextField.setText(settings.getPresentedText());
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

        linkToTextLabel = new javax.swing.JLabel();
        linkToTextSeparator = new javax.swing.JSeparator();
        linkToTextCheckBox = new javax.swing.JCheckBox();
        linkToBibtexCheckBox = new javax.swing.JCheckBox();
        linksLabel = new javax.swing.JLabel();
        linksSeparator = new javax.swing.JSeparator();
        abstractLabel = new javax.swing.JLabel();
        abstractComboBox = new javax.swing.JComboBox();
        bibtexLabel = new javax.swing.JLabel();
        paperLabel = new javax.swing.JLabel();
        bibtexComboBox = new javax.swing.JComboBox();
        paperComboBox = new javax.swing.JComboBox();
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
        titleLinkComboBox = new javax.swing.JComboBox();

        linkToTextLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        linkToTextLabel.setForeground(new java.awt.Color(51, 51, 102));
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

        linksLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        linksLabel.setForeground(new java.awt.Color(51, 51, 102));
        linksLabel.setText("Per publication links");

        abstractLabel.setText("Include the abstract for:");

        abstractComboBox.setModel(new DefaultComboBoxModel(PublicationType.values()));
        abstractComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abstractComboBoxActionPerformed(evt);
            }
        });

        bibtexLabel.setText("Include the BibTeX for:");

        paperLabel.setText("Include the paper for:");

        bibtexComboBox.setModel(new DefaultComboBoxModel(Arrays.copyOfRange(PublicationType.values(), 0, 4)));
        bibtexComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bibtexComboBoxActionPerformed(evt);
            }
        });

        paperComboBox.setModel(new DefaultComboBoxModel(PublicationType.values()));
        paperComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paperComboBoxActionPerformed(evt);
            }
        });

        analyticsLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        analyticsLabel.setForeground(new java.awt.Color(51, 51, 102));
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

        presentedLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        presentedLabel.setForeground(new java.awt.Color(51, 51, 102));
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

        titleLinkLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        titleLinkLabel.setForeground(new java.awt.Color(51, 51, 102));
        titleLinkLabel.setText("Title link");

        titleLinkComboText.setText("Use the title as link for:");

        titleLinkComboBox.setModel(new DefaultComboBoxModel(HTMLSettings.TitleLinkTarget.values()));
        titleLinkComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                titleLinkComboBoxActionPerformed(evt);
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
                        .addComponent(linkToTextLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(linkToTextSeparator))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(linksLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(linksSeparator))
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
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                .addComponent(abstractLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(abstractComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(linkToTextCheckBox)
                            .addComponent(linkToBibtexCheckBox)
                            .addComponent(analyticsCheckBox)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(bibtexLabel)
                                    .addComponent(paperLabel))
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(bibtexComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(paperComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(linkToTextLabel)
                    .addComponent(linkToTextSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(linkToTextCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(linkToBibtexCheckBox)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(linksLabel)
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
        settings.setLinkToTextVersion(linkToTextCheckBox.isSelected());
    }//GEN-LAST:event_linkToTextCheckBoxActionPerformed

    private void abstractComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abstractComboBoxActionPerformed
        settings.setIncludeAbstract((PublicationType) abstractComboBox.getSelectedItem());
    }//GEN-LAST:event_abstractComboBoxActionPerformed

    private void bibtexComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bibtexComboBoxActionPerformed
        settings.setIncludeBibtex((PublicationType) bibtexComboBox.getSelectedItem());
    }//GEN-LAST:event_bibtexComboBoxActionPerformed

    private void paperComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paperComboBoxActionPerformed
        settings.setIncludePaper((PublicationType) paperComboBox.getSelectedItem());
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
        settings.setLinkToBibtexVersion(linkToBibtexCheckBox.isSelected());
    }//GEN-LAST:event_linkToBibtexCheckBoxActionPerformed

    private void titleLinkComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_titleLinkComboBoxActionPerformed
        settings.setTitleTarget((HTMLSettings.TitleLinkTarget) titleLinkComboBox.getSelectedItem());
    }//GEN-LAST:event_titleLinkComboBoxActionPerformed
    
    private void analyticsUserTextFieldTextChanged(javax.swing.event.DocumentEvent evt) {
        // Update the settings
        settings.setGoogleAnalyticsUser(analyticsUserTextField.getText());
    }
    
    private void presentedTextFieldTextChanged(javax.swing.event.DocumentEvent evt) {
        // Update the settings
        settings.setPresentedText(presentedTextField.getText());
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox abstractComboBox;
    private javax.swing.JLabel abstractLabel;
    private javax.swing.JCheckBox analyticsCheckBox;
    private javax.swing.JLabel analyticsLabel;
    private javax.swing.JSeparator analyticsSeparator;
    private javax.swing.JLabel analyticsUserLabel;
    private javax.swing.JTextField analyticsUserTextField;
    private javax.swing.JComboBox bibtexComboBox;
    private javax.swing.JLabel bibtexLabel;
    private javax.swing.JCheckBox linkToBibtexCheckBox;
    private javax.swing.JCheckBox linkToTextCheckBox;
    private javax.swing.JLabel linkToTextLabel;
    private javax.swing.JSeparator linkToTextSeparator;
    private javax.swing.JLabel linksLabel;
    private javax.swing.JSeparator linksSeparator;
    private javax.swing.JComboBox paperComboBox;
    private javax.swing.JLabel paperLabel;
    private javax.swing.JLabel presentedLabel;
    private javax.swing.JSeparator presentedSeparator;
    private javax.swing.JTextField presentedTextField;
    private javax.swing.JComboBox titleLinkComboBox;
    private javax.swing.JLabel titleLinkComboText;
    private javax.swing.JLabel titleLinkLabel;
    private javax.swing.JSeparator titleLinksSeparator;
    // End of variables declaration//GEN-END:variables
}
