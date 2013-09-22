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
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import publy.data.category.conditions.FieldCondition;
import publy.data.category.conditions.FieldContainsCondition;
import publy.data.category.conditions.FieldEqualsCondition;
import publy.data.category.conditions.FieldExistsCondition;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class FieldConditionPanel extends javax.swing.JPanel {

    private enum Operation {

        EXISTS, EQUALS, CONTAINS;

        @Override
        public String toString() {
            switch (this) {
                case EXISTS:
                    return "Exists";
                case EQUALS:
                    return "Equals";
                case CONTAINS:
                    return "Contains";
                default:
                    throw new AssertionError("Unexpected operation: " + this.name());
            }
        }
    }
    private FieldCondition condition;
    private boolean initializing;

    /**
     * Creates new form FieldConditionPanel.
     *
     * @param condition The condition edited by this panel.
     * @param conditions The full list of conditions.
     * @param index The index of this condition in the list.
     */
    public FieldConditionPanel(FieldCondition condition) {
        this.condition = condition;

        initComponents();
        populateValues();
    }

    private void populateValues() {
        initializing = true;

        // Inversion
        if (condition.isInverted()) {
            invertCheckBox.setSelected(true);
            invertCheckBox.setForeground(UIManager.getDefaults().getColor("CheckBox.foreground"));
        } else {
            invertCheckBox.setSelected(false);
            invertCheckBox.setForeground(Color.GRAY);
        }

        // Field
        fieldTextField.setText(condition.getField());

        // Type
        if (condition instanceof FieldExistsCondition) {
            operationComboBox.setSelectedItem(Operation.EXISTS);

            valueTextField.setEnabled(false);
            valueTextField.setText("");
        } else if (condition instanceof FieldEqualsCondition) {
            operationComboBox.setSelectedItem(Operation.EQUALS);

            valueTextField.setEnabled(true);
            valueTextField.setText(makeString(((FieldEqualsCondition) condition).getValues()));
        } else if (condition instanceof FieldContainsCondition) {
            operationComboBox.setSelectedItem(Operation.CONTAINS);

            valueTextField.setEnabled(true);
            valueTextField.setText(makeString(((FieldContainsCondition) condition).getValues()));
        } else {
            throw new AssertionError("Unexpected condition type: " + condition.getClass());
        }

        initializing = false;
    }

    private String makeString(List<String> values) {
        StringBuilder text = new StringBuilder();
        boolean first = true;

        for (String val : values) {
            if (first) {
                first = false;
            } else {
                text.append(';');
            }

            text.append(val);
        }

        return text.toString();
    }

    public FieldCondition getCondition() {
        return condition;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        invertCheckBox = new javax.swing.JCheckBox();
        fieldTextField = new javax.swing.JTextField();
        operationComboBox = new javax.swing.JComboBox<Operation>();
        valueTextField = new javax.swing.JTextField();
        deleteButton = new javax.swing.JButton();

        setAlignmentX(LEFT_ALIGNMENT);

        invertCheckBox.setText("Not");
        invertCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertCheckBoxActionPerformed(evt);
            }
        });

        fieldTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                fieldTextFieldTextChanged(e);
            }
            public void removeUpdate(DocumentEvent e) {
                fieldTextFieldTextChanged(e);
            }
            public void changedUpdate(DocumentEvent e) {
                //Plain text components do not fire these events
            }
        });
        fieldTextField.setColumns(7);

        operationComboBox.setModel(new DefaultComboBoxModel<>(Operation.values()));
        operationComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                operationComboBoxItemStateChanged(evt);
            }
        });

        valueTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                valueTextFieldTextChanged(e);
            }
            public void removeUpdate(DocumentEvent e) {
                valueTextFieldTextChanged(e);
            }
            public void changedUpdate(DocumentEvent e) {
                //Plain text components do not fire these events
            }
        });
        valueTextField.setColumns(12);

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/publy/gui/resources/cancel-14.png"))); // NOI18N
        deleteButton.setPreferredSize(new java.awt.Dimension(49, 22));
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(invertCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(operationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(valueTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(invertCheckBox)
                    .addComponent(fieldTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(operationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(valueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void invertCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertCheckBoxActionPerformed
        if (invertCheckBox.isSelected()) {
            condition.setInverted(true);
            invertCheckBox.setForeground(UIManager.getDefaults().getColor("CheckBox.foreground"));
        } else {
            condition.setInverted(false);
            invertCheckBox.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_invertCheckBoxActionPerformed

    private void operationComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_operationComboBoxItemStateChanged
        if (!initializing && evt.getStateChange() == ItemEvent.SELECTED) {
            // Update our condition
            switch ((Operation) operationComboBox.getSelectedItem()) {
                case EXISTS:
                    condition = new FieldExistsCondition(invertCheckBox.isSelected(), fieldTextField.getText());
                    valueTextField.setEnabled(false);
                    break;
                case EQUALS:
                    condition = new FieldEqualsCondition(invertCheckBox.isSelected(), fieldTextField.getText(), valueTextField.getText().split(";"));
                    valueTextField.setEnabled(true);
                    break;
                case CONTAINS:
                    condition = new FieldContainsCondition(invertCheckBox.isSelected(), fieldTextField.getText(), valueTextField.getText().split(";"));
                    valueTextField.setEnabled(true);
                    break;
                default:
                    throw new AssertionError("Unexpected operation: " + (Operation) operationComboBox.getSelectedItem());
            }
        }
    }//GEN-LAST:event_operationComboBoxItemStateChanged

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        Container parent = getParent();
        
        parent.remove(this);
        parent.revalidate();
        parent.repaint();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void valueTextFieldTextChanged(DocumentEvent e) {
        if (!initializing) {
            if (condition instanceof FieldEqualsCondition) {
                ((FieldEqualsCondition) condition).setValues(valueTextField.getText().split(";"));
            } else if (condition instanceof FieldContainsCondition) {
                ((FieldContainsCondition) condition).setValues(valueTextField.getText().split(";"));
            }
        }
    }

    private void fieldTextFieldTextChanged(DocumentEvent e) {
        if (!initializing) {
            condition.setField(fieldTextField.getText());
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField fieldTextField;
    private javax.swing.JCheckBox invertCheckBox;
    private javax.swing.JComboBox<Operation> operationComboBox;
    private javax.swing.JTextField valueTextField;
    // End of variables declaration//GEN-END:variables
}