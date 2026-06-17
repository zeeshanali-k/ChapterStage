# ChapterStage Frontend Revision Plan

Date: 2026-06-17
Audience: KMP frontend implementation agents
Related handoff: `chapterstage_frontend_kmp_handoff.md`
Status: Revision checklist plus implementation notes. This file does not replace
the handoff yet.

## Purpose

The original frontend handoff is broadly aligned with the ChapterStage product
flow, but it is stale against the backend and against the actual frontend
platform matrix. This plan lists the frontend-side revisions needed to align the
KMP app with the current backend contract, and separates them from backend gaps
that should be fixed before the frontend depends on them.

The frontend should still act as the control panel and viewer:

- create a chapter from text or upload
- configure generation
- start a generation job
- show progress and trace
- open the hosted generated experience URL

The frontend should not parse or reimplement the generated mini-site renderer.

## Current Frontend Repo Reality

The current KMP repo is already broader than the old handoff describes:

- `shared`
- `androidApp`
- `iosApp`
- `desktopApp`
- `webApp`
- shared source sets for Android, iOS, JVM desktop, JS, and Wasm JS

Revision work should therefore target mobile, desktop, and web explicitly.
Keep shared business logic in `shared/src/commonMain`, and isolate true platform
differences behind interfaces or small platform implementations.

## Verified Backend Contract

All API paths below are relative to `API_BASE_URL`, whose local default is:

```text
http://localhost:8000/api/v1
```

Android emulator usually needs:

```text
http://10.0.2.2:8000/api/v1
```

### Health

```text
GET /health
```

Response:

```json
{
  "status": "ok",
  "version": "0.1.0"
}
```

### Create Text Chapter

```text
POST /chapters/text
Content-Type: application/json
```

Request:

```json
{
  "book_title": "optional",
  "chapter_title": "optional",
  "text": "500 to 80000 characters"
}
```

Response:

```json
{
  "chapter_id": "...",
  "book_id": "...",
  "title": "...",
  "source_type": "text",
  "created_at": "..."
}
```

### Upload Chapter

```text
POST /chapters/upload
Content-Type: multipart/form-data
```

Fields:

- `file`: required binary part
- `book_title`: optional form field
- `chapter_title`: optional form field

Supported file extensions:

- `.txt`
- `.pdf`

Limits:

- text content: 500 to 80000 characters
- file size: default 20 MB
- PDFs must contain extractable text; scanned PDFs have no OCR in the backend

### Start Generation

```text
POST /generation-jobs
Content-Type: application/json
```

Request:

```json
{
  "chapter_id": "...",
  "audience_level": "beginner",
  "experience_style": "visual_story",
  "target_screen_count": 6,
  "enable_auto_brainstorm": true
}
```

Supported `audience_level` values:

- `beginner`
- `intermediate`
- `expert`

Supported `experience_style` values:

- `visual_story`
- `lecture_mode`
- `concept_map_first`
- `quiz_first`
- `case_study`

Response:

```json
{
  "job_id": "...",
  "chapter_id": "...",
  "status": "queued",
  "status_url": "http://localhost:8000/api/v1/generation-jobs/...",
  "events_url": "http://localhost:8000/api/v1/generation-jobs/.../events"
}
```

### List Recent Jobs

The backend supports recent job listing now. The old handoff does not document
this correctly.

```text
GET /generation-jobs?limit=20&offset=0
```

Response:

```json
{
  "jobs": [
    {
      "job_id": "...",
      "chapter_id": "...",
      "status": "...",
      "progress": 0.72,
      "current_step": "Assembling modular chapter site.",
      "band_room_id": "...",
      "experience_id": "...",
      "public_url": "...",
      "error": null,
      "created_at": "...",
      "updated_at": "..."
    }
  ],
  "limit": 20,
  "offset": 0
}
```

There is no `GET /generation-jobs/recent` endpoint.

### Fetch Job Status

```text
GET /generation-jobs/{job_id}
```

Response shape is the same job object used inside `JobListResponse.jobs`.

Important nullable fields:

