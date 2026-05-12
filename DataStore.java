import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

/**
 * DataStore.java
 * @author Javen
 * 
 * Handles reading and writing the inventory to a CSV file so the
 * users data doesnt disappear when they close the program.
 * Uses try-catch-finally to make sure files always get closed properly.
 */
public class DataStore {
    private static final String FILE_PATH = "inventory.csv";

    // saves all the food items to a csv file
    // format: category,id,name,quantity,unit,expirationDate
    public static void save(List<FoodItem> items) throws FoodTrackerException {
        Formatter output = null;

        try {
            output = new Formatter(FILE_PATH);

            for (FoodItem item : items) {
                output.format("%s,%d,%s,%.2f,%s,%s%n",
                        item.getCategory(),
                        item.getId(),
                        item.getName(),
                        item.getQuantity(),
                        item.getUnit(),
                        item.getExpirationDate().toString());
            }

        } catch (FileNotFoundException e) {
            throw new FoodTrackerException("Could not save inventory file: " + e.getMessage(), e);

        } finally {
            // always close the file even if something went wrong
            if (output != null) {
                output.close();
            }
        }
    }

    // reads the csv file and builds a list of FoodItem objects
    // creates PerishableItem or NonPerishableItem based on the category column
    public static List<FoodItem> load() throws FoodTrackerException {
        List<FoodItem> items = new ArrayList<>();
        Scanner input = null;

        try {
            input = new Scanner(Paths.get(FILE_PATH));

            while (input.hasNextLine()) {
                String line = input.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");

                if (parts.length < 6) {
                    continue; // skip bad lines
                }

                // pull out each field from the csv
                String category = parts[0].trim();
                int id = Integer.parseInt(parts[1].trim());
                String name = parts[2].trim();
                double quantity = Double.parseDouble(parts[3].trim());
                String unit = parts[4].trim();
                LocalDate expDate = LocalDate.parse(parts[5].trim());

                // make the right type of object based on the saved category
                if (category.equals("Perishable")) {
                    items.add(new PerishableItem(id, name, quantity, unit, expDate));
                } else {
                    items.add(new NonPerishableItem(id, name, quantity, unit, expDate));
                }
            }

        } catch (IOException e) {
            throw new FoodTrackerException("Could not read inventory file: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            throw new FoodTrackerException("Inventory file contains invalid data: " + e.getMessage(), e);
        } finally {
            // always close the scanner when done
            if (input != null) {
                input.close();
            }
        }

        return items;
    }

    // quick check to see if there's a save file to load
    public static boolean saveFileExists() {
        return Files.exists(Paths.get(FILE_PATH));
    }
}
