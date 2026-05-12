import java.time.LocalDate;

/**
 * PerishableItem.java
 * @author Uri
 *
 * Subclass for food that spoils quickly (dairy, produce, meat, etc).
 * Overrides the warning threshold to 3 days since these go bad fast.
 */
public class PerishableItem extends FoodItem {

    public PerishableItem(String name, double quantity, String unit, LocalDate expirationDate)
            throws FoodTrackerException {
        super(name, quantity, unit, expirationDate);
    }

    // For loading saved items with a known ID
    public PerishableItem(int id, String name, double quantity, String unit, LocalDate expirationDate)
            throws FoodTrackerException {
        super(id, name, quantity, unit, expirationDate);
    }

    @Override
    public String getCategory() {
        return "Perishable";
    }

    @Override
    public int getExpirationWarningDays() {
        return 3;
    }
}
