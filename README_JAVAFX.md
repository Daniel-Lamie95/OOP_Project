JavaFX setup and run instructions for this project

Requirements
- JDK 17 or later (recommended adoptopenjdk or Temurin)
- Gradle (optional; the wrapper can be added by the user) or use local JavaFX SDK

Using Gradle (recommended)
1. Install Gradle or use Gradle wrapper.
2. From project root, run:
   gradle run

The provided build.gradle uses the `org.openjfx.javafxplugin` to download JavaFX modules automatically.

Manual run with JavaFX SDK (if not using Gradle)
1. Download JavaFX SDK for your platform from https://gluonhq.com/products/javafx/
2. Unzip to a folder, e.g., C:\javafx-sdk-20
3. Compile sources:
   javac --module-path "C:\javafx-sdk-20\lib" --add-modules javafx.controls -d out src\\*.java
4. Run:
   java --module-path "C:\javafx-sdk-20\lib" --add-modules javafx.controls -cp out Main

Notes
- The project sources are in `src/` and the main class is `Main` in the default package.
- If your IDE (IntelliJ) is used, import as Gradle project or configure JavaFX SDK in Project Structure.