- `current_step`
- `band_room_id`
- `experience_id`
- `public_url`
- `error`

Known statuses observed in the backend today:

- `queued`
- `extracting`
- `creating_band_room`
- `building_site`
- `publishing`
- `completed`
- `failed_agent_workflow`

### Stream Job Events

```text
GET /generation-jobs/{job_id}/events
Accept: text/event-stream
```

Current event names actually published by the backend:

- `job_progress`
- `agent_message`
- `agent_error`
- `experience_ready`
- `job_failed`

Events currently not implemented by the backend:

- `brainstorm_variant`
- `validation_report`
- `heartbeat`
- `cancelled`

Payloads include at least:

```json
{
  "job_id": "...",
  "created_at": "..."
}
```

`job_progress` data:

```json
{
  "job_id": "...",
  "created_at": "...",
  "status": "extracting",
  "progress": 0.1,
  "message": "Preparing chapter text."
}
```

`agent_message` data:

```json
{
  "job_id": "...",
  "created_at": "...",
  "agent_name": "structure",
  "title": "structure to brainstorm",
  "message": "Delivered knowledge_pack envelope."
}
```

`agent_error` data:

```json
{
  "job_id": "...",
  "created_at": "...",
  "agent_name": "...",
  "title": "...",
  "message": "...",
  "payload": {}
}
```

`experience_ready` data:

```json
{
  "job_id": "...",
  "created_at": "...",
  "experience_id": "...",
  "public_url": "http://localhost:8000/public/experiences/.../index.html"
}
```

`job_failed` data:

```json
{
  "job_id": "...",
  "created_at": "...",
  "status": "failed_agent_workflow",
  "progress": 0.18,
  "message": "...",
  "error": {
    "code": "AGENT_WORKFLOW_FAILED",
    "message": "..."
  }
}
```

The stream terminates on `experience_ready` or `job_failed`.

### Fetch Agent Trace

```text
GET /generation-jobs/{job_id}/trace
```

Response:

```json
{
  "job_id": "...",
  "band_room_id": "...",
  "events": [
    {
      "id": "...",
      "agent_name": "structure",
      "event_type": "handoff",
      "title": "structure to brainstorm",
      "message": "Delivered knowledge_pack envelope.",
      "payload": {},
      "created_at": "..."
    }
  ]
}
```

`payload` is an object and should be represented as `JsonObject` or `JsonElement`,
not as a string.

### Fetch Experience Metadata

```text
GET /experiences/{experience_id}
```

Response:

```json
{
  "experience_id": "...",
  "job_id": "...",
  "public_url": "...",
  "metadata": {},
  "created_at": "..."
}
```

The current frontend DTO named `ExperienceMetadataResponse` is not aligned with
this shape.

### Reader Progress

The backend has anonymous global reader-progress endpoints. The generated site
uses these itself, but the frontend may also use them for resume cards or viewer
state.

```text
GET /experiences/{experience_id}/progress
PUT /experiences/{experience_id}/progress
```

PUT request:

```json
{
  "current_screen_id": "screen-2",
  "completed_screen_ids": ["screen-1", "screen-2"],
  "last_checkpoint": "checkpoint-2",
  "interaction_state": {}
}
```

Response:

```json
{
  "experience_id": "...",
  "current_screen_id": "screen-2",
  "completed_screen_ids": ["screen-1", "screen-2"],
  "last_checkpoint": "checkpoint-2",
  "interaction_state": {},
  "updated_at": "..."
}
```

No auth is required. Progress is scoped only by `experience_id`.

## Frontend Revisions To Make Now

## Implementation Status: 2026-06-17

Completed in the current KMP revision:

- `ChapterStageApi.getRecentJobs()` now calls
  `GET /generation-jobs?limit=20&offset=0`.
- Backend job progress values in the `0.0..1.0` range are mapped to UI percent.
- Nullable `current_step` is accepted and mapped to a readable fallback label.
- Recent-job rows preserve `experience_id`, `public_url`, `progress`, current
  step, and error message.
- Completed recent jobs open the actual backend `public_url`/viewer state
  instead of falling back to demo content.
