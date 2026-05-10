import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class InventoryGUI extends JFrame {
    private final Inventory inventory;
    private final DefaultTableModel tableModel;
    private final JLabel summaryLabel;

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

        JTable table = new JTable(tableModel);
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

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadInventory());
        bottomPanel.add(refreshButton, BorderLayout.EAST);

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