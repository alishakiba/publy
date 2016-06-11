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

import publy.Constants;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import publy.data.bibitem.BibItem;
import publy.data.category.OutputCategory;
import publy.data.category.conditions.FieldCondition;
import publy.data.category.conditions.FieldContainsCondition;
import publy.data.category.conditions.FieldEqualsCondition;
import publy.data.category.conditions.FieldExistsCondition;
import publy.data.category.conditions.TypeCondition;
import publy.data.settings.Settings;
import publy.io.bibtexparser.PublicationListParser;

/**
 *
 *
 */
public class ConditionsDialog extends javax.swing.JDialog {

    private static final Map<TextAttribute, Object> strikeThroughAttribute;
    private static final Map<TextAttribute, Object> notStrikeThroughAttribute;
    
    static {
        strikeThroughAttribute = new HashMap<>();
        strikeThroughAttribute.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        
        notStrikeThroughAttribute = new HashMap<>();
        notStrikeThroughAttribute.put(TextAttribute.STRIKETHROUGH, false);
    }
    
    private final OutputCategory category;
    private final TypeCondition typeCondition;

    /**
     * Creates new form ConditionsDialog
     * @param parent
     * @param category
     */
    public ConditionsDialog(java.awt.Frame parent, OutputCategory category) {
        super(parent, true);

        this.category = category;
        typeCondition = new TypeCondition(category.getTypeCondition());

        initComponents();
        applyStyles();
        populateValues();
        setLocationRelativeTo(parent);
    }

    private void applyStyles() {
        UIStyles.applyHeaderStyle(typeHeader, testHeader);
        UIStyles.applyHeaderStyle((TitledBorder) fieldConditionsScrollPane.getBorder());
    }

    private void populateValues() {
        // Type condition
        if (typeCondition.isInverted()) {
            typeInvertCheckBox.setSelected(true);
            typeLabel.setText(typeLabel.getText().substring(0, 1).toLowerCase() + typeLabel.getText().substring(1));
        } else {
            typeInvertCheckBox.setSelected(false);
            typeInvertCheckBox.setFont(typeInvertCheckBox.getFont().deriveFont(strikeThroughAttribute));
            typeLabel.setText(typeLabel.getText().substring(0, 1).toUpperCase() + typeLabel.getText().substring(1));
        }

        typeTextField.setText(UIStyles.convertToDisplayString(typeCondition.getTypes()));

        // Field conditions
        for (int i = 0; i < category.getFieldConditions().size(); i++) {
            FieldCondition cond = category.getFieldConditions().get(i);

            if (cond instanceof FieldExistsCondition) {
                fieldConditionsPanel.add(new FieldConditionPanel(new FieldExistsCondition((FieldExistsCondition) cond)), i);
            } else if (cond instanceof FieldEqualsCondition) {
                fieldConditionsPanel.add(new FieldConditionPanel(new FieldEqualsCondition((FieldEqualsCondition) cond)), i);
            } else if (cond instanceof FieldContainsCondition) {
                fieldConditionsPanel.add(new FieldConditionPanel(new FieldContainsCondition((FieldContainsCondition) cond)), i);
            } else {
                throw new AssertionError("Unexpected condition type: " + cond.getClass());
            }
        }

        fieldConditionsPanel.revalidate();
    }