- The viewer screen now carries the real job/chapter title when available and
  shows an explicit hosted-experience handoff instead of a hardcoded sample
  lesson preview.
- Failed recent jobs route to progress with a visible failure message.
- Trace and SSE payloads accept arbitrary JSON objects via `JsonElement` and
  convert them to previewable text for the timeline.
- Backend error `details` accepts arbitrary JSON instead of only string maps.
- Runtime API base URL is generated into `AppFlavor.API_BASE_URL`.
  Default: `http://localhost:8000/api/v1`.
  Override example:

```bash
./gradlew :shared:compileKotlinJvm \
  -Pflavor=prod \
  -PapiBaseUrl=http://localhost:8000/api/v1
```

Still pending before a fully polished integration:

- Add explicit reader-progress API methods if the shell app wants resume cards.
- Add platform-specific browser/WebView launching strategy where desired. The
  current shell opens the backend `public_url` externally and avoids pretending
  that the generated mini-site is rendered in-app.
- Add richer recent-job display metadata if the backend later returns title,
  book, and style directly.
- Add cancellation/retry UI only after backend endpoints exist.

### 1. Correct Platform Scope

Update the handoff and implementation assumptions from Android/iOS-only to:

- Android app
- iOS app
- desktop JVM app
- web JS/Wasm app

Recommended platform viewer behavior:

- Android: top-level Android WebView can open the generated `public_url`.
- iOS: top-level WKWebView can open the generated `public_url`.
- Desktop: prefer external browser unless a stable desktop WebView dependency is
  intentionally adopted.
- Web: do not embed the generated experience in an iframe. The backend sends
  frame-denial headers and CSP `frame-ancestors 'none'`. Open the `public_url` in
  a new browser tab/window.

### 2. Fix API Client Paths

Current frontend code should use:

```text
GET /generation-jobs?limit=&offset=
```

not:

```text
GET /generation-jobs/recent
```

Keep typed service methods in the API layer. Repositories should not call Ktor
directly.

### 3. Fix DTOs

Revise DTOs to match the verified backend:

- Add `HealthResponse`.
- Make `GenerationJobResponse.currentStep` nullable.
- Replace the current recent-jobs DTO with `JobListResponse`.
- Replace `ExperienceMetadataResponse` with actual `ExperienceResponse`.
- Add `ReaderProgressUpdate` and `ReaderProgressResponse`.
- Add `payload: JsonObject = JsonObject(emptyMap())` to trace event DTOs.
- Change `ApiErrorBody.details` from `Map<String, String>` to `JsonElement`,
  `JsonObject`, or another arbitrary JSON representation.
- Treat all backend datetimes as strings at DTO boundaries, then map to domain
  time types only if the app already has a cross-platform time strategy.

### 4. Fix Upload Support

Ensure multipart uploads use:

- binary part key: `file`
- optional form field: `book_title`
- optional form field: `chapter_title`

Frontend validation should mirror backend defaults:

- text min length: 500 characters
- text max length: 80000 characters
- file extensions: `.txt`, `.pdf`
- file max size: 20 MB unless environment config says otherwise

PDF messaging should say extractable-text PDF only. Scanned PDFs are not
supported by the backend today.

### 5. Fix SSE Parsing And State Mapping

Model the SSE stream as:

```kotlin
sealed interface GenerationStreamEvent {
    data class JobProgress(...) : GenerationStreamEvent
    data class AgentMessage(...) : GenerationStreamEvent
    data class AgentError(...) : GenerationStreamEvent
    data class ExperienceReady(...) : GenerationStreamEvent
    data class JobFailed(...) : GenerationStreamEvent
    data class Unknown(val event: String?, val data: JsonElement) : GenerationStreamEvent
}
```

Required behavior:

- parse `event:` and `data:` lines
- decode data JSON by event type
- update progress from `job_progress`
- append visible agent timeline items from `agent_message` and `agent_error`
- set final `experienceId` and `publicUrl` from `experience_ready`
- set failure state from `job_failed`
- when SSE fails, poll `GET /generation-jobs/{job_id}` every 2 seconds
- do not wait for `heartbeat`, because backend does not publish it

