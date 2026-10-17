# Security and privacy

## Android

- The manifest declares `INTERNET`. Camera and microphone permissions were not supported by inspected source behavior and have been removed.
- Audio selection uses `ACTION_OPEN_DOCUMENT`; broad read/write storage permission prompts were removed. Persistable URI grants are retained when the document picker offers them.
- Cleartext traffic is enabled only in `src/debug/AndroidManifest.xml` for local development. Release builds do not inherit the debug overlay. Use HTTPS for a deployed API.
- The Retrofit client no longer logs request/response bodies. This avoids exposing shared workout content and user-entered data through body-level debug logs.
- The debug base URL defaults to the emulator host bridge. Override it locally with `BOXING_API_BASE_URL`; do not commit private or machine-specific endpoints.

## Backend and local database

- MongoDB URI and server port are externalized as `MONGODB_URI` and `SERVER_PORT`.
- Compose exposes ports only on `127.0.0.1` and uses a named volume. Its checked-in default credentials are explicitly local-only and intended for a disposable development database; override them using an untracked local `.env` file. Never reuse these values for production.
- The API has no authentication, authorization, TLS termination, rate limiting, or ownership boundaries. Do not expose it publicly or store sensitive data.
- Request validation constrains required fields and basic timing ranges. Validation does not yet enforce unique names or matching move/duration array lengths.
- No credentials or signing keys are required by the checked-in application configuration. No production credential rotation or database access was attempted.

## Repository hygiene

The tracked release APK and its output metadata were generated build artifacts, not a documented release workflow, and were removed. Tracked IDE workspace state contained personal emulator/project paths; those files were removed from both Android IDE directories. Ignore rules now exclude local environment files, signing material, Android/Maven build outputs, and IDE state.

The initial commit also contains a hardcoded public API address and weak MongoDB development credentials in the old Compose configuration. The current app uses a configurable emulator/LAN development URL, and current Compose uses local-only development defaults. Historical IDE metadata also included personal machine paths. These values remain in Git history because history rewriting was explicitly out of scope; they are not reproduced here. No production use of the historical database credentials was established. If the old API or database is still active or controlled by you, verify and retire or rotate its access outside this repository workflow. Do not publish signing material or embed production secrets in the Android client.

A keyword scan of the single available commit found those development/configuration artifacts but no committed signing key or production secret. This was not a dedicated secret-scanning service and is not a guarantee that every historical value is safe.
