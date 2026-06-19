This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop (JVM).

It pairs with the **ChapterStage backend** at:
https://github.com/humilityisavirtue-collab/the-cell-on-band

The agent orchestration uses the [Band](https://band.ai) platform to route work between specialist agents.

Generated for the **[Band of Agents Hackathon](https://lablab.ai/ai-hackathons/band-of-agents-hackathon)**.

### Stack

- **Kotlin Multiplatform** with **Compose Multiplatform** for shared UI across Android, iOS, Web (JS/Wasm), and Desktop (JVM).
- **Ktor** for HTTP and Server-Sent Events (SSE).
- **Koin** for dependency injection.
- **Kotlinx Serialization** for JSON handling.

### Functionality

ChapterStage turns a book chapter into an interactive learning experience. The app lets you:

- Paste chapter text or upload a PDF/TXT file.
- Configure generation settings (audience level, experience style, screen count).
- Watch a live **Band of Agents** workflow (`structure → brainstorm → visual → verifier`) generate the experience in real time, with a live trace and timer.
- Inspect the full agent trace, including expandable payloads showing what was sent to each agent.
- Open the generated interactive chapter in an embedded viewer.
- Browse and resume recent generation jobs.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- Desktop app:
  - Hot reload: `./gradlew :desktopApp:hotRun --auto`
  - Standard run: `./gradlew :desktopApp:run`
- Web app:
  - Wasm target (faster, modern browsers): `./gradlew :webApp:wasmJsBrowserDevelopmentRun`
  - JS target (slower, supports older browsers): `./gradlew :webApp:jsBrowserDevelopmentRun`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).