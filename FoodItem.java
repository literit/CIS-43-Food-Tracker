import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FoodItem {
    private static int nextId = 1;

    private final int id;
    private String name;
    private double quantity;
    private String unit;
    private LocalDate expirationDate;

    // Constructor: initialize a FoodItem with name, quantity, unit, and expiration date
    public FoodItem(String name, double quantity, String unit, LocalDate expirationDate) {
        this.id = nextId++;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expirationDate = expirationDate;
    }


    // Return the unique id of this FoodItem
    public int getId() {
        return id;
    }


    // Return the name of the food item
    public String getName() {
        return name;
    }


    // Set a new name for the food item
    public void setName(String name) {
        this.name = name;
    }


    // Return the current quantity of the food item
    public double getQuantity() {
        return quantity;
    }


    // Update the quantity of the food item
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }


    // Return the measurement unit for the quantity
    public String getUnit() {
        return unit;
    }


    // Set the measurement unit for the quantity
    public void setUnit(String unit) {
        this.unit = unit;
    }


    // Return the expiration date of the food item
    public LocalDate getExpirationDate() {
        return expirationDate;
    }


    // Update the expiration date for the food item
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }


    // Return true if the item is expired compared to today
    public boolean isExpired(LocalDate today) {
        return expirationDate.isBefore(today);
    }


    // Calculate days from today until the expiration date
    public long daysUntilExpiration(LocalDate today) {
        return ChronoUnit.DAYS.between(today, expirationDate);
    }


    // Return true if the item will expire within the given threshold
    public boolean isExpiringSoon(LocalDate today, int daysThreshold) {
        long days = daysUntilExpiration(today);
        return days >= 0 && days <= daysThreshold;
    }


    @Override
    // Return a readable string representation of the FoodItem
    public String toString() {
        return String.format("[%d] %s: %.2f %s (exp: %s)", id, name, quantity, unit, expirationDate.toString());
    }
}
