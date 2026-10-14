# Notepad – Your Notes, Your Tasks

A native Android notes and task management application developed as part of the Mobile Applications Development (MCA 5162) course at Manipal Institute of Technology, Manipal.

Notepad combines note-taking, to-do management, reminders, tags, favourites, and search in a single offline application. All data is stored locally using SQLite, while Android's native alarm and notification APIs handle reminders.

## Features

- **Notes:** Create, edit, view, delete, pin, and favourite notes.
- **Rich Text:** Support for bold, italic, underline, hyperlinks, and bullet points.
- **Word and Character Count:** Live text statistics while editing notes.
- **To-Do Management:** Create standalone tasks or convert notes into tasks, track completion, and manage due dates.
- **Reminders:** Schedule one-time or repeating alarms for notes and tasks.
- **Search and Filters:** Search note titles and content, with filters for dates, time, tags, favourites, task status, and due dates.
- **Tags:** Create, rename, recolour, and organise notes and tasks using reusable tags.
- **Favourites:** Keep important notes accessible and pin them to the top of the list.
- **Notification History:** View previous notifications, manage read status, and navigate to associated notes or tasks.
- **Customisation:** Choose between system, light, dark, and lavender themes, with additional list-view options.
- **Local Storage:** All application data is stored on the device. No account, backend, Firebase, or internet connection is required.

## Tech Stack

| Component | Technology |
|---|---|
| Programming language | Java |
| UI development | XML layouts |
| Lists | RecyclerView |
| Database | SQLite |
| Database helper | SQLiteOpenHelper |
| Alarm scheduling | AlarmManager |
| Background event handling | BroadcastReceiver |
| Notifications | Android Notification APIs |
| Build system | Gradle |
| Minimum Android version | Android 7.0 (API 24) |
| Compile SDK / Target SDK | 34 |

## Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17
- Android SDK Platform 34
- Android emulator or physical Android device running Android 7.0 or newer

### Installation

1. Clone the repository:

   ```bash
   git clone <repository-url>
   ```

2. Open Android Studio and select **File → Open**.
3. Choose the cloned project directory.
4. Allow Gradle to sync and download the required dependencies.
5. Make sure Android SDK Platform 34 is installed.
6. Select an emulator or connect an Android device with USB debugging enabled.
7. Build and run the `app` configuration.

The project uses Gradle 8.2 and Android Gradle Plugin 8.2.2. If the Gradle wrapper is not included in the repository, generate it using a compatible Gradle installation:

```bash
gradle wrapper --gradle-version 8.2
```

On Windows, use `gradlew.bat` to run Gradle commands. On Linux or macOS, use `./gradlew`.

## Permissions

The application uses the following permissions for reminders and notifications:

| Permission | Purpose |
|---|---|
| `POST_NOTIFICATIONS` | Allows notifications on Android 13 and newer. |
| `SCHEDULE_EXACT_ALARM` | Provides access to exact alarm scheduling where permitted by Android. |
| `RECEIVE_BOOT_COMPLETED` | Allows eligible alarms to be restored after a device reboot. |

Notification permission is requested at first launch on supported Android versions. If permission is denied, the app retains its local reminder history and explains how to enable notifications through system settings.

When exact-alarm access is unavailable, the application falls back to an inexact alarm where supported. Reminder delivery is not guaranteed at the exact scheduled time because Android may delay alarms due to Doze mode, battery optimisation, or other system restrictions.

Eligible alarms are re-registered after a reboot or app update. Repeating alarms advance to their next scheduled occurrence rather than replaying every missed reminder.

## Project Structure

The application follows a modular structure that separates UI components, database operations, business logic, and alarm handling.

```text
app/src/main/java/com/mit/notepad/
├── activities/
│   ├── SplashActivity
│   ├── MainActivity
│   ├── NoteEditorActivity
│   ├── NoteViewActivity
│   ├── SearchActivity
│   ├── TodoEditorActivity
│   ├── AlarmActivity
│   ├── TagNotesActivity
│   ├── SettingsActivity
│   └── Shared UI helpers and BaseActivity
├── fragments/
│   ├── NotesFragment
│   ├── TodoFragment
│   ├── TagsFragment
│   ├── FavouritesFragment
│   └── NotificationsFragment
├── adapters/
│   └── RecyclerView adapters
├── models/
│   ├── Note
│   ├── Todo
│   ├── Tag
│   ├── Alarm
│   └── AppNotification
├── database/
│   └── NotepadDatabase
├── repositories/
│   └── Data and settings repositories
├── alarms/
│   ├── AlarmScheduler
│   ├── AlarmReceiver
│   ├── BootReceiver
│   └── AlarmDelivery
├── notifications/
│   └── NotificationHelper
└── utils/
    └── Shared utility classes
```

