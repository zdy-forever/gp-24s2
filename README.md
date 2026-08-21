# SmartCity

SmartCity is an Android application for discovering local facilities, making and managing bookings, reporting issues, receiving notifications, and connecting with other users. The app uses Java/Kotlin Android components, Google Maps and Firebase Authentication, Cloud Firestore and Cloud Messaging.

## Local setup

This public repository intentionally contains no API keys, Firebase configuration files, service-account credentials, or prebuilt APKs.

1. Open `androidProgram` in Android Studio and let Gradle sync.
2. Copy `androidProgram/local.properties.example` to `androidProgram/local.properties`.
3. Set `sdk.dir` and `GOOGLE_MAPS_API_KEY` in that local file.
4. Create or select your own Firebase project, register the Android package `com.example.smartcity`, and download its `google-services.json` to `androidProgram/app/google-services.json`.
5. Enable only the Firebase products needed by the app and configure appropriate security rules before running it with real data.

If `google-services.json` is absent, the project can still be imported and compiled, but Firebase-backed functions will not work at runtime. If the Maps key is absent, the map cannot load Google map tiles.

## Security

- Never place a Firebase Admin SDK/service-account JSON file inside an Android project. Admin credentials belong only in a trusted server environment.
- Keep `google-services.json`, `local.properties`, signing keys, and generated APK/AAB files out of version control.
- Restrict Android API keys by package name and signing-certificate fingerprint in Google Cloud.
- Use a separate Firebase project with non-production data for local development.

The original course specification and project report are retained in `gpSpec` and `items` for project context. Any example accounts documented there should not be treated as active credentials.
