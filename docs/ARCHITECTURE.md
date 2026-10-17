# Architecture

## Android

`MainActivity` hosts the Navigation Component graph in `res/navigation/main_nav.xml`. Fragments implement the home panel, basic timer setup, round timer, combination/workout editor and chooser, shared-workout dialog, music library, and tutorial. Java classes under `ViewModel` share timer and edit state. Room provides three local stores: basic timer presets (`DataBaseTimer`), combination plans (`DatabaseCombo`), and selected music (`MusicDataBase`).

`Timer` runs configured preparation, fight, rest, and completion phases. `TimerComb` additionally advances a list of `Model` moves and their durations while a round is active. Both use `CountDownTimer`, `Handler`, `SoundPool`, and optional `MediaPlayer`. Timer UI is local; server calls do not control the timer.

`APIClient` creates Retrofit using a Gradle-configurable base URL. `APIInterface` declares calls for shared template names, GET by name, POST, and PUT. `WebWorkouts` imports returned templates into the local Room `Combo` store. `WorkoutChooser` publishes local plans to the backend (create or update, based on name lookup).

## Backend

The Spring Boot application exposes `WorkoutController` routes under `/api/v1`, uses `WorkoutRepository` (`MongoRepository<Workout, String>`) for persistence, and maps documents in the Mongo `Workout` collection. There is no service layer; the current endpoint surface and data model are small enough for direct controller-to-repository calls. Bean Validation rejects blank names, empty moves, and invalid timing ranges. `ApiExceptionHandler` returns concise 400 responses for validation and malformed JSON.

## Data flow and compatibility notes

The API exposes a workout document keyed by Mongo ID and a unique-name lookup in practice. `prepareMin` is the JSON contract expected by Android. Mongo documents retain the historical `preparetMin` field through `@Field` mapping. The DELETE path parameter is named `{name}` and is implemented as a name lookup followed by deletion of that document. The endpoint does not enforce name uniqueness; duplicate names are therefore an unresolved data-integrity risk.

The app submits workout move durations as millisecond values, and the combination timer decrements these values in 1000 ms steps. API transport is asynchronous through Retrofit callbacks. Error presentation is inconsistent in existing screens, and some callbacks ignore non-success responses.

## Design decisions

- Kept Java, XML views, Room, Retrofit, and existing navigation rather than replacing the app architecture.
- Externalized backend URLs and Mongo URI without changing the route paths.
- Retained the old Android build versions until a compatible, repeatable build baseline is established.
- Added controller-slice tests with a mocked repository, so ordinary tests do not need MongoDB.
