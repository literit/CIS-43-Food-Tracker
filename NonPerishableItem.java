    import java.time.LocalDate;

/**
 * NonPerishableItem.java
 * @author Uri
 *
 * Subclass for food with a long shelf life (canned goods, grains, spices).
 * Uses a 30-day warning threshold since these items last much longer.
 */
public class NonPerishableItem extends FoodItem {

    public NonPerishableItem(String name, double quantity, String unit, LocalDate expirationDate)
            throws FoodTrackerException {
        super(name, quantity, unit, expirationDate);
    }

    // For loading saved items with a known ID
    public NonPerishableItem(int id, String name, double quantity, String unit, LocalDate expirationDate)
            throws FoodTrackerException {
        super(id, name, quantity, unit, expirationDate);
    }

    @Override
    public String getCategory() {
        return "Non-Perishable";
    }

    @Override
    public int getExpirationWarningDays() {
        return 30;
    }
}
