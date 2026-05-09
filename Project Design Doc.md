Core Features to Implement

1. Add, edit, delete, and view food items the user owns.  
2. Store expiration date for each food item.  
3. Show alerts for expired items and items expiring soon.  
4. Sort food items by expiration date.  
5. Suggest meals from a premade meal database based on available ingredients.

Functional Requirements:  
Food item tracking

* User can add a food item with name, quantity, unit, category, expiration date.  
* User can update a food item.  
* User can remove a food item.  
* User can view all food items.

Expiration tracking

* Program compares each food item's expiration date to today's date.  
* Program marks items as expired, expiring soon, or fresh.  
* Expiring soon uses a configurable number of days (example: 3\)

Notifications

* On startup, program checks all food items.  
* Program prints alerts for expired items and items expiring within N days.  
* User can manually run a check.

Meal suggestion

* Program loads meals from a database file.  
* Each meal has a name and ingredient list.  
* Program suggests meals the user can fully make.

Sorting

* User can view food items sorted by expiration date.  
* Expired items appear first.

Class Design

FoodItem:

* Fields: id, name, quantity, unit, expirationDate  
  Methods: getters/setters, isExpired(today), isExpiringSoon(today, daysThreshold), daysUntilExpiration(today), toString()

Meal:

* Fields: id, name, ingredients  
  Methods: getters/setters, toString()

Inventory

* Fields: list of FoodItem  
  Methods: addItem, removeItem, getItemById, getAllItems, getItemsSortedByExpiration, getExpiredItems, getExpiringSoonItems, updateItem, getAvailableIngredientNames

MealSuggester

* Methods: suggestMeals(meals, availableIngredients)

Matching Rules

* Match by ingredient name only.  
* Ignore quantity.  
* Only non-expired items count.

Main Program Flow  
Startup:

1. Load food items  
2. Load meals  
3. Check expiration  
4. Print alerts  
5. Show menu

Menu:

* Add item  
* View items  
* Edit item  
* Delete item  
* Sort by expiration  
* Check alerts  
* Meal suggestions  
* Save and exit

Meal suggestion logic:

* Put inventory names into a set  
* For each meal, check if all ingredients exist in the set