// Sets the package name to com.mit.notepad.activities so this class belongs to the app module namespace.
package com.mit.notepad.activities;

// Imports android.app.DatePickerDialog so this file can use that type by its short name.
import android.app.DatePickerDialog;
// Imports android.app.TimePickerDialog so this file can use that type by its short name.
import android.app.TimePickerDialog;
// Imports android.content.DialogInterface so this file can use that type by its short name.
import android.content.DialogInterface;
// Imports android.os.Bundle so this file can use that type by its short name.
import android.os.Bundle;
// Imports android.os.Handler so this file can use that type by its short name.
import android.os.Handler;
// Imports android.os.Looper so this file can use that type by its short name.
import android.os.Looper;
// Imports android.text.Editable so this file can use that type by its short name.
import android.text.Editable;
// Imports android.text.TextWatcher so this file can use that type by its short name.
import android.text.TextWatcher;
// Imports android.view.View so this file can use that type by its short name.
import android.view.View;
// Imports android.widget.PopupMenu so this file can use that type by its short name.
import android.widget.PopupMenu;

// Imports androidx.recyclerview.widget.LinearLayoutManager so this file can use that type by its short name.
import androidx.recyclerview.widget.LinearLayoutManager;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.adapters.NoteAdapter so this file can use that type by its short name.
import com.mit.notepad.adapters.NoteAdapter;
// Imports com.mit.notepad.databinding.ActivitySearchBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.ActivitySearchBinding;
// Imports com.mit.notepad.models.Note so this file can use that type by its short name.
import com.mit.notepad.models.Note;
// Imports com.mit.notepad.repositories.NoteRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.NoteRepository;
// Imports com.mit.notepad.repositories.SettingsRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.SettingsRepository;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.DateTimeUtils so this file can use that type by its short name.
import com.mit.notepad.utils.DateTimeUtils;
// Imports com.mit.notepad.utils.UiUtils so this file can use that type by its short name.
import com.mit.notepad.utils.UiUtils;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.Calendar so this file can use that type by its short name.
import java.util.Calendar;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;
// Imports java.util.Locale so this file can use that type by its short name.
import java.util.Locale;

/** Screen 5: keyword / date / time search with type, status, tag, due-date and favourite filters. */
// Declares class SearchActivity to group related state and behavior.
public class SearchActivity extends BaseActivity implements NoteAdapter.Listener {
    // Initializes DEBOUNCE_MS with 200 so later statements can use the computed value.
    private static final long DEBOUNCE_MS = 200;

    // Declares b for use by the surrounding method or class.
    private ActivitySearchBinding b;
    // Declares repo for use by the surrounding method or class.
    private NoteRepository repo;
    // Declares adapter for use by the surrounding method or class.
    private NoteAdapter adapter;
    // Initializes handler with new Handler(Looper.getMainLooper()) so later statements can use the computed value.
    private final Handler handler = new Handler(Looper.getMainLooper());
    // Initializes searchRunnable with this::runSearch so later statements can use the computed value.
    private final Runnable searchRunnable = this::runSearch;

