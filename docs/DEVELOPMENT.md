# Development setup

## Backend

Requirements: Java 17 (Spring Boot 3.0.2 baseline), Maven 3.8.6 or compatible, and Docker Compose if using the bundled database. The Maven wrapper downloads Maven 3.8.6 when available; otherwise an installed Maven can be used.

From `backend/`:

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
```

Configure a local MongoDB using `MONGODB_URI`, for example `mongodb://localhost:27017/boxing_assistant`; `SERVER_PORT` defaults to `9093`. Do not point development or tests at a production database. To run backend and disposable local MongoDB together from `backend/`:

```powershell
docker compose up --build
docker compose down
```

Compose binds MongoDB and the API to localhost. Its default database credentials are for this local disposable Compose environment only. Optionally copy `backend/.env.example` to an untracked `backend/.env` and replace the password placeholder with a local-only value. Keep URI-reserved characters URL-encoded in a Mongo password. The named volume persists local data; remove it manually only when you intend to discard that development database.

## Android

Requirements in the checked-in project: Android Studio capable of importing AGP 4.1.3 projects, Android SDK Platform 30 and Build Tools 30.0.3, and JDK 8 through 14 to run Gradle 6.5. AGP 4.1 requires Gradle 6.5 or newer; the wrapper pins 6.5. These compatibility limits follow the [AGP release notes](https://developer.android.com/build/releases/agp-4-1-0-release-notes) and [Gradle's Java compatibility table](https://docs.gradle.org/6.5/userguide/compatibility.html). Set Android Studio's Gradle JDK to a compatible installed JDK. Android Studio installs the SDK; `local.properties` should be generated locally and is ignored by Git.

From `client/`:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

The default debug endpoint is the Android emulator's host bridge, `http://10.0.2.2:9093/`. To use a trusted LAN development server, set `BOXING_API_BASE_URL` in a local `client/gradle.properties` or pass `-PBOXING_API_BASE_URL=http://<host-address>:9093/`. Keep the trailing slash. The main manifest does not permit cleartext globally; the debug manifest enables it for local HTTP development.

## Tests and environment

Backend MVC tests use MockMvc and a mocked repository. They do not start MongoDB or contact a remote service. Android unit tests run on the local JVM; instrumentation tests require an emulator or connected test device and are not part of the ordinary unit-test command.

The repository's Android wrapper is Gradle 6.5 with AGP 4.1.3, compile SDK 30, and Java 8 source compatibility. Do not upgrade one of these in isolation without checking AGP/Gradle/JDK/SDK compatibility. The legacy baseline may need JDK 8 or 11; the build must be confirmed locally.

## Troubleshooting

- `SDK location not found`: open the project in Android Studio or create a local `local.properties` with the Android SDK path.
- Gradle fails before compilation: check the configured Gradle JDK against the legacy Gradle/AGP pair and confirm the wrapper distribution can be downloaded.
- Android cannot reach backend: start Compose, use `10.0.2.2` from the emulator, and check local firewall rules. A physical device needs a reachable host LAN address.
- Backend cannot connect to Mongo: use the Compose service URI when running in Compose; `localhost` inside the API container refers to that container, not the Mongo service.
- Port conflict: change `SERVER_PORT` and matching Compose mapping, or stop the process bound to 9093.
