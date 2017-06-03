package com.deseteral.infipad;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.deseteral.infipad.domain.Note;
import com.deseteral.infipad.service.ApplicationState;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements OnItemClickListener {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.note_list);
        listView.setOnItemClickListener(this);

        EditText searchBox = (EditText) findViewById(R.id.search_box);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshListView(s.toString());
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final TextView textView = (TextView) view;
        final String noteName = textView.getText().toString();
        final Note note = ApplicationState
                .getState()
                .getNotepad()
                .findNoteByName(noteName);

        startNoteActivity(note);
    }

    private void startNoteActivity(Note note) {
        ApplicationState
                .getState()
                .getStorage()
                .loadNoteContent(note);

        final Intent intent = new Intent(this, NoteActivity.class);
        final int index = ApplicationState
                .getState()
                .getNotepad()
                .findIndexByName(note.getName());
        intent.putExtra(NoteActivity.NOTE_ID, index);

        startActivity(intent);
    }

    private void refreshListView(String phrase) {
        ApplicationState
                .getState()
                .getNotepad()
                .refresh();

        final List<Note> noteList = ApplicationState
                .getState()
                .getNotepad()
                .search(phrase);

        // map(Note -> name)
        final List<String> list = new ArrayList<>();
        for (Note n : noteList) {
            list.add(n.getName());
        }

        listView.setAdapter(new ArrayAdapter<>(this, R.layout.element_note, list));
    }
}
