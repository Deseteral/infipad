package com.deseteral.infipad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.deseteral.infipad.domain.Note;
import com.deseteral.infipad.service.ApplicationState;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView.setWebContentsDebuggingEnabled(true);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set the initial state
        ApplicationState.createState(getApplicationContext());

        listView = (ListView) findViewById(R.id.note_list);
        listView.setOnItemClickListener(onItemNoteClick);
        listView.setOnItemLongClickListener(onItemNoteLongClick);
        refreshListView();
    }

    private AdapterView.OnItemClickListener onItemNoteClick = new AdapterView.OnItemClickListener() {
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
    };

    private AdapterView.OnItemLongClickListener onItemNoteLongClick = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final TextView textView = (TextView) view;
            final String noteName = textView.getText().toString();
            final Note noteReference = ApplicationState
                    .getState()
                    .getNotepad()
                    .findNoteByName(noteName);
            final AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());

            builder.setTitle(R.string.remove_note_dialog_title)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            ApplicationState
                                    .getState()
                                    .getStorage()
                                    .deleteNote(noteReference);
                            refreshListView();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            builder.create().show();

            return true;
        }
    };

    private void refreshListView() {
        ApplicationState
                .getState()
                .getNotepad()
                .refresh();

        final List<Note> noteList = ApplicationState
                .getState()
                .getNotepad()
                .getNotes();

        // map(Note -> name)
        final List<String> list = new ArrayList<>();
        for (Note n : noteList) {
            list.add(n.getName());
        }

        listView.setAdapter(new ArrayAdapter<>(this, R.layout.element_note, list));
    }

    public void onFabClick(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.new_note_dialog_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        final AlertDialog dialog = (AlertDialog) dialogInterface;
                        final EditText nameField = (EditText) dialog.findViewById(R.id.dialog_new_note_name_field);
                        final String name = nameField.getText().toString().trim();

                        newNote(name);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setView(getLayoutInflater().inflate(R.layout.dialog_new_note, null));

        builder.create().show();
    }

    private void newNote(String noteName) {
        Log.i(TAG, "Create new note with title: " + noteName);

        final Note note = new Note(noteName);
        ApplicationState
                .getState()
                .getNotepad()
                .getNotes()
                .add(note);

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

    private void startSearchActivity() {
        final Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.app_bar_search:
                startSearchActivity();
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
