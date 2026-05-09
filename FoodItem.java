import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FoodItem {
    private static int nextId = 1;

    private final int id;
    private String name;
    private double quantity;
    private String unit;
    private LocalDate expirationDate;

    public FoodItem(String name, double quantity, String unit, LocalDate expirationDate) {
        this.id = nextId++;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expirationDate = expirationDate;
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

    public boolean isExpired(LocalDate today) {
        return expirationDate.isBefore(today);
    }

    public long daysUntilExpiration(LocalDate today) {
        return ChronoUnit.DAYS.between(today, expirationDate);
    }

    public boolean isExpiringSoon(LocalDate today, int daysThreshold) {
        long days = daysUntilExpiration(today);
        return days >= 0 && days <= daysThreshold;
    }
}
