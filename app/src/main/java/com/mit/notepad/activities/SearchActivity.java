package com.mit.notepad.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.PopupMenu;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.mit.notepad.R;
import com.mit.notepad.adapters.NoteAdapter;
import com.mit.notepad.databinding.ActivitySearchBinding;
import com.mit.notepad.models.Note;
import com.mit.notepad.repositories.NoteRepository;
import com.mit.notepad.repositories.SettingsRepository;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.DateTimeUtils;
import com.mit.notepad.utils.UiUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/** Screen 5: keyword / date / time search with type, status, tag, due-date and favourite filters. */
public class SearchActivity extends BaseActivity implements NoteAdapter.Listener {
    private static final long DEBOUNCE_MS = 200;

    private ActivitySearchBinding b;
    private NoteRepository repo;
    private NoteAdapter adapter;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable searchRunnable = this::runSearch;

    private long dayStart;
    private int hour = -1;
    private long dueDay;
    private List<Long> tagIds = new ArrayList<>();
    private String sort = NoteRepository.SORT_RELEVANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        repo = new NoteRepository(this);
        setupBackHeader(b.header, R.string.title_search, R.string.subtitle_search);

        adapter = new NoteAdapter(this);
        b.recycler.setLayoutManager(new LinearLayoutManager(this));
        b.recycler.setAdapter(adapter);

