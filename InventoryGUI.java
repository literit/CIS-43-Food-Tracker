import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class InventoryGUI extends JFrame {
    private final Inventory inventory;
    private final DefaultTableModel tableModel;
    private final JLabel summaryLabel;
    private JTable table;

    public InventoryGUI() {
        this(new Inventory());
    }

    public InventoryGUI(Inventory inventory) {
        super("Inventory Viewer");
        this.inventory = inventory;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        setMinimumSize(new Dimension(820, 480));

        JLabel titleLabel = new JLabel("Current Inventory", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(20f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[] {"ID", "Name", "Quantity", "Unit", "Expiration Date", "Status"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));

        summaryLabel = new JLabel();
        bottomPanel.add(summaryLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel();
        
        JButton addButton = new JButton("+ Add Food");
        addButton.addActionListener(e -> showAddFoodDialog());
        buttonPanel.add(addButton);
        
        JButton removeButton = new JButton("- Remove Food");
        removeButton.addActionListener(e -> removeSelectedFood());
        buttonPanel.add(removeButton);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadInventory());
        buttonPanel.add(refreshButton);
        
        JButton suggestButton = new JButton("Suggest Meals");
        suggestButton.addActionListener(e -> showMealSuggestions());
        buttonPanel.add(suggestButton);
        
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        loadInventory();
        pack();
        setLocationRelativeTo(null);
    }

    private void loadInventory() {
        tableModel.setRowCount(0);

        LocalDate today = LocalDate.now();
        List<FoodItem> items = inventory.getItemsSortedByExpiration(today);
        int expiredCount = 0;
        int expiringSoonCount = 0;

        for (FoodItem item : items) {
            String status;
            if (item.isExpired(today)) {
                status = "Expired";
                expiredCount++;
            } else if (item.isExpiringSoon(today, 7)) {
                status = "Expiring soon";
                expiringSoonCount++;
            } else {
                status = "Fresh";
            }

            tableModel.addRow(new Object[] {
                    item.getId(),
                    item.getName(),
                    item.getQuantity(),
                    item.getUnit(),
                    item.getExpirationDate(),
                    status
            });
        }

        summaryLabel.setText(String.format(
                "Total items: %d | Expired: %d | Expiring soon: %d",
                items.size(), expiredCount, expiringSoonCount));
    }

    private void showAddFoodDialog() {
        JDialog dialog = new JDialog(this, "Add Food Item", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel contentPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        dialog.add(contentPanel);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        contentPanel.add(nameLabel);
        contentPanel.add(nameField);

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();
        contentPanel.add(quantityLabel);
        contentPanel.add(quantityField);

        JLabel unitLabel = new JLabel("Unit:");
        JTextField unitField = new JTextField();
        contentPanel.add(unitLabel);
        contentPanel.add(unitField);

        JLabel expirationLabel = new JLabel("Expiration Date:");
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date());
        contentPanel.add(expirationLabel);
        contentPanel.add(dateSpinner);

        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton cancelBtn = new JButton("Cancel");
        
        addBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                double quantity = Double.parseDouble(quantityField.getText().trim());
                String unit = unitField.getText().trim();
                Date selectedDate = (Date) dateSpinner.getValue();
                LocalDate expirationDate = selectedDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a food name.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (unit.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a unit.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                FoodItem newItem = new FoodItem(name, quantity, unit, expirationDate);
                inventory.addItem(newItem);
                loadInventory();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Food item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);
        contentPanel.add(buttonPanel);

        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void removeSelectedFood() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a food item to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int foodId = (int) tableModel.getValueAt(modelRow, 0);
        String foodName = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove \"" + foodName + "\"?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (inventory.removeItem(foodId)) {
                loadInventory();
                JOptionPane.showMessageDialog(this, "Food item removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove food item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showMealSuggestions() {
        LocalDate today = LocalDate.now();
        List<Meal> suggestedMeals = MealSuggester.suggestMealsFromInventory(Meal.defaultMeals(), inventory, today);

        if (suggestedMeals.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No meals can be suggested with your current inventory.",
                    "No Suggestions",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Suggested Meals", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        dialog.add(contentPanel);

        JLabel titleLabel = new JLabel("Meals you can make with your ingredients:");
        titleLabel.setFont(titleLabel.getFont().deriveFont(14f));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        StringBuilder mealList = new StringBuilder("<html><body>");
        for (int i = 0; i < suggestedMeals.size(); i++) {
            Meal meal = suggestedMeals.get(i);
            mealList.append("<b>").append(i + 1).append(". ").append(meal.getName()).append("</b>");
            mealList.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;Ingredients: ")
                    .append(String.join(", ", meal.getIngredients()))
                    .append("<br><br>");
        }
        mealList.append("</body></html>");

        JLabel mealsLabel = new JLabel(mealList.toString());
        JScrollPane scrollPane = new JScrollPane(mealsLabel);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Keep default look and feel if system LAF is unavailable.
            }

            InventoryGUI window = new InventoryGUI();
            window.getContentPane().setBackground(Color.WHITE);
            window.setVisible(true);
        });
    }
}