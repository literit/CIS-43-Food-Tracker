import java.time.LocalDate;

/**
 * Expirable.java
 * @author Krish
 * 
 * This is an interface for things that can expire.
 * Any class that implements this has to have these three methods.
 * FoodItem implements this interface.
 */
public interface Expirable {
    // checks if the item is expired based on todays date
    boolean isExpired(LocalDate today);

    // returns how many days until the item expires
    long daysUntilExpiration(LocalDate today);

    // checks if the item is going to expire soon (within a certain number of days)
    boolean isExpiringSoon(LocalDate today, int daysThreshold);
}
