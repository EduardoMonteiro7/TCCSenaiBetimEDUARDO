package com.bakeryinventory.ui;

import com.bakeryinventory.model.Product;
import com.bakeryinventory.model.User;
import com.bakeryinventory.service.AuthService;
import com.bakeryinventory.service.InventoryService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.Timer;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

public class MainFrame extends JFrame {
    private final User currentUser;
    private final InventoryService inventoryService = new InventoryService();
    private final AuthService authService = new AuthService();

    private final JTextField searchField = new JTextField(24);
    private final ProductTableModel productModel = new ProductTableModel();
    private final JTable productTable = new JTable(productModel);
    private final ProductTableModel nearExpirationModel = new ProductTableModel();
    private final ProductTableModel expiredModel = new ProductTableModel();
    private final UserTableModel userModel = new UserTableModel();
    private JTable userTable;
    private JLabel summaryLabel;

    public MainFrame(User currentUser) {
        super("Bakery Inventory Management System");
        this.currentUser = currentUser;
        build();
        refreshAll();
        new Timer(450, event -> refreshProducts()).start();
        showStartupAlerts();
    }

    private void build() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 720);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        UiTheme.pad(root);

        root.add(buildHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Products", buildProductsPanel());
        tabs.addTab("Expiration", buildExpirationPanel());
        if (currentUser.isManager()) {
            tabs.addTab("Users", buildUsersPanel());
        }
        root.add(tabs, BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Bakery Inventory");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        JLabel user = new JLabel("Logged in as " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        header.add(title, BorderLayout.WEST);
        header.add(user, BorderLayout.EAST);
        return header;
    }

    private JPanel buildProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JPanel toolbar = new JPanel(new BorderLayout(8, 8));
        toolbar.setOpaque(false);
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        search.setOpaque(false);
        search.add(new JLabel("Search"));
        search.add(searchField);
        JButton searchButton = new JButton("Refresh");
        searchButton.addActionListener(event -> refreshAll());
        search.add(searchButton);
        toolbar.add(search, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);
        JButton addButton = UiTheme.primaryButton("Add");
        addButton.addActionListener(event -> addProduct());
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(event -> editProduct());
        JButton deleteButton = UiTheme.dangerButton("Delete");
        deleteButton.addActionListener(event -> deleteProduct());
        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        toolbar.add(actions, BorderLayout.EAST);

        panel.add(toolbar, BorderLayout.NORTH);

        configureProductTable(productTable);
        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        summaryLabel = new JLabel(" ");
        panel.add(summaryLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildExpirationPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setOpaque(false);

        JTable nearTable = new JTable(nearExpirationModel);
        configureProductTable(nearTable);
        JTable expiredTable = new JTable(expiredModel);
        configureProductTable(expiredTable);

        panel.add(section("Near expiration", nearTable));
        panel.add(section("Expired products", expiredTable));
        return panel;
    }

    private JPanel buildUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton addButton = UiTheme.primaryButton("Create User");
        addButton.addActionListener(event -> createUser());
        JButton deleteButton = UiTheme.dangerButton("Delete User");
        deleteButton.addActionListener(event -> deleteUser());
        actions.add(addButton);
        actions.add(deleteButton);
        panel.add(actions, BorderLayout.NORTH);

        userTable = new JTable(userModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel section(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        UiTheme.card(panel);
        JLabel label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void configureProductTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setDefaultRenderer(Object.class, new ProductStatusRenderer((ProductTableModel) table.getModel()));
        table.getColumnModel().getColumn(0).setPreferredWidth(45);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(8).setPreferredWidth(180);
        TableRowSorter<?> sorter = (TableRowSorter<?>) table.getRowSorter();
        sorter.setSortKeys(List.of(new RowSorter.SortKey(6, SortOrder.ASCENDING)));
    }

    private Product selectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return productModel.getProductAt(productTable.convertRowIndexToModel(selectedRow));
    }

    private void addProduct() {
        ProductDialog dialog = new ProductDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            try {
                inventoryService.save(dialog.getProduct(), currentUser);
                refreshAll();
            } catch (Exception ex) {
                Dialogs.error(this, ex);
            }
        }
    }

    private void editProduct() {
        Product selected = selectedProduct();
        if (selected == null) {
            Dialogs.info(this, "Select a product to edit.");
            return;
        }
        Product copy = new Product(
                selected.getId(), selected.getName(), selected.getCategory(), selected.getQuantity(),
                selected.getIdealQuantity(), selected.getUnitPrice(), selected.getExpirationDate(),
                selected.getRegistrationDate()
        );
        ProductDialog dialog = new ProductDialog(this, copy);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            try {
                inventoryService.save(dialog.getProduct(), currentUser);
                refreshAll();
            } catch (Exception ex) {
                Dialogs.error(this, ex);
            }
        }
    }

    private void deleteProduct() {
        Product selected = selectedProduct();
        if (selected == null) {
            Dialogs.info(this, "Select a product to delete.");
            return;
        }
        if (Dialogs.confirm(this, "Delete product \"" + selected.getName() + "\"?")) {
            try {
                inventoryService.delete(selected, currentUser);
                refreshAll();
            } catch (Exception ex) {
                Dialogs.error(this, ex);
            }
        }
    }

    private void createUser() {
        UserDialog dialog = new UserDialog(this);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            try {
                authService.createUser(currentUser, dialog.getUsername(), dialog.getPassword());
                refreshUsers();
            } catch (Exception ex) {
                Dialogs.error(this, ex);
            }
        }
    }

    private void deleteUser() {
        int selectedRow = userTable == null ? -1 : userTable.getSelectedRow();
        if (selectedRow < 0) {
            Dialogs.info(this, "Select a user to delete.");
            return;
        }
        User selected = userModel.getUserAt(userTable.convertRowIndexToModel(selectedRow));
        if (Dialogs.confirm(this, "Delete user \"" + selected.getUsername() + "\"?")) {
            try {
                authService.deleteUser(currentUser, selected.getId());
                refreshUsers();
            } catch (Exception ex) {
                Dialogs.error(this, ex);
            }
        }
    }

    private void refreshAll() {
        refreshProducts();
        refreshExpiration();
        refreshUsers();
    }

    private void refreshProducts() {
        try {
            List<Product> products = inventoryService.search(searchField.getText());
            productModel.setProducts(products);
            long lowStock = products.stream().filter(Product::isBelowIdealStock).count();
            long expired = products.stream().filter(Product::isExpired).count();
            long near = products.stream().filter(product -> product.isNearExpiration(InventoryService.EXPIRATION_WARNING_DAYS)).count();
            summaryLabel.setText("Products: " + products.size() + " | Below ideal stock: " + lowStock
                    + " | Near expiration: " + near + " | Expired: " + expired);
        } catch (Exception ex) {
            Dialogs.error(this, ex);
        }
    }

    private void refreshExpiration() {
        try {
            nearExpirationModel.setProducts(inventoryService.nearExpiration());
            expiredModel.setProducts(inventoryService.expired());
        } catch (Exception ex) {
            Dialogs.error(this, ex);
        }
    }

    private void refreshUsers() {
        if (!currentUser.isManager()) {
            return;
        }
        try {
            userModel.setUsers(authService.listUsers());
        } catch (Exception ex) {
            Dialogs.error(this, ex);
        }
    }

    private void showStartupAlerts() {
        try {
            int near = inventoryService.nearExpiration().size();
            int expired = inventoryService.expired().size();
            if (near > 0 || expired > 0) {
                Dialogs.info(this, "Expiration alert:\n" + near + " product(s) near expiration.\n"
                        + expired + " product(s) already expired.");
            }
        } catch (Exception ex) {
            Dialogs.error(this, ex);
        }
    }
}
