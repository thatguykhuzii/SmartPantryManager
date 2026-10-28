# Smart Pantry Manager — Initial Design

Author: Siphosethu Khuzwayo

Status: Proposed design. Implementation and testing are still in progress.

## 1. Screen structure

The app will use five Activities:

### PantryListActivity
- Display saved pantry ingredients.
- Show each ingredient's name, quantity and unit.
- Provide an Add Ingredient button.
- Allow the user to select an ingredient for editing.
- Allow deletion with confirmation.
- Show a helpful message when the pantry is empty.

### AddEditIngredientActivity
- Use the same form for adding and editing ingredients.
- Require an ingredient name, quantity and unit.
- Reject blank names and invalid or non-positive quantities.
- Save valid changes to the database.
- Return to the pantry list after saving.

### SuggestedRecipesActivity
- Read the current pantry and stored recipes.
- Display only recipes that satisfy strict matching.
- Show a helpful message when no recipes qualify.
- Open the selected recipe's detail screen.

### RecipeDetailActivity
- Display the selected recipe's name.
- Display all required ingredients, quantities and units.
- Display preparation steps.

### SettingsActivity
- Allow the user to choose whether the pantry list is sorted
  by ingredient name in ascending or descending order.
- Preserve the selected preference between app sessions.

## 2. Navigation

Pantry List
-> Add/Edit Ingredient
-> Suggested Recipes
-> Settings

Suggested Recipes
-> Recipe Detail
-> Pantry List
-> Settings

Add/Edit Ingredient
-> Return to Pantry List after saving or cancelling

Recipe Detail
-> Return to Suggested Recipes

Settings
-> Pantry List
-> Suggested Recipes

A toolbar menu will connect the main screens.
Explicit Intents will navigate between Activities.

An ingredient ID will identify a pantry item when opening Edit.
A recipe ID will identify a recipe when opening Recipe Detail.

## 3. Database approach

Use SQLite through SQLiteOpenHelper.

Database name:
smart_pantry.db

Initial database version:
1

### Table: ingredients

Purpose:
Store canonical ingredient names shared by pantry items and recipes.

Columns:
- id: INTEGER PRIMARY KEY
- canonical_name: TEXT NOT NULL UNIQUE
- measurement_type: TEXT NOT NULL

Supported measurement types:
- mass
- volume
- count

### Table: pantry_items

Purpose:
Store the ingredients currently available to the user.

Columns:
- id: INTEGER PRIMARY KEY
- ingredient_id: INTEGER NOT NULL UNIQUE
- quantity: REAL NOT NULL
- unit: TEXT NOT NULL

Relationship:
ingredient_id references ingredients.id

Validation:
- Quantity must be greater than zero.
- Unit must be compatible with the ingredient's measurement type.
- Only one pantry record is allowed per canonical ingredient.
- If an ingredient already exists, ask the user to edit its quantity.

### Table: recipes

Purpose:
Store recipe names and preparation instructions.

Columns:
- id: INTEGER PRIMARY KEY
- name: TEXT NOT NULL
- preparation_steps: TEXT NOT NULL

### Table: recipe_ingredients

Purpose:
Store the ingredient requirements for each recipe.

Columns:
- recipe_id: INTEGER NOT NULL
- ingredient_id: INTEGER NOT NULL
- required_quantity: REAL NOT NULL
- unit: TEXT NOT NULL

Primary key:
(recipe_id, ingredient_id)

Relationships:
- recipe_id references recipes.id
- ingredient_id references ingredients.id

Validation:
- Required quantity must be greater than zero.
- Unit must be compatible with the ingredient's measurement type.

### Relationships

ingredients 1 ---- 0..1 pantry_items

ingredients 1 ---- many recipe_ingredients

recipes 1 -------- many recipe_ingredients

### Initial data

Seed a collection of canonical ingredients and 20 recipes
when the database is first created.

Do not replace or reset the user's pantry every time the app opens.

## 4. Ingredient-name normalisation

Before looking up an ingredient:
- Remove leading and trailing spaces.
- Convert the name to lowercase.
- Collapse repeated internal spaces.
- Apply a small, explicit alias dictionary.

Example aliases:
- tomatoes -> tomato
- potatoes -> potato
- eggs -> egg

Do not remove the final letter "s" from every name.
Use explicit aliases to avoid changing unrelated words incorrectly.

Store and match ingredients using their canonical ingredient IDs.

## 5. Supported units

Mass:
- g
- kg

Volume:
- ml
- l

Count:
- piece

Accept simple unit aliases such as:
- grams -> g
- kilograms -> kg
- millilitres -> ml
- litres -> l
- pieces -> piece

Convert compatible quantities to base units before comparison:
- Mass: grams
- Volume: millilitres
- Count: pieces

Do not convert between mass, volume and count.
Do not assume a cup, spoon or single item has a universal mass.

## 6. Strict-matching algorithm

For each recipe:
1. Load all its ingredient requirements.
2. Exclude the recipe if its ingredient requirements are empty.
3. Look up each required ingredient in the pantry.
4. Exclude the recipe immediately if an ingredient is missing.
5. Convert compatible units to their base units.
6. Exclude the recipe if a unit is incompatible or unsupported.
7. Exclude the recipe if any available quantity is insufficient.
8. Include the recipe only if every requirement passes.

Evaluate each recipe independently.
Displaying a suggestion does not consume pantry ingredients.

## 7. Planned matching tests

- All ingredients present with sufficient quantities:
  recipe appears.

- One required ingredient missing:
  recipe does not appear.

- One required ingredient has insufficient quantity:
  recipe does not appear.

- Available quantity exactly equals required quantity:
  recipe appears.

- Compatible units, such as kilograms and grams:
  compare converted quantities.

- Supported plural alias, such as tomatoes and tomato:
  resolve to the same ingredient.

- Incompatible units:
  recipe does not appear.

- Empty pantry:
  show the no-matching-recipes message.

- Close and reopen the app:
  saved pantry data remains.