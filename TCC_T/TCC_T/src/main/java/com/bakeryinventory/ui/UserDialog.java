package com.bakeryinventory.ui;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class UserDialog extends JDialog {
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private boolean saved;

    public UserDialog(MainFrame owner) {
        super(owner, "Create User", true);
        build();
    }

    public boolean isSaved() {
        return saved;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    private void build() {
        setSize(360, 230);
        setLocationRelativeTo(getOwner());
        JPanel root = new JPanel(new BorderLayout(10, 10));
        UiTheme.pad(root);

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.setOpaque(false);
        form.add(new LabeledField("Username", usernameField));
        form.add(new LabeledField("Password", passwordField));
        root.add(form, BorderLayout.CENTER);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(event -> dispose());
        JButton saveButton = UiTheme.primaryButton("Create");
        saveButton.addActionListener(event -> {
            saved = true;
            dispose();
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(cancelButton);
        actions.add(saveButton);
        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);
    }
}