Activities and fragments handle the UI and user interactions. RecyclerView adapters display lists, while repositories handle database queries and application-level data operations. Alarm scheduling and notification delivery are managed separately from the UI.

Lists reload in `onResume()` to keep changes synchronised across screens.

## Database Design

The application uses SQLite through `SQLiteOpenHelper`. The current database schema is version 1.

| Table | Description |
|---|---|
| `notes` | Note content, timestamps, formatting, favourites, and pinned state. |
| `todos` | Standalone tasks and tasks linked to notes. |
| `tags` | Tag names, colours, icons, and usage information. |
| `note_tags` | Relationships between notes and tags. |
| `todo_tags` | Relationships between tasks and tags. |
| `alarms` | Reminder schedules associated with notes or tasks. |
| `notifications` | Local notification history. |
| `settings` | Persistent application preferences. |

### Database Implementation

- Foreign-key enforcement is enabled through `onConfigure()`.
- Indexes support common search, sorting, and relationship queries.
- Related database operations use transactions where necessary.
- Database upgrades use a versioned migration structure rather than recreating the database on startup.
- Deleting a note removes its linked task, alarm, and tag relationships.
- Deleting a task removes its alarm and tag relationships.
- Deleting a tag removes its relationships without deleting associated notes or tasks.
- Notification history is preserved when a note or task is deleted by setting the associated foreign key to `NULL`.
- Each note or task can have at most one alarm, enforced through database constraints.

### Design Decisions

Due dates and alarm schedules are stored as epoch-millisecond values to simplify sorting and date comparisons. Rich-text formatting is stored as JSON spans alongside the original plain text. Additional fields support tag icons, recently used tags, task repetition, and completion timestamps.

The separate `todo_tags` table allows standalone tasks to use tags independently of notes.

The current implementation performs database operations on the main thread. This keeps the implementation straightforward for the project's limited scope, but background execution would be preferable if the application were expanded.

## Text Processing

Text statistics are handled by the `TextStats` utility.

- **Word count:** Counts whitespace-separated tokens after trimming the text.
- **Character count:** Counts Unicode code points, including spaces and line breaks.

Counts are recalculated when a note is saved through `NoteRepository.save()`.

Rich-text formatting is stored separately from plain text as JSON spans. Bullet points are represented as ordinary text prefixed with `• `.

## Testing

The project includes JVM unit tests and Android instrumentation tests.

Run JVM tests using:

```bash
./gradlew test
```

Run instrumentation tests on a connected device or emulator using:

```bash
./gradlew connectedAndroidTest
```

Testing should cover note creation and editing, text formatting, search filters, task completion, alarm scheduling, notification permissions, alarm restoration after reboot, database relationships, and settings persistence.

**Build status:** The complete Android application has not yet been compiled or tested on an emulator or physical device. Standalone Java utilities have been compiled and tested on a regular JVM, and scripts have been used to check XML resources, IDs, and binding references. The Android build and runtime behaviour still need verification in Android Studio.

## Known Limitations

- The UI follows the supplied design reference, but exact pixel-level matching has not been verified.
- The image-insertion feature from the original design is not implemented.
- Backup and restore functionality is not included.
- Alarm management is accessible through the navigation drawer, notes, and tasks.
- Tags cannot be assigned directly to alarms.
- Favourites and pins apply to notes, not standalone tasks.
- A one-time task reminder is switched off after it fires; another reminder must be scheduled if needed.
- Database operations run on the main thread.
- App data backup is disabled using `allowBackup=false`.

## Future Improvements

Potential improvements include moving database operations to a background executor, expanding automated test coverage, improving accessibility, refining the UI, and adding backup and restore functionality.

## Academic Information

**Course:** Mobile Applications Development (MCA 5162)  
**Institution:** Manipal Institute of Technology, Manipal

This project explores native Android application development, SQLite persistence, modular application architecture, task management, and system-level alarm and notification handling using Java and XML.
