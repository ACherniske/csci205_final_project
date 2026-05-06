# Five Nights at Dana — CSCI 205

[![Java][java-badge]][java-url]
[![Gradle][gradle-badge]][gradle-url]
[![JavaFX][javafx-badge]][javafx-url]

## Authors

- Aiden Cherniske — Computer Engineering ’27
- Mason Barlow — Computer Engineering ’27
- Tyler Seitz — Computer Engineering ’27
- Faris Lufti — Computer Engineering ’28

## Summary

Five Nights at Dana is a JavaFX game inspired by the tension and pacing of *Five Nights at Freddy's* — reimagined inside Bucknell's Dana Engineering building.

---
## Story

It’s late. The Dana Engineering building should be empty. The cameras disagree.

You play as Professor Lily grading late at night: last through the night by monitoring camera feeds, reacting to movement across the building, and managing limited resources before the students realize you are still available for late night questions.

> Fan project / parody: This is a student course project and is not affiliated with or endorsed by the creators of *Five Nights at Freddy's*.

## Gameplay (high level)

- Monitor cameras to track movement through Dana
- React to threats using building systems (doors, vents, etc.)
- Balance safety with scarce attention/resources
- Survive multiple nights as difficulty ramps up

## Requirements

- Java 21 (recommended; this project uses JavaFX 21)
- Windows / macOS / Linux (JavaFX platform dependency is selected automatically by Gradle)

## Build & run

This project uses the Gradle Wrapper, so you don’t need a system Gradle install.

### Windows (PowerShell)

```powershell
./gradlew.bat run
```

### macOS / Linux

```bash
./gradlew run
```

### Common tasks

```bash
# Compile + run tests
./gradlew test

# Full build (runs tests, creates JAR)
./gradlew build
```

The application entry point is `org.five_nights_at_dana.DanaEngineering` (JavaFX `Application`).

## Tech stack

- Java 21
- JavaFX 21 (controls, fxml, media)
- Gradle (application plugin)
- JUnit 5

[java-badge]: https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white
[java-url]: https://adoptium.net/

[gradle-badge]: https://img.shields.io/badge/Gradle-Wrapper-02303A?logo=gradle&logoColor=white
[gradle-url]: https://gradle.org/

[javafx-badge]: https://img.shields.io/badge/JavaFX-21-2C2255
[javafx-url]: https://openjfx.io/