        b.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            @Override public void onTextChanged(CharSequence s, int st, int bf, int c) { }
            @Override
            public void afterTextChanged(Editable s) {
                b.btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                scheduleSearch();
            }
        });
        b.chipGroup.setOnCheckedStateChangeListener((g, ids) -> runSearch());
        b.statusGroup.setOnCheckedStateChangeListener((g, ids) -> runSearch());
        b.btnClear.setOnClickListener(v -> b.etSearch.setText(""));
        b.btnClearSearch.setOnClickListener(v -> clearAll());
        b.btnFilter.setOnClickListener(v -> b.panelFilters.setVisibility(
                b.panelFilters.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
        b.btnDate.setOnClickListener(v -> pickDay(false));
        b.btnDue.setOnClickListener(v -> pickDay(true));
        b.btnTime.setOnClickListener(v -> pickHour());
        b.btnTags.setOnClickListener(v -> TagDialogs.pickTags(this, tagIds, ids -> {
            tagIds = ids;
            refreshButtons();
            runSearch();
        }));
        b.tvSort.setOnClickListener(v -> {
            PopupMenu pm = new PopupMenu(this, v);
            pm.getMenu().add(0, 1, 0, R.string.sort_relevance);
            pm.getMenu().add(0, 2, 1, R.string.sort_newest);
            pm.getMenu().add(0, 3, 2, R.string.sort_oldest);
            pm.getMenu().add(0, 4, 3, R.string.sort_title);
            pm.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                sort = id == 2 ? NoteRepository.SORT_NEWEST : id == 3 ? NoteRepository.SORT_OLDEST
                        : id == 4 ? NoteRepository.SORT_TITLE : NoteRepository.SORT_RELEVANCE;
                runSearch();
                return true;
            });
            pm.show();
        });
        refreshButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        runSearch();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(searchRunnable);
        super.onDestroy();
    }

    private void scheduleSearch() {
        handler.removeCallbacks(searchRunnable);
        handler.postDelayed(searchRunnable, DEBOUNCE_MS);
    }

    private void clearAll() {
        dayStart = 0;
        hour = -1;
        dueDay = 0;
        tagIds = new ArrayList<>();
        sort = NoteRepository.SORT_RELEVANCE;
        b.chipGroup.check(R.id.chip_all);
        b.statusGroup.check(R.id.chip_status_any);
        b.etSearch.setText("");
        refreshButtons();
        runSearch();
    }

    private void refreshButtons() {
        b.btnDate.setText(dayStart > 0 ? DateTimeUtils.formatDate(dayStart) : getString(R.string.any_date));
        b.btnTime.setText(hour >= 0 ? String.format(Locale.getDefault(), "%02d:00 – %02d:59", hour, hour)
                : getString(R.string.any_time));
        b.btnDue.setText(dueDay > 0 ? getString(R.string.due_filter, DateTimeUtils.formatDate(dueDay))
                : getString(R.string.due_any));
        b.btnTags.setText(tagIds.isEmpty() ? getString(R.string.tags_any)
                : getString(R.string.tags_selected, tagIds.size()));
    }

    private void pickDay(boolean due) {
        Calendar c = Calendar.getInstance();
        long current = due ? dueDay : dayStart;
        if (current > 0) c.setTimeInMillis(current);
        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            Calendar n = Calendar.getInstance();
            n.set(y, m, d, 12, 0, 0);
            long start = DateTimeUtils.startOfDay(n.getTimeInMillis());
            if (due) dueDay = start; else dayStart = start;
            refreshButtons();
            runSearch();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(due ? R.string.due_any : R.string.any_date),
                (d, w) -> {
                    if (due) dueDay = 0; else dayStart = 0;
                    refreshButtons();
                    runSearch();
                });
        dialog.show();
    }

    /** The time filter matches notes created/modified within the chosen hour. */
    private void pickHour() {
        TimePickerDialog dialog = new TimePickerDialog(this, (view, h, min) -> {
            hour = h;
            refreshButtons();
            runSearch();
        }, hour >= 0 ? hour : 9, 0, false);
        dialog.setTitle(R.string.pick_hour_title);
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.any_time), (d, w) -> {
            hour = -1;
            refreshButtons();
            runSearch();
        });
        dialog.show();
    }

    private NoteRepository.NoteQuery buildQuery() {
        NoteRepository.NoteQuery q = new NoteRepository.NoteQuery();
        q.keyword = b.etSearch.getText().toString();
        q.sort = sort;
        int checked = b.chipGroup.getCheckedChipId();
        if (checked == R.id.chip_notes) q.noteType = Constants.NOTE_TYPE_NOTE;
        else if (checked == R.id.chip_todo) q.noteType = Constants.NOTE_TYPE_TODO;
        else if (checked == R.id.chip_fav) q.favoritesOnly = true;
        int status = b.statusGroup.getCheckedChipId();
        if (status == R.id.chip_status_pending) q.todoStatus = Constants.STATUS_PENDING;
        else if (status == R.id.chip_status_completed) q.todoStatus = Constants.STATUS_COMPLETED;
        q.tagIds = new ArrayList<>(tagIds);
        if (dayStart > 0) {
            q.dayStart = dayStart;
            q.dayEnd = DateTimeUtils.endOfDay(dayStart);
        }
        q.hour = hour;
        if (dueDay > 0) {
            q.dueDayStart = dueDay;
            q.dueDayEnd = DateTimeUtils.endOfDay(dueDay);
        }
        return q;
    }

    private void runSearch() {
        NoteRepository.NoteQuery q = buildQuery();
        List<Note> results = repo.query(q);
        adapter.setListStyle(new SettingsRepository(this).get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT));
        adapter.setHighlight(q.keyword);
        adapter.setItems(results);

        String keyword = q.keyword.trim();
        b.tvResultCount.setText(keyword.isEmpty()
                ? getResources().getQuantityString(R.plurals.results_found, results.size(), results.size())
                : getString(R.string.results_found_for, results.size(), keyword));
        int sortLabel = NoteRepository.SORT_NEWEST.equals(sort) ? R.string.sort_newest
                : NoteRepository.SORT_OLDEST.equals(sort) ? R.string.sort_oldest
                : NoteRepository.SORT_TITLE.equals(sort) ? R.string.sort_title : R.string.sort_relevance;
        b.tvSort.setText(getString(R.string.sort_by, getString(sortLabel)));

        boolean empty = results.isEmpty();
        b.empty.getRoot().setVisibility(empty ? View.VISIBLE : View.GONE);
        b.recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        b.empty.emptyTitle.setText(R.string.empty_search_title);
        b.empty.emptyMessage.setText(R.string.empty_search_message);
    }

    @Override public void onOpen(Note note) { NoteActions.open(this, note); }

    @Override public void onFavoriteToggle(Note note) { NoteActions.toggleFavorite(this, note, this::runSearch); }

    @Override public void onMenu(Note note, View anchor) { NoteActions.showMenu(this, anchor, note, this::runSearch); }
}
