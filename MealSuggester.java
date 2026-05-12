import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MealSuggester.java
 * @author Javen
 * 
 * Handles the logic for figuring out what meals the user can make
 * based on whats currently in their inventory. Only counts items
 * that havent expired yet.
 */
public class MealSuggester {

    // goes through each meal and checks if the user has all the ingredients
    // only returns meals where every single ingredient is available
    public static List<Meal> suggestMeals(List<Meal> meals, Set<String> availableIngredients) {
        List<Meal> suggestions = new ArrayList<>();

        // convert everything to lowercase so "Tomato" matches "tomato"
        Set<String> lowered = new HashSet<>();
        for (String s : availableIngredients) {
            lowered.add(s.trim().toLowerCase());
        }

        for (Meal m : meals) {
            boolean canMake = true;
            for (String ing : m.getIngredients()) {
                if (!lowered.contains(ing)) {
                    canMake = false;
                    break; // no point checking the rest if one is missing
                }
            }
            if (canMake) suggestions.add(m);
        }
        return suggestions;
    }

    // convenience method - gets available ingredient names from inventory
    // and passes them to suggestMeals
    public static List<Meal> suggestMealsFromInventory(List<Meal> meals, Inventory inventory, LocalDate today) {
        Set<String> avail = inventory.getAvailableIngredientNames(today);
        return suggestMeals(meals, avail);
    }

}
