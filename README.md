An Android mobile app that displays current exchange rates and historical values ​​for previous days. 
Users can track exchange rate dynamics and compare different currencies.

App Description
The app displays a list of currencies with current exchange rates, as well as historical data for the previous several days.
Currencies are visualized in a grid using CardView or Compose Card. 
The app is further expandable with local storage via Room, server support, MVVM architecture, DI, historical data charts, 
and push notifications for significant exchange rate changes.

Part 1 - of the requirements
Implement a main screen with a list of currencies and the current exchange rate.
Implement the following CRUD operations:
Add a new currency to the list
Edit a currency (icon, three-letter identifier)
Delete a currency
A "random" exchange rate may be generated for the added currency.
Use the following visual representation:
LazyVerticalGrid (Jetpack Compose).
For displaying each currency, use:
Card (Jetpack Compose).
Displayed information includes:
three-letter currency identifier;
current exchange rate;
currency icon (required only for standard currencies).
Data can be stored only in the application's memory (not persisted across restarts).
Data entry should be implemented through a dialog box.

Part 2 – Fragments and Local Storage
Functional Requirements
Divide the app into at least two fragments:
a list of currencies with current rates;
historical rates for the selected currency.
Implement navigation between fragments.
Pass data between fragments via Bundle or SafeArgs (if using the Navigation Component).
Implement local storage of the currency list between app restarts in SharedPreferences.
Add the ability to mark selected currencies as "favorites" and display them at the top of the screen BEFORE the regular ones in the grid.
The "favorites" status should also be saved in SharedPreferences.
Use a snackbar to confirm actions:
when selecting a currency;
when updating historical data.
