# Notepad – Your Notes, Your Tasks

Native Android notes application with to-do management, alarms, tags, favourites and search.
Academic project for **Mobile Applications Development (MCA 5162)**, Manipal Institute of Technology, Manipal.

Java · XML layouts · SQLite (`SQLiteOpenHelper`) · RecyclerView · AlarmManager / BroadcastReceiver · Android notifications · no network, no Firebase, no Kotlin.

> **Build status (honest note):** this project was written without access to an Android SDK, so it has **not been compiled
> or run on a device/emulator**. The pure-Java logic (word/character counts, repeat calculation) was compiled and tested
> on a plain JVM, and all XML/resource/ID/binding references were cross-checked by script. Expect to fix a few small
> compile errors on first build in Android Studio; please report/fix them and run the test suites listed below.

## Setup
1. Install **Android Studio** (Hedgehog or newer, JDK 17).
2. `File ▸ Open…` and choose this folder. Let Gradle sync (it downloads Gradle 8.2 and Android Gradle Plugin 8.2.2).
   The Gradle wrapper *jar/scripts* are not included; Android Studio creates them, or run `gradle wrapper` once.
3. Run the **app** configuration on an emulator/device with Android 7.0 (API 24) or newer. `compileSdk/targetSdk = 34`.
4. Tests: `./gradlew test` (JVM unit tests) and `./gradlew connectedAndroidTest` (SQLite/alarm tests on a device/emulator).

## Permissions
| Permission | Why |
|---|---|
| `POST_NOTIFICATIONS` (Android 13+) | Show reminder/alarm notifications. Requested once at first launch; denial is handled (history is still recorded, an explanation + shortcut to settings is shown). |
| `SCHEDULE_EXACT_ALARM` (Android 12+) | Exact alarm delivery. If not granted, alarms fall back to `setAndAllowWhileIdle()` and the app **says so** (banner on the Alarm screen, hint on the To-Do form, row in Settings). Exact timing is never promised: battery optimisation, Doze and Do-Not-Disturb can still delay delivery. |
| `RECEIVE_BOOT_COMPLETED` | Re-register alarms after a reboot / app update. Alarms missed while the device was off are delivered on boot; repeating alarms move to their next occurrence. |

## Using the app
* **Notes tab** – summary, live search, filters (All/Notes/To-Do/Favourites), sort, pinned notes first, FAB to add a note (max **50** notes; a dialog explains the limit).
* **Add/Edit note** – title, rich text (bold, italic, underline, bullets, links), live word/character counts, favourite switch, to-do switch, alarm, tags, unsaved-changes warning.
* **View note** – full note, counts, created/modified times, note ID, quick actions (favourite, to-do, alarm, tags), delete with confirmation.
* **Search** (drawer or header icon) – keyword in title + content, date, time (hour), type, to-do status, tags, due date, favourites, sorting, clear search.
* **To-Do tab** – Pending/Completed tabs, overdue highlight, search, filters (overdue/with reminder/tag), complete/reopen, edit, delete.
* **Alarms** (drawer, note, task) – set/edit/cancel/delete, enable switch, repeat (none/daily/weekly/every N days), active alarm list.
* **Tags tab** – coloured tag cards with note counts, create/rename/recolour/delete, open a tag to see its notes, recent tags, search.
* **Favourites tab** – starred notes, filters, search, pin/unpin.
* **Notifications tab** – history with All/To-Do/Alarm filters, read/unread, mark all read, clear all, tap to open the note/task if it still exists.
* **Settings** (drawer) – theme (system/light/dark/lavender), list-view theme (default/compact/detailed), notification preferences, exact-alarm status, privacy, about, exit with confirmation.

## Architecture
```
app/src/main/java/com/mit/notepad/
  activities/     Splash, Main (bottom nav + drawer), NoteEditor, NoteView, Search, TodoEditor, Alarm, TagNotes, Settings,
                  BaseActivity (theme), NoteActions / TagDialogs / RepeatDialogs (shared UI helpers)
  fragments/      Notes, Todo, Tags, Favourites, Notifications (the five bottom-navigation tabs)
  adapters/       RecyclerView adapters (Note, Todo, Tag, Alarm, Notification)
  models/         Note, Todo, Tag, Alarm, AppNotification (plain data classes)
  database/       NotepadDatabase (schema, foreign keys, versioning/migrations)
  repositories/   Note, Todo, Tag, Alarm, Notification, Settings repositories (all SQL + business rules)
  alarms/         AlarmScheduler (AlarmManager), AlarmReceiver, BootReceiver, AlarmDelivery
  notifications/  NotificationHelper (channels + system notifications)
  utils/          Constants, TextStats, Repeat, DateTimeUtils, ThemeUtils, RichText, TagStyle, Validator, UiUtils
```
UI code never writes SQL; repositories never touch views. Lists reload in `onResume`, so favourites/tags/pins stay in sync everywhere.
Database calls run on the main thread: with at most 50 notes the queries are tiny (documented simplification).

