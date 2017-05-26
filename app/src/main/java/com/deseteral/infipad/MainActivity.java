package com.deseteral.infipad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.deseteral.infipad.storage.Storage;
import com.deseteral.infipad.storage.StorageOrchestrator;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StorageOrchestrator.setInstance(
                new StorageOrchestrator(this)
        );

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layout_swipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                StorageOrchestrator.getInstance().synchronize(new StorageOrchestrator.OnSynchronizeFinishedCallback() {
                    @Override
                    public void onSynchronizeFinished() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        listView = (ListView) findViewById(R.id.note_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView textView = (TextView) view;
                final String noteName = textView.getText().toString();
                final String noteContent = StorageOrchestrator.getInstance().loadNoteContent(noteName);

                startNoteActivity(noteName, noteContent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView textView = (TextView) view;
                final String noteName = textView.getText().toString();
                final AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());

                builder.setTitle(R.string.remove_note_dialog_title)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                StorageOrchestrator.getInstance().deleteNote(noteName);
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
        });
        refreshListView();
    }

    private void refreshListView() {
        final MainActivity mainActivity = this;

        StorageOrchestrator.getInstance().getList(new Storage.OnListFetchedCallback() {
            @Override
            public void onListFetched(List<String> list) {
                listView.setAdapter(new ArrayAdapter<>(mainActivity, R.layout.element_note, list));
            }
        });
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

                        newFile(name);
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

    private void newFile(String noteName) {
        Log.i(TAG, "Create new note with title: " + noteName);

        final String initialNoteContent = "# This is a new note";

        StorageOrchestrator.getInstance().saveNote(noteName, initialNoteContent);
        startNoteActivity(noteName, initialNoteContent);
    }

    private void startNoteActivity(String noteName, String noteContent) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(NoteActivity.NOTE_NAME, noteName);
        intent.putExtra(NoteActivity.NOTE_CONTENT, noteContent);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
