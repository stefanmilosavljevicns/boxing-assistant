# Interview guide

## Two-minute overview

Boxing Assistant is an older Java Android app that helps a boxer or coach create round-based training sessions. It has two local timer flows: a basic round/rest/preparation timer and a combination timer that advances through timed moves. Room stores timer presets, combination plans, and selected music. A small Spring Boot API backed by MongoDB shares workout templates; Android imports them into local storage and can publish local plans. The app is not a cloud account or server-driven workout product.

## How to explain the implementation

- **Android structure:** `MainActivity` hosts a Navigation Component graph. XML layouts bind to Java fragments. Room databases are separated by timer presets, combinations, and music. ViewModels share selected configuration and edit state.
- **Timer behavior:** the basic timer runs preparation, fight, rest, and completion phases using `CountDownTimer`, with `SoundPool` cues and pause/reset controls. The combination timer additionally displays moves and advances their millisecond durations within the round. Process-death restoration and background timing accuracy are not established.
- **Backend:** `WorkoutController` maps REST requests to a Spring Data Mongo repository. `Workout` is stored in the Mongo `Workout` collection. The API uses a single model shape for JSON and persistence.
- **Client integration:** Retrofit calls `getAllNames`, `getWorkout/{name}`, `addWorkout`, and `updateWorkout/{name}`. `WebWorkouts` imports templates to Room; the workout chooser publishes by name lookup and POST/PUT. The backend also exposes list-all and delete, though the Android interface does not declare those calls.
- **Stack rationale:** this is the project's existing Java/XML/Room/Retrofit and Spring/Mongo stack. It keeps the original app recognizable and avoids an unnecessary migration.

## Issues found and changes made

- Backend lookup projected the name out, the preparation-minute wire field disagreed with Android's model, and DELETE treated a name as a Mongo ID. These were corrected and regression-covered.
- Request validation and concise malformed/invalid request responses were added. POST now returns `201 Created`; update retains the stored identifier.
- Backend Mongo and port settings are environment-driven. Compose now builds the app and runs Mongo with localhost-only ports and local-only development defaults.
- Android had a hardcoded public HTTP base URL and body logging. These are removed in favor of a configurable debug base URL and no body logging.
- The app requested camera, microphone, and broad storage permissions without matching source functionality. Music uses the system picker, so its broad storage gate was removed; persisted picker grants are retained where offered.
- The tracked APK and generated release metadata/IDE state were removed because no release distribution workflow is documented.

## Testing and trade-offs

Backend controller tests use MockMvc and a mocked repository, covering names, lookup, missing records, create, update, delete, invalid timing, malformed JSON, wire naming, and ID preservation. They avoid a live database. Android currently has starter tests and no extracted pure-Java timer state machine; hardware/emulator lifecycle behavior remains manual. Build commands and the environment's actual build results should be reported from the current checkout, not assumed.

The project uses a direct controller-to-repository design because the API is small. Introducing service/DTO layers would be reasonable if validation, transaction behavior, authentication, or additional consumers grew, but is not justified by size alone today. The Android toolchain is old; a deliberate staged upgrade is a follow-up after reproducing its current build.

## Likely follow-up questions

1. How would you model the timer as a deterministic state machine and test preparation, round, rest, pause, resume, and completion transitions?
2. How would you preserve accurate timers during sleep, rotation, and process death?
3. What should the API contract return when a name is duplicated, and should names be unique or replaced by stable IDs?
4. How would you distinguish request models from Mongo documents while keeping compatibility with existing documents?
5. How would you add authentication, ownership, authorization, and safe public deployment?
6. How would you test Mongo serialization and repository queries against an isolated disposable Mongo instance?
7. How would the Android UI represent loading, empty, offline, HTTP-error, and retry states consistently?
8. What Gradle/AGP/JDK/SDK sequence would you use to modernize the Android build without combining it with a UI rewrite?
9. How should music URI permissions and media playback behave after app restart and fragment destruction?
10. How would you enforce consistency between `moves` and `movesTime` and migrate the legacy Mongo preparation field?

## Next improvements

1. Reproduce Android builds with a compatible JDK/SDK and add CI for the verified toolchain.
2. Extract timer transition logic into a pure Java component and add boundary/state-transition tests.
3. Add isolated Mongo integration tests and a unique-name/index decision.
4. Improve Retrofit error and loading/empty-state handling, then add API mapping tests.
5. Plan a small Android toolchain upgrade after obtaining a clean baseline.
6. Add authentication and HTTPS only when a real deployment and ownership model are defined.
