package com.deseteral.infipad;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.deseteral.infipad.domain.Note;
import com.deseteral.infipad.service.ApplicationState;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteActivity extends AppCompatActivity implements NoteEditor.OnEditorContentChangedListener {

    private Note note;
    private SectionsPagerAdapter sectionsPagerAdapter;

    public static final String NOTE_ID = "com.deseteral.infipad.NOTE_ID";
    public static final String NOTE_CONTENT = "com.deseteral.infipad.NOTE_CONTENT";
    private static final String TAG = "NOTE_ACTIVITY";
    private static final int ACTION_PICK_PHOTO = 0;
    private static final int ACTION_TAKE_PICTURE = 1;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApplicationState.createState(getApplicationContext());

        final Intent intent = getIntent();
        final int index = intent.getIntExtra(NOTE_ID, -1);
        note = ApplicationState
                .getState()
                .getNotepad()
                .getNotes()
                .get(index);

        setContentView(R.layout.activity_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(note.getName());

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    @Override
    public void onEditorContentChanged(String newContent) {
        sectionsPagerAdapter.noteViewer.updateContentView(newContent);
        note.setContent(newContent);
        ApplicationState
                .getState()
                .getStorage()
                .saveNote(note);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.bar_pick_photo:
                showPickPhotoDialog();
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPickPhotoDialog() {
        final Context context = getApplicationContext();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle(R.string.insert_photo)
                .setItems(R.array.photo_picker_dialog_items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent pickPhoto = new Intent(
                                        Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                );
                                startActivityForResult(pickPhoto , ACTION_PICK_PHOTO);
                                break;
                            case 1:
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePicture.resolveActivity(getPackageManager()) != null) {
                                    File photoFile = null;
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }

                                    if (photoFile != null) {
                                        Uri photoURI = FileProvider.getUriForFile(
                                                context,
                                                "com.deseteral.infipad",
                                                photoFile
                                        );
                                        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                        startActivityForResult(takePicture, ACTION_TAKE_PICTURE);
                                    }
                                }
                                break;
                        }
                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case ACTION_PICK_PHOTO:
                if (resultCode == RESULT_OK) {
                    final String imageUri = imageReturnedIntent.getData().toString();
                    sectionsPagerAdapter.noteEditor.insertText(
                            "![image](" + imageUri + ")\n\n"
                    );
                }

                break;
            case ACTION_TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    final URI imageUri = new File(currentPhotoPath).toURI();
                    sectionsPagerAdapter.noteEditor.insertText(
                            "![image](" + imageUri.toString() + ")\n\n"
                    );
                }
                break;
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
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
                noteViewer = NoteViewer.newInstance(note.getContent());
                return noteViewer;
            } else if (position == 1) {
                noteEditor = NoteEditor.newInstance(note.getContent());
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
