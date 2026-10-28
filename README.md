# Smart Pantry Manager

## Author
Siphosethu Khuzwayo

## Module
Mobile App Development 700

## Project overview
Smart Pantry Manager is a Java Android application being developed
to help users reduce food waste by tracking ingredients available
at home and suggesting recipes they can prepare using those ingredients.

## Development status
Initial Android project created and uploaded to GitHub.
Application features are not yet implemented or tested.

## Planned features
- Add, view, edit and delete pantry ingredients.
- Record ingredient names, quantities and units.
- Store pantry data between application sessions.
- Preload 20 recipes with ingredients and preparation steps.
- Suggest recipes only when every required ingredient is available
  in a sufficient quantity.
- Display recipe ingredients and preparation instructions.
- Provide a settings screen.
- Display validation errors and helpful empty-state messages.

## Strict recipe-matching rule
A recipe will qualify as a suggestion only when the pantry contains
every required ingredient in at least the required quantity.

Recipes with missing ingredients or insufficient quantities will
not appear in the Suggested Recipes list.

The implementation will include basic ingredient-name
normalisation and supported unit conversions.

## Planned technology
- Java for application code.
- XML layouts for the interface.
- SQLite through SQLiteOpenHelper for persistent application data.
- RecyclerView with custom adapters for dynamic lists.
- Git and GitHub for version control.

## Database choice
The planned database is SQLite, accessed through SQLiteOpenHelper.

The application is designed around a single user's pantry.
Cloud synchronisation is not part of the planned scope.

The database will store pantry items, recipes and recipe
ingredient requirements.

## Planned screens
1. Pantry List
2. Add/Edit Ingredient
3. Suggested Recipes
4. Recipe Detail
5. Settings

## Open the project
1. Download or clone this repository.
2. Open the project folder in Android Studio.
3. Allow Gradle sync to complete.
4. Install any required SDK components if prompted.

## Run instructions
Once a suitable Android test device is configured:
1. Select the app run configuration.
2. Select the connected test device.
3. Click Run.

The test-device setup has not yet been completed.
These instructions will be updated after a successful test run.

## Scope restrictions
This project will not include maps, GPS/location services
or payment processing.