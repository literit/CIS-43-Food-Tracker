import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Inventory {
    private final List<FoodItem> items = new ArrayList<>();

    public Inventory() {
        // built-in preset inventory
        LocalDate today = LocalDate.now();
        items.add(new FoodItem("pasta", 1, "box", today.plusDays(30)));
        items.add(new FoodItem("tomato", 5, "count", today.plusDays(1)));
        items.add(new FoodItem("cheese", 0.5, "lb", today.plusDays(5)));
        items.add(new FoodItem("bread", 1, "loaf", today.plusDays(4)));
        items.add(new FoodItem("butter", 0.25, "lb", today.plusDays(60)));
        items.add(new FoodItem("lettuce", 1, "head", today.minusDays(2)));
        items.add(new FoodItem("cucumber", 2, "count", today.plusDays(6)));
        items.add(new FoodItem("eggs", 12, "count", today.plusDays(2)));
        items.add(new FoodItem("milk", 1, "quart", today.minusDays(1)));
        items.add(new FoodItem("rice", 2, "lb", today.plusDays(365)));
        items.add(new FoodItem("broccoli", 1, "head", today.plusDays(3)));
        items.add(new FoodItem("carrot", 6, "count", today.plusDays(7)));
        items.add(new FoodItem("soy sauce", 1, "bottle", today.plusDays(400)));
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

    public List<FoodItem> getItemsSortedByExpiration(LocalDate today) {
        List<FoodItem> copy = new ArrayList<>(items);
        copy.sort(Comparator.comparing((FoodItem fi) -> !fi.isExpired(today))
                .thenComparing(FoodItem::getExpirationDate));
        return copy;
    }

    public List<FoodItem> getExpiredItems(LocalDate today) {
        List<FoodItem> res = new ArrayList<>();
        for (FoodItem i : items) {
            if (i.isExpired(today)) res.add(i);
        }
        return res;
    }

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
