package com.bakeryinventory;

import com.bakeryinventory.config.Database;
import com.bakeryinventory.ui.LoginFrame;
import com.bakeryinventory.ui.UiTheme;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UiTheme.apply();
                Database.initialize();
                new LoginFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Unable to start the application:\n" + ex.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
