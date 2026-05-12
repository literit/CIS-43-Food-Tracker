import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * InventoryGUI.java
 * @author Vikram
 * 
 * Main window for the Food Tracker. Shows inventory in a table,
 * color codes items by expiration, and has search/filter/edit.
 */
public class InventoryGUI extends JFrame {

    // row colors for expiration status
    private static final Color ROW_RED = new Color(255, 200, 200);
    private static final Color ROW_AMBER = new Color(255, 240, 180);
    private static final Color ROW_GREEN = new Color(200, 245, 200);

    private final Inventory inventory;
    private final DefaultTableModel tableModel;
    private JLabel summaryLabel;
    private JTable table;
    private JTextField searchField;
    private JComboBox<String> filterCombo;

    public InventoryGUI() {
        this(new Inventory());
    }

    public InventoryGUI(Inventory inventory) {
        super("Food Tracker");
        this.inventory = inventory;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        setMinimumSize(new Dimension(850, 500));

        // title
        JLabel titleLabel = new JLabel("Food Inventory", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(20f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        add(titleLabel, BorderLayout.NORTH);

        // center section - search bar + table
        JPanel centerPanel = new JPanel(new BorderLayout(8, 8));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        // search and filter bar
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { loadInventory(); }
            public void insertUpdate(DocumentEvent e) { loadInventory(); }
            public void removeUpdate(DocumentEvent e) { loadInventory(); }
        });
        searchPanel.add(searchField);

        searchPanel.add(new JLabel("  Status:"));
        filterCombo = new JComboBox<>(new String[]{"All Items", "Expired", "Expiring Soon", "Fresh"});
        filterCombo.addActionListener(e -> loadInventory());
        searchPanel.add(filterCombo);

        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // table
        String[] columns = {"ID", "Name", "Category", "Qty", "Unit", "Expiration", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        // center-align most columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columns.length; i++) {
            if (i != 1) { // skip Name column
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // color rows by expiration status
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    int statusCol = t.getColumnCount() - 1;
                    String status = (String) t.getValueAt(row, statusCol);
                    if ("Expired".equals(status)) {
                        c.setBackground(ROW_RED);
                    } else if ("Expiring Soon".equals(status)) {
                        c.setBackground(ROW_AMBER);
                    } else {
                        c.setBackground(ROW_GREEN);
                    }
                    c.setForeground(Color.BLACK);
                }
                setHorizontalAlignment(column == 1 ? SwingConstants.LEFT : SwingConstants.CENTER);
                return c;
            }
        });

        // double click to edit
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    showEditFoodDialog();
                }
            }
        });

        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // bottom section - summary + buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));

        summaryLabel = new JLabel();
        bottomPanel.add(summaryLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("+ Add Food");
        addButton.addActionListener(e -> showAddFoodDialog());
        buttonPanel.add(addButton);

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> showEditFoodDialog());
        buttonPanel.add(editButton);

        JButton removeButton = new JButton("- Remove");
        removeButton.addActionListener(e -> removeSelectedFood());
        buttonPanel.add(removeButton);

        JButton suggestButton = new JButton("Suggest Meals");
        suggestButton.addActionListener(e -> showMealSuggestions());
        buttonPanel.add(suggestButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadInventory());
        buttonPanel.add(refreshButton);

        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        loadInventory();
        pack();
        setLocationRelativeTo(null);

        // show alerts after window is visible
        SwingUtilities.invokeLater(this::showStartupAlerts);
    }

    // loads items into the table, applies search and filter
    private void loadInventory() {
        tableModel.setRowCount(0);
        LocalDate today = LocalDate.now();

        String query = (searchField != null) ? searchField.getText().trim().toLowerCase() : "";
        String filter = (filterCombo != null) ? (String) filterCombo.getSelectedItem() : "All Items";

        List<FoodItem> items = inventory.getItemsSortedByExpiration(today);
        int expiredCount = 0, expiringCount = 0, freshCount = 0;

        for (FoodItem item : items) {
            String status;
            int warningDays = item.getExpirationWarningDays();

            if (item.isExpired(today)) {
                status = "Expired";
                expiredCount++;
            } else if (item.isExpiringSoon(today, warningDays)) {
                status = "Expiring Soon";
                expiringCount++;
            } else {
                status = "Fresh";
                freshCount++;
            }

            // search and filter
            boolean matchesSearch = query.isEmpty()
                    || item.getName().toLowerCase().contains(query);
            boolean matchesFilter = "All Items".equals(filter) || filter.equals(status);

            if (matchesSearch && matchesFilter) {
                tableModel.addRow(new Object[]{
                        item.getId(),
                        item.getName(),
                        item.getCategory(),
                        item.getQuantity(),
                        item.getUnit(),
                        item.getExpirationDate(),
                        status
                });
            }
        }

        summaryLabel.setText(String.format(
                "Total: %d | Expired: %d | Expiring soon: %d | Fresh: %d",
                items.size(), expiredCount, expiringCount, freshCount));
    }

    // add food dialog
    private void showAddFoodDialog() {
        JDialog dialog = new JDialog(this, "Add Food Item", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        dialog.add(panel);

        JTextField nameField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField unitField = new JTextField();
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setValue(new Date());
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"Perishable", "Non-Perishable"});

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Unit:"));
        panel.add(unitField);
        panel.add(new JLabel("Expiration:"));
        panel.add(dateSpinner);
        panel.add(new JLabel("Category:"));
        panel.add(categoryBox);

        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton cancelBtn = new JButton("Cancel");

        addBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                double quantity = Double.parseDouble(quantityField.getText().trim());
                String unit = unitField.getText().trim();
                Date selectedDate = (Date) dateSpinner.getValue();
                LocalDate expDate = selectedDate.toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                String category = (String) categoryBox.getSelectedItem();

                FoodItem newItem;
                if ("Perishable".equals(category)) {
                    newItem = new PerishableItem(name, quantity, unit, expDate);
                } else {
                    newItem = new NonPerishableItem(name, quantity, unit, expDate);
                }

                inventory.addItem(newItem);
                saveData();
                loadInventory();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Food item added!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (FoodTrackerException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid number for quantity.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(addBtn);
        btnPanel.add(cancelBtn);
        panel.add(new JLabel());
        panel.add(btnPanel);

        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // edit food dialog - opens when you double click or press Edit
    private void showEditFoodDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a food item to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int foodId = (int) tableModel.getValueAt(modelRow, 0);
        FoodItem item = inventory.getItemById(foodId);
        if (item == null) return;

        JDialog dialog = new JDialog(this, "Edit Food Item", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        dialog.add(panel);

        JTextField nameField = new JTextField(item.getName());
        JTextField quantityField = new JTextField(String.valueOf(item.getQuantity()));
        JTextField unitField = new JTextField(item.getUnit());
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setValue(Date.from(item.getExpirationDate()
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Unit:"));
        panel.add(unitField);
        panel.add(new JLabel("Expiration:"));
        panel.add(dateSpinner);

        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                double quantity = Double.parseDouble(quantityField.getText().trim());
                String unit = unitField.getText().trim();
                Date selectedDate = (Date) dateSpinner.getValue();
                LocalDate expDate = selectedDate.toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate();

                FoodItem.validateInput(name, quantity, unit);

                item.setName(name);
                item.setQuantity(quantity);
                item.setUnit(unit);
                item.setExpirationDate(expDate);

                saveData();
                loadInventory();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Food item updated!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (FoodTrackerException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid number for quantity.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        panel.add(new JLabel());
        panel.add(btnPanel);

        dialog.setSize(400, 260);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void removeSelectedFood() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a food item to remove.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int foodId = (int) tableModel.getValueAt(modelRow, 0);
        String foodName = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove \"" + foodName + "\"?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (inventory.removeItem(foodId)) {
                saveData();
                loadInventory();
                JOptionPane.showMessageDialog(this, "Food item removed!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove food item.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showMealSuggestions() {
        LocalDate today = LocalDate.now();
        List<Meal> suggestedMeals = MealSuggester.suggestMealsFromInventory(
                Meal.defaultMeals(), inventory, today);

        if (suggestedMeals.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No meals can be suggested with your current inventory.",
                    "No Suggestions", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Suggested Meals", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        dialog.add(contentPanel);

        JLabel titleLbl = new JLabel("Meals you can make:");
        titleLbl.setFont(titleLbl.getFont().deriveFont(14f));
        contentPanel.add(titleLbl, BorderLayout.NORTH);

        StringBuilder sb = new StringBuilder("<html><body>");
        for (int i = 0; i < suggestedMeals.size(); i++) {
            Meal meal = suggestedMeals.get(i);
            sb.append("<b>").append(i + 1).append(". ").append(meal.getName()).append("</b>");
            sb.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;Ingredients: ")
                    .append(String.join(", ", meal.getIngredients()))
                    .append("<br><br>");
        }
        sb.append("</body></html>");

        JLabel mealsLabel = new JLabel(sb.toString());
        contentPanel.add(new JScrollPane(mealsLabel), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(closeBtn);
        contentPanel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // checks for expired/expiring items on startup
    private void showStartupAlerts() {
        LocalDate today = LocalDate.now();
        List<FoodItem> expired = inventory.getExpiredItems(today);
        List<FoodItem> expiring = inventory.getExpiringSoonItems(today, 3);

        if (expired.isEmpty() && expiring.isEmpty()) return;

        StringBuilder msg = new StringBuilder();
        if (!expired.isEmpty()) {
            msg.append("EXPIRED ITEMS:\n");
            for (FoodItem item : expired) {
                msg.append("  - ").append(item.getName())
                        .append(" (expired ").append(Math.abs(item.daysUntilExpiration(today)))
                        .append(" days ago)\n");
            }
            msg.append("\n");
        }
        if (!expiring.isEmpty()) {
            msg.append("EXPIRING SOON:\n");
            for (FoodItem item : expiring) {
                msg.append("  - ").append(item.getName())
                        .append(" (").append(item.daysUntilExpiration(today))
                        .append(" days left)\n");
            }
        }

        JOptionPane.showMessageDialog(this, msg.toString(),
                "Expiration Alert", JOptionPane.WARNING_MESSAGE);
    }

    // saves inventory to file
    private void saveData() {
        try {
            DataStore.save(inventory.getItems());
        } catch (FoodTrackerException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not save data: " + e.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            // try loading from file, fall back to preset data
            Inventory inventory;
            try {
                if (DataStore.saveFileExists()) {
                    List<FoodItem> loaded = DataStore.load();
                    inventory = new Inventory(loaded);
                } else {
                    inventory = new Inventory();
                }
            } catch (FoodTrackerException e) {
                System.err.println("Could not load saved data: " + e.getMessage());
                inventory = new Inventory();
            }

            InventoryGUI window = new InventoryGUI(inventory);
            window.getContentPane().setBackground(Color.WHITE);
            window.setVisible(true);
        });
    }
}