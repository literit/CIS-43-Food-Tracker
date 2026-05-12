# CIS 43 Food Tracker

Java app to manage kitchen inventory. Tracks expiration. Shows alerts. Suggests meals based on what you have.

## Features

### Track food items

Add, edit, delete items. Each item has name, quantity, unit, expiration date.

### Expiration tracking

Items split into perishable and non perishable. Each type has its own warning window.

### Expiration alerts

On startup, the app shows expired and soon to expire items.

### Color coded table

* Red means expired
* Yellow means close
* Green means safe

### Meal suggestions

Matches your current non expired items to simple recipes.

### Search and filter

Search by name. Filter by expiration status.

### Data persistence

Saves to CSV. Loads on startup.

## How to run

```
javac *.java  
java InventoryGUI
```

Java 8 or higher. No external libraries.

## Team contributions

### Krish, Food item tracking

Built the base data model.

* **FoodItem.java**
  Abstract class. Holds name, quantity, unit, expiration date. Handles validation. Implements Expirable.

* **Expirable.java**
  Interface for expiration checks.

### Uri, Expiration logic

Handled item categories and thresholds.

* **PerishableItem.java**
  Extends FoodItem. Uses 3 day warning window.

* **NonPerishableItem.java**
  Extends FoodItem. Uses 30 day warning window.

* **FoodTrackerException.java**
  Custom exception for validation and file errors.

### Vikram, GUI and alerts

Built the UI and notification system.

* **InventoryGUI.java**
  Main window. Handles UI, events, color coded table, search, alerts.

* **Inventory.java**
  Stores items. Adds, removes, sorts. Finds expired and soon items.

### Javen, meals and storage

Handled suggestions and file I O.

* **Meal.java**
  Defines meals with ingredient lists. Includes preset meals.

* **MealSuggester.java**
  Checks available items against recipes.

* **DataStore.java**
  Reads and writes CSV using Scanner and Formatter with proper error handling.

## Project structure

```
CIS 43 Food Tracker
├── Expirable.java
├── FoodItem.java
├── PerishableItem.java
├── NonPerishableItem.java
├── FoodTrackerException.java
├── Inventory.java
├── InventoryGUI.java
├── Meal.java
├── MealSuggester.java
├── DataStore.java
├── Project Design Doc.md
└── README.md
```
