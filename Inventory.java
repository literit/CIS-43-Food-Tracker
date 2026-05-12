import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Inventory.java
 * @author Vikram
 * 
 * Manages the collection of food items. Has two constructors -
 * one loads preset sample data, the other takes items loaded from file.
 * Also handles sorting by expiration and finding expired items for alerts.
 */
public class Inventory {
    private final List<FoodItem> items = new ArrayList<>();

    // Default constructor - loads sample data so the app has something to show
    public Inventory() {
        LocalDate today = LocalDate.now();
        try {
            // perishable items
            items.add(new PerishableItem("tomato", 5, "count", today.plusDays(1)));
            items.add(new PerishableItem("cheese", 0.5, "lb", today.plusDays(5)));
            items.add(new PerishableItem("lettuce", 1, "head", today.minusDays(2)));
            items.add(new PerishableItem("cucumber", 2, "count", today.plusDays(6)));
            items.add(new PerishableItem("eggs", 12, "count", today.plusDays(2)));
            items.add(new PerishableItem("milk", 1, "quart", today.minusDays(1)));
            items.add(new PerishableItem("broccoli", 1, "head", today.plusDays(3)));
            items.add(new PerishableItem("carrot", 6, "count", today.plusDays(7)));
            items.add(new PerishableItem("bread", 1, "loaf", today.plusDays(4)));
            items.add(new PerishableItem("butter", 0.25, "lb", today.plusDays(60)));

            // non-perishable items
            items.add(new NonPerishableItem("pasta", 1, "box", today.plusDays(30)));
            items.add(new NonPerishableItem("rice", 2, "lb", today.plusDays(365)));
            items.add(new NonPerishableItem("soy sauce", 1, "bottle", today.plusDays(400)));
        } catch (FoodTrackerException e) {
            System.err.println("Error loading preset data: " + e.getMessage());
        }
    }

    // Second constructor - takes a list of items that were loaded from file
    public Inventory(List<FoodItem> loadedItems) {
        items.addAll(loadedItems);
    }

    public void addItem(FoodItem item) {
        items.add(item);
    }

    public boolean removeItem(int id) {
        return items.removeIf(i -> i.getId() == id);
    }

    public FoodItem getItemById(int id) {
        for (FoodItem i : items) {
            if (i.getId() == id) return i;
        }
        return null;
    }

    public List<FoodItem> getAllItems() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    // returns a copy of the items list for saving
    public List<FoodItem> getItems() {
        return new ArrayList<>(items);
    }

    // sorts items so expired ones show up first, then by expiration date
    public List<FoodItem> getItemsSortedByExpiration(LocalDate today) {
        List<FoodItem> copy = new ArrayList<>(items);
        copy.sort(Comparator.comparing((FoodItem fi) -> !fi.isExpired(today))
                .thenComparing(FoodItem::getExpirationDate));
        return copy;
    }

    // finds all items that are already expired
    public List<FoodItem> getExpiredItems(LocalDate today) {
        List<FoodItem> res = new ArrayList<>();
        for (FoodItem i : items) {
            if (i.isExpired(today)) res.add(i);
        }
        return res;
    }

    // finds items that will expire within the given number of days
    public List<FoodItem> getExpiringSoonItems(LocalDate today, int daysThreshold) {
        List<FoodItem> res = new ArrayList<>();
        for (FoodItem i : items) {
            if (!i.isExpired(today) && i.isExpiringSoon(today, daysThreshold)) res.add(i);
        }
        return res;
    }

    public boolean updateItem(FoodItem updated) {
        FoodItem found = getItemById(updated.getId());
        if (found == null) return false;
        found.setName(updated.getName());
        found.setQuantity(updated.getQuantity());
        found.setUnit(updated.getUnit());
        found.setExpirationDate(updated.getExpirationDate());
        return true;
    }

    // gets the names of all non-expired items for meal suggestion matching
    public Set<String> getAvailableIngredientNames(LocalDate today) {
        Set<String> names = new HashSet<>();
        for (FoodItem i : items) {
            if (!i.isExpired(today)) {
                names.add(i.getName().trim().toLowerCase());
            }
        }
        return names;
    }
}
