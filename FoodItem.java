import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * FoodItem.java
 * @author Krish
 * 
 * This is the base class for all food items in the tracker.
 * It is abstract so you cant make a FoodItem directly,
 * you have to use PerishableItem or NonPerishableItem instead.
 * It also implements the Expirable interface so all food items
 * can check if they are expired.
 */
public abstract class FoodItem implements Expirable {
    private static int nextId = 1; // keeps track of the next id to assign

    private final int id;
    private String name;
    private double quantity;
    private String unit;
    private LocalDate expirationDate;

    // main constructor - creates a new food item with an auto generated id
    // throws FoodTrackerException if the input is not valid
    public FoodItem(String name, double quantity, String unit, LocalDate expirationDate)
            throws FoodTrackerException {
        validateInput(name, quantity, unit);
        this.id = nextId++;
        this.name = name.trim();
        this.quantity = quantity;
        this.unit = unit.trim();
        this.expirationDate = expirationDate;
    }

    // second constructor - this one lets you set the id manually
    // we need this for when we load items from the save file
    public FoodItem(int id, String name, double quantity, String unit, LocalDate expirationDate)
            throws FoodTrackerException {
        validateInput(name, quantity, unit);
        this.id = id;
        this.name = name.trim();
        this.quantity = quantity;
        this.unit = unit.trim();
        this.expirationDate = expirationDate;
        // make sure the next auto id doesnt clash with loaded ids
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    // validates the input fields before creating the object
    // this is static because we also call it from the edit dialog
    public static void validateInput(String name, double quantity, String unit)
            throws FoodTrackerException {
        if (name == null || name.trim().isEmpty()) {
            throw new FoodTrackerException("Food name cannot be empty.");
        }
        if (quantity <= 0) {
            throw new FoodTrackerException("Quantity must be greater than zero.");
        }
        if (unit == null || unit.trim().isEmpty()) {
            throw new FoodTrackerException("Unit cannot be empty.");
        }
    }

    // subclasses have to implement this to return their category
    // like "Perishable" or "Non-Perishable"
    public abstract String getCategory();

    // default warning days - subclasses can override this
    // perishable items will want a shorter warning
    public int getExpirationWarningDays() {
        return 7;
    }

    // --- getters and setters below ---

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    // returns true if this item is expired (expiration date is before today)
    @Override
    public boolean isExpired(LocalDate today) {
        return expirationDate.isBefore(today);
    }

    // calculates how many days until this item expires
    // negative number means it already expired that many days ago
    @Override
    public long daysUntilExpiration(LocalDate today) {
        return ChronoUnit.DAYS.between(today, expirationDate);
    }

    // returns true if the item expires within the threshold number of days
    @Override
    public boolean isExpiringSoon(LocalDate today, int daysThreshold) {
        long days = daysUntilExpiration(today);
        return days >= 0 && days <= daysThreshold;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s): %.2f %s (exp: %s)",
                id, name, getCategory(), quantity, unit, expirationDate.toString());
    }
}
