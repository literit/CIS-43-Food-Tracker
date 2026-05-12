/**
 * FoodTrackerException.java
 * @author Uri
 *
 * Custom checked exception for invalid operations in the food tracker.
 */
public class FoodTrackerException extends Exception {

    public FoodTrackerException(String message) {
        super(message);
    }

    public FoodTrackerException(String message, Throwable cause) {
        super(message, cause);
    }
}