### Database (SQLite, version 1)
Tables: `notes`, `todos`, `tags`, `note_tags`, `todo_tags`, `alarms`, `notifications`, `settings`.
Foreign keys are enforced (`onConfigure`), indexes exist for sort/search/relationship columns, related writes use transactions,
and `onUpgrade` is a versioned migration skeleton (the database is never recreated at start-up).

Deviations from the proposed schema (refinements):
* `todos.due_date` + `due_time` → single `due_at` epoch-millis column (simpler sorting/comparison). Same for `alarms.scheduled_at`.
* Extra columns: `notes.formatting` (rich-text spans as JSON), `tags.icon`, `tags.last_used_at`, `todos.repeat_interval`, `todos.completed_at`.
* Extra table `todo_tags` so standalone tasks can have tags. `alarms` has `note_id` **or** `todo_id` (CHECK constraint, unique per owner).
* Deletion rules: note → cascades to its linked task, alarm and tag links; task → cascades to alarm and tag links; tag → only links are removed;
  notification history is kept (`note_id/todo_id` set to NULL, so a tap says the item no longer exists).
* The 50-note limit applies to **notes** only; standalone tasks, tags and notification history are not limited.

### Definitions / decisions
* **Word count** = whitespace-separated tokens of the trimmed text. **Character count** = Unicode code points including spaces and line breaks (`TextStats`, used everywhere, computed in `NoteRepository.save`).
* A **to-do note** (note with the to-do switch) has a linked task row; standalone tasks come from the To-Do tab. Linked tasks are edited as notes; their due date/reminder comes from the note's alarm.
* Each note/task has at most one alarm; its database id is the `PendingIntent` request code (unique), so editing never duplicates a reminder.
* **Time search** matches notes created/modified in the chosen **hour**; **date search** matches the chosen day.
* Search uses parameterised queries; `LIKE` wildcards typed by the user are escaped.
* Completing a task cancels its alarm; reopening restores it (repeating alarms advance to the next future time).
* Text formatting (bold/italic/underline/links) is stored as JSON spans next to plain text; bullets are plain "• " text.

## Requirement mapping (summary)
| Synopsis / UI screen | Implementation |
|---|---|
| 1 Splash | `SplashActivity` |
| 2 Notes list | `NotesFragment`, `NoteAdapter` |
| 3 Add/Edit note | `NoteEditorActivity` |
| 4 View note | `NoteViewActivity` |
| 5 Search | `SearchActivity`, `NoteRepository.query` |
| 6 To-Do | `TodoFragment`, `TodoAdapter`, `TodoRepository` |
| 7 Add To-Do | `TodoEditorActivity` |
| 8 Alarm | `AlarmActivity`, `AlarmRepository`, `AlarmScheduler`, `AlarmReceiver`, `BootReceiver` |
| 9 Tags | `TagsFragment`, `TagNotesActivity`, `TagDialogs`, `TagRepository` |
| 10 Favourites | `FavouritesFragment` |
| 11 Notifications | `NotificationsFragment`, `NotificationHelper`, `NotificationRepository` |
| 12 Settings | `SettingsActivity`, `SettingsRepository` (SQLite `settings` table) |

## Known limitations / deviations from the UI reference
* Not built or run (see top). Layout fidelity to the PDF screens is approximate (colours, cards, chips, icons follow the design; pixel match is not claimed).
* The editor's **image** button from the design is omitted (images are outside the synopsis); **Backup & Restore** in Settings is omitted (not required, would otherwise be a non-functional control).
* Settings has no bottom navigation bar (it opens from the drawer with a back arrow). Alarm management (screen 8) is reached from the drawer, a note, or a task.
* Alarm tags (shown in the reference) are not implemented; tags belong to notes and tasks.
* Favourites/pins apply to notes (not standalone tasks).
* The reminder for a task whose reminder already fired is switched off until edited (one-off alarms only fire once).
* Back-up of data (`allowBackup=false`) is disabled for privacy.

## Suggested manual test script
1. Create 50 notes → the 51st shows the limit dialog. Delete one → creating works again.
2. Add notes with bold/italic/links, check counts, edit, rotate the screen, press Back with unsaved changes.
3. Set an alarm 1–2 minutes ahead on a note and on a standalone task; lock the screen; confirm notification, history entry, tap opens the item.
4. Revoke notification permission → alarm still recorded in history; banner/Settings explain it.
5. Complete a task with a reminder (alarm cancelled), reopen it (alarm restored), delete the note (alarm cancelled).
6. Reboot the device with a future alarm → it still fires.
7. Settings: change theme and list view, restart the app, confirm persistence; try Exit.