    private List<FieldCondition> getFieldConditions() {
        List<FieldCondition> fieldConditions = new ArrayList<>(fieldConditionsPanel.getComponents().length);

        for (Component c : fieldConditionsPanel.getComponents()) {
            if (c instanceof FieldConditionPanel) {
                fieldConditions.add(((FieldConditionPanel) c).getCondition());
            }
        }

        return fieldConditions;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        typeHeader = new javax.swing.JLabel();
        typeSeparator = new javax.swing.JSeparator();
        typeInvertCheckBox = new javax.swing.JCheckBox();
        typeLabel = new javax.swing.JLabel();
        typeTextField = new javax.swing.JTextField();
        fieldConditionsScrollPane = new javax.swing.JScrollPane();
        fieldConditionsPanel = new javax.swing.JPanel();
        addConditionButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        testHeader = new javax.swing.JLabel();
        testSeparator = new javax.swing.JSeparator();
        testButton = new javax.swing.JButton();
        testScrollPane = new javax.swing.JScrollPane();
        testOutputTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Publy " + publy.Constants.MAJOR_VERSION + "." + publy.Constants.MINOR_VERSION + " - Filters");

        typeHeader.setText("Types");

        typeInvertCheckBox.setText("Not");
        typeInvertCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeInvertCheckBoxActionPerformed(evt);
            }
        });

        typeLabel.setText("One of");

        typeTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                typeTextFieldTextChanged(e);
            }
            public void removeUpdate(DocumentEvent e) {
                typeTextFieldTextChanged(e);
            }
            public void changedUpdate(DocumentEvent e) {
                //Plain text components do not fire these events
            }
        });
        typeTextField.setToolTipText("Types to include in this category. A * matches all types; multiple types can be separated by semicolons.");

        fieldConditionsScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Fields"));

        fieldConditionsPanel.setLayout(new javax.swing.BoxLayout(fieldConditionsPanel, javax.swing.BoxLayout.PAGE_AXIS));

        addConditionButton.setText("Add filter");
        addConditionButton.setToolTipText("Add another filter. All filters must match for an entry to be included.");
        addConditionButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        addConditionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addConditionButtonActionPerformed(evt);
            }
        });
        fieldConditionsPanel.add(addConditionButton);

        fieldConditionsScrollPane.setViewportView(fieldConditionsPanel);

        cancelButton.setText("Don't save");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("Save");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        testHeader.setText("Test");

        testButton.setText("Test");
        testButton.setToolTipText("List all matching entries in the current publications list.");
        testButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testButtonActionPerformed(evt);
            }
        });

        testOutputTextArea.setEditable(false);
        testOutputTextArea.setColumns(20);
        testOutputTextArea.setLineWrap(true);
        testOutputTextArea.setRows(3);
        testScrollPane.setViewportView(testOutputTextArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(typeHeader)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(typeSeparator))
                    .addComponent(fieldConditionsScrollPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(testHeader)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(testSeparator))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(testScrollPane, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(typeInvertCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(typeLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(typeTextField))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(testButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 171, Short.MAX_VALUE)
                                .addComponent(okButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(typeSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeHeader))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeInvertCheckBox)
                    .addComponent(typeLabel)
                    .addComponent(typeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(fieldConditionsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(testHeader)
                    .addComponent(testSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(testScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testButton)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void typeTextFieldTextChanged(DocumentEvent e) {
        typeCondition.setTypes(UIStyles.parseDisplayString(typeTextField.getText()));
    }

    private void addConditionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addConditionButtonActionPerformed
        FieldCondition cond = new FieldExistsCondition(false, "field");
        FieldConditionPanel newComponent = new FieldConditionPanel(cond);

        fieldConditionsPanel.add(newComponent, fieldConditionsPanel.getComponentCount() - 1); // Add the new object before the 'Add' button
        fieldConditionsPanel.revalidate(); // Recompute the layout and propagate the changes
        fieldConditionsPanel.scrollRectToVisible(new Rectangle(0, Integer.MAX_VALUE, 0, 0)); // Scroll to the bottom
    }//GEN-LAST:event_addConditionButtonActionPerformed

    private void typeInvertCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeInvertCheckBoxActionPerformed
        if (typeInvertCheckBox.isSelected()) {
            typeCondition.setInverted(true);

            typeInvertCheckBox.setFont(typeInvertCheckBox.getFont().deriveFont(notStrikeThroughAttribute));
            typeLabel.setText(typeLabel.getText().substring(0, 1).toLowerCase() + typeLabel.getText().substring(1));
        } else {
            typeCondition.setInverted(false);

            typeInvertCheckBox.setFont(typeInvertCheckBox.getFont().deriveFont(strikeThroughAttribute));
            typeLabel.setText(typeLabel.getText().substring(0, 1).toUpperCase() + typeLabel.getText().substring(1));
        }
    }//GEN-LAST:event_typeInvertCheckBoxActionPerformed

    private void testButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testButtonActionPerformed
        // Clear the output
        testOutputTextArea.setText("");
        
        // Get the publications list
        Settings settings = ((MainFrame) SwingUtilities.getRoot(getParent())).getSettings();
        Path pubList = settings.getFileSettings().getPublications();

        if (pubList == null) {
            testOutputTextArea.setText("No publication list was set.");
        } else if (Files.notExists(pubList)) {
            testOutputTextArea.setText(String.format("No publication list was found at: %s", pubList));
        } else {
            // Parse all publications
            List<BibItem> items = null;

            try {
                items = PublicationListParser.parseFile(settings.getFileSettings().getPublications());
            } catch (Exception | AssertionError ex) {
                testOutputTextArea.setText(String.format("Exception while parsing publications list:%n%s", ex.toString()));
            }

            if (items != null) {
                // Find all matching items
                List<FieldCondition> fieldConditions = getFieldConditions();
                List<BibItem> matches = new ArrayList<>();

                for (BibItem item : items) {
                    boolean match = typeCondition.matches(item);

                    for (FieldCondition condition : fieldConditions) {
                        match = match && condition.matches(item);
                    }

                    if (match) {
                        matches.add(item);
                    }
                }

                // Print info
                if (!matches.isEmpty()) {
                    testOutputTextArea.append("Matching items: ");
                    
                    boolean first = true;

                    for (BibItem item : matches) {
                        if (first) {
                            first = false;
                        } else {
                            testOutputTextArea.append(", ");
                        }
                        
                        testOutputTextArea.append(item.getId());
                    }
                    
                    testOutputTextArea.append(".\n");
                }

                testOutputTextArea.append(matches.size() + " matches.");
            }
        }
    }//GEN-LAST:event_testButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // Sync settings to the category
        category.setTypeCondition(typeCondition);
        category.setFieldConditions(getFieldConditions());

        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addConditionButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel fieldConditionsPanel;
    private javax.swing.JScrollPane fieldConditionsScrollPane;
    private javax.swing.JButton okButton;
    private javax.swing.JButton testButton;
    private javax.swing.JLabel testHeader;
    private javax.swing.JTextArea testOutputTextArea;
    private javax.swing.JScrollPane testScrollPane;
    private javax.swing.JSeparator testSeparator;
    private javax.swing.JLabel typeHeader;
    private javax.swing.JCheckBox typeInvertCheckBox;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JSeparator typeSeparator;
    private javax.swing.JTextField typeTextField;
    // End of variables declaration//GEN-END:variables
}