    // Declares dayStart for use by the surrounding method or class.
    private long dayStart;
    // Initializes hour with -1 so later statements can use the computed value.
    private int hour = -1;
    // Declares dueDay for use by the surrounding method or class.
    private long dueDay;
    // Initializes tagIds with new ArrayList<>() so later statements can use the computed value.
    private List<Long> tagIds = new ArrayList<>();
    // Initializes sort with NoteRepository.SORT_RELEVANCE so later statements can use the computed value.
    private String sort = NoteRepository.SORT_RELEVANCE;

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Runs component initialization when Android invokes this lifecycle callback.
    protected void onCreate(Bundle savedInstanceState) {
        // Calls super.onCreate with savedInstanceState to perform the operation shown.
        super.onCreate(savedInstanceState);
        // Stores the result of ActivitySearchBinding.inflate(getLayoutInflater()) in b for use by later logic.
        b = ActivitySearchBinding.inflate(getLayoutInflater());
        // Calls setContentView with b.getRoot( to perform the operation shown.
        setContentView(b.getRoot());
        // Stores the result of new NoteRepository(this) in repo for use by later logic.
        repo = new NoteRepository(this);
        // Calls setupBackHeader with b.header, R.string.title_search, R.string.subtitle_search to perform the operation shown.
        setupBackHeader(b.header, R.string.title_search, R.string.subtitle_search);

        // Stores the result of new NoteAdapter(this) in adapter for use by later logic.
        adapter = new NoteAdapter(this);
        // Calls b.recycler.setLayoutManager with new LinearLayoutManager(this to perform the operation shown.
        b.recycler.setLayoutManager(new LinearLayoutManager(this));
        // Calls b.recycler.setAdapter with adapter to perform the operation shown.
        b.recycler.setAdapter(adapter);

        // Calls b.etSearch.addTextChangedListener with new TextWatcher() { to perform the operation shown.
        b.etSearch.addTextChangedListener(new TextWatcher() {
            // Applies @Override public void beforeTextChanged metadata to the declaration that follows.
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            // Applies @Override public void onTextChanged metadata to the declaration that follows.
            @Override public void onTextChanged(CharSequence s, int st, int bf, int c) { }
            // Applies @Override metadata to the declaration that follows.
            @Override
            // Runs the registered update after the text value changes.
            public void afterTextChanged(Editable s) {
                // Calls b.btnClear.setVisibility with s.length() > 0 ? View.VISIBLE : View.GONE to perform the operation shown.
                b.btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                // Calls scheduleSearch with  to perform the operation shown.
                scheduleSearch();
            // Closes the current method, control-flow, or type block.
            }
        // Executes }) as a statement.
        });
        // Calls b.chipGroup.setOnCheckedStateChangeListener with (g, ids) -> runSearch( to perform the operation shown.
        b.chipGroup.setOnCheckedStateChangeListener((g, ids) -> runSearch());
        // Calls b.statusGroup.setOnCheckedStateChangeListener with (g, ids) -> runSearch( to perform the operation shown.
        b.statusGroup.setOnCheckedStateChangeListener((g, ids) -> runSearch());
        // Calls b.btnClear.setOnClickListener with v -> b.etSearch.setText("" to perform the operation shown.
        b.btnClear.setOnClickListener(v -> b.etSearch.setText(""));
        // Calls b.btnClearSearch.setOnClickListener with v -> clearAll( to perform the operation shown.
        b.btnClearSearch.setOnClickListener(v -> clearAll());
        // Calls b.btnFilter.setOnClickListener with v -> b.panelFilters.setVisibility( to perform the operation shown.
        b.btnFilter.setOnClickListener(v -> b.panelFilters.setVisibility(
                // Calls b.panelFilters.getVisibility with ) == View.VISIBLE ? View.GONE : View.VISIBLE to perform the operation shown.
                b.panelFilters.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
        // Calls b.btnDate.setOnClickListener with v -> pickDay(false to perform the operation shown.
        b.btnDate.setOnClickListener(v -> pickDay(false));
        // Calls b.btnDue.setOnClickListener with v -> pickDay(true to perform the operation shown.
        b.btnDue.setOnClickListener(v -> pickDay(true));
        // Calls b.btnTime.setOnClickListener with v -> pickHour( to perform the operation shown.
        b.btnTime.setOnClickListener(v -> pickHour());
        // Starts the callback body that handles the event or asynchronous result.
        b.btnTags.setOnClickListener(v -> TagDialogs.pickTags(this, tagIds, ids -> {
            // Stores the result of ids in tagIds for use by later logic.
            tagIds = ids;
            // Calls refreshButtons with  to perform the operation shown.
            refreshButtons();
            // Calls runSearch with  to perform the operation shown.
            runSearch();
        // Executes })) as a statement.
        }));
        // Starts the callback body that handles the event or asynchronous result.
        b.tvSort.setOnClickListener(v -> {
            // Initializes pm with new PopupMenu(this, v) so later statements can use the computed value.
            PopupMenu pm = new PopupMenu(this, v);
            // Calls pm.getMenu with ).add(0, 1, 0, R.string.sort_relevance to perform the operation shown.
            pm.getMenu().add(0, 1, 0, R.string.sort_relevance);
            // Calls pm.getMenu with ).add(0, 2, 1, R.string.sort_newest to perform the operation shown.
            pm.getMenu().add(0, 2, 1, R.string.sort_newest);
            // Calls pm.getMenu with ).add(0, 3, 2, R.string.sort_oldest to perform the operation shown.
            pm.getMenu().add(0, 3, 2, R.string.sort_oldest);
            // Calls pm.getMenu with ).add(0, 4, 3, R.string.sort_title to perform the operation shown.
            pm.getMenu().add(0, 4, 3, R.string.sort_title);
            // Starts the callback body that handles the event or asynchronous result.
            pm.setOnMenuItemClickListener(item -> {
                // Initializes id with item.getItemId() so later statements can use the computed value.
                int id = item.getItemId();
                // Stores the result of id == 2 ? NoteRepository.SORT_NEWEST : id == 3 ? NoteRepository.SORT_OLDEST in sort for use by later logic.
                sort = id == 2 ? NoteRepository.SORT_NEWEST : id == 3 ? NoteRepository.SORT_OLDEST
                        // Executes : id == 4 ? NoteRepository.SORT_TITLE : NoteRepository.SORT_RELEVANCE as a statement.
                        : id == 4 ? NoteRepository.SORT_TITLE : NoteRepository.SORT_RELEVANCE;
                // Calls runSearch with  to perform the operation shown.
                runSearch();
                // Returns true to the caller.
                return true;
            // Executes }) as a statement.
            });
            // Calls pm.show with  to perform the operation shown.
            pm.show();
        // Executes }) as a statement.
        });
        // Calls refreshButtons with  to perform the operation shown.
        refreshButtons();
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Refreshes visible data when this component becomes active.
    protected void onResume() {
        // Calls super.onResume with  to perform the operation shown.
        super.onResume();
        // Calls runSearch with  to perform the operation shown.
        runSearch();
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Declares onDestroy() to implement this part of the component.
    protected void onDestroy() {
        // Calls handler.removeCallbacks with searchRunnable to perform the operation shown.
        handler.removeCallbacks(searchRunnable);
        // Calls super.onDestroy with  to perform the operation shown.
        super.onDestroy();
    // Closes the current method, control-flow, or type block.
    }

    // Declares scheduleSearch() to implement this part of the component.
    private void scheduleSearch() {
        // Calls handler.removeCallbacks with searchRunnable to perform the operation shown.
        handler.removeCallbacks(searchRunnable);
        // Calls handler.postDelayed with searchRunnable, DEBOUNCE_MS to perform the operation shown.
        handler.postDelayed(searchRunnable, DEBOUNCE_MS);
    // Closes the current method, control-flow, or type block.
    }

    // Clears all.
    private void clearAll() {
        // Stores the result of 0 in dayStart for use by later logic.
        dayStart = 0;
        // Stores the result of -1 in hour for use by later logic.
        hour = -1;
        // Stores the result of 0 in dueDay for use by later logic.
        dueDay = 0;
        // Stores the result of new ArrayList<>() in tagIds for use by later logic.
        tagIds = new ArrayList<>();
        // Stores the result of NoteRepository.SORT_RELEVANCE in sort for use by later logic.
        sort = NoteRepository.SORT_RELEVANCE;
        // Calls b.chipGroup.check with R.id.chip_all to perform the operation shown.
        b.chipGroup.check(R.id.chip_all);
        // Calls b.statusGroup.check with R.id.chip_status_any to perform the operation shown.
        b.statusGroup.check(R.id.chip_status_any);
        // Calls b.etSearch.setText with "" to perform the operation shown.
        b.etSearch.setText("");
        // Calls refreshButtons with  to perform the operation shown.
        refreshButtons();
        // Calls runSearch with  to perform the operation shown.
        runSearch();
    // Closes the current method, control-flow, or type block.
    }

    // Refreshes buttons.
    private void refreshButtons() {
        // Calls b.btnDate.setText with dayStart > 0 ? DateTimeUtils.formatDate(dayStart) : getString(R.string.any_date to perform the operation shown.
        b.btnDate.setText(dayStart > 0 ? DateTimeUtils.formatDate(dayStart) : getString(R.string.any_date));
        // Calls b.btnTime.setText with hour >= 0 ? String.format(Locale.getDefault(), "%02d:00 – %02d:59", hour, hour to perform the operation shown.
        b.btnTime.setText(hour >= 0 ? String.format(Locale.getDefault(), "%02d:00 – %02d:59", hour, hour)
                // Calls getString with R.string.any_time to perform the operation shown.
                : getString(R.string.any_time));
        // Calls b.btnDue.setText with dueDay > 0 ? getString(R.string.due_filter, DateTimeUtils.formatDate(dueDay to perform the operation shown.
        b.btnDue.setText(dueDay > 0 ? getString(R.string.due_filter, DateTimeUtils.formatDate(dueDay))
                // Calls getString with R.string.due_any to perform the operation shown.
                : getString(R.string.due_any));
        // Calls b.btnTags.setText with tagIds.isEmpty() ? getString(R.string.tags_any to perform the operation shown.
        b.btnTags.setText(tagIds.isEmpty() ? getString(R.string.tags_any)
                // Calls getString with R.string.tags_selected, tagIds.size( to perform the operation shown.
                : getString(R.string.tags_selected, tagIds.size()));
    // Closes the current method, control-flow, or type block.
    }

    // Prompts the user to choose day.
    private void pickDay(boolean due) {
        // Initializes c with Calendar.getInstance() so later statements can use the computed value.
        Calendar c = Calendar.getInstance();
        // Initializes current with due ? dueDay : dayStart so later statements can use the computed value.
        long current = due ? dueDay : dayStart;
        // If current > 0 is true, c.setTimeInMillis(current) immediately.
        if (current > 0) c.setTimeInMillis(current);
        // Starts the callback body that handles the event or asynchronous result.
        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            // Initializes n with Calendar.getInstance() so later statements can use the computed value.
            Calendar n = Calendar.getInstance();
            // Calls n.set with y, m, d, 12, 0, 0 to perform the operation shown.
            n.set(y, m, d, 12, 0, 0);
            // Initializes start with DateTimeUtils.startOfDay(n.getTimeInMillis()) so later statements can use the computed value.
            long start = DateTimeUtils.startOfDay(n.getTimeInMillis());
            // If due is true, dueDay = start; else dayStart = start immediately.
            if (due) dueDay = start; else dayStart = start;
            // Calls refreshButtons with  to perform the operation shown.
            refreshButtons();
            // Calls runSearch with  to perform the operation shown.
            runSearch();
        // Calls c.get with Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH to perform the operation shown.
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        // Calls dialog.setButton with DialogInterface.BUTTON_NEUTRAL, getString(due ? R.string.due_any : R.string.any_date), to perform the operation shown.
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(due ? R.string.due_any : R.string.any_date),
                // Starts the callback body that handles the event or asynchronous result.
                (d, w) -> {
                    // If due is true, dueDay = 0; else dayStart = 0 immediately.
                    if (due) dueDay = 0; else dayStart = 0;
                    // Calls refreshButtons with  to perform the operation shown.
                    refreshButtons();
                    // Calls runSearch with  to perform the operation shown.
                    runSearch();
                // Executes }) as a statement.
                });
        // Calls dialog.show with  to perform the operation shown.
        dialog.show();
    // Closes the current method, control-flow, or type block.
    }

    /** The time filter matches notes created/modified within the chosen hour. */
    // Prompts the user to choose hour.
    private void pickHour() {
        // Starts the callback body that handles the event or asynchronous result.
        TimePickerDialog dialog = new TimePickerDialog(this, (view, h, min) -> {
            // Stores the result of h in hour for use by later logic.
            hour = h;
            // Calls refreshButtons with  to perform the operation shown.
            refreshButtons();
            // Calls runSearch with  to perform the operation shown.
            runSearch();
        // Executes }, hour >= 0 ? hour : 9, 0, false) as a statement.
        }, hour >= 0 ? hour : 9, 0, false);
        // Calls dialog.setTitle with R.string.pick_hour_title to perform the operation shown.
        dialog.setTitle(R.string.pick_hour_title);
        // Starts the callback body that handles the event or asynchronous result.
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.any_time), (d, w) -> {
            // Stores the result of -1 in hour for use by later logic.
            hour = -1;
            // Calls refreshButtons with  to perform the operation shown.
            refreshButtons();
            // Calls runSearch with  to perform the operation shown.
            runSearch();
        // Executes }) as a statement.
        });
        // Calls dialog.show with  to perform the operation shown.
        dialog.show();
    // Closes the current method, control-flow, or type block.
    }

    // Builds query.
    private NoteRepository.NoteQuery buildQuery() {
        // Initializes q with new NoteRepository.NoteQuery() so later statements can use the computed value.
        NoteRepository.NoteQuery q = new NoteRepository.NoteQuery();
        // Stores the result of b.etSearch.getText().toString() in q.keyword for use by later logic.
        q.keyword = b.etSearch.getText().toString();
        // Stores the result of sort in q.sort for use by later logic.
        q.sort = sort;
        // Initializes checked with b.chipGroup.getCheckedChipId() so later statements can use the computed value.
        int checked = b.chipGroup.getCheckedChipId();
        // If checked == R.id.chip_notes is true, q.noteType = Constants.NOTE_TYPE_NOTE immediately.
        if (checked == R.id.chip_notes) q.noteType = Constants.NOTE_TYPE_NOTE;
        // Checks the alternate condition (checked == R.id.chip_todo) after the earlier branch did not run.
        else if (checked == R.id.chip_todo) q.noteType = Constants.NOTE_TYPE_TODO;
        // Checks the alternate condition (checked == R.id.chip_fav) after the earlier branch did not run.
        else if (checked == R.id.chip_fav) q.favoritesOnly = true;
        // Initializes status with b.statusGroup.getCheckedChipId() so later statements can use the computed value.
        int status = b.statusGroup.getCheckedChipId();
        // If status == R.id.chip_status_pending is true, q.todoStatus = Constants.STATUS_PENDING immediately.
        if (status == R.id.chip_status_pending) q.todoStatus = Constants.STATUS_PENDING;
        // Checks the alternate condition (status == R.id.chip_status_completed) after the earlier branch did not run.
        else if (status == R.id.chip_status_completed) q.todoStatus = Constants.STATUS_COMPLETED;
        // Stores the result of new ArrayList<>(tagIds) in q.tagIds for use by later logic.
        q.tagIds = new ArrayList<>(tagIds);
        // If dayStart > 0 is true, { immediately.
        if (dayStart > 0) {
            // Stores the result of dayStart in q.dayStart for use by later logic.
            q.dayStart = dayStart;
            // Stores the result of DateTimeUtils.endOfDay(dayStart) in q.dayEnd for use by later logic.
            q.dayEnd = DateTimeUtils.endOfDay(dayStart);
        // Closes the current method, control-flow, or type block.
        }
        // Stores the result of hour in q.hour for use by later logic.
        q.hour = hour;
        // If dueDay > 0 is true, { immediately.
        if (dueDay > 0) {
            // Stores the result of dueDay in q.dueDayStart for use by later logic.
            q.dueDayStart = dueDay;
            // Stores the result of DateTimeUtils.endOfDay(dueDay) in q.dueDayEnd for use by later logic.
            q.dueDayEnd = DateTimeUtils.endOfDay(dueDay);
        // Closes the current method, control-flow, or type block.
        }
        // Returns q to the caller.
        return q;
    // Closes the current method, control-flow, or type block.
    }

    // Executes the current search text and filters.
    private void runSearch() {
        // Initializes q with buildQuery() so later statements can use the computed value.
        NoteRepository.NoteQuery q = buildQuery();
        // Initializes results with repo.query(q) so later statements can use the computed value.
        List<Note> results = repo.query(q);
        // Calls adapter.setListStyle with new SettingsRepository(this).get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT to perform the operation shown.
        adapter.setListStyle(new SettingsRepository(this).get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT));
        // Calls adapter.setHighlight with q.keyword to perform the operation shown.
        adapter.setHighlight(q.keyword);
        // Calls adapter.setItems with results to perform the operation shown.
        adapter.setItems(results);

        // Initializes keyword with q.keyword.trim() so later statements can use the computed value.
        String keyword = q.keyword.trim();
        // Calls b.tvResultCount.setText with keyword.isEmpty( to perform the operation shown.
        b.tvResultCount.setText(keyword.isEmpty()
                // Calls getResources with ).getQuantityString(R.plurals.results_found, results.size(), results.size( to perform the operation shown.
                ? getResources().getQuantityString(R.plurals.results_found, results.size(), results.size())
                // Calls getString with R.string.results_found_for, results.size(), keyword to perform the operation shown.
                : getString(R.string.results_found_for, results.size(), keyword));
        // Initializes sortLabel with NoteRepository.SORT_NEWEST.equals(sort) ? R.string.sort_newest so later statements can use the computed value.
        int sortLabel = NoteRepository.SORT_NEWEST.equals(sort) ? R.string.sort_newest
                // Calls NoteRepository.SORT_OLDEST.equals with sort) ? R.string.sort_oldest to perform the operation shown.
                : NoteRepository.SORT_OLDEST.equals(sort) ? R.string.sort_oldest
                // Calls NoteRepository.SORT_TITLE.equals with sort) ? R.string.sort_title : R.string.sort_relevance to perform the operation shown.
                : NoteRepository.SORT_TITLE.equals(sort) ? R.string.sort_title : R.string.sort_relevance;
        // Calls b.tvSort.setText with getString(R.string.sort_by, getString(sortLabel to perform the operation shown.
        b.tvSort.setText(getString(R.string.sort_by, getString(sortLabel)));

        // Initializes empty with results.isEmpty() so later statements can use the computed value.
        boolean empty = results.isEmpty();
        // Calls b.empty.getRoot with ).setVisibility(empty ? View.VISIBLE : View.GONE to perform the operation shown.
        b.empty.getRoot().setVisibility(empty ? View.VISIBLE : View.GONE);
        // Calls b.recycler.setVisibility with empty ? View.GONE : View.VISIBLE to perform the operation shown.
        b.recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        // Calls b.empty.emptyTitle.setText with R.string.empty_search_title to perform the operation shown.
        b.empty.emptyTitle.setText(R.string.empty_search_title);
        // Calls b.empty.emptyMessage.setText with R.string.empty_search_message to perform the operation shown.
        b.empty.emptyMessage.setText(R.string.empty_search_message);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override public void onOpen metadata to the declaration that follows.
    @Override public void onOpen(Note note) { NoteActions.open(this, note); }

    // Applies @Override public void onFavoriteToggle metadata to the declaration that follows.
    @Override public void onFavoriteToggle(Note note) { NoteActions.toggleFavorite(this, note, this::runSearch); }

    // Applies @Override public void onMenu metadata to the declaration that follows.
    @Override public void onMenu(Note note, View anchor) { NoteActions.showMenu(this, anchor, note, this::runSearch); }
// Closes the current method, control-flow, or type block.
}
