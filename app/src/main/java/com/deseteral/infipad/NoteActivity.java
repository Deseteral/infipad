package com.deseteral.infipad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.deseteral.infipad.storage.StorageOrchestrator;

public class NoteActivity extends AppCompatActivity implements NoteEditor.OnEditorContentChangedListener {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private String initialNoteContent;
    private StorageOrchestrator storage;

    public static final String NOTE_TITLE = "com.deseteral.infipad.NOTE_TITLE";
    public static final String NOTE_CONTENT = "com.deseteral.infipad.NOTE_CONTENT";
    private static final String TAG = "NOTE_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final String noteTitle = intent.getStringExtra(NOTE_TITLE);
        initialNoteContent = intent.getStringExtra(NOTE_CONTENT);

        setContentView(R.layout.activity_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(noteTitle);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    @Override
    public void onEditorContentChanged(String newContent) {
        sectionsPagerAdapter.noteViewer.updateContentView(newContent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO: this could be unnecessary, I might want to replace it with menu_main.
        getMenuInflater().inflate(R.menu.menu_note, menu);
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

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        NoteViewer noteViewer;
        NoteEditor noteEditor;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                noteViewer = NoteViewer.newInstance(initialNoteContent);
                return noteViewer;
            } else if (position == 1) {
                noteEditor = NoteEditor.newInstance(initialNoteContent);
                return noteEditor;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Note viewer";
                case 1:
                    return "Note editor";
            }
            return null;
        }
    }
}
