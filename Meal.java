import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Meal.java
 * @author Javen
 * 
 * Stores a meal name and its list of ingredients.
 * defaultMeals() has a bunch of preset meals we can suggest to the user.
 */
public class Meal {
    private static int nextId = 1;

    private final int id;
    private String name;
    private List<String> ingredients;

    public Meal(String name, List<String> ingredients) {
        this.id = nextId++;
        this.name = name;
        // store ingredients in lowercase so matching is easier later
        this.ingredients = new ArrayList<>();
        for (String s : ingredients) {
            this.ingredients.add(s.trim().toLowerCase());
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    @Override
    public String toString() {
        return String.format("%s - ingredients: %s", name, String.join(", ", ingredients));
    }

    // returns a list of sample meals the user might want to make
    public static List<Meal> defaultMeals() {
        List<Meal> m = new ArrayList<>();
        m.add(new Meal("Pasta with Tomato & Cheese", List.of("pasta", "tomato", "cheese")));
        m.add(new Meal("Grilled Cheese", List.of("bread", "cheese", "butter")));
        m.add(new Meal("Simple Salad", List.of("lettuce", "tomato", "cucumber")));
        m.add(new Meal("Omelette", List.of("eggs", "milk", "cheese")));
        m.add(new Meal("Stir Fry Rice", List.of("rice", "broccoli", "carrot", "soy sauce")));
        return m;
    }
}
