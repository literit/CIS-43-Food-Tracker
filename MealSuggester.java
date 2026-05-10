import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MealSuggester {

    public static List<Meal> suggestMeals(List<Meal> meals, Set<String> availableIngredients) {
        List<Meal> suggestions = new ArrayList<>();
        Set<String> lowered = new HashSet<>();
        for (String s : availableIngredients) {
            lowered.add(s.trim().toLowerCase());
        }

        for (Meal m : meals) {
            boolean ok = true;
            for (String ing : m.getIngredients()) {
                if (!lowered.contains(ing)) {
                    ok = false;
                    break;
                }
            }
            if (ok) suggestions.add(m);
        }
        return suggestions;
    }

    public static List<Meal> suggestMealsFromInventory(List<Meal> meals, Inventory inventory, LocalDate today) {
        Set<String> avail = inventory.getAvailableIngredientNames(today);
        return suggestMeals(meals, avail);
    }

}