### 6. Fix Error Mapping

Frontend user-facing error mapping should include the full backend stable set:

- `INVALID_REQUEST`
- `INVALID_FILE_TYPE`
- `FILE_TOO_LARGE`
- `EXTRACTION_FAILED`
- `CHAPTER_TOO_SHORT`
- `CHAPTER_TOO_LONG`
- `JOB_NOT_FOUND`
- `EXPERIENCE_NOT_FOUND`
- `JOB_ALREADY_RUNNING`
- `BAND_ROOM_CREATE_FAILED`
- `AGENT_WORKFLOW_FAILED`
- `SITE_VALIDATION_FAILED`
- `PUBLISH_FAILED`
- `INTERNAL_ERROR`

Do not assume `details` values are strings.

### 7. Fix Local URL Handling

The backend emits `status_url`, `events_url`, and `public_url` based on backend
environment variables.

For local development, prefer configuring the backend per target:

```text
API_BASE_URL=http://10.0.2.2:8000
PUBLIC_SITE_BASE_URL=http://10.0.2.2:8000/public/experiences
```

when testing Android emulator.

If this is not practical, the frontend may apply a debug-only local URL rewrite:

- `localhost` to `10.0.2.2` for Android emulator
- keep `localhost` for iOS simulator and desktop
- use the actual host origin for web where possible

This rewrite must be debug-only and must not alter production URLs.

### 8. Add Reader Progress Client Support

Even though the generated site manages its own progress, add API support for:

- `getReaderProgress(experienceId)`
- `updateReaderProgress(experienceId, request)`

Frontend usage can be minimal:

- show resume state on a generated experience card
- clear stale viewer assumptions when progress is blank

Do not add auth. Backend progress is anonymous and globally scoped per
experience.

### 9. Keep Generated Site Rendering Delegated

The app can show preview cards and metadata, but should not fetch or parse:

- `manifest.json`
- screen JSON
- generated `script.js`
- generated HTML internals

Opening `public_url` is the integration boundary.

## Backend Gaps The Frontend Should Not Depend On Yet

These are product/API gaps to fix on the backend before the frontend treats them
as real contracts:

- cancellation endpoint and `cancelled` status/event
- first-class retry/regenerate endpoint
- `heartbeat` SSE event
- `brainstorm_variant` SSE event
- `validation_report` SSE event
- richer trace payloads beyond handoff metadata
- Band room deep link URL, not only `band_room_id`
- richer recent-job rows with title/book/style if the recent jobs screen needs
  display metadata without a second query

Until those exist, the frontend should:

- create a new generation job for retry
- use polling as the stream fallback
- render unknown SSE events non-fatally
- use job status rows for recent jobs

## Suggested Implementation Order

1. Update DTOs and JSON tests against backend sample payloads.
2. Fix `ChapterStageApi.getRecentJobs()` to call `/generation-jobs`.
3. Add progress endpoint methods and DTOs.
4. Fix `ExperienceResponse` mapping and generated-experience card state.
5. Fix SSE event sealed model and mappers.
6. Add debug-only local URL rewrite helper for public URLs.
7. Update Android/iOS/desktop/web viewer strategies.
8. Update UI states for nullable `currentStep` and richer error mapping.
9. Update the original handoff after implementation details settle.

## Verification Checklist

Run the narrowest checks after each implementation batch:

```text
./gradlew :shared:jvmTest
./gradlew :shared:compileKotlinJvm
./gradlew :desktopApp:run
./gradlew :webApp:wasmJsBrowserDevelopmentExecutableDistribution
./gradlew :androidApp:assembleDebug
```

Also verify manually:

- text chapter creation works
- txt upload works
- PDF with extractable text works
- scanned/encrypted PDF shows a friendly extraction error
- job progress works with SSE
- polling fallback works when SSE is unavailable
- trace screen shows backend handoff events
- generated URL opens correctly on Android, iOS, desktop, and web
- web target opens generated URL in a new tab/window, not an iframe
