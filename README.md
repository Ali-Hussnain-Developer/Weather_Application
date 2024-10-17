1. Instructions on How to Set Up and Run the Project
Pre-requisites:
* Android Studio: Make sure Android Studio (preferably the latest stable version) is installed.
* Java Development Kit (JDK): JDK 8 or higher.
* Gradle: Ensure you sync the project with Gradle once it's imported.
Project Setup:
1. Clone the Repository: Clone the project from the Git repository 
2. Open in Android Studio:
    * Open Android Studio and select Open an Existing Project.
    * Navigate to the cloned project directory and open it.
    * Wait for Android Studio to index and set up the project.
3. Sync Gradle: The project uses Gradle for dependency management. Ensure that all dependencies (like Realm) are properly installed by syncing the project.
4. Run the Project:
    * Select a device or emulator to run the app.
    * 
2. Explanation of Design Choices and Challenges Faced
Design Choices:
1. MVVM Architecture:
    * I implemented the Model-View-ViewModel (MVVM) architecture to maintain a clean separation of concerns.
    * ViewModel is used to handle business logic and interact with the Realm database in a way that keeps the UI (Fragment) decoupled from the data layer.
    * Observers (LiveData) are used to update the UI when data changes, ensuring that the UI remains reactive and responsive.
2. Realm for Local Storage:
    * I chose Realm for local storage due to its ease of use, efficiency, and ability to handle complex queries efficiently on mobile devices.
    * Realm offers easy-to-use database operations and requires less boilerplate code compared to SQLite.
3. Coroutines for Async Operations:
    * I used Kotlin Coroutines with Dispatchers.IO to handle Realm database operations off the UI thread, ensuring a smooth user experience without blocking the main thread.
    * Coroutine scopes in ViewModel (viewModelScope) ensure that background tasks are automatically canceled when the ViewModel is cleared, avoiding memory leaks.
4. Retrofit for API Integration:
    * I used Retrofit for Api Integration .The UI dynamically updates based on data coming from Realm using LiveData, and Data coming from Server Using Flows ensuring real-time synchronization between the database and the interface. or server and the interface


Challenges Faced:
1. Realm Threading Issues:
    * One challenge was handling Realm objects across threads. Realm objects are thread-bound, so I had to carefully ensure that Realm operations are done on the correct thread. This was resolved by using coroutines and ensuring the Realm instance was closed on the thread it was opened.